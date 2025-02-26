package com.example.swiftmart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftmart.Account.Language_Activity;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private TextView signUpSignInTxt;
    private ScrollView signUpScrollView;
    private EditText signUpUserNameInput, signUpEmailInput, signUpPasswordInput, signUpConfirmPasswordInput;
    private MaterialButton signUpButton, signupGoogleBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar signUpProgressBar;

    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;

    private GoogleSignInClient mGoogleSignInClient;
    private String userID;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialization();
        inputValidation();
        handleSignInTextClick();
        handleSignUpButtonClick();
        handleGoogleButton();
        handlePasswordToggle();
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
        signupGoogleBtn = findViewById(R.id.signupGoogleBtn);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);
        passwordToggle = findViewById(R.id.passwordToggle);

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
                String email = signUpEmailInput.getText().toString().trim();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signUpEmailInput.setError("Invalid email format");
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

    // handle google button click
    private void handleGoogleButton(){
        signupGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                  .requestIdToken(getString(R.string.default_web_client_id))
                                                  .requestEmail()
                                                  .build();

                // Initialize Google SignIn Client
                mGoogleSignInClient = GoogleSignIn.getClient(SignupActivity.this, gso);

                // Start the sign-in intent
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
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

    // On Activity Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign-In failed
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Get the users data
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get user details
                        String userName = account.getDisplayName();
                        String userEmail = account.getEmail();
                        String userImage = account.getPhotoUrl().toString();

                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("Users").document(userID);

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("Username", userName);
                        userMap.put("Email", userEmail);
                        userMap.put("Image", userImage);
                        userMap.put("UserId", userID);

                        documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                // Sign-in successful
                                Toast.makeText(SignupActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, Language_Activity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        // Sign-in failed
                        Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handlePasswordToggle() {
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide Password
                    signUpPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.icon_eye_closed);
                } else {
                    // Show Password
                    signUpPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;
                signUpPasswordInput.setSelection(signUpPasswordInput.getText().length());
            }
        });
    }


}