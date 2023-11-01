package com.example.myproject.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity {

    protected void setupToolbar() {
       // Toolbar toolbar = findViewById(R.id.toolbar);

        ImageView cartIcon = findViewById(R.id.cart_icon);
        ImageView chatIcon = findViewById(R.id.chat_icon);
        ImageView infoIcon = findViewById(R.id.infor_icon);
        ImageView backImageView = findViewById(R.id.back_button);
        TextView cartCount = findViewById(R.id.cart_count);
        String userId = "1"; // Thay thế bằng ID của người dùng cần đếm số lượng phần tử trong "carts/userId"
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
                Intent intent = new Intent(BaseActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, ContactActivity.class);
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
