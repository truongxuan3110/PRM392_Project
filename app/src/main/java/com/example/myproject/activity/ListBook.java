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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.adapter.BookAdapter;
import com.example.myproject.models.Book;
import com.example.myproject.utils.Utils;
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

public class ListBook extends AppCompatActivity {

    private RecyclerView mRecyclerBook;
    private BookAdapter mBookAdapter;
    private List<Book> productList;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_book);
        mRecyclerBook = findViewById(R.id.rcv_product);
        mBookAdapter = new BookAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerBook.setLayoutManager(layoutManager);
        mBookAdapter.setData(new ArrayList<>()); // Khởi tạo Adapter với danh sách ban đầu rỗng
        mRecyclerBook.setAdapter(mBookAdapter);
        getListProductFromFirebase();
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
        ImageView carticon, chaticon, infoicon;
        carticon = findViewById(R.id.cart_icon);
        chaticon = findViewById(R.id.chat_icon);
        infoicon = findViewById(R.id.infor_icon);
        infoicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListBook.this, ContactActivity.class);
                startActivity(intent);
            }
        });
        carticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Log.d("dđ","d");
                Intent intent = new Intent(ListBook.this, CartActivity.class);
                startActivity(intent);
            }
        });
        chaticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_current.getEmail().equals(Utils.EMAIL_AD)){
                    Intent intent = new Intent(ListBook.this, UserChatActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(ListBook.this, ChatActivity.class);
                    startActivity(intent);
                }
            }
        });


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
                // Cập nhật danh sách sản phẩm trong Adapter và hiển thị lên RecyclerView
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.ic_cart) {
//            Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (itemId == R.id.ic_search) {
//            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
//            return true;
//        } else
//            return super.onOptionsItemSelected(item);
//    }
}