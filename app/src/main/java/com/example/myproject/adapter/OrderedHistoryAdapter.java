package com.example.myproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.activity.BookDetailActivity;
import com.example.myproject.activity.OrderDetailHistoryActivity;
import com.example.myproject.models.Orders;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderedHistoryAdapter extends RecyclerView.Adapter<OrderedHistoryAdapter.OrderedViewHolder> {


    private Context context;
    private List<Orders> ordersList;



    public OrderedHistoryAdapter(Context context) {
        this.context = context;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
        notifyDataSetChanged(); // Thông báo rằng dữ liệu đã thay đổi
    }



    @NonNull
    @Override
    public OrderedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordered_history_item, parent, false);
        return new OrderedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedViewHolder holder, int position) {
        if (ordersList == null || ordersList.isEmpty()) {
            return;
        }

        Orders orders = ordersList.get(position);
        if (orders == null) return;

        // Chuyển đổi ngày về định dạng "day-month-year" sử dụng SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(orders.getOrderDate());

        holder.ordered_history_date.setText(formattedDate);
      //  holder.ordered_history_fullname.setText(orders.getUserId());
        holder.ordered_history_phone.setText(orders.getPhone());
        holder.ordered_history_note.setText(orders.getNote());
        holder.ordered_history_totalcost.setText(String.valueOf(orders.getOrderTotalCost()));
        holder.ordered_history_address.setText(orders.getAddress());
        holder.ordered_history_orderedStatus.setText(orders.getStatus());
        String orderStatus = orders.getStatus();

        switch (orderStatus) {
            case "Pending":
                holder.ordered_history_orderedStatus.setTextColor(ContextCompat.getColor(context, R.color.orange)); // Màu xám
                break;
            case "Failed":
                holder.ordered_history_orderedStatus.setTextColor(ContextCompat.getColor(context, R.color.red)); // Màu đỏ
                break;
            case "Done":
                holder.ordered_history_orderedStatus.setTextColor(ContextCompat.getColor(context, R.color.green)); // Màu xanh
                break;
            default:
                // Màu mặc định nếu không khớp với trạng thái nào
                holder.ordered_history_orderedStatus.setTextColor(ContextCompat.getColor(context, android.R.color.black)); // Màu đen hoặc màu mặc định
                break;
        }

        // Thiết lập Listener khi người dùng nhấn vào mỗi mục trong RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("kkkk " , orders.getOrderId());
                Intent intent = new Intent(context, OrderDetailHistoryActivity.class);
                intent.putExtra("orderId", orders.getOrderId());
                context.startActivity(intent);
               // orderedHistoryListener.onOrderedItemClicked(view, orders.getOrderId());
            }
        });
    }


    @Override
    public int getItemCount() {
        if (ordersList != null) {
            return ordersList.size();
        } else {
            return 0;
        }
    }

    public class OrderedViewHolder extends RecyclerView.ViewHolder {
        TextView ordered_history_date, ordered_history_fullname,
                ordered_history_phone, ordered_history_note, ordered_history_totalcost, ordered_history_orderedStatus,

        ordered_history_address;


        public OrderedViewHolder(@NonNull View itemView) {
            super(itemView);
            ordered_history_date = itemView.findViewById(R.id.ordered_history_date);
           // ordered_history_fullname = itemView.findViewById(R.id.ordered_history_fullname);
            ordered_history_phone = itemView.findViewById(R.id.ordered_history_phone);
            ordered_history_note = itemView.findViewById(R.id.ordered_history_note);
            ordered_history_totalcost = itemView.findViewById(R.id.ordered_history_totalcost);
            ordered_history_orderedStatus = itemView.findViewById(R.id.ordered_history_orderedStatus);
            ordered_history_address = itemView.findViewById(R.id.ordered_history_address);
        }
    }


}
