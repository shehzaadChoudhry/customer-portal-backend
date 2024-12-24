package com.customerpotal_backend.service;

import com.customerpotal_backend.entity.UserDetail;

public interface UserService {

	UserDetail signupUser(UserDetail userDetail);
	
	int loginUser(String email, String password);

	int sendOtpToEmail(String userEmail);

	int verifyOtp(String userEmail, String otp);

	int updatePassword(String userEmail, String userPassword);

	UserDetail getUserDetails(String userEmail);
}
