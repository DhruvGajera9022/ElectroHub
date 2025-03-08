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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextView signInSignUpTxt, signInForgotPassword;
    private ScrollView signInScrollView;
    private EditText signInEmailInput, signInPasswordInput;
    private MaterialButton signInButton, loginGoogleBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar signInProgressBar;

    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;

    private GoogleSignInClient mGoogleSignInClient;
    private String userID;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialization();
        inputValidation();
        handleForgotPasswordClick();
        handleSignUnTextClick();
        handleGoogleButton();
        handleSignInButtonClick();
        handlePasswordToggle();

        setStatusBarColor(R.color.home);
    }

    // All the id initialize
    private void initialization(){
        signInSignUpTxt = findViewById(R.id.signInSignUpTxt);
        signInForgotPassword = findViewById(R.id.signInForgotPassword);
        signInScrollView = findViewById(R.id.signInScrollView);
        signInEmailInput = findViewById(R.id.signInEmailInput);
        signInPasswordInput = findViewById(R.id.signInPasswordInput);
        signInButton = findViewById(R.id.signInButton);
        loginGoogleBtn = findViewById(R.id.loginGoogleBtn);
        signInProgressBar = findViewById(R.id.signInProgressBar);
        passwordToggle = findViewById(R.id.passwordToggle);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        signInScrollView.setVerticalScrollBarEnabled(false);

    }

    // validate the input
    private void inputValidation(){
        // Email input
        signInEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = signInEmailInput.getText().toString().trim();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signInEmailInput.setError("Invalid email format");
                    signInEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    signInEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Password input
        signInPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (signInPasswordInput.getText().toString().isEmpty()) {
                    signInPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    signInPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // handle forgot password text click
    private void handleForgotPasswordClick(){
        signInForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle sign un text click
    private void handleSignUnTextClick() {
        signInSignUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle sign in button click
    private void handleSignInButtonClick(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = signInEmailInput.getText().toString().trim();
                String txtPassword = signInPasswordInput.getText().toString().trim();
                boolean isValid = true;

                if(txtEmail.isEmpty()){
                    signInEmailInput.setError("Please provide your email");
                    signInEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }
                if(txtPassword.isEmpty()){
                    signInPasswordInput.setError("Please provide your password");
                    signInPasswordInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if (isValid){
                    progress();

                    CollectionReference usersRef = firestore.collection("Users");

                    usersRef.whereEqualTo("email", txtEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        // Email found, proceed with authentication
                                        mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        Intent intent = new Intent(LoginActivity.this, Language_Activity.class);
                                                        CustomToast.showToast(LoginActivity.this, "Login successful");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        signInButton.setVisibility(View.VISIBLE);
                                                        signInProgressBar.setVisibility(View.GONE);
                                                        CustomToast.showToast(LoginActivity.this, "Password incorrect");
                                                    }
                                                });
                                    } else {
                                        // Email not found, redirect to sign-up page
                                        CustomToast.showToast(LoginActivity.this, "User not found. Redirecting to sign-up...");
                                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                        intent.putExtra("email", txtEmail);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

    // handle google button click
    private void handleGoogleButton(){
        loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                  .requestIdToken(getString(R.string.default_web_client_id))
                                                  .requestEmail()
                                                  .build();

                // Initialize Google SignIn Client
                mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

                // Start the sign-in intent
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    // handle progress bar
    public void progress(){
        if (signInButton.isPressed()){
            signInButton.setVisibility(View.GONE);
            signInProgressBar.setVisibility(View.VISIBLE);
        }else {
            signInButton.setVisibility(View.VISIBLE);
            signInProgressBar.setVisibility(View.GONE);
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
                                Toast.makeText(LoginActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, Language_Activity.class);
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
                    signInPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.icon_eye_closed);
                } else {
                    // Show Password
                    signInPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordToggle.setImageResource(R.drawable.ic_eye_open);
                }
                isPasswordVisible = !isPasswordVisible;
                signInPasswordInput.setSelection(signInPasswordInput.getText().length());
            }
        });
    }

}