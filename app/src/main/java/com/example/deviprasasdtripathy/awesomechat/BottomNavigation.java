package com.example.deviprasasdtripathy.awesomechat;
//do not use
//import android.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class BottomNavigation extends AppCompatActivity {
    public ActionBar toolbar;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Profile);
        setContentView(R.layout.activity_bottom_navigation);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in do nothing
            Log.e("User:", user.getEmail().toString());
        } else {
            // No user is signed in commence Login
            Intent intent = new Intent(BottomNavigation.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //toolbar.setTitle("Home");
        loadFragment(new ChatThreads());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //toolbar.setTitle("Home");
                    loadFragment(new ChatThreads());
                    return true;
                case R.id.add_user:
//                    fragment = new SellFragment();
//                    loadFragment(fragment);
                    Intent intent=new Intent(getApplicationContext(),AddUser.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_profile:
                    //FirebaseAuth.getInstance().signOut();
                    Intent intent3=new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent3);
                    finish();
                    return true;
            }

            return false;
        }
    };
//
//    @Override
//    public void onBackPressed() {
//        if (fragment == storeFragment) {
//            finish();
//        } else {
//            loadFragment(storeFragment);
//            fragment = storeFragment;
//        }
//
//    }
@Override
public void onBackPressed() {
    if (doubleBackToExitPressedOnce) {
        finish();
        return;
    }

    this.doubleBackToExitPressedOnce = true;
    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

    new Handler().postDelayed(new Runnable() {

        @Override
        public void run() {
            doubleBackToExitPressedOnce=false;
        }
    }, 2000);
}

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
