package com.example.myproject.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.Interface.TotalPriceListener;
import com.example.myproject.R;
import com.example.myproject.adapter.CartAdapter;
import com.example.myproject.models.Book;
import com.example.myproject.models.Cart;
import com.example.myproject.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends BaseActivity {
    private RecyclerView mRecyclerProduct;
    private CartAdapter mCartAdapter;
   // private List<Cart> mCartProducts;
   TextView totalpriceTextView ;
    private double totalPrice; // Biến để lưu giá trị tổng giá

   // Bắt sự kiện thanh toán
   Button btn_Buy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_cart);
        setupToolbar();
        totalpriceTextView = findViewById(R.id.totalprice);

        mRecyclerProduct = findViewById(R.id.rcv_product_cart);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerProduct.setLayoutManager(linearLayoutManager);
        mCartAdapter = new CartAdapter(this, new ArrayList<>(), new TotalPriceListener() {
            @Override
            public void onTotalPriceUpdated(double totalPrice) {
                CartActivity.this.totalPrice = totalPrice;
                totalpriceTextView.setText(String.valueOf(totalPrice));
            }
        });
        getCartProducts("1"); // fix cứng userID
        // Sự kiện thanh toán
        btn_Buy = findViewById(R.id.btn_buy);
        btn_Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Cart> selectedProducts = new ArrayList<>();

                for (int i = 0; i < mCartAdapter.getItemCount(); i++) {
                    if (mCartAdapter.isProductSelected(i)) {
                        selectedProducts.add(mCartAdapter.mListCart.get(i));
                    }
                }
                if (!selectedProducts.isEmpty()) {
                    Intent paymentIntent = new Intent(CartActivity.this, PaymentActivity.class);
                    ArrayList<Cart> selectedCarts = new ArrayList<>(selectedProducts);
                    Gson gson = new Gson();
                    String jsonCarts = gson.toJson(selectedCarts);
                    paymentIntent.putExtra("selectedCarts", jsonCarts);
                    startActivity(paymentIntent);
                } else {
                    Toast.makeText(CartActivity.this, "Bạn chưa chọn sản phẩm nào", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getCartProducts(String userID) {
        List<Cart> cartProducts = new ArrayList<>();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userID);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cartProducts.clear(); // Xóa danh sách cũ trước khi cập nhật

                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    int bookId = cartSnapshot.child("bookId").getValue(Integer.class);
                    int quantity = cartSnapshot.child("quantity").getValue(Integer.class);
                    getBookInfoFromFirebase(bookId, new FirebaseCallback() {
                        @Override
                        public void onCallback(Book book) {
                            if (book != null) {
                                Cart cartItem = new Cart("1", quantity, book);
                                cartProducts.add(cartItem);
                                mCartAdapter.setData(cartProducts);
                                mRecyclerProduct.setAdapter(mCartAdapter);
                            }
                        }
                    });
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(Book book);
    }

    private void getBookInfoFromFirebase(int bookId, FirebaseCallback callback) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("books").child(String.valueOf(bookId));

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                callback.onCallback(book);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
