package com.example.myproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myproject.R;
import com.example.myproject.adapter.ChatAdapter;
import com.example.myproject.models.ChatMessage;
import com.example.myproject.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatAdminActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Toolbar toolbar;
    ImageView imgSend;
    EditText edtMess;
    FirebaseFirestore db;
    ChatAdapter adapter;
    List<ChatMessage> list;
    FirebaseUser user_current = FirebaseAuth.getInstance().getCurrentUser();
    String emailUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_admin);
        emailUser = getIntent().getStringExtra("email");
        initView();
        initToolbar();
        initControl();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initControl() {
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gửi tin nhắn mới lên Firestore
                sendMessageToFirestore(edtMess.getText().toString());
                edtMess.setText(""); // Xóa nội dung tin nhắn trong EditText
            }
        });
    }
    private void sendMessageToFirestore(String str_mes) {
        if (TextUtils.isEmpty(str_mes)) {

        } else {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Utils.SENDID, user_current.getEmail()); //message.put(Utils.SENDID,String.valueOf(Utils.user_current.getId())); //lấy Id người gửi
            message.put(Utils.RECEIVEDID, emailUser); //message.put(Utils.RECEIVEDID,Utils.ID_RECEIVED); //lấy Id người nhận
            message.put(Utils.PARTICIPANTID, emailUser);
            message.put(Utils.MESS, str_mes);
            message.put(Utils.DATETIME, new Date());

            // Thêm tin nhắn mới vào Firestore
            db.collection(Utils.PATH_CHAT)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Tin nhắn đã được gửi thành công
                            // Không cần thêm bước tường minh để cập nhật danh sách chatMessages,
                            // vì sự kiện lắng nghe Firestore sẽ tự động cập nhật danh sách khi có thay đổi
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Xử lý lỗi khi gửi tin nhắn
                        }
                    });
        }
    }

    private String format_date(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy- hh:mm a", Locale.getDefault()).format(date);
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycleChat);
        imgSend = findViewById(R.id.imagechat);
        edtMess = findViewById(R.id.edtinputext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new ChatAdapter(getApplicationContext(),list,String.valueOf(user_current.getEmail()));
        //adapter = new ChatAdapter(getApplicationContext(),list,String.valueOf(Utils.user_current.getId())));
        recyclerView.setAdapter(adapter);
        retrieveInitialMessages();
    }
    private void retrieveInitialMessages() {
        // Lấy tin nhắn ban đầu từ Firestore và thêm vào danh sách chatMessages
        // Thông qua Firebase Firestore Query
        db.collection(Utils.PATH_CHAT)
//                .whereEqualTo(Utils.PARTICIPANTID, emailUser)
                .orderBy("datetime", Query.Direction.ASCENDING) // Sắp xếp theo thời gian
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Xử lý lỗi
                            return;
                        }
                        int count = list.size();
                        list.clear(); // Xóa danh sách tin nhắn hiện tại
                        for (QueryDocumentSnapshot document : value) {
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.sendid = document.getString(Utils.SENDID);
                            chatMessage.receivedid = document.getString(Utils.RECEIVEDID);
                            chatMessage.mess = document.getString(Utils.MESS);
                            chatMessage.dateObj = document.getDate(Utils.DATETIME);
                            chatMessage.datetime = format_date(document.getDate(Utils.DATETIME));
                            list.add(chatMessage);
                        }

                        Collections.sort(list,(obj1, obj2)-> obj1.dateObj.compareTo(obj2.dateObj));
                        if (count==0){
                            adapter.notifyDataSetChanged();
                        }else {
                            adapter.notifyItemRangeInserted(list.size(),list.size());
                            recyclerView.smoothScrollToPosition(list.size()-1);
                        }
                    }
                });
    }
}