package com.SimpleSoft.dspot.DSpot.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

@Entity
@Table(name = "distributors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Distributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "distributor", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    private Timestamp createdAt;
    private Timestamp updatedAt;
}