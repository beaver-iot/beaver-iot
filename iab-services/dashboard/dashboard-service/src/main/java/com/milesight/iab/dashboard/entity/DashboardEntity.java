package com.milesight.iab.dashboard.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author loong
 * @date 2024/10/14 15:09
 */
@Data
public class DashboardEntity {

    private Long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;

}
