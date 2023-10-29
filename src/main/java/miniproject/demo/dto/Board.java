package miniproject.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString

public class Board {
    private int boardId;
    private String title;
    private String content;
    private String name;
    private int userId;
    private LocalDateTime regdate;
    private int viewCnt;
}


/*
 board_id	int	NO	PRI		auto_increment
title	varchar(100)	NO
content	text	NO
user_id	int	NO	MUL
regdate	timestamp	NO		CURRENT_TIMESTAMP	DEFAULT_GENERATED
view_cnt	int	YES		0
*/