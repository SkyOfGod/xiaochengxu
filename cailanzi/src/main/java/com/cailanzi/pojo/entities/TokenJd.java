package com.cailanzi.pojo.entities;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by v-hel27 on 2018/9/10.
 */
@Data
public class TokenJd {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String token;
    private String expiresIn;
    private String time;
    private String uid;
    private String userNick;
    private String venderId;
    private Date createTime;

}
