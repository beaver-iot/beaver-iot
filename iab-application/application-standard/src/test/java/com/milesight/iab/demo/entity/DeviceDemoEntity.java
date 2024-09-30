package com.milesight.iab.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author leon
 */
@Data
@Entity
@Table(name = "device_demo")
public class DeviceDemoEntity {

    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy=GenerationType.AUTO, generator = "device_demo_id_seq")
//    @SequenceGenerator(name = "device_demo_id_seq", sequenceName = "device_demo_id_seq", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_demo_id_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "created_by")
    private Long createdBy;


}
