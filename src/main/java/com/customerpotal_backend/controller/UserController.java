package com.customerpotal_backend.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.customerpotal_backend.entity.OtpDetail;
import com.customerpotal_backend.entity.UserDetail;
import com.customerpotal_backend.implementation.UserServiceImpl;
import com.customerpotal_backend.response.ApiResponse;
import com.customerpotal_backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@PostMapping("/signup")
	@Operation(summary = "Sign up a new user", description = "This API allows users to sign up by providing their details.", responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully signed up", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDetail.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input") })
	public ResponseEntity<ApiResponse<UserDetail>> signUpUser(
			@Parameter(description = "User signup details", required = true) @Valid @RequestBody UserDetail userDetail) {

		UserDetail savedUser = userService.signupUser(userDetail);

		if (savedUser != null) {
			ApiResponse<UserDetail> response = new ApiResponse<>("User successfully signed up", savedUser,
					HttpStatus.CREATED);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			ApiResponse<UserDetail> response = new ApiResponse<>("Email already exists. Please login instead", null,
					HttpStatus.CONFLICT);
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/login")
	@Operation(summary = "Login existing user", description = "This API allows existing users to sign in by providing their credentials.", responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetail.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Email not found"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Incorrect password") })
	public ResponseEntity<ApiResponse<UserDetail>> loginUser(
			@Parameter(description = "User login credentials", required = true) @RequestBody UserDetail userDetail) {

		int loggedInUser = 0;
		try {
			loggedInUser = userService.loginUser(userDetail.getUserEmail(), userDetail.getUserPassword());

			if (loggedInUser == 1) {
				ApiResponse<UserDetail> response = new ApiResponse<UserDetail>("Email not find", null,
						HttpStatus.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			} else if (loggedInUser == 2) {
				ApiResponse<UserDetail> response = new ApiResponse<>("Incorrect password for user", null,
						HttpStatus.UNAUTHORIZED);
				return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
			} else if (loggedInUser == 0) {
				UserDetail user = userService.getUserDetails(userDetail.getUserEmail());
				ApiResponse<UserDetail> response = new ApiResponse<>("Successfully logged in", user, HttpStatus.OK);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}

		} catch (Exception e) {
			logger.error("Error in loginUser controller loginUser method " + e.getMessage());
		}
		return null;
	}

	@PostMapping("/forgotPassword")
	@Operation(summary = "Update password", description = "This API allows existing users to change their password in case they can't remember their current password.", responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetail.class))) })
	public ResponseEntity<ApiResponse<UserDetail>> forgotPassword(
			@Parameter(description = "Email ID required to send OTP", required = true) @RequestBody UserDetail userDetail) {

		try {
			int responseMessage = userService.sendOtpToEmail(userDetail.getUserEmail());

			if (responseMessage == 0) {
				ApiResponse<UserDetail> response = new ApiResponse<>("OTP sent successfully on email ", null,
						HttpStatus.OK);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (responseMessage == 1) {
				ApiResponse<UserDetail> response = new ApiResponse<UserDetail>("Email not find, please sign up first",
						null, HttpStatus.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			} else {
				ApiResponse<UserDetail> response = new ApiResponse<UserDetail>(
						"Something went wrong, please again later", null, HttpStatus.INTERNAL_SERVER_ERROR);
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error("Error in loginUser controller forgotPassword method " + e.getMessage());
		}
		return null;
	}

	@PostMapping("/verifyOtp")
	@Operation(summary = "Verify OTP", description = "This API allows users to verify OTP sent to their email for password reset.", responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetail.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invalid OTP", content = @Content(mediaType = "application/json")),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "OTP expired", content = @Content(mediaType = "application/json")) })

	public ResponseEntity<ApiResponse<UserDetail>> verifyOtp(
			@Parameter(description = "Email ID and OTP to verify", required = true) @RequestBody OtpDetail otpDetail) {
		try {
			int responseMessage = userService.verifyOtp(otpDetail.getUserEmail(), otpDetail.getOtp());

			if (responseMessage == 0) {
				ApiResponse<UserDetail> response = new ApiResponse<>("OTP verified successfully", null, HttpStatus.OK);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else if (responseMessage == 1) {
				ApiResponse<UserDetail> response = new ApiResponse<>("Invalid OTP", null, HttpStatus.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			} else if (responseMessage == 2) {
				ApiResponse<UserDetail> response = new ApiResponse<>("OTP expired", null, HttpStatus.BAD_REQUEST);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else {
				ApiResponse<UserDetail> response = new ApiResponse<>("Something went wrong, please try again later",
						null, HttpStatus.INTERNAL_SERVER_ERROR);
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error("Error in verifyOtp controller " + e.getMessage());
		}
		return null;
	}

	@PostMapping("/updatePassword")
	@Operation(summary = "Update Password", description = "This API allows users to update their password.", responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password updated successfully", content = @Content(mediaType = "application/josn", schema = @Schema(implementation = UserDetail.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Password cannot be same as previous", content = @Content(mediaType = "application/josn")) })
	public ResponseEntity<ApiResponse<UserDetail>> updatePassword(
			@Parameter(description = "Email and new password", required = true) @RequestBody UserDetail userDetail) {

		try {
			int responseMessage = userService.updatePassword(userDetail.getUserEmail(), userDetail.getUserPassword());

			if (responseMessage == 0) {
				ApiResponse<UserDetail> response = new ApiResponse<>("Password updated successfully", null,
						HttpStatus.OK);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error("Error in updatePassword controller " + e.getMessage());
		}
		return null;

	}
}
