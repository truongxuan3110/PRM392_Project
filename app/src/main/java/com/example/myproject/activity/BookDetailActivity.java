package com.example.myproject.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.models.Cart;
import com.example.myproject.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BookDetailActivity extends BaseActivity {
    private Context mContext;
    private Cart mCart;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        setContentView(R.layout.book_detail);
        mContext = this; // Khởi tạo mContext ở đây
        countCartItems(user_current.getUid());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CHANNEL_1_ID";
            String description = "MyChannelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = getIntent();
        int productId = intent.getIntExtra("PRODUCT_ID", 0);
        String productName = intent.getStringExtra("PRODUCT_NAME");
        String productImageResource = intent.getStringExtra("PRODUCT_IMAGE");
        int productPrice = intent.getIntExtra("PRODUCT_PRICE", 0);
        int quantity = intent.getIntExtra("PRODUCT_QUANTITY", 0);
        String description = intent.getStringExtra("PRODUCT_DESCRIPTION");



        ImageView descriptionImageView = findViewById(R.id.detail_image_view);
        TextView nameTextView = findViewById(R.id.detail_text_view_name);
        TextView priceTextView = findViewById(R.id.detail_text_view_price);
        TextView quanityTextView = findViewById(R.id.detail_text_view_quanity);
        TextView desTextView = findViewById(R.id.detail_text_view_desciption);

        TextView nameScreen = findViewById(R.id.nameScreen);
        nameScreen.setText("Chi tiết sản phẩm");


        // descriptionImageView.setImageResource(productImageResource);
        nameTextView.setText(productName);
        priceTextView.setText(String.valueOf(productPrice) + "VNĐ");
        quanityTextView.setText("Số lượng: " + String.valueOf(quantity));
        desTextView.setText(String.valueOf(description));
        Glide.with(mContext)
                .load(productImageResource)
                .into(descriptionImageView);
        Button addtocart, buynow;
        setupToolbar();
        // mua luôn
        buynow = findViewById(R.id.buy_now);
        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                String userId = prefs.getString("userId", null);
//                String authToken = prefs.getString("authToken", null);
//                Log.d("UserId", userId); // In giá trị userId vào Logcat
//                Log.d("AuthToken", authToken); // In giá trị authToken vào Logcat
                if (user_current.getUid() != null) {
                    Intent intent2 = new Intent(BookDetailActivity.this, CartActivity.class);
                    startActivity(intent2);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Yêu cầu đăng nhập");
                    builder.setMessage("Bạn cần đăng nhập để tiếp tục. Bạn có muốn đăng nhập không?");

                    // Xác nhận đăng nhập
                    builder.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, LoginActivity.class);
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
        });

        // thêm vào giỏ hàng
        addtocart = findViewById(R.id.add_to_cart);
        addtocart.setOnClickListener(new View.OnClickListener() {
            private static final int NOTIFICATION_ID = 1;

            @Override
            public void onClick(View view) {
//                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
//                String userId = prefs.getString("userId", null);
//                String authToken = prefs.getString("authToken", null);
//                Log.d("UserId", userId); // In giá trị userId vào Logcat
//                Log.d("AuthToken", authToken); // In giá trị authToken vào Logcat
                if (user_current.getUid() != null ) {
                    // if (userIsLoggedIn) {
                    // Thêm sách vào giỏ hàng trên Firebase
                    addToCart(productId, user_current.getUid());
                    Toast.makeText(mContext, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
                    sendNotification();

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Yêu cầu đăng nhập");
                    builder.setMessage("Bạn cần đăng nhập để tiếp tục. Bạn có muốn đăng nhập không?");

                    // Xác nhận đăng nhập
                    builder.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, LoginActivity.class);
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

            private void sendNotification() {
                //   Toast.makeText(mContext, "Sản phẩm đã được thêm vào giỏ hàng", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, CartActivity.class);
                //  CartManager.addToCart(mContext, product); đoạn này add vào firebase
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.baseline_add_shopping_cart_24);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "1")
                        .setSmallIcon(R.drawable.mail)
                        .setContentTitle("Bạn có thông báo từ giỏ hàng!")
                        .setContentText(productName + " đã được thêm vào giỏ hàng")
                        .setLargeIcon(bitmap)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
        });
    }


    private void countCartItems(String userId) {
        ImageView cartIcon = findViewById(R.id.cart_icon);
        TextView cartCount = findViewById(R.id.cart_count);
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Duyệt qua tất cả các bản ghi trong giỏ hàng của người dùng
                Set<Integer> bookIds = new HashSet<>();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Integer bookId = itemSnapshot.child("bookId").getValue(Integer.class);
                    bookIds.add(bookId);
                }

                // bookIds là một tập hợp chứa các bookId độc nhất trong giỏ hàng
                int numUniqueBooks = bookIds.size(); // Số loại sách trong giỏ hàng

                // Cập nhật giá trị của cartCount
                updateCartIconCount(numUniqueBooks, cartIcon, cartCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void updateCartIconCount(int count, ImageView cartIcon, TextView cartCount) {
        // Tùy chỉnh biểu tượng giỏ hàng để hiển thị số loại sách
        if (count >= 0) {
            cartIcon.setVisibility(View.VISIBLE);
            cartCount.setText(String.valueOf(count));
        } else {
            cartIcon.setVisibility(View.GONE);
        }
    }

}


