package com.example.swiftmart.EmailService;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

public class OTPManager {
	private static final String PREFS_NAME = "OTP_PREFS";
	private static final String KEY_OTP = "KEY_OTP";
	private static final String KEY_EXPIRY = "KEY_EXPIRY";

	//generate otp
	public static String generateOTP() {
		Random random = new Random();
		int otp = 1000 + random.nextInt(9000); // Generate a 4-digit OTP
		return String.valueOf(otp);
	}

	// Save OTP with an expiry time (in milliseconds)
	public static void saveOTP(Context context, String otp, long expiryDurationMillis) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(KEY_OTP, otp);
		editor.putLong(KEY_EXPIRY, System.currentTimeMillis() + expiryDurationMillis); // Calculate expiry time
		editor.apply();
	}

	// Validate OTP automatically clearing expired data
	public static boolean validateOTP(Context context, String inputOTP) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String storedOTP = sharedPreferences.getString(KEY_OTP, null);
		long expiryTime = sharedPreferences.getLong(KEY_EXPIRY, 0);

		// Check if OTP exists
		if (storedOTP == null || expiryTime == 0) {
			return false; // No OTP stored
		}

		// Check if OTP is expired
		if (System.currentTimeMillis() > expiryTime) {
			clearOTP(context); // Automatically clear expired OTP
			return false; // OTP is expired
		}

		// Validate the input OTP
		if (storedOTP.equals(inputOTP)) {
			clearOTP(context); // Clear OTP after successful validation
			return true; // OTP is valid
		}

		return false; // OTP does not match
	}

	// Automatically clear OTP data after expiry or validation
	public static void clearOTP(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		sharedPreferences.edit().clear().apply();
	}

	// Helper to check if an OTP is still valid without validating the input
	public static boolean isOTPValid(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		long expiryTime = sharedPreferences.getLong(KEY_EXPIRY, 0);

		if (System.currentTimeMillis() > expiryTime) {
			clearOTP(context); // Automatically clear expired OTP
			return false; // OTP has expired
		}

		return true; // OTP is still valid
	}
}
