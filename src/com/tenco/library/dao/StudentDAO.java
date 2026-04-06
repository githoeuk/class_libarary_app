package com.tenco.library.dao;

import com.tenco.library.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentDAO {
    // 학생 등록
    public int addStudent(){
        String sql = """
                INSERT INTO student(name) value (?,?)
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,"name");
            pstmt.setString(2,"student_id");

            int rows = pstmt.executeUpdate();
            return rows;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // 목록 조회




}
