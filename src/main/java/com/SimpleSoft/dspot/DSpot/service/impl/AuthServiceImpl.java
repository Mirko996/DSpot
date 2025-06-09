package com.SimpleSoft.dspot.DSpot.service.impl;

import com.SimpleSoft.dspot.DSpot.domain.Distributor;
import com.SimpleSoft.dspot.DSpot.domain.User;
import com.SimpleSoft.dspot.DSpot.dto.LoginRequest;
import com.SimpleSoft.dspot.DSpot.dto.LoginResponse;
import com.SimpleSoft.dspot.DSpot.dto.SignupRequest;
import com.SimpleSoft.dspot.DSpot.exception.ServiceException;
import com.SimpleSoft.dspot.DSpot.repository.DistributorRepository;
import com.SimpleSoft.dspot.DSpot.repository.UserRepository;
import com.SimpleSoft.dspot.DSpot.security.JWTUtil;
import com.SimpleSoft.dspot.DSpot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DistributorRepository distributorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public void signup(SignupRequest request) {
        Distributor distributor = validateSignupRequest(request);
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .distributor(distributor)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .updatedAt(new Timestamp(System.currentTimeMillis()))
                .build();

        userRepository.save(user);
    }

    private Distributor validateSignupRequest(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ServiceException("Email already in use.");

        return distributorRepository.findById(request.getDistributorId())
                .orElseThrow(() -> new ServiceException("Distributor not found."));
    }

    public LoginResponse login(LoginRequest request) {
        User user = validateLoginRequest(request);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getDistributor().getId());
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }

    private User validateLoginRequest(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new ServiceException("Invalid credentials");
        return user;
    }
}
