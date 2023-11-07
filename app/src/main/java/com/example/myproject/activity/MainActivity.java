package com.example.myproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.myproject.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvName, tvEmail;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.navigation_view);

        mNavigationView.setNavigationItemSelectedListener(this);

        tvEmail = mNavigationView.getHeaderView(0).findViewById(R.id.tv_email);
        tvName = mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);

        showUserInformation();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Log.e("eeeee", id + "");
        if (id == R.id.aview_ordered) {
            Log.i("abc", "abbbbbb");
            Intent intent = new Intent(MainActivity.this, OrderedHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Log.i("nav_profile", "nav_profile");
        } else if (id == R.id.nav_password) {
            Log.i("nav_password", "nav_password");
        } else if (id == R.id.nav_signout) {
            Log.i("nav_signout", "nav_signout");
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//
//            startActivity(intent);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//            mDrawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    private void showUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Người dùng chưa đăng nhập, không cần hiển thị thông tin.
            tvName.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
        } else {
            String name = user.getDisplayName();
            String email = user.getEmail();

            if (name != null && !name.isEmpty()) {
                tvName.setText(name);
            } else {
                tvName.setText("Không có tên người dùng");
            }

            if (email != null && !email.isEmpty()) {
                tvEmail.setText(email);
            } else {
                tvEmail.setText("Không có địa chỉ email");
            }
        }
    }


}