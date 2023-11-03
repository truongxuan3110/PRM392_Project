package com.example.myproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.R;
import com.example.myproject.activity.Last_Payment_Activity;
import com.example.myproject.activity.PaymentActivity;
import com.example.myproject.models.Cart;

import java.util.List;

public class LastPaymentAdapter extends RecyclerView.Adapter<LastPaymentAdapter.LastPaymentViewHolder> {
    private Context mContext;
    private List<Cart> mListOrderDetail;

    public LastPaymentAdapter(Last_Payment_Activity mContext) {
        this.mContext = mContext;
    }


    public void setData(List<Cart> list) {
        this.mListOrderDetail = list;
        notifyDataSetChanged();
        // hàm notifyDataSetChanged để gọi để adapter bind vào adapter
    }

    @NonNull
    @Override
    public LastPaymentAdapter.LastPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new LastPaymentAdapter.LastPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LastPaymentAdapter.LastPaymentViewHolder holder, int position) {
        Cart orderDetail = mListOrderDetail.get(position);
        if (orderDetail == null) {
            return;
        }
        Glide.with(mContext)
                .load(orderDetail.getBook().getImg())
                .into(holder.item_payment_image);
        // holder.item_payment_image.setImageResource(orderDetail.getBook().getImg());
        holder.item_payment_name.setText(orderDetail.getBook().getBookTitle());
        holder.item_payment_price.setText(String.valueOf(orderDetail.getBook().getPrice()));
        holder.item_payment_quantity.setText(String.valueOf(orderDetail.getQuantity()));
        holder.item_payment_totalPrice.setText(String.valueOf("Tổng tiền của " + orderDetail.getQuantity() + " sản phẩm là: " + orderDetail.getQuantity() * orderDetail.getBook().getPrice()));

    }


    @Override
    public int getItemCount() {
        if (mListOrderDetail != null) {
            return mListOrderDetail.size();
        }
        return 0;

    }

    public class LastPaymentViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_payment_image;
        private TextView item_payment_name;
        private TextView item_payment_price;
        private TextView item_payment_quantity;
        private TextView item_payment_totalPrice;

        // private TextView txtPrice;


        // là nơi khai báo các thành phần có trong rcv_product_last_payment.xml
        public LastPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            item_payment_image = itemView.findViewById(R.id.item_payment_image);
            item_payment_name = itemView.findViewById(R.id.item_payment_name);
            item_payment_price = itemView.findViewById(R.id.item_payment_price);
            item_payment_quantity = itemView.findViewById(R.id.item_payment_quantity);
            item_payment_totalPrice = itemView.findViewById(R.id.item_payment_Total_price);

        }
    }
}
