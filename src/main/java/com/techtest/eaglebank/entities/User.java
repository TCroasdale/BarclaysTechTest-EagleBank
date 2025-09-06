package com.techtest.eaglebank.entities;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "userid")
    public String userid;

    @Column(name = "name")
    public String name;

    @Column(name = "phoneNumber")
    public String phoneNumber;

    @Column(name = "email")
    public String email;

    @Column(name = "address")
    public String address;

    @Column(name = "createdAt")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public OffsetDateTime createdTimestamp;

    @Column(name = "updatedAt")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public OffsetDateTime updatedTimestamp;
}
