package com.example.myproject.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
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

public class ListBook extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerBook;
    private BookAdapter mBookAdapter;
    private List<Book> productList;
    private TextView tvName, tvEmail;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();
    private Handler networkCheckHandler = new Handler();
    private int networkCheckInterval = 30000; // Đợi 30 giây trước khi kiểm tra lại kết nối

    private Runnable networkCheckRunnable = new Runnable() {
        @Override
        public void run() {
            // Kiểm tra kết nối mạng
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (capabilities != null) {
                // Nếu có kết nối mạng
                SharedPreferences sharedPreferences = getSharedPreferences("authen", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");
                String authToken = sharedPreferences.getString("email", "");

                if (!userId.isEmpty() && !authToken.isEmpty()) {
                    // Nếu có kết nối mạng và đã đăng nhập, tải dữ liệu
                    getListProductFromFirebase();
                    searchBook();
                    setToolbar();
                }
            } else {
                Log.d("AuthToken", "Không có kết nối mạng"); // In giá trị authToken vào Logcat
                // Không có kết nối mạng
                AlertDialog.Builder builder = new AlertDialog.Builder(ListBook.this);
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn cần kết nối mạng để sử dụng ứng dụng");
                builder.setPositiveButton("Đóng", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            // Lập lịch kiểm tra lại kết nối sau khoảng thời gian
            networkCheckHandler.postDelayed(this, networkCheckInterval);
        }
    };

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
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerBook.setLayoutManager(layoutManager);
        mBookAdapter.setData(new ArrayList<>()); // Khởi tạo Adapter với danh sách ban đầu rỗng
        mRecyclerBook.setAdapter(mBookAdapter);
        networkCheckHandler.post(networkCheckRunnable);

// Tạo một Runnable để kiểm tra lại kết nối mạng

//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
//            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//
//            if (capabilities != null) {
//               // if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                    SharedPreferences sharedPreferences = getSharedPreferences("authen", MODE_PRIVATE);
//                    String userId = sharedPreferences.getString("userId", "");
//                    String authToken = sharedPreferences.getString("email", "");
//                    if (userId != "" && authToken != "") {
//                        // Nếu có kết nối mạng và đã đăng nhập, bắt đầu kiểm tra lại kết nối mạng theo định kỳ
//                      //  isConnected = true;
//                        // Nếu có kết nối mạng, tải dữ liệu
//                        getListProductFromFirebase();
//                        searchBook();
//                        setToolbar();
//                    }
//               // }
//            }else {
//                    Log.d("AuthToken","ko co ket noi mang"); // In giá trị authToken vào Logcat
//                    // Không có kết nối mạng
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("Thông báo");
//                    builder.setMessage("Bạn cần kết nối mạng để sử dụng ứng dụng");
//                    builder.setPositiveButton("Đóng", null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
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
                return;
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

                if (user_current.getEmail().equals(Utils.EMAIL_AD)) {
                    Intent intent = new Intent(ListBook.this, UserChatActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ListBook.this, ChatActivity.class);
                    startActivity(intent);
                }
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
                return;
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
                return;
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

        if (id == R.id.aview_ordered) {
            Intent intent = new Intent(ListBook.this, OrderedHistoryActivity.class);
            startActivity(intent);
        }
           else if (id == R.id.nav_signout) {
                SharedPreferences prefs = getSharedPreferences("authen", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("userId");
                editor.remove("email");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ListBook.this, LoginActivity.class);
                startActivity(intent);
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
}