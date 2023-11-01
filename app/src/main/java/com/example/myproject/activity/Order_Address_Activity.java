package com.example.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myproject.R;
import com.example.myproject.models.Cart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Order_Address_Activity extends AppCompatActivity {
     private Button submitInfor;
     private EditText receiverName;
     private EditText receiverPhone;
     private EditText receiverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_order);

        receiverName = findViewById(R.id.txt_Name_Receiver);
        receiverPhone = findViewById(R.id.txt_phone_Receiver);
        receiverAddress = findViewById(R.id.txt_address_Receiver);
        submitInfor = findViewById(R.id.btn_submit);
        Intent intent = getIntent();
        String totalPrice = intent.getStringExtra("TOTAL_PRICE");
        String choose_Book = intent.getStringExtra("chooseBook");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Cart>>() {}.getType(); // Định nghĩa kiểu dữ liệu mục tiêu
        ArrayList<Cart> selectedCart = gson.fromJson(choose_Book, type);


        submitInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = receiverName.getText().toString();
                String phone = receiverPhone.getText().toString();
                String address = receiverAddress.getText().toString();
                Intent intent = new Intent(Order_Address_Activity.this, Last_Payment_Activity.class);
                intent.putExtra("RECEIVER_NAME", name);
                intent.putExtra("RECEIVER_PHONE", phone);
                intent.putExtra("RECEIVER_ADDRESS", address);
                intent.putExtra("TOTAL_PRICE_PAYMENT",totalPrice );

                ArrayList<Cart> selectedCarts = new ArrayList<>(selectedCart);
                Gson gson = new Gson();
                String jsonCarts = gson.toJson(selectedCarts);
                intent.putExtra("listCart", jsonCarts);
                startActivity(intent);
            }
        });
    }
}
