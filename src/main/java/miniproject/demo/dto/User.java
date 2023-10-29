package miniproject.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor //기본생성자 자동 생성
@ToString // odbject의 toString 매서드를 자동으로 만들어줌
public class User {
    private int userId;
    private String email;
    private String name;
    private String password;
    private String regdate; // 원래는 날짜 타입으로 읽어와서 문자열로바꿔야댐


}
/*
    user_id  int primary key auto_increment,
    email    varchar(255) not null,
    name     varchar(50) not null,
    password varchar(500) not null,
    regdate  timestamp not null default now()
 */
