package com.SimpleSoft.dspot.DSpot.dto;

import com.SimpleSoft.dspot.DSpot.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private Role role;
}
