package com.xinda.xiaoxing.entity.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Tag {
    private Date time;
    private String item;
    private Float  value;
}
