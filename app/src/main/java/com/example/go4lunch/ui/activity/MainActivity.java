package com.example.go4lunch.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.ui.fragments.MapsFragment;
import com.example.go4lunch.ui.fragments.RestaurantListFragment;
import com.example.go4lunch.ui.fragments.WorkerListFragment;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import static com.example.go4lunch.R.color.white;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SIGN_OUT_TASK = 10;

    WorkerViewModel workerViewModel;
    Worker currentUser = new Worker();

    FrameLayout frameLayout;
    MapsFragment mapsFragment;
    RestaurantListFragment restaurantListFragment;
    WorkerListFragment workerListFragment;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    ImageView headerPicture;
    TextView headerName;
    TextView headerMail;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
        setViewModels();
        pageSelected();
        configureToolBar();
        setHeader();
    }

    public void findViewById(){
        bottomNavigationView = findViewById(R.id.bottom_menu);
        frameLayout = findViewById(R.id.fragment_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_menu);
        toolbar = findViewById(R.id.top_toolbar);
    }

    //TOP BAR
    private void configureToolBar(){
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
                );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(white));

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        //setSearchView();


        return super.onCreateOptionsMenu(menu);
    }

    private void setSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    //DRAWER MENU
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.your_lunch:
                startRestaurantDetailActivity();
                break;
            case R.id.settings:
                startSettingsActivity();
                break;
            case R.id.logout:
                signOutUserFromFirebase();
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startRestaurantDetailActivity(){
        workerViewModel.getCurrentUser().observe(this, worker -> {
           currentUser = worker;
        });

        if (currentUser.getRestaurantId() != null) {
            Intent intent = new Intent(this.getApplicationContext(), RestaurantDetailActivity.class);
            intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT, currentUser.getRestaurantId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "You didn't make a choice", Toast.LENGTH_LONG).show();
        }
    }

    private void startSettingsActivity(){
        finish();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setHeader(){
        View header = navigationView.inflateHeaderView(R.layout.drawer_menu_header);
        headerPicture = header.findViewById(R.id.profil_image);
        headerName = header.findViewById(R.id.worker_name);
        headerMail = header.findViewById(R.id.worker_mail);

        FirebaseUser firebaseUser = workerViewModel.getFirebaseUser();

        if (firebaseUser != null){
            if (firebaseUser.getPhotoUrl() != null){
                Glide.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(headerPicture);
            } else
                Glide.with(this)
                        .load(R.drawable.ic_baseline_person_24)
                        .apply(RequestOptions.circleCropTransform())
                        .into(headerPicture);

            headerName.setText(firebaseUser.getDisplayName());
            headerMail.setText(firebaseUser.getEmail());
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return aVoid -> {
            if (origin == SIGN_OUT_TASK) {
                startAuthenticationActivity();
            }
        };
    }

    private void startAuthenticationActivity() {
        finish();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }

    //BOTTOM MENU
    @SuppressLint("NonConstantResourceId")
    private void pageSelected(){
        initFragment();
        setFragment(mapsFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.map_page:
                    toolbar.setTitle(getString(R.string.toolbar_text_1));
                    setFragment(mapsFragment);
                    break;
                case R.id.restaurant_page:
                    toolbar.setTitle(getString(R.string.toolbar_text_1));
                    setFragment(restaurantListFragment);
                    break;
                case R.id.worker_page:
                    toolbar.setTitle(getString(R.string.toolbar_text_2));
                    setFragment(workerListFragment);
                    break;
            }
            return true;
        });
    }

    private void initFragment() {
        mapsFragment = new MapsFragment();
        restaurantListFragment = new RestaurantListFragment();
        workerListFragment = new WorkerListFragment();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    private void setViewModels() {
        ViewModelFactory viewModelFactory = Injections.provideViewModelFactory(this.getApplicationContext());
        this.workerViewModel = ViewModelProviders.of(this, viewModelFactory).get(WorkerViewModel.class);
    }
}
