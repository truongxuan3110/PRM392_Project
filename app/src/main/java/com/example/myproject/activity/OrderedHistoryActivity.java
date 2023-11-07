    package com.example.myproject.activity;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toolbar;

    import com.example.myproject.R;
    import com.example.myproject.adapter.OrderedHistoryAdapter;
    import com.example.myproject.models.Orders;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

    public class OrderedHistoryActivity extends AppCompatActivity implements OrderedHistoryAdapter.IOrderedHistoryListener {

        RecyclerView rv_ordersHistory;
        OrderedHistoryAdapter orderedHistoryAdapter;

        List<Orders> ordersList; // Khai báo danh sách đơn hàng

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        private Spinner statusOrderSpinner, sortSpinner;


        private  String sortCriteria = "Newest to Oldest", selectedStatus = "All";
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.ordered_history);

            initView();
            getOrdersByUserId(user.getUid(), "All", sortCriteria);

        }

        private void getOrdersByUserId(String userId, String filterByStatus, String sort) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");

            databaseReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ordersList = new ArrayList<>(); // Khởi tạo danh sách đơn hàng

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Orders order = snapshot.getValue(Orders.class);

                            if (filterByStatus.equals("All") || order.getStatus().equals(filterByStatus)) {
                                ordersList.add(order); // Thêm đơn hàng vào danh sách nếu status khớp hoặc chọn "All"
                            }
                        }

                        if(sortCriteria.equals("Newest to Oldest")){
                            Collections.sort(ordersList, (order1, order2) -> order1.getOrderDate().compareTo(order2.getOrderDate()));
                        }else{
                            Collections.sort(ordersList, (order1, order2) -> order2.getOrderDate().compareTo(order1.getOrderDate()));
                        }

                        orderedHistoryAdapter = new OrderedHistoryAdapter(OrderedHistoryActivity.this);
                        orderedHistoryAdapter.setOrdersList(ordersList);
                        rv_ordersHistory.setAdapter(orderedHistoryAdapter);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderedHistoryActivity.this);
                        rv_ordersHistory.setLayoutManager(linearLayoutManager);
                    } else {
                        // Xử lý khi không có đơn hàng nào
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý khi có lỗi xảy ra trong quá trình truy vấn
                }
            });
        }

        private void initView() {
            rv_ordersHistory = findViewById(R.id.rv_ordered_history);
            orderedHistoryAdapter = new OrderedHistoryAdapter(this);
            rv_ordersHistory.setAdapter(orderedHistoryAdapter);


            orderedHistoryAdapter.setOrderedHistoryListener(this);
            TextView nameScreen = findViewById(R.id.nameScreen);
            nameScreen.setText("Sản phẩm đã đặt");

            statusOrderSpinner = findViewById(R.id.filter_ordered_history_status);
            List<String> statusOrder = new ArrayList<>();
            statusOrder.add("All");
            statusOrder.add("Pending");
            statusOrder.add("Done");
            statusOrder.add("Failed");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOrder);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusOrderSpinner.setAdapter(adapter);

            sortSpinner = findViewById(R.id.sort_by_date);
            List<String> sortOptions = new ArrayList<>();
            sortOptions.add("Newest to Oldest");
            sortOptions.add("Oldest to Newest");

            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortSpinner.setAdapter(adapter2);
            statusOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedStatus = statusOrder.get(position);
                    getOrdersByUserId(user.getUid(), selectedStatus, sortCriteria);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Xử lý khi không có lựa chọn nào
                }
            });


            //sort
           sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   sortCriteria = sortOptions.get(i);
                   getOrdersByUserId(user.getUid(), selectedStatus, sortCriteria);
               }

               @Override
               public void onNothingSelected(AdapterView<?> adapterView) {

               }
           });
        }

        @Override
        public void onOrderedItemClicked(View view, int position) {

        }
    }