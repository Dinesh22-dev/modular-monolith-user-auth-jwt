package com.example.auth.service.otp;

import com.example.user.entity.User;

public interface OtpService {

    String startChallenge(String userEmail, String channel, String sendTo);

    boolean verify(String transactionId, String otp);

    User checkUser(String email, String password);

    User verifyy(String transactionId, String otp);

    User checkUserOnlyEmail(String email);

    String resendOtp(String transactionId);

}
