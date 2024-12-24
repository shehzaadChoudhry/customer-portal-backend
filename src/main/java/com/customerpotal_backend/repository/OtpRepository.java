package com.customerpotal_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.customerpotal_backend.entity.OtpDetail;

import jakarta.transaction.Transactional;

@Repository
public interface OtpRepository extends JpaRepository<OtpDetail, Long>{

	OtpDetail findOtpByUserEmailAndOtp(String userEmail, String otp);

	@Transactional
	void deleteByUserEmailAndOtp(String userEmail, String otp);

}
