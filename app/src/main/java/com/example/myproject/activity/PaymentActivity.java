package com.example.myproject.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.adapter.PaymentAdapter;
import com.example.myproject.models.Cart;
import com.example.myproject.models.OrderDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PaymentActivity  extends AppCompatActivity {
    private RecyclerView mRecyclerProduct;
    private PaymentAdapter mPaymentAdapter;
    double totalValue = 0.0;
    private Spinner paymentMethodSpinner;
    private String paymentMethod;
    //ImageView btnnext;
    private Button btn_payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);


        mRecyclerProduct = findViewById(R.id.rcv_product_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerProduct.setLayoutManager(linearLayoutManager);
        mPaymentAdapter = new PaymentAdapter(this);
        mRecyclerProduct.setAdapter(mPaymentAdapter);
        Intent intent = getIntent();
        String jsonCarts = intent.getStringExtra("selectedCarts");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Cart>>() {}.getType(); // Định nghĩa kiểu dữ liệu mục tiêu
        ArrayList<Cart> selectedCarts = gson.fromJson(jsonCarts, type);

        if (selectedCarts != null) {
            mPaymentAdapter.setData(selectedCarts);
            for (Cart cart : selectedCarts) {
                totalValue += cart.getBook().getPrice() * cart.getQuantity();
            }
            TextView  totalpriceTextView = findViewById(R.id.totalprice);
            totalpriceTextView.setText(String.valueOf(totalValue));
        }
        btn_payment = findViewById(R.id.btn_payment);
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_total = new Intent(PaymentActivity.this , Order_Address_Activity.class);
                intent_total.putExtra("TOTAL_PRICE", String.valueOf(totalValue));
                ArrayList<Cart> cartSelected = new ArrayList<>(selectedCarts);
                Gson gson = new Gson();
                String jsonCart = gson.toJson(cartSelected);
                intent_total.putExtra("chooseBook", jsonCart);
                startActivity(intent_total);
            }
        });
    }
}



