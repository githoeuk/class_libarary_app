package com.tenco.library.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // toString 출력 시 - 비밀번호 제외

// 관리자 데이터 - DTO클래스
public class Admin {

    private int id;
    private String adminId;
    private String password;
    private String name;

} // end of class
