package com.example.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://myproject-41936-default-rtdb.firebaseio.com");

    private EditText fullName, email, password, phone;
    private Button registerBtn;
    private TextView goToLogin;
    boolean valid = true;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi();
        initListener();

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initUi() {
        fullName = findViewById(R.id.edt_fullName);
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);
        phone = findViewById(R.id.edt_phone);

        registerBtn = findViewById(R.id.btn_sign_up);
        goToLogin = findViewById(R.id.btn_go_to_Login);
    }

    private void initListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();

            }
        });
    }

    private void onClickSignUp() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String fullNameTxt = fullName.getText().toString();
        String emailTxt = email.getText().toString();
        String passwordTxt = password.getText().toString();
        String phoneTxt = phone.getText().toString();

        if (fullNameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty() || passwordTxt.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Can not be blacked", Toast.LENGTH_LONG).show();
        } else if (phoneTxt.length() < 10 || !phoneTxt.matches("[0-9]+")) {
            Toast.makeText(RegisterActivity.this, "Phone must be number", Toast.LENGTH_LONG).show();
        } else if (passwordTxt.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password must be morethan 6 characters", Toast.LENGTH_LONG).show();
        } else {
            auth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                databaseReference.child("user").child(FirebaseAuth.getInstance().getUid()).child("fullname").setValue(fullNameTxt);
                                databaseReference.child("user").child(FirebaseAuth.getInstance().getUid()).child("email").setValue(emailTxt);
                                databaseReference.child("user").child(FirebaseAuth.getInstance().getUid()).child("password").setValue(passwordTxt);
                                databaseReference.child("user").child(FirebaseAuth.getInstance().getUid()).child("phone").setValue(phoneTxt);

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Email is already registerd",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(RegisterActivity.this, "Email is already registerd",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }


    }
}