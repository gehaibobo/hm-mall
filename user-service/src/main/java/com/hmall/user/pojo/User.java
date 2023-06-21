package com.hmall.user.pojo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private Integer status;
    private Long balance;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}