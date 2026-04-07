package com.tenco.library.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class Student {

    private int id;
    private String name;
    private String student_Id;

    @Builder
    public Student(String name, String student_id){
        this.name = name;
        this.student_Id = student_id;
    }

}
