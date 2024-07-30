package com.example.onlineshopapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.onlineshopapp.Activity.LoginActivity;
import com.example.onlineshopapp.Fragment.CartFragment;
import com.example.onlineshopapp.Fragment.FavoriteFragment;
import com.example.onlineshopapp.Fragment.HomeFragment;
import com.example.onlineshopapp.Fragment.AccountFragment;
import com.example.onlineshopapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameFragment;
    TextView smsCountTxt;
    int pendingSMSCount = 100;

    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private CartFragment cartFragment;
    private Fragment activeFragment;
    private FavoriteFragment favoriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        frameFragment = findViewById(R.id.frameFragment);

        homeFragment = new HomeFragment();
        accountFragment = new AccountFragment();
        cartFragment = new CartFragment();
        favoriteFragment = new FavoriteFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.frameFragment, accountFragment, "4").hide(accountFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frameFragment, homeFragment, "1").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frameFragment, cartFragment, "3").hide(cartFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frameFragment, favoriteFragment, "2").hide(favoriteFragment).commit();

        activeFragment = homeFragment;

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        actionBar.setTitle("Trang chủ");
                        loadFragment(homeFragment);
                        return true;
                    case R.id.navigation_wishlist:
                        actionBar.setTitle("Sản phẩm yêu thích");
                        loadFragment(favoriteFragment);
                        return true;
                    case R.id.navigation_cart:
                        actionBar.setTitle("Giỏ hàng");
                        loadFragment(cartFragment);
                        return true;
                    case R.id.navigation_account:
                        actionBar.setTitle("Tài khoản");
                        loadFragment(accountFragment);
                        return true;
                }
                return false;
            }
        });
        actionBar.setTitle("Trang chủ");
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != activeFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment);
            transaction.show(fragment);
            transaction.commit();
            activeFragment = fragment;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("Tìm kiếm sản phẩm...");
        }

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = MenuItemCompat.getActionView(menuItem);
        smsCountTxt = (TextView) actionView.findViewById(R.id.notification_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications:
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        if (smsCountTxt != null) {
            if (pendingSMSCount == 0) {
                if (smsCountTxt.getVisibility() != View.GONE) {
                    smsCountTxt.setVisibility(View.GONE);
                }
            } else {
                smsCountTxt.setText(String.valueOf(Math.min(pendingSMSCount, 99)));
                if (smsCountTxt.getVisibility() != View.VISIBLE) {
                    smsCountTxt.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
