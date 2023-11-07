package com.example.myproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.activity.OrderedHistoryActivity;
import com.example.myproject.models.Book;
import com.example.myproject.models.OrderDetail;
import com.example.myproject.models.Orders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDetailsHistoryAdapter extends RecyclerView.Adapter<OrderDetailsHistoryAdapter.OrderDetailsHistoryViewHolder>{

    private Context context;
    private List<OrderDetail> odHistoryList;

    private IODHistoryListener iodHistoryListener;



    public OrderDetailsHistoryAdapter(Context context) {
        this.context = context;
    }

    public void setOrdersList(List<OrderDetail> ordersList) {
        this.odHistoryList = ordersList;
        notifyDataSetChanged();
    }

    public void setIodHistoryListener(IODHistoryListener iodHistoryListener) {
        this.iodHistoryListener = iodHistoryListener;
    }

    @NonNull
    @Override
    public OrderDetailsHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.odhistory_item, parent, false);
        return new OrderDetailsHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsHistoryViewHolder holder, int position) {
        if (odHistoryList == null || odHistoryList.isEmpty()) {
            return;
        }
        OrderDetail od = odHistoryList.get(position);
        if (od == null) return;
        int bookId = od.getBookID();
        getBookByBookID(holder, bookId, od);
    }


    private void getBookByBookID(@NonNull OrderDetailsHistoryViewHolder holder, int bookId, OrderDetail od) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("books");

        databaseReference.orderByChild("bookId").equalTo(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);
                        if (book != null) {

                            holder.odhistory_name.setText(book.getBookTitle() + "");
                            holder.odhistory_unitprice.setText(book.getPrice() + "");
                            holder.odhistory_quantity.setText("X" + od.getQuantity());
                            holder.odhistory_totalprice.setText(od.getTotal() + "");
                            Glide.with(context)
                                    .load(book.getImg())
                                    .into(holder.odhistory_img);

                            holder.odhistory_repurchase_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    iodHistoryListener.Repurchase(book.getBookId());
                                }
                            });

                        } else {
                            Log.i("abc", "error onBindViewHolder odhistory");
                        }
                    }
                } else {
                    Log.i("abc", "no data found for the query");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn
            }
        });
    }
    @Override
    public int getItemCount() {
        if (odHistoryList != null) {
            return odHistoryList.size();
        } else {
            return 0;
        }
    }

    public class OrderDetailsHistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView odhistory_img;
        TextView odhistory_name, odhistory_quantity, odhistory_unitprice, odhistory_totalprice;
        Button odhistory_repurchase_btn;

        public OrderDetailsHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            odhistory_img = itemView.findViewById(R.id.odhistory_img);
            odhistory_name = itemView.findViewById(R.id.odhistory_name);
            odhistory_quantity = itemView.findViewById(R.id.odhistory_quantity);
            odhistory_unitprice = itemView.findViewById(R.id.odhistory_unitprice);
            odhistory_totalprice = itemView.findViewById(R.id.odhistory_total);
            odhistory_repurchase_btn = itemView.findViewById(R.id.odhistory_reorder_btn);
        }
    }

    public interface IODHistoryListener{
        void Repurchase(int bookId);
    }

}
