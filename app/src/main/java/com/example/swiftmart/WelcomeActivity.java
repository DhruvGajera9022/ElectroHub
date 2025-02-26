package com.example.swiftmart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftmart.Account.Language_Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {
    private MaterialButton welcomeEmailBtn, welcomeGoogleBtn;
    private TextView welcomeCreateAccountTxt;
    private ScrollView welcomeScrollView;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private String userID;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initialization();
        handleEmailButton();
        handleGoogleButton();
        handleCreateAccountTextClick();
        setStatusBarColor(R.color.home);

    }

    // All the id initialize
    private void initialization(){
        welcomeEmailBtn = findViewById(R.id.welcomeEmailBtn);
        welcomeGoogleBtn = findViewById(R.id.welcomeGoogleBtn);
        welcomeCreateAccountTxt = findViewById(R.id.welcomeCreateAccountTxt);
        welcomeScrollView = findViewById(R.id.welcomeScrollView);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

//        if (mAuth.getCurrentUser() != null){
//            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
//            finish();
//        }

        welcomeScrollView.setVerticalScrollBarEnabled(false);
    }

    // handle email button click
    private void handleEmailButton(){
        welcomeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // handle google button click
    private void handleGoogleButton(){
        welcomeGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                // Initialize Google SignIn Client
                mGoogleSignInClient = GoogleSignIn.getClient(WelcomeActivity.this, gso);

                // Start the sign-in intent
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    // handle create account text click
    private void handleCreateAccountTextClick(){
        welcomeCreateAccountTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
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
                                Toast.makeText(WelcomeActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WelcomeActivity.this, Language_Activity.class);
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

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }

}