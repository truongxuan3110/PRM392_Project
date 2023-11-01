package com.example.myproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.Interface.TotalPriceListener;
import com.example.myproject.R;
import com.example.myproject.models.Cart;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context mContext;
    public List<Cart> mListCart;
    private TotalPriceListener totalPriceListener;

    public CartAdapter(Context mContext, List<Cart> mListCart, TotalPriceListener totalPriceListener) {
        this.mContext = mContext;
        this.mListCart = mListCart;
        this.totalPriceListener = totalPriceListener;
    }

    public void setData(List<Cart> list) {
        this.mListCart = list;
        notifyDataSetChanged();
        // Gọi hàm notifyDataSetChanged để cập nhật dữ liệu trong adapter
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = mListCart.get(position);

        // Đảm bảo rằng cart.getBook() không phải là null
        if (cart.getBook() != null) {
            holder.itemcartname.setText(cart.getBook().getBookTitle());
            holder.itemcartprice.setText(String.valueOf(cart.getBook().getPrice()));

            holder.add_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getBindingAdapterPosition();
                    Cart cartItem = mListCart.get(position);
                    int qty = cartItem.getQuantity() + 1;
                    updateCartQty(cartItem.getBook().getBookId(), qty);
                }
            });

            holder.remove_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getBindingAdapterPosition();
                    Cart cartItem = mListCart.get(position);
                    int qty = cartItem.getQuantity() - 1;
                    if (qty < 0) {
                        qty = 0;
                    }
                    updateCartQty(cartItem.getBook().getBookId(), qty);
                }
            });
        }

        holder.quantitybook.setText(String.valueOf(cart.getQuantity()));

        holder.checkbook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cart.setChecked(isChecked);
                calculateTotalPrice();
            }
        });
    }

    private void updateCartQty(int bookId, int newQty) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carts");
        ref.child("1").child(String.valueOf(bookId)).child("quantity").setValue(newQty);
    }

    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (Cart cartItem : mListCart) {
            if (cartItem.isChecked()) {
                int quantity = cartItem.getQuantity();
                double price = cartItem.getBook().getPrice();
                totalPrice += quantity * price;
            }
        }
        if (totalPriceListener != null) {
            Log.d("hh", String.valueOf(totalPrice));
            totalPriceListener.onTotalPriceUpdated(totalPrice);
        }
    }
    public boolean isProductSelected(int position) {
        if (position >= 0 && position < mListCart.size()) {
            Cart cartItem = mListCart.get(position);
            return cartItem.isChecked();
        }
        return false;
    }
//    public boolean isProductSelected(int position) {
//        if (position >= 0 && position < mListCart.size()) {
//            return mListCart.get(position).isChecked();
//        }
//        return false;
//    }


    @Override
    public int getItemCount() {
        return mListCart.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView itemcartname;
        private TextView itemcartprice;
        private TextView quantitybook;
        private CheckBox checkbook;
        private ImageView remove_item;
        private ImageView add_item;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemcartname = itemView.findViewById(R.id.item_cart_name);
            itemcartprice = itemView.findViewById(R.id.item_car_price);
            quantitybook = itemView.findViewById(R.id.quantity);
            checkbook = itemView.findViewById(R.id.check);
            remove_item = itemView.findViewById(R.id.remove_item);
            add_item = itemView.findViewById(R.id.add_item);
        }
    }

//    public interface TotalPriceListener {
//        void onTotalPriceUpdated(double totalPrice);
//    }
}
