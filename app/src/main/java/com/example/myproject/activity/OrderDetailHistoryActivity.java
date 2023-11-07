package com.example.myproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myproject.R;
import com.example.myproject.adapter.OrderDetailsHistoryAdapter;
import com.example.myproject.adapter.OrderedHistoryAdapter;
import com.example.myproject.models.OrderDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailHistoryActivity extends AppCompatActivity implements OrderDetailsHistoryAdapter.IOdHistoryListener {

    RecyclerView rv_ordersDetailsHistory;
    OrderDetailsHistoryAdapter orderDetailsHistoryAdapter;

    List<OrderDetail> orderDetailList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_history);
        initView();

        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");

        DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance().getReference("orderDetails");
        orderDetailsRef.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderDetailList = new ArrayList<>(); // Khởi tạo danh sách trước khi thêm dữ liệu

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderDetail orderDetail = snapshot.getValue(OrderDetail.class);
                        orderDetailList.add(orderDetail);
                    }
                    // Sau khi thêm dữ liệu vào danh sách, gán danh sách cho Adapter và cập nhật RecyclerView
                    orderDetailsHistoryAdapter.setOrdersList(orderDetailList);
                    rv_ordersDetailsHistory.setAdapter(orderDetailsHistoryAdapter);
                } else {
                    Log.d("null", "null ne");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailHistoryActivity.this);
        rv_ordersDetailsHistory.setLayoutManager(linearLayoutManager);


        orderDetailsHistoryAdapter.setOrdersList(orderDetailList);
        orderDetailsHistoryAdapter.setOdHistoryListener(this); // Setting the listener
        rv_ordersDetailsHistory.setAdapter(orderDetailsHistoryAdapter);


    }

    private void initView() {
        rv_ordersDetailsHistory = findViewById(R.id.rv_ordereddetails_history);
        orderDetailsHistoryAdapter = new OrderDetailsHistoryAdapter(this);
        orderDetailList = new ArrayList<>();

    }

    @Override
    public void RepurchaseClicked(View view, int position) {

    }
}