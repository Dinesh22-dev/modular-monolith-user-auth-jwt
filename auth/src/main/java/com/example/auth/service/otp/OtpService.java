package com.example.auth.service.otp;

import com.example.user.entity.User;

public interface OtpService {

    public String startChallenge(String userEmail, String channel, String sendTo);

    public boolean verify(String transactionId, String otp);

    User checkUser(String email, String password);

}
