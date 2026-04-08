package com.tenco.library.dao;

import com.tenco.library.dto.Admin;
import com.tenco.library.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // 관리자 인증 처리 - (adminId + password)
    public Admin authenticate (String adminId, String password)throws SQLException {

        String sql = """
                SELECT * FROM admins WHERE admin_id = ? AND password = ?
                """;

        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ){
            pstmt.setString(1,adminId);
            pstmt.setString(2,password);

            try(ResultSet rs = pstmt.executeQuery()){
                if (rs.next()){
                    return Admin.builder()
                            .id(rs.getInt("id"))
                            .adminId(rs.getString("admin_id"))
                            .name(rs.getString("name"))
                            .build();
                    // tip. 인증 후에는 일반적으로 비밀번호를 리턴하지 않는다.
                }

            } // end of rs

        } // end of pstmt

        // TODO - 반드시 Admin으로 리턴해야 함
        return null; // 로그인 실패 - 인증 실패
    } // end of authenticate


} // end of class
