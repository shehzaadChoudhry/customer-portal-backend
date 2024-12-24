package com.customerpotal_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OtpDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userEmail;
	private String otp;
	private long expiryTime;
	
	public OtpDetail(String userEmail, String otp, long expiryTime) {
        this.userEmail = userEmail;
        this.otp = otp;
        this.expiryTime = expiryTime;
    }
	
	public OtpDetail() {}
}
