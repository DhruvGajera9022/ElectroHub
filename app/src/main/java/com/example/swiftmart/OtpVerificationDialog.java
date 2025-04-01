package com.example.swiftmart;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.swiftmart.EmailService.OTPManager;
import com.example.swiftmart.EmailService.SendEmailTask;

public class OtpVerificationDialog extends Dialog {
	private EditText otpET1,otpET2,otpET3,otpET4;
	private TextView resendBtn;
	private Button verifyBtn;

	//resend otp time in seconds
	private int resendTime = 60;

	//will br true after 60 seconds
	private boolean resendEnabled = false;
	private  int selectedETPosition = 0;
	private final String email;
	private onOTPsubmit callback;
	private Context context;

	public OtpVerificationDialog(Context context, String email,onOTPsubmit callback) {
		super(context);
		this.context = context;
		this.email = email;
		this.callback = callback;
	}

	public interface onOTPsubmit{
		void onSubmit(String otp);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
		setContentView(R.layout.otp_dialog_layout);

		otpET1 = findViewById(R.id.otpET1);
		otpET2 = findViewById(R.id.otpET2);
		otpET3 = findViewById(R.id.otpET3);
		otpET4 = findViewById(R.id.otpET4);

		resendBtn = findViewById(R.id.resendBtn);
		verifyBtn = findViewById(R.id.verifyBtn);
		final TextView Email = findViewById(R.id.txt_ContactEmail);
		otpET1.addTextChangedListener(textWatcher);
		otpET2.addTextChangedListener(textWatcher);
		otpET3.addTextChangedListener(textWatcher);
		otpET4.addTextChangedListener(textWatcher);

		// By default open keyboard on first EditText
		showKeyboard(otpET1);

		// start countdown timer
		startCountDownTimer();

		// set mobile number textview
		Email.setText(email);

		resendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (resendEnabled){
					String recipientEmail = email; // Replace with the recipient's email
					String otp = OTPManager.generateOTP();   // Generate OTP
					OTPManager.clearOTP(context);
					OTPManager.saveOTP(context, otp, System.currentTimeMillis() + (1 * 60 * 1000));

					String subject = "Your OTP Code";
					String messageBody = "Your OTP for authentication is: " + otp;

					SendEmailTask sendEmailTask = new SendEmailTask(recipientEmail, subject, messageBody);
					sendEmailTask.execute(); // Send the email in the background
					// resend code here
					startCountDownTimer();
				}
			}
		});

		verifyBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final String getOTP = otpET1.getText().toString() + otpET2.getText().toString() + otpET3.getText().toString() + otpET4.getText().toString();

				if(getOTP.length() == 4){
					callback.onSubmit(getOTP);
				}
			}
		});
	}

	private  final TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable s) {

			if (s.length() > 0){

				if (selectedETPosition == 0){

					// select next edit text
					selectedETPosition = 1;
					showKeyboard(otpET2);
				}
				else if (selectedETPosition == 1) {

					// select next edit text
					selectedETPosition = 2;
					showKeyboard(otpET3);
				}
				else if (selectedETPosition == 2) {

					// select next edit text
					selectedETPosition = 3;
					showKeyboard(otpET4);
				}
				else {
					verifyBtn.setBackgroundColor(R.drawable.btn);
				}
			}
		}
	};

	private void showKeyboard(EditText otpET){

		otpET.requestFocus();
		InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
	}

	private void startCountDownTimer(){

		resendEnabled = true;
		resendBtn.setTextColor(Color.parseColor("#99000000"));

		new CountDownTimer(resendTime * 1000,1000){

			@Override
			public void onTick(long millisUntilFinished) {
				resendBtn.setText("Resend Code ("+(millisUntilFinished / 1000)+")");
			}

			@Override
			public void onFinish() {
				resendEnabled = true;
				resendBtn.setText("Resend Code");
				resendBtn.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
			}
		}.start();
	}

	@Override
	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_DEL){

			if (selectedETPosition  == 3){

				// select previous Edit Text
				selectedETPosition = 2;
				showKeyboard(otpET3);
			}
			else if (selectedETPosition == 2) {

				// select previous Edit Text
				selectedETPosition = 1;
				showKeyboard(otpET2);
			}
			else if (selectedETPosition == 1) {

				// select previous Edit Text
				selectedETPosition = 0;
				showKeyboard(otpET1);
			}

			verifyBtn.setBackgroundResource(R.drawable.round_back_brown_100);
			return true;
		}
		else {
			return super.onKeyUp(keyCode, event);
		}

	}
}
