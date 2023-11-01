package com.example.myproject.activity;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myproject.R;

public class ContactActivity extends AppCompatActivity {

    private ImageView mMap;
    private Button btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mMap = findViewById(R.id.mMap);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        btnCall = findViewById(R.id.callPhone);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0974414699"));
                if (ActivityCompat.checkSelfPermission(ContactActivity.this,CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContactActivity.this, new String[]{CALL_PHONE}, 1);
                    return;
                }
                startActivity(intent);
            }
        });
    }

}