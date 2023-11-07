package com.example.myproject.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.Interface.TotalPriceListener;
import com.example.myproject.R;
import com.example.myproject.models.Cart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context mContext;
    public List<Cart> mListCart;
    private TotalPriceListener totalPriceListener;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();

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

                    // Lấy ID của sản phẩm
                    int bookId = cartItem.getBook().getBookId();
                    DatabaseReference unitInStockRef = FirebaseDatabase.getInstance().getReference().child("books").child(String.valueOf(bookId)).child("unitInStock");

                    unitInStockRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                long unitInStock = dataSnapshot.getValue(Long.class);

                                if (unitInStock >= qty) {
                                    // Cập nhật giỏ hàng nếu số lượng sách còn đủ
                                    updateCartQty(bookId, qty);
                                } else {
                                    // Số lượng sách còn không đủ, thông báo cho người dùng
                                    Toast.makeText(mContext, "Sản phẩm đã hết hàng hoặc không đủ số lượng!", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Xử lý lỗi nếu có
                        }
                    });
                }
            });

            holder.remove_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getBindingAdapterPosition();

                    if (position >= 0 && position < mListCart.size()) {
                        Cart cartItem = mListCart.get(position);
                        int qty = cartItem.getQuantity() - 1;
                        if (qty < 0) {
                            qty = 0;
                        }
                        updateCartQty(cartItem.getBook().getBookId(), qty);
                    } else {
                        // Xử lý tình huống khi position không hợp lệ
                    }
                }
            });
        }

        holder.quantitybook.setText(String.valueOf(cart.getQuantity()));
        Glide.with(mContext)
                .load(cart.getBook().getImg())
                .into(holder.item_cart_image);
        holder.checkbook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cart.setChecked(isChecked);
                calculateTotalPrice();
            }
        });
        holder.icon_delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = holder.getBindingAdapterPosition();
                showDeleteConfirmationDialog(position);
            }
        });
    }
    public void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xóa sản phẩm");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xóa sản phẩm ở vị trí `position` trong danh sách của bạn
                deleteItem(position);
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Không làm gì khi hủy
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void deleteItem(int position) {
                Cart cartItem = mListCart.get(position);
                int productId = cartItem.getBook().getBookId(); // Lấy productID từ Cart
                removeFromCart(user_current.getUid(), productId);
    }

    private void removeFromCart(String userId, int productId) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);

        // Xóa sản phẩm khỏi giỏ hàng
        cartRef.child(String.valueOf(productId)).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Sản phẩm đã được xóa!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Xóa không thành công!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateCartQty(int bookId, int newQty) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("carts");
        ref.child(user_current.getUid()).child(String.valueOf(bookId)).child("quantity").setValue(newQty);
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
        private ImageView add_item,item_cart_image;

        private ImageView icon_delete_item;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemcartname = itemView.findViewById(R.id.item_cart_name);
            itemcartprice = itemView.findViewById(R.id.item_car_price);
            quantitybook = itemView.findViewById(R.id.quantity);
            checkbook = itemView.findViewById(R.id.check);
            remove_item = itemView.findViewById(R.id.remove_item);
            add_item = itemView.findViewById(R.id.add_item);
            icon_delete_item = itemView.findViewById(R.id.icon_delete_item);
            itemcartname = itemView.findViewById(R.id.item_cart_name);
            item_cart_image =  itemView.findViewById(R.id.item_cart_image);
        }
    }

//    public interface TotalPriceListener {
//        void onTotalPriceUpdated(double totalPrice);
//    }
}
