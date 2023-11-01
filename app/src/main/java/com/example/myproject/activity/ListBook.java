package com.example.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.adapter.BookAdapter;
import com.example.myproject.models.Book;
import com.example.myproject.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListBook extends BaseActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView mRecyclerBook;
    private BookAdapter mBookAdapter;
    private List<Book> productList;
    private TextView tvName, tvEmail;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        tvEmail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);
        tvName = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);

        showUserInformation();

        mRecyclerBook = findViewById(R.id.rcv_product);
        mBookAdapter = new BookAdapter(this);
        //  setupToolbar();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerBook.setLayoutManager(layoutManager);
        mBookAdapter.setData(new ArrayList<>()); // Khởi tạo Adapter với danh sách ban đầu rỗng
        mRecyclerBook.setAdapter(mBookAdapter);
        getListProductFromFirebase();
        searchBook();
        setToolbar();
    }
    public void searchBook() {
        SearchView search = findViewById(R.id.searchView);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = search.getQuery().toString();
                if (query == "") {
                    getListProductFromFirebase();
                } else {
                    List<Book> searchResults = performSearch(query);
                    mBookAdapter.setData(searchResults);
                    mBookAdapter.notifyDataSetChanged();
                }

            }
        });
    }
    public void setToolbar() {
        ImageView cartIcon = findViewById(R.id.cart_icon);
        ImageView chatIcon = findViewById(R.id.chat_icon);
        ImageView infoIcon = findViewById(R.id.infor_icon);
        TextView cartCount = findViewById(R.id.cart_count);
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(user_current.getUid());
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
                Intent intent = new Intent(ListBook.this, CartActivity.class);
                startActivity(intent);
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListBook.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListBook.this, ContactActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Người dùng chưa đăng nhập, không cần hiển thị thông tin.
            tvName.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
        } else {
            String name = user.getDisplayName();
            String email = user.getEmail();

            if (name != null && !name.isEmpty()) {
                tvName.setText(name);
            } else {
                tvName.setText("Không có tên người dùng");
            }

            if (email != null && !email.isEmpty()) {
                tvEmail.setText(email);
            } else {
                tvEmail.setText("Không có địa chỉ email");
            }
        }
    }
    private List<Book> performSearch(String query) {
        List<Book> searchResults = new ArrayList<>();
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("books");

        Query searchQuery = databaseReference.orderByChild("bookTitle") // Thay "title" bằng trường bạn muốn tìm kiếm
                .startAt(query) // Bắt đầu từ query
                .endAt(query + "\uf8ff"); // Kết thúc bằng query với ký tự \uf8ff (đảm bảo khớp đầy đủ)

        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    searchResults.add(book);
                }

                // Cập nhật RecyclerView với kết quả tìm kiếm
                mBookAdapter.setData(searchResults);
                mBookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                Toast.makeText(ListBook.this, "Lỗi khi tải dữ liệu từ Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return searchResults;
    }


    private void getListProductFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("books");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Book product = productSnapshot.getValue(Book.class);
                    productList.add(product);
                }
                mBookAdapter.setData(productList);
                mBookAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                Toast.makeText(ListBook.this, "Lỗi khi tải dữ liệu từ Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_password) {

        } else if (id == R.id.nav_signout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ListBook.this, LoginActivity.class);

            startActivity(intent);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}