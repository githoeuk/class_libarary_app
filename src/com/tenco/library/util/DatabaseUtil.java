package com.tenco.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/library?severTimezone = Asia/Seoul";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // 역할
    // 새로운 DB연결 객체를 반환하는 것.

    public static Connection getConnection() throws SQLException {

        // 효과 만들어 보기 (...)
        Thread thread = new Thread(() -> {
            System.out.print("Connecting to database");
            for (int i = 0; i < 5; i++) {
                System.out.print(".");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } // end of for
        }); // end of Thread

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Connection connection = DriverManager.getConnection(URL,DB_USER,PASSWORD);
        System.out.println();
        System.out.println(connection.getMetaData().getDatabaseProductName());
        System.out.println(connection.getMetaData().getDatabaseProductVersion());
        return connection;

    }// end of getConnection

    // TODO - 삭제 예정
    // test 코드 작성
    public static void main(String[] args) {
        try (Connection connection = DatabaseUtil.getConnection()) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
