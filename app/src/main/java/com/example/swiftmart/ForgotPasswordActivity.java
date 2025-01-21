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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ScrollView forgotPasswordScrollView;
    private EditText forgotPasswordEmailInput;
    private MaterialButton forgotPasswordButton;
    private FirebaseAuth mAuth;
    private ProgressBar forgotPasswordProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initialization();
        inputValidation();
        handleForgotPasswordButtonClick();

        setStatusBarColor(R.color.home);

    }

    // All the id initialize
    private void initialization(){
        forgotPasswordEmailInput = findViewById(R.id.forgotPasswordEmailInput);
        forgotPasswordScrollView = findViewById(R.id.forgotPasswordScrollView);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        forgotPasswordProgressBar = findViewById(R.id.forgotPasswordProgressBar);

        mAuth = FirebaseAuth.getInstance();

        forgotPasswordScrollView.setVerticalScrollBarEnabled(false);

    }

    // validate the input
    private void inputValidation(){
        // Email input
        forgotPasswordEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (forgotPasswordEmailInput.getText().toString().isEmpty()) {
                    forgotPasswordEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                } else {
                    forgotPasswordEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_success);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // handle forgot password button
    private void handleForgotPasswordButtonClick(){
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = forgotPasswordEmailInput.getText().toString();
                boolean isValid = true;

                if(txtEmail.isEmpty()){
                    forgotPasswordEmailInput.setError("Please provide your email");
                    forgotPasswordEmailInput.setBackgroundResource(R.drawable.rounded_edit_text_error);
                    isValid = false;
                }

                if (isValid){
                    progress();
                    mAuth.sendPasswordResetEmail(txtEmail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    CustomToast.showToast(ForgotPasswordActivity.this, "Password reset email sent");
                                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    forgotPasswordButton.setVisibility(View.VISIBLE);
                                    forgotPasswordProgressBar.setVisibility(View.GONE);
                                    CustomToast.showToast(ForgotPasswordActivity.this, "Password reset email failed");
                                }
                            });
                }
            }
        });
    }

    // handle progress bar
    public void progress(){
        if (forgotPasswordButton.isPressed()){
            forgotPasswordButton.setVisibility(View.GONE);
            forgotPasswordProgressBar.setVisibility(View.VISIBLE);
        }else {
            forgotPasswordButton.setVisibility(View.VISIBLE);
            forgotPasswordProgressBar.setVisibility(View.GONE);
        }
    }

    private void setStatusBarColor(int colorResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(colorResource));
        }
    }

}