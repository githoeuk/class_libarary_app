package com.tenco.library.view;

import com.tenco.library.dto.Book;
import com.tenco.library.dto.Borrow;
import com.tenco.library.dto.Student;
import com.tenco.library.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// 사용자 입출력을 처리하는 View 클래스
public class LibraryView {

    private final LibraryService service = new LibraryService();
    private final Scanner sc = new Scanner(System.in);

    // 메모리상에 저장. 종료 시 사라짐
    private Integer currentStudentId = null; // 로그인 중인 학생의 DB ID 저장
    private String currentStudentName = null; // 로그인 중인 학생 이름

    // 프로그램 메인 루프
    public void start() {
        System.out.println("도서 관리 시스템 시작....");

        while (true) {
            printMenu();
            int ch = readInt("선택 : ");

            try {
                switch (ch) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        listBooks();
                        break;
                    case 3:
                        searchBooks();
                        break;
                    case 4:
                        addStudent();
                        break;
                    case 5:
                        listStudents();
                        break;
                    case 6:
                        borrowBooks();
                        break;
                    case 7:
                        listBorrowedBooks();
                        break;
                    case 8:
                        returnBooks();
                        break;
                    case 9:
                        login();
                        break;
                    case 10:
                        logout();
                        break;
                    case 11:
                        System.out.println("프로그램을 종료합니다.");
                        sc.close();
                        return; // while문 종료
                    default:
                        System.out.println("1~11 사이의 숫자를 입력하세요");

                } // switch 종료


            } catch (Exception e) {
                System.out.println("오류 : " + e.getMessage());
            }

        } // end of while

    } // end of start


    // 10. 로그아웃
    private void logout() {
        if (currentStudentId == null) {
            System.out.println("현재 로그인 상태가 아닙니다.");
        } else {
            System.out.println(currentStudentName + "님이 로그아웃되었습니다");
            currentStudentId = null;
            currentStudentName = null;
        }

    } // end of logout

    // 9. 로그인
    private void login() throws SQLException {
        if (currentStudentId != null) {
            System.out.println(" 이미 로그인 중입니다. (" + currentStudentName + ")");
            return;
        } // 로그인했는데 시도 중일 시
        // 유효성 검사
        System.out.println("학번 : ");
        String student_id = sc.nextLine().trim(); // 학번 - pk아님
        if (student_id.isEmpty()) {
            System.out.println("학번을 입력해주세요");
            return;
        }
        // 예외는 start매서드에 처리하도록 한다.
        Student student = service.authenticateStudent(student_id);
        if (student == null) {
            System.out.println("존재하지 않는 학번입니다.");
        } else {
            currentStudentId = student.getId();
            currentStudentName = student.getName();
            System.out.println(currentStudentName + "님, 환영합니다.");
        }

    } // end of login

    // 8. 도서 반납
    private void returnBooks() throws SQLException {
        System.out.print("반납할 책 번호를 입력하세요 : ");
        int bookId = Integer.parseInt(sc.nextLine().trim());
        if (bookId <= 0){
            System.out.println("유효하지 않는 도서ID입니다.");
        }
        System.out.print("반납 학생의 ID(PK)를 입력하세요 : ");
        int studentId = Integer.parseInt(sc.nextLine().trim());
        if (studentId <= 0){
            System.out.println("유효하지 않는 학생ID입니다.");
        }

        service.returnBook(bookId,studentId);
        System.out.println("정상적으로 반납되었습니다.");
    } // end of returnBook

    // 7. 대출 도서 조회
    private void listBorrowedBooks() throws SQLException {
        List<Borrow> borrowList = service.getBorrowsBooks();
        if (borrowList.isEmpty()){
            System.out.println("대출된 책이 없습니다.");
        }else {
            System.out.println("=========================");
            for (Borrow b : borrowList){
                System.out.printf("책ID : %3d | 학생ID : %7d | 대출날짜 : %15s",
                        b.getBook_id(),
                        b.getStudent_id(),
                        b.getBorrowDate());
                System.out.println();
            } // end of for
        }
    } // end of listBorrowedBooks

    // 6.도서 대출
    private void borrowBooks() throws SQLException {

        System.out.print("대여할 책 번호를 입력하세요 : ");
        int bookId = Integer.parseInt(sc.nextLine().trim());
        if (bookId <= 0){
            System.out.println("유효하지 않는 도서ID입니다.");
        }
        System.out.println("대여할 학생의 ID(pk)를 입력하세요 : ");
        int studentId = Integer.parseInt(sc.nextLine().trim());
        if (studentId <= 0){
            System.out.print("유효하지 않는 도서ID입니다.");
        }

        service.borrowBook(bookId,studentId);
        System.out.println("정삭적으로 대출되었습니다.");

        System.out.println();

    } // end of borrowBook

    // 5. 학생 목록
    private void listStudents() throws SQLException {
        List<Student> studentList = service.getAllStudent();
        if (studentList.isEmpty()) {
            System.out.println("등록된 학생이 없습니다.");
        } else {
            for (Student s : studentList) {
                System.out.println("========================");
                System.out.printf("학생이름 : %2d | 학생 학번 : %-30s  ",
                        s.getName(),
                        s.getStudent_Id());
            }
        }
    } // end of listStudents

    // 4. 학생 등록
    private void addStudent() throws SQLException {
        System.out.println("학생 이름 : ");
        String studentName = sc.nextLine().trim();
        if (studentName.isEmpty()) {
            System.out.println("학생 이름을 넣어주세요");
        }
        System.out.println("학생 학번 : ");
        String studentId = sc.nextLine().trim();
        if (studentName.isEmpty()) {
            System.out.println("학번을 넣어주세요 ");
        }
        Student student = Student.builder()
                .name(studentName)
                .student_Id(studentId)
                .build();

        service.addStudent(student);
        System.out.println("학생 등록 완료 " + studentName);
    } // end of addStudent

    // 3. 도서 조회
    private void searchBooks() throws SQLException {
        System.out.print("검색 제목 : ");
        String title = sc.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("검색어를 입력하세요");
            return;
        }
        List<Book> bookList = service.searchBooksByTitle(title);
        if (bookList.isEmpty()) {
            System.out.println("검색 결과가 없습니다.");
        } else {
            for (Book b : bookList) {
                System.out.println("========================");
                System.out.printf("ID : %2d | %-30s | %-15s | %s%n ",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.isAvailable() ? "대출 가능" : "대출 중");
            }
        }

    } // end of 도서 조회

    // 2번 도서 목록
    private void listBooks() throws SQLException {
        List<Book> bookList = service.getAllBooks();
        if (bookList.isEmpty()) {
            System.out.println("등록된 도서가 없습니다.");
        } else {
            for (Book b : bookList) {
                System.out.println("========================");
                System.out.printf("ID : %2d | %-30s | %-15s | %s%n ",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.isAvailable() ? "대출 가능" : "대출 중");

            }
        }

    } // end of listBooks

    // 1. 도서 추가
    private void addBook() throws SQLException {
        // 유효성 검사
        System.out.print("제목 : ");
        String title = sc.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("제목은 필수입니다.");
            return;
        } // 제목
        // 검사
        System.out.println("저자 : ");
        String author = sc.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("저자는 필수입니다.");
            return;
        } // 저자 검사

        // 출판사
        System.out.println("츨판사 : ");
        String publisher = sc.nextLine().trim();

        // 출판년도
        int publisherYear = readInt("출판년도");

        // ISBN
        System.out.println("ISBN : ");
        String ISBN = sc.nextLine().trim();

        Book book = Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher.isEmpty() ? null : publisher)
                .publicationYear(publisherYear)
                .isbn(ISBN.isEmpty() ? null : ISBN)
                .available(true)
                .build();

        service.addBook(book);
        System.out.println("도서 추가 : " + title);
    }


    private void printMenu() {
        System.out.println("\n=====도서관리 시스템======");

        System.out.println("-----------------------------");
        System.out.println("1. 도서 추가");
        System.out.println("2. 도서 목록");
        System.out.println("3. 도서 검색");
        System.out.println("4. 학생 등록");
        System.out.println("5. 학생 목록");
        System.out.println("6. 도서 대출");
        System.out.println("7. 대출 중인 도서");
        System.out.println("8. 도서 반납");
        System.out.println("9. 로그인");
        System.out.println("10. 로그아웃");
        System.out.println("11. 종료");

    }

    // 숫자 입력을 안전하게 처리 ( 잘못된 입력 시 재요청)
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요 ");
            }
        }
    } // end of readInt

} // end of class
