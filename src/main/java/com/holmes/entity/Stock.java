package com.holmes.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author holmes
 */
@Data
public class Stock implements Serializable {
    private Integer id;

    private String name;

    private Integer count;

    private Integer sale;

    private Integer version;

    private Date createTime;

    private Date updateTime;

    private Boolean deleteFlag;


}
