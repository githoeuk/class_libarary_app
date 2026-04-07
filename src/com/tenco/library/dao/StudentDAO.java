package com.tenco.library.dao;


import com.tenco.library.dto.Student;
import com.tenco.library.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 등록, 목록, 조회, 로그인
public class StudentDAO {

    // 학생 등록
    public void addStudent(Student student) throws SQLException {

        String sql = """
                INSERT INTO students(name,student_id) VALUES (? , ?)
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudent_Id());
            pstmt.executeUpdate();

        } // end of try

    } // end of addStudent

    // 전체 학생 조회
    public List<Student> getAllStudents() throws SQLException {

        List<Student> studentList = new ArrayList<>();
        String sql = """
                SELECT * FROM students ORDER BY ID
                """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()

        ) {
            while (rs.next()) {
//                Student student = new Student();
//                student.setId(rs.getInt("id"));
//                student.setName(rs.getString("name"));
//                student.setStudent_Id(rs.getString("student_id"));

                studentList.add(mapToStudent(rs));
            } // end of while

        } // end of try

        return studentList;
    } // end of getAllStudents

    // 학번으로 학생 조회 - 로그인 - 기능
    public Student authenticateStudent(String studentId) throws SQLException {
        String sql = """
                SELECT * FROM students WHERE student_id = ?
                """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            pstmt.setString(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToStudent(rs);
//                    Student student = new Student();
//                    student.setId(rs.getInt("id"));
//                    student.setName(rs.getString("name"));
//                    student.setStudent_Id(rs.getString("student_id"));
//                    return student;
                }
            } // end of inner_try
        }
        return null;
    }

    // ResultSet -> Student 로 변환하는 함수 // 예외는 쓰는사람이 처리하도록 설정
    private Student mapToStudent(ResultSet rs) throws SQLException {

//        Student student = new Student();
//        student.setId(rs.getInt("id"));
//        student.setName(rs.getString("name"));
//        student.setStudent_Id(rs.getString("student_id"));

        return Student.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .student_Id(rs.getString("student_id"))
                .build();
    }


    // 테스트 코드 작성
    public static void main(String[] args) throws SQLException {

        //new Student("이길동","12345");
        // builder패턴 -- 직관성이 뛰어남
        Student student = Student
                .builder()
                .student_id("202612345")
                .name("고길동")
                .build();
        StudentDAO studentDAO = new StudentDAO();

        //
        Student resultStudent = studentDAO.authenticateStudent("20230001");
        System.out.println(resultStudent);

        // 전체 학생 조회
        //System.out.println(studentDAO.getAllStudents().toString());

        // 학생 등록
        //studentDAO.addStudent(student);

    } // end of main
} // end of StudentDAO
