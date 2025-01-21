package com.example.swiftmart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftmart.Account.Language_Activity;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private TextView signUpSignInTxt;
    private ScrollView signUpScrollView;
    private EditText signUpUserNameInput, signUpEmailInput, signUpPasswordInput, signUpConfirmPasswordInput;
    private MaterialButton signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar signUpProgressBar;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialization();
        inputValidation();
        handleSignInTextClick();
        handleSignUpButtonClick();
        setStatusBarColor(R.color.home);
    }

    // All the id initialize
    private void initialization(){
        signUpSignInTxt = findViewById(R.id.signUpSignInTxt);
        signUpScrollView = findViewById(R.id.signUpScrollView);
        signUpUserNameInput = findViewById(R.id.signUpUserNameInput);
        signUpEmailInput = findViewById(R.id.signUpEmailInput);
        signUpPasswordInput = findViewById(R.id.signUpPasswordInput);
        signUpConfirmPasswordInput = findViewById(R.id.signUpConfirmPasswordInput);
        signUpButton = findViewById(R.id.signUpButton);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        signUpScrollView.setVerticalScrollBarEnabled(false);
    }

    // validate the input
    private void inputValidation(){
        // Username input
        signUpUserNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (signUpUserNameInput.getText().toString().isEmpty()) {
                    signUpUserNameInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    signUpUserNameInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Email input
        signUpEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (signUpEmailInput.getText().toString().isEmpty()) {
                    signUpEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    signUpEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Password input
        signUpPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (signUpPasswordInput.getText().toString().isEmpty()) {
                    signUpPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    signUpPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Confirm Password input
        signUpConfirmPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (signUpConfirmPasswordInput.getText().toString().isEmpty()) {
                    signUpConfirmPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    signUpConfirmPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // handle sign in text click
    private void handleSignInTextClick() {
        signUpSignInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle sign up button click
    private void handleSignUpButtonClick(){
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = signUpUserNameInput.getText().toString().trim();
                String txtEmail = signUpEmailInput.getText().toString().trim();
                String txtPassword = signUpPasswordInput.getText().toString().trim();
                String txtConfirmPassword = signUpConfirmPasswordInput.getText().toString().trim();
                boolean isValid = true;

                if(txtUsername.isEmpty()){
                    signUpUserNameInput.setError("Please provide your username");
                    signUpUserNameInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if(txtEmail.isEmpty()){
                    signUpEmailInput.setError("Please provide your email");
                    signUpEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if(txtPassword.isEmpty()){
                    signUpPasswordInput.setError("Please provide your password");
                    signUpPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if(txtConfirmPassword.isEmpty()){
                    signUpConfirmPasswordInput.setError("Please provide confirm password");
                    signUpConfirmPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if(!txtPassword.equals(txtConfirmPassword)){
                    signUpConfirmPasswordInput.setError("Password not match");
                    signUpConfirmPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if (isValid) {
                    progress();
                    mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = task.getResult().getUser();

                                        if (user != null) {
                                            String userID = user.getUid();
                                            DocumentReference documentReference = firestore.collection("Users").document(userID);

                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("Username", txtUsername);
                                            userMap.put("Email", txtEmail);
                                            userMap.put("UserId", userID);

                                            documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    startActivity(new Intent(SignupActivity.this, Language_Activity.class));
                                                    CustomToast.showToast(SignupActivity.this, "Account created");
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    CustomToast.showToast(SignupActivity.this, "Failed to save user data");
                                                }
                                            });
                                        }
                                    } else {
                                        CustomToast.showToast(SignupActivity.this, "Signup failed");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signUpButton.setVisibility(View.VISIBLE);
                                    signUpProgressBar.setVisibility(View.GONE);
                                    CustomToast.showToast(SignupActivity.this, "Signup failed");
                                }
                            });
                }

            }
        });
    }

    // handle progress bar
    public void progress(){
        if (signUpButton.isPressed()){
            signUpButton.setVisibility(View.GONE);
            signUpProgressBar.setVisibility(View.VISIBLE);
        }else {
            signUpButton.setVisibility(View.VISIBLE);
            signUpProgressBar.setVisibility(View.GONE);
        }
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }

}