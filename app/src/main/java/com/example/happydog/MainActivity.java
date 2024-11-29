package com.example.happydog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_product_catalog) {
                loadFragment(new ProductCatalogFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_shopping_cart) {
                loadFragment(new ShoppingCartFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            } else if (item.getItemId() == R.id.nav_educational_content) {
                loadFragment(new EdContentFragment());
                return true;
            }else if (item.getItemId() == R.id.nav_orders) {
                loadFragment(new OrdersFragment());
                return true;
            } else {
                return false;
            }
        });

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new ProductCatalogFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
