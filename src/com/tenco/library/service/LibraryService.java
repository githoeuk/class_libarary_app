package com.tenco.library.service;


// 비즈니스 로직을 처리하는 서비스 클래스 -- Service 계층
// view 계층(화면) -> Service 계층 --> Data 계층
// 뷰 계층에서는 DAO를 직접 호출하지 말고 항상 Service 계층을 통해서 접근한다.

import com.tenco.library.dao.BookDAO;
import com.tenco.library.dao.BorrowDAO;
import com.tenco.library.dao.StudentDAO;
import com.tenco.library.dto.Book;
import com.tenco.library.dto.Borrow;
import com.tenco.library.dto.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LibraryService {
    private final BookDAO bookDAO = new BookDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    // 만약 화면단에 도서 추가 기능 요청 (화면단 코드 ) 발생 한다면
    // 서비스 단에서는 사용자가 입력한 데이터가 유효한지 유효성 검사를 진행.
    // 입력한 데이터가 정상적이라면 DB에 반영할 예정

    // 1. 도서 추가 기능 (제목, 저자 필수 검증)
    public void addBook(Book book) throws SQLException {
        // 유효성 검사
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new SQLException("도서 제목은 필수 입력 항목입니다. ");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new SQLException("도서 저자는 필수 입력 항목입니다. ");
        }
        bookDAO.addBook(book);
    } // end of addBook

    // 2. 전체 도서 목록 조회 ( 대출 여부 상관 없음)
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    } // getAllBooks

    // 3. 책 제목으로 검색
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new SQLException("검색어를 입력해주세요");
        }
        return bookDAO.searchBooksByTitle(title);
    } // end of searchBooksByTitle

    // 4. 학생 등록 기능 (이름 , 학번 필수 검증 )
    public void addStudent(Student student) throws SQLException {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new SQLException("학생의 이름은 필수 항목입니다.");
        }
        if (student.getStudent_Id() == null || student.getStudent_Id().trim().isEmpty()) {
            throw new SQLException("학생의 이름은 필수 항목입니다.");
        }

        studentDAO.addStudent(student);
    } // end of addStudent

    // 5. 전체 학생 조회 목록
    public List<Student> getAllStudent() throws SQLException {
        return studentDAO.getAllStudents();
    }

    // 5. 학번이 유요한지 조회(로드인 처리)

    /***
     *
     * @param studentId = string (pk아님)
     * @return
     */
    public Student authenticateStudent(String studentId) throws SQLException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new SQLException("학번을 입력해주세요");
        }
        return studentDAO.authenticateStudent(studentId);
    } // end of authenticateStudent

    // 6. 도서 대출 요청

    /**
     *
     * @param bookId
     * @param studentId : 학번이 아니라 pK값
     * @throws SQLException
     */
    public void borrowBook(int bookId, int studentId) throws SQLException {
        // 유효성 검사
        if (bookId <= 0 || studentId <= 0) {
            throw new SQLException("유효한 도서ID , 학생 ID를 입력해주세요 ");
        }
        borrowDAO.borrowBook(bookId, studentId);
    } // end of borrowBook

    // 7. 도서 반납 처리

    /**
     *
     * @param bookId
     * @param studentId : 학번 아님 -> pk)
     * @throws SQLException
     */
    public void returnBook(int bookId, int studentId) throws SQLException {
        // 유효성 검사
        if (bookId <= 0 || studentId <= 0) {
            throw new SQLException("유효한 도서ID , 학생 ID를 입력해주세요 ");
        }
        borrowDAO.returnBook(bookId, studentId);
    }

    // 8. 대출 도서 조회
    public List<Borrow> getBorrowsBooks() throws SQLException {
        return borrowDAO.getBorrowsBooks();
    }

    // Todo - 관리자 기능 추가 예정

} // end of LibraryService
