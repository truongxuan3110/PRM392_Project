package com.example.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.adapter.LastPaymentAdapter;
import com.example.myproject.adapter.PaymentAdapter;
import com.example.myproject.models.Book;
import com.example.myproject.models.Cart;
import com.example.myproject.models.OrderDetail;
import com.example.myproject.models.Orders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Last_Payment_Activity extends BaseActivity {
    private RecyclerView mRecyclerProduct;
   // private PaymentAdapter mPaymentAdapter;
    private LastPaymentAdapter mlastPaymentAdapter;
    double totalValue = 0.0;
    private Spinner paymentMethodSpinner;
    private String paymentMethod;
    private TextView totalprice;
    //ImageView btnnext;
    private Button btn_payment;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_payment);


        mRecyclerProduct = findViewById(R.id.rcv_product_last_payment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerProduct.setLayoutManager(linearLayoutManager);
        mlastPaymentAdapter = new LastPaymentAdapter(this);
        mlastPaymentAdapter.setData(new ArrayList<>()); // Khởi tạo Adapter với danh sách ban đầu rỗng

        mRecyclerProduct.setAdapter(mlastPaymentAdapter);

        setupToolbar();
        // set name toolbar
        TextView nameScreen = findViewById(R.id.nameScreen);
        nameScreen.setText("Thanh toán");
         // set up Spinner
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        List<String> paymentOptions = new ArrayList<>();
        paymentOptions.add("Thẻ tín dụng");
        paymentOptions.add("Thanh toán qua thẻ ngân hàng");
        paymentOptions.add("Thanh toán khi nhận hàng");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);
        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lưu tùy chọn được chọn vào biến paymentMethod
                paymentMethod = paymentOptions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Xử lý khi không có tùy chọn nào được chọn (nếu cần)
            }
        });
        Intent intent = getIntent();
        // thong tin nguoi nhan
        String totalPrice = intent.getStringExtra("TOTAL_PRICE_PAYMENT");
        String receiver_name = intent.getStringExtra("RECEIVER_NAME");
        String receiver_phone = intent.getStringExtra("RECEIVER_PHONE");
        String receiver_address = intent.getStringExtra("RECEIVER_ADDRESS");
        String receiver_note = intent.getStringExtra("RECEIVER_NOTE");

        String choose_Book = intent.getStringExtra("listCart");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Cart>>() {}.getType(); // Định nghĩa kiểu dữ liệu mục tiêu
        ArrayList<Cart> selected = gson.fromJson(choose_Book, type);
        mlastPaymentAdapter.setData(selected);
        mlastPaymentAdapter.notifyDataSetChanged();

        totalprice = findViewById(R.id.totalprice);
        totalprice.setText(String.valueOf(totalPrice));
        TextView txt_rv_name = findViewById(R.id.txt_User_name);
        TextView txt_rv_phone = findViewById(R.id.txt_phone);
        TextView txt_rv_address = findViewById(R.id.txt_address);
        ImageView img_book = findViewById(R.id.item_payment_image);

        txt_rv_name.setText("Tên người nhận: " + String.valueOf(receiver_name));
        txt_rv_phone.setText("Số điện thoại:" + String.valueOf(receiver_phone));
        txt_rv_address.setText("Địa chỉ nhận hàng:" + String.valueOf(receiver_address));
        btn_payment = findViewById(R.id.btn_payment);
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentMethod != null) {
                    if (paymentMethod.equals("Thanh toán khi nhận hàng")) {
                   Button btnpayment = findViewById(R.id.btn_payment);
                        btnpayment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");
                                Date orderTime = new Date();
                                float orderTotalCost = Float.parseFloat(totalPrice);
                                DatabaseReference newOrderRef = databaseReference.push();
                                String orderID = newOrderRef.getKey(); // Lấy ID đơn đặt hàng tự động
                                Orders order = new Orders(
                                        user_current.getUid(), // userId - thay bằng ID người dùng thực tế
                                        receiver_phone,
                                        receiver_address,
                                        orderTime,
                                        receiver_note, // Thêm thông tin note ở đây
                                        orderTotalCost, // Chuyển đổi totalPrice sang kiểu float
                                        "Pending", // Trạng thái đơn hàng mặc định
                                        "Thanh toán khi nhận hàng"
                                );
                                newOrderRef.setValue(order);
                                DatabaseReference orderDetailsRef = FirebaseDatabase.getInstance().getReference("orderDetails");
                                for (Cart orderDetail : selected) {
                                    OrderDetail orderDetailData = new OrderDetail(
                                            orderID,
                                            orderDetail.getBook().getBookId(), // bookId
                                            orderDetail.getQuantity(), // quantity
                                            orderDetail.getQuantity() * orderDetail.getBook().getPrice() // total
                                    );

                                    DatabaseReference newOrderDetailRef = orderDetailsRef.push();
                                    newOrderDetailRef.setValue(orderDetailData);
                                }
                                Toast.makeText(Last_Payment_Activity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Last_Payment_Activity.this, WaitOrderActivity.class);
                                startActivity(intent);
                            }
                        });
                    } else if (paymentMethod.equals("Thanh toán qua thẻ ngân hàng")) {
                        Intent intent = new Intent(Last_Payment_Activity.this, PaymentWithCreditCardActivity.class);
                        startActivity(intent);
                    }
                } else {
                    // Xử lý khi không có tùy chọn nào được chọn
                }

            }
        });
    }

}