package com.example.myproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;

public class OrderDetailHistoryActivity extends AppCompatActivity   implements OrderDetailsHistoryAdapter.IODHistoryListener {

    Button repurchase_btn;
    RecyclerView rv_ordersDetailsHistory;
    OrderDetailsHistoryAdapter orderDetailsHistoryAdapter;

    List<OrderDetail> orderDetailList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_history);
        context = this;
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

        rv_ordersDetailsHistory.setAdapter(orderDetailsHistoryAdapter);


    }

    private void initView() {
        rv_ordersDetailsHistory = findViewById(R.id.rv_ordereddetails_history);
        orderDetailsHistoryAdapter = new OrderDetailsHistoryAdapter(this);
        orderDetailList = new ArrayList<>();
        repurchase_btn = findViewById(R.id.odhistory_reorder_btn);
        orderDetailsHistoryAdapter.setIodHistoryListener(this);
    }

    private Context context;

    @Override
    public void Repurchase(int bookId) {

        Log.i("cucuc ", bookId + "");
      int NOTIFICATION_ID = 2;
        if (user.getUid() != null ) {
            // if (userIsLoggedIn) {
            // Thêm sách vào giỏ hàng trên Firebase
            addToCart(bookId, user.getUid());
            Toast.makeText(OrderDetailHistoryActivity.this, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
            sendNotification(NOTIFICATION_ID);

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Yêu cầu đăng nhập");
            builder.setMessage("Bạn cần đăng nhập để tiếp tục. Bạn có muốn đăng nhập không?");

            // Xác nhận đăng nhập
            builder.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Người dùng đã chọn hủy bỏ
                    // Có thể thực hiện các hành động khác tùy ý ở đây
                }
            });
        }

    }



    private void addToCart(int bookId, String userId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);
        // Kiểm tra xem cuốn sách đã tồn tại trong giỏ hàng chưa
        cartRef.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Cuốn sách đã tồn tại trong giỏ hàng
                    for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                        // Cập nhật số lượng của cuốn sách
                        int currentQuantity = cartItemSnapshot.child("quantity").getValue(Integer.class);
                        cartItemSnapshot.getRef().child("quantity").setValue(currentQuantity + 1);
                    }
                } else {
                    // Cuốn sách chưa tồn tại trong giỏ hàng, thêm mới
                    int cartItemId = bookId;
                    HashMap<String, Object> cartItemData = new HashMap<>();
                    cartItemData.put("bookId", bookId);
                    cartItemData.put("userId", userId);
                    cartItemData.put("quantity", 1);
                    cartRef.child(String.valueOf(cartItemId)).setValue(cartItemData);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
    private void sendNotification(int NOTIFICATION_ID) {
        //   Toast.makeText(mContext, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, CartActivity.class);
        //  CartManager.addToCart(mContext, product); đoạn này add vào firebase
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.baseline_add_shopping_cart_24);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(R.drawable.mail)
                .setContentTitle("Bạn có thông báo từ giỏ hàng!")
//                .setContentText(productName + " đã được thêm vào giỏ hàng")
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }


}