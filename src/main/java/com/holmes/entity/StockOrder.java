package com.holmes.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author holmes
 */
@Data
public class StockOrder implements Serializable {
    private Integer id;

    private Integer sid;

    private String name;

    private Date createTime;

    private Date updateTime;

    private Boolean deleteFlag;

}
