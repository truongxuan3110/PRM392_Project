package com.example.myproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private LinearLayout layoutSignup;
    private EditText edtMail, edtPassword;
    private Button btnSignin;

    private TextView tvForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getSharedPreferences("authen", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        String authToken = sharedPreferences.getString("email", "");

       Log.d("UserId", userId); // In giá trị userId vào Logcat
      Log.d("AuthToken", authToken); // In giá trị authToken vào Logcat
        if (userId != "" && authToken != "") {
            Intent intent = new Intent(LoginActivity.this, ListBook.class);

            startActivity(intent);
        } else {
            initUi();
            initListener();
        }


    }

    private void initUi() {
        layoutSignup = findViewById(R.id.layout_sign_up);
        edtMail = findViewById(R.id.edt_email_login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnSignin = findViewById(R.id.btn_sign_in_login);
        tvForgot=findViewById(R.id.tv_forgotpassword);
    }

    private void initListener() {
        layoutSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);


            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignIn();
            }
        });

        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickSignIn() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String strEmail = edtMail.getText().toString().trim();
        String strPass = edtPassword.getText().toString().trim();


        if (strEmail.isEmpty() || strPass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Can not be blacked", Toast.LENGTH_LONG).show();
        } else if ( !strEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            Toast.makeText(LoginActivity.this, "Email wrong format", Toast.LENGTH_LONG).show();
        } else if (strPass.length() < 6) {
            Toast.makeText(LoginActivity.this, "Password must be more than 6 characters", Toast.LENGTH_LONG).show();
        }else {
            auth.signInWithEmailAndPassword(strEmail, strPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();
                                if (user_current != null) {
                                    // Lưu thông tin người dùng vào SharedPreferences
                                    SharedPreferences prefs = getSharedPreferences("authen", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("userId", user_current.getUid());
                                    editor.putString("email", user_current.getEmail());
                                    editor.apply();
                                    // Log.d("UserId", strEmail); // In giá trị userId vào Logcat
                                    //  Log.d("AuthToken", strPass); // In giá trị authToken vào Logcat
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(LoginActivity.this, ListBook.class);
                                    startActivity(intent);
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Check your email or password ",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }


}