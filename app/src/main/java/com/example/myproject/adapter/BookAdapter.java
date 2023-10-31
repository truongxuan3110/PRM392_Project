package com.example.myproject.adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myproject.R;
import com.example.myproject.activity.BookDetailActivity;
import com.example.myproject.activity.ListBook;
import com.example.myproject.models.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private Context mContext;
    private List<Book> mListBook;
    public BookAdapter(ListBook mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Book> list) {
        this.mListBook = list;
        notifyDataSetChanged();
        // hàm notifyDataSetChanged để gọi để adapter bind vào adapter
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_book, parent, false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel =
//                    new NotificationChannel("1", "CHANNEL_1_ID",
//                            NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(notificationChannel);
//
//        }
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = mListBook.get(position);
        if(book == null){
            return;
        }
      //  holder.imgProduct.setImageResource(book.getImg());
        holder.txtName.setText(book.getBookTitle());
        holder.txtPrice.setText(String.valueOf(book.getPrice()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click, open DetailActivity
                Intent intent = new Intent(mContext, BookDetailActivity.class);
                intent.putExtra("PRODUCT_ID", book.getBookId());
                intent.putExtra("PRODUCT_NAME", book.getBookTitle());
                intent.putExtra("PRODUCT_PRICE", book.getPrice());
                intent.putExtra("PRODUCT_IMAGE", book.getImg());
                intent.putExtra("PRODUCT_QUANTITY", book.getUnitInStock());
                intent.putExtra("PRODUCT_DESCRIPTION", book.getDescription());

              //  Log.d("ImageDebugAdapter", "Product Image Resource: " + book());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListBook != null) {
            return mListBook.size();
        }
        return 0;
    }

    // là nơi khai báo các thành phần có trong items_book.xml
    public class BookViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgProduct;
        private TextView txtName;
        private TextView txtPrice;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            txtName = itemView.findViewById(R.id.text_name);
            txtPrice = itemView.findViewById(R.id.text_price);
        }
    }
}
