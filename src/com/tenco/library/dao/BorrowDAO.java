package com.tenco.library.dao;

import com.tenco.library.dto.Borrow;
import com.tenco.library.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // 도서 대출 처리
    // 대출 가능 여부 확인 - 특정 책 관련 -> 가능 - borrow 테이블에 기록 - available 수정
    // 트랙잭션 처리를 해야 함
    // try - with - resource 블록 문법 - 블록이 끝나는 순간 무조건 자원을 먼저 닫아 버림
    // 이게 트랙잭션 처리할 때는 값을 확인해서 commit 또는 rollback을 해야하기 때문에 사용하면 안된다.
    // 즉, 직접 close()처리 해야 함 - 트랙잭션 처리를 위해서

    /**
     *
     * @param bookId
     * @param studentId : 학번이 아니라 student 테이블의 pk 참조 -> int타입
     * @throws SQLException
     */

    public void borrowBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 대출 가능 여부 확인
            String checkSql = """
                    SELECT available FROM books WHERE id = ?
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, bookId);

                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (rs.next() == false) {
                        throw new SQLException("존재하지 않는 도서입니다. 도서 번호 : " + bookId);
                    } // end of if(rs.next)
                    // 책이 있을 시
                    if (rs.getBoolean("available") == false) {
                        throw new SQLException("현재 대출 중인 도서입니다. 도서 번호 : " + bookId);
                    } // end of if(rs.getBoolean)
                } // end of try(ResultSet)
            } // end of try_1

            // 대출 가능한 상태 --> 대출 테이블에 학번 , 책 ,번호를 기록 해야함 .
            // 2, 대출 기록 추가
            String borrowSql = """
                    INSERT INTO borrows (book_id,student_id,borrow_date) VALUES (? , ? ,?)
                    """;

            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1, bookId);
                borrowPstmt.setInt(2, studentId);
                // LocalDate타입은 mysql에서 없는 타입이기에 변환해줘야 함
                borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
                borrowPstmt.executeUpdate();
            } // end of try_2

            // 3. 도서 상태 변경
            String updateSql = """
                    UPDATE books SET available = FALSE WHERE id = ?
                    """;
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)
            ) {
                updatePstmt.setInt(1, bookId);
                updatePstmt.executeUpdate();

            } // end of try_3

            // 1,2,3 전부 성공 시
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                // 중간에 오류 발생으로 처리 불가 시 트랜잭션 전으로 rollback시켜버림
                conn.rollback();
            }
            System.out.println("오류 발생 " + e.getMessage());
        } finally {

            if (conn != null) {
                // 중간에 오류 발생으로 처리 불가 시 트랜잭션 전으로 rollback시켜버림
                // conn.rollback(); 여기에 작성하면 성공하더라도 rollback하게 되어버림

                //autocommit 복구
                conn.setAutoCommit(true);
                // 직접 닫아줘야함
                conn.close();
            } // end of if(conn)

        } // end of try-finally

    } // end of borrowBook


    // 현재 대출 중인 도서 목록 조회

    public List<Borrow> getBorrowsBooks() throws SQLException {
        List<Borrow> borrowList = new ArrayList<>();
        String sql = """
                SELECT * FROM borrows WHERE return_date IS NULL ORDER BY borrow_date;
                """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                Borrow borrow = Borrow.builder()
                        .id(rs.getInt("id"))
                        .book_id(rs.getInt("book_id"))
                        .student_id(rs.getInt("student_id"))
                        // sql에서는 borrow_date가 date타입이기에 localDate로 변환해서 받는다
                        //.borrowDate(rs.getDate("borrow_date").toLocalDate())
                        .borrowDate(rs.getDate("borrow_date") != null
                                ? rs.getDate("borrow_date").toLocalDate()
                                : null
                        )
                        .build();
                borrowList.add(borrow);
            }

        }

        return borrowList;
    } // end of getBorrowsBooks

    // 도서 반납 처리
    // 순서
    // 1. 대출 기록 확인 2. return_date 업데이트 3. Book 도서 상태 업데이트처리(available)

    /**
     *
     * @param bookId
     * @param StudentId : student 테이블 - pk값
     */

    public void returnBook(int bookId, int StudentId) throws SQLException {
        // 트랜잭션 시작
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 대출 기록 확인
            String checkSql = """
                    SELECT id FROM borrows 
                    WHERE book_id = ? AND student_id = ? AND return_date IS NULL
                    """;

            int borrowID;

            try (PreparedStatement checkPstm = conn.prepareStatement(checkSql)) {
                checkPstm.setInt(1, bookId);
                checkPstm.setInt(2, StudentId);

                try (ResultSet rs = checkPstm.executeQuery()) {
                    if (rs.next() == false) {
                        throw new SQLException("해당 대출 기록이 없거나 이미 반납 되었습니다.");
                    } // 필터
                    // 대출 테이블에 해당하는 PK추출
                    borrowID = rs.getInt("id");
                } // end of try
            } // end of checkPstm

            // 2. 반납일 기록
            String updateBorrowSql = """
                    UPDATE borrows SET return_date = ? WHERE id = ?
                    """;
            try (PreparedStatement updateBorrowPStmt = conn.prepareStatement(updateBorrowSql)) {
                updateBorrowPStmt.setDate(1, Date.valueOf(LocalDate.now()));
                updateBorrowPStmt.setInt(2, borrowID);
                updateBorrowPStmt.executeUpdate();
            } // end of updateBorrow
            // 3. 도서 상태 변경 (대출 가능 )
            String updateBookSql = """
                    UPDATE books SET available = TRUE WHERE id = ?
                    """;
            try (PreparedStatement updateBookPstmt = conn.prepareStatement(updateBookSql)) {
                updateBookPstmt.setInt(1, bookId);
                updateBookPstmt.executeUpdate();

            } // end of updateBook

            // 모두 성공 --> commit 처리
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            System.out.println("오류 발생 : " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } // end of try_finally

    } // end of returnBook
}
