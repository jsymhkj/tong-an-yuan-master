package com.ymh.ssh.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by
 * On 11/16/2017.11:50 PM
 */
@Data
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "created")
    private Long created = System.currentTimeMillis();

    @Column(name = "username")
    private String username;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "remark")
    private String remark;
}
