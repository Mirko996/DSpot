package com.SimpleSoft.dspot.DSpot.dto;

import com.SimpleSoft.dspot.DSpot.enums.Role;
import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private Long distributorId;
}

