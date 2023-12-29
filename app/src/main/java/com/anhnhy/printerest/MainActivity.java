package com.anhnhy.printerest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.anhnhy.printerest.fragment.AccountFragment;
import com.anhnhy.printerest.fragment.HomeFragment;
import com.anhnhy.printerest.fragment.LikeFragment;
import com.anhnhy.printerest.fragment.YourFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_LIKE = 2;
    private static final int FRAGMENT_YOUR = 3;
    private static final int FRAGMENT_ACCOUNT = 4;
    private int currentFragment = FRAGMENT_HOME;
    private TextView u_name, u_email;
    private DatabaseReference dbRef;
    private String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbRef = FirebaseDatabase.getInstance().getReference().
                child("users").child(UID);

        displayData();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        navigationView.setCheckedItem(R.id.nav_home);
        setTitleToolBar();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (FRAGMENT_HOME != currentFragment) {
                replaceFragment(new HomeFragment());
                currentFragment = FRAGMENT_HOME;
            }
        } else if (id == R.id.nav_like_images) {
            if (FRAGMENT_LIKE != currentFragment) {
                replaceFragment(new LikeFragment());
                currentFragment = FRAGMENT_LIKE;
            }
        } else if (id == R.id.nav_your_images) {
            if (FRAGMENT_YOUR != currentFragment) {
                replaceFragment(new YourFragment());
                currentFragment = FRAGMENT_YOUR;
            }
        } else if (id == R.id.nav_account) {
            if (FRAGMENT_ACCOUNT != currentFragment) {
                replaceFragment(new AccountFragment());
                currentFragment = FRAGMENT_ACCOUNT;
            }
        }

        setTitleToolBar();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    u_name = findViewById(R.id.u_name);
                    u_email = findViewById(R.id.u_email);
                    String name = snapshot.child("name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    u_name.setText(name);
                    u_email.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    private void setTitleToolBar() {
        String title = "";
        switch (currentFragment) {
            case FRAGMENT_HOME:
                title = getString(R.string.title_home);
                break;
            case FRAGMENT_LIKE:
                title = getString(R.string.like_images);
                break;
            case FRAGMENT_YOUR:
                title = getString(R.string.your_images);
                break;
            case FRAGMENT_ACCOUNT:
                title = getString(R.string.title_account);
                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}