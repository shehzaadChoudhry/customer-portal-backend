package com.customerpotal_backend.implementation;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.customerpotal_backend.entity.OtpDetail;
import com.customerpotal_backend.entity.UserDetail;
import com.customerpotal_backend.repository.OtpRepository;
import com.customerpotal_backend.repository.UserRepository;
import com.customerpotal_backend.response.ApiResponse;
import com.customerpotal_backend.service.EmailService;
import com.customerpotal_backend.service.UserService;
import com.customerpotal_backend.util.CommonUtil;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private EmailService emailService;

	@Override
	public UserDetail signupUser(UserDetail userDetail) {
		try {
			if (userDetail != null) {
				if (userDetail.getUserEmail() != null) {
					UserDetail existingEmail = userRepository.getUserDetail(userDetail.getUserEmail());
					if (existingEmail != null) {
						return null;
					} else {
						userDetail.setUserEmail(userDetail.getUserEmail());
					}
				}
				userDetail.setUserPassword(CommonUtil.convertToMD5(userDetail.getUserPassword()));
			}
			UserDetail savedUser = userRepository.save(userDetail);
			logger.info("User signed up successfully: {}" + savedUser.getUsername());

			return savedUser;
		} catch (Exception e) {
			logger.error("Error occurred while signing up user: {}" + userDetail.getUsername(), e);
			throw e;
		}
	}

	@Override
	public int loginUser(String email, String password) throws RuntimeException {
		try {
			logger.info(email);
			logger.info(password);
			UserDetail userDetail = userRepository.getUserDetail(email);

			if (userDetail == null) {
				logger.error("Invalid login attempt for user: {} - Email not found " + email);
				return 1;
			}
			if (userDetail.getUserEmail().equalsIgnoreCase(email)) {
				logger.info("MD5 - "+CommonUtil.convertToMD5(password)+" db "+userDetail.getUserPassword());
				if (CommonUtil.convertToMD5(password).equalsIgnoreCase(userDetail.getUserPassword())) {
					return 0;
				} else {
					logger.error("Incorrect password for user " + email);
					return 2;
				}
			}
		} catch (RuntimeException e) {
			logger.error("Error occured while logging in user: {} " + email + " " + e.getMessage());
		} catch (Exception e) {
			logger.error("Unexpected error occurred while logging in user: {} " + email + " " + e.getMessage());
			throw new RuntimeException("An error occurred during login");
		}
		return 2;
	}

	@Override
	public int sendOtpToEmail(String email) {

		try {
			UserDetail userDetail = userRepository.getUserDetail(email);

			if (userDetail == null) {
				return 1;
			}
			String otp = CommonUtil.generateOtp();

			OtpDetail otpDetail = new OtpDetail(email, otp,
					System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(CommonUtil.OTP_EXPIRY_MINUTES));
			otpRepository.save(otpDetail);

			String mailStatus = emailService.sendOtpEmail(email, otp);
			if (mailStatus.equalsIgnoreCase("Mail sent Successfully")) {
				return 0;
			} else {
				return 2;
			}
		} catch (Exception e) {
			logger.error("Error in sendOtpToEmail method " + e.getMessage());
		}
		return 0;
	}

	@Override
	public int verifyOtp(String userEmail, String otp) {

		try {
			OtpDetail otpDetail = otpRepository.findOtpByUserEmailAndOtp(userEmail, otp);

			if (otpDetail == null) {
				return 1;
			}

			if (otpDetail.getExpiryTime() < System.currentTimeMillis()) {
				return 2;
			}

			if (otpDetail.getOtp().equals(otp)) {
				otpRepository.deleteByUserEmailAndOtp(userEmail, otp);
				return 0;
			} else {
				return 1;
			}
		} catch (Exception e) {
			logger.error("Error in verifyOtp method " + e.getMessage());
		}
		return 3;
	}

	@Override
	public int updatePassword(String userEmail, String userPassword) {
		try {
			logger.info("new password "+userPassword);
			String encryptedPassword = CommonUtil.convertToMD5(userPassword);
			UserDetail existingEmail = userRepository.getUserDetail(userEmail);
			if (existingEmail != null) {
				userRepository.updateUserPassword(encryptedPassword, userEmail);
				return 0;
			}
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}

	@Override
	public UserDetail getUserDetails(String userEmail) {
		UserDetail user = userRepository.getUserDetail(userEmail);
		return user;
	}
}
