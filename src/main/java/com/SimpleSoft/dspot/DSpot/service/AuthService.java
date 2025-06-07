package com.SimpleSoft.dspot.DSpot.service;

import com.SimpleSoft.dspot.DSpot.dto.LoginRequest;
import com.SimpleSoft.dspot.DSpot.dto.LoginResponse;
import com.SimpleSoft.dspot.DSpot.dto.SignupRequest;

public interface AuthService {

    /**
     * This endpoint is used for simple signup.
     *
     * @param request - Object containing all the necessary data
     */
    void signup(SignupRequest request);

    /**
     * This method logs in user by creating and issuing the token that can be used
     * for furhter requests
     *
     * @param request Object containing users email and password
     * @return {@link LoginResponse} - Object containing users role and token
     */
    LoginResponse login(LoginRequest request);
}
