package com.customerpotal_backend.model;

import lombok.Data;

@Data
public class UserDetail {
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String userEmail;
	private String userPassword;
	private String mobile;
	private String dob;
}
