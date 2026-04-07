package com.tenco.library.dao;

import com.tenco.library.dto.Borrow;
import com.tenco.library.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO2 {

    // 도서 대출 처리
    // 대출 가능 여부 확인 - 특정 책 관련 -> 가능 - borrow 테이블에 기록 - available 수정
    // 트랙잭션 처리를 해야 함
    // try - with - resource 블록 문법 - 블록이 끝나는 순간 무조건 자원을 먼저 닫아 버림
    // 이게 트랙잭션 처리할 때는 값을 확인해서 commit 또는 rollback을 해야하기 때문에 사용하면 안된다.
    // 즉, 직접 close()처리 해야 함 - 트랙잭션 처리를 위해서

    // 1. 대출 가능 여부 확인
    public void borrowBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 트랙잭션 시작

            // 1. 대출 가능 여부 확인
            String checksql = """
                    SELECT available FROM books WHERE id = ?
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checksql)) {
                checkPstmt.setInt(1, bookId);

                try (ResultSet rs = checkPstmt.executeQuery()) {
                    // 등록되지 않은 책일 경우
                    if (rs.next() == false) {
                        throw new SQLException("존재하지 않는 도서입니다. 도서 번호 : " + bookId);
                    }
                    // 등록 된 책이지만 대출 중인 경우
                    if (rs.getBoolean("available") == false) {
                        throw new SQLException("현재 대출 중인 도서입니다. 도서번호 : " + bookId);
                    } //

                } // end of ResultSet
            } // end of checkPstmt

            // 대출 가능 확인 -> 대출 테이블에 기록
            // 2. 대출 테이블에 기록
            String borrowSql = """
                    INSERT INTO borrows (book_id,student_id,borrow_date) values (? , ? , ?)
                    """;
            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1, bookId);
                borrowPstmt.setInt(2, studentId);
                borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
                borrowPstmt.executeUpdate();
            } // end of borrowPstmt

            // 대출 테이브 기록 -> 도서 상태 변경(available = 1 -> available = 0)
            // 3. 도서 상태 변경
            String updateSql = """
                    UPDATE books
                    SET abailable = FALSE WHERE = id =? 
                    """;
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

                updatePstmt.setInt(1, bookId);
                updatePstmt.executeUpdate();
            } // end of updatePstmt

            // 1,2,3 단계 전부 성공 시
            conn.commit(); // 트랜잭션 성공

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // 중간에 오류가 발생하면 트랜잭션 시작 전으로 복구
            }
            System.out.println("오류 발생 : " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    } // end of borrowBook

    // 2. 대출 기록

    public List<Borrow> getBorrowsBooks() throws SQLException {

        List<Borrow> borrowList = new ArrayList<>();
        String sql = """
                SELECT * FROM borrows WHERE return_date IS NULL ORDER BY borrow_date
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement borrowListPstmt = conn.prepareStatement(sql);
             ResultSet rs = borrowListPstmt.executeQuery()
        ){
            while(rs.next()){
                Borrow borrow = Borrow.builder()
                        .id(rs.getInt("id"))
                        .book_id(rs.getInt("book_id"))
                        .student_id(rs.getInt("student_id"))
                        .borrowDate(rs.getDate("borrow_date"))
                        .build();
            }
        }



    } // end of getBorrowsBooks

    // 3. 대출 가능 여부 수정
}
