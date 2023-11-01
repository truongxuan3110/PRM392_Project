package com.example.myproject.activity;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactActivity extends AppCompatActivity {

    private ImageView mMap;
    private Button btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mMap = findViewById(R.id.mMap);
        setupToolbar();
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        btnCall = findViewById(R.id.callPhone);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0974414699"));
                if (ActivityCompat.checkSelfPermission(ContactActivity.this,CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactActivity.this, new String[]{CALL_PHONE}, 1);
                    return;
                }
                startActivity(intent);
            }
        });
    }
    public void setupToolbar(){
        FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();
        ImageView cartIcon = findViewById(R.id.cart_icon);
        ImageView chatIcon = findViewById(R.id.chat_icon);
        ImageView backImageView = findViewById(R.id.back_button);
        TextView cartCount = findViewById(R.id.cart_count);
        String userId = user_current.getUid(); // Thay thế bằng ID của người dùng cần đếm số lượng phần tử trong "carts/userId"
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long itemCount = dataSnapshot.getChildrenCount();

                    if (itemCount >= 0) {
                        cartIcon.setVisibility(View.VISIBLE);
                        cartCount.setText(String.valueOf(itemCount));
                    } else {
                        cartIcon.setVisibility(View.GONE);
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    }
