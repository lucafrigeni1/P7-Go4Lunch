package com.example.go4lunch.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.di.Injections;
import com.example.go4lunch.di.ViewModelFactory;
import com.example.go4lunch.models.Worker;
import com.example.go4lunch.notifications.Notifications;
import com.example.go4lunch.ui.fragments.MapsFragment;
import com.example.go4lunch.ui.fragments.RestaurantListFragment;
import com.example.go4lunch.ui.fragments.WorkerListFragment;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.example.go4lunch.viewmodel.WorkerViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import static com.example.go4lunch.R.color.white;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.currentUserId;
import static com.example.go4lunch.viewmodel.WorkerDataRepository.latLng;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    WorkerViewModel workerViewModel;
    RestaurantViewModel restaurantViewModel;

    FrameLayout frameLayout;
    MapsFragment mapsFragment;
    RestaurantListFragment restaurantListFragment;
    WorkerListFragment workerListFragment;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ImageView headerPicture;
    TextView headerName;
    TextView headerMail;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;

    ConstraintLayout searchBar;
    ImageButton searchButton;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();
        setViewModels();
        pageSelected();
        configureToolBar();
        setHeader();
        setSearchButton();

        currentUserId = FirebaseAuth.getInstance().getUid();

        sendNotifications();
    }

    public void sendNotifications() {
        Notifications.launchWorker(this);
    }

    public void findViewById() {
        bottomNavigationView = findViewById(R.id.bottom_menu);
        frameLayout = findViewById(R.id.fragment_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.drawer_menu);
        toolbar = findViewById(R.id.top_toolbar);
        searchButton = findViewById(R.id.top_bar_search_btn);
        searchBar = findViewById(R.id.search_bar);
        searchText = findViewById(R.id.search_text);
    }

    //TOP BAR
    private void configureToolBar() {
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

    private void setSearchButton() {
        searchButton.setOnClickListener(v -> searchBar.setVisibility(View.VISIBLE));

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 3) {
                    if (latLng != null) {
                        setSearchResult(s.toString());
                    }
                }
            }
        });
    }

    private void setSearchResult(String search) {
        if (mapsFragment.isVisible()) {
            restaurantViewModel.getFilteredRestaurantsList(search).observe(MainActivity.this, mapsFragment::setMarkers);
        } else if (restaurantListFragment.isVisible()) {
            restaurantViewModel.getFilteredRestaurantsList(search).observe(MainActivity.this, restaurantListFragment::setRestaurantList);
        }
    }

    //DRAWER MENU
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.your_lunch:
                getCurrentUserChoice();
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

    private void getCurrentUserChoice() {
        workerViewModel.getCurrentUser().observe(this, this::startRestaurantDetailActivity);
    }

    private void startRestaurantDetailActivity(Worker worker) {
        if (worker.getRestaurant() != null) {
            Intent intent = new Intent(this.getApplicationContext(), RestaurantDetailActivity.class);
            intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT, worker.getRestaurant().getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.choice), Toast.LENGTH_LONG).show();
        }
    }

    private void startSettingsActivity() {
        finish();
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (searchBar.isShown()) {
            searchBar.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void setHeader() {
        View header = navigationView.inflateHeaderView(R.layout.drawer_menu_header);
        headerPicture = header.findViewById(R.id.profil_image);
        headerName = header.findViewById(R.id.worker_name);
        headerMail = header.findViewById(R.id.worker_mail);

        FirebaseUser firebaseUser = workerViewModel.getFirebaseUser();

        if (firebaseUser != null) {
            if (firebaseUser.getPhotoUrl() != null) {
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

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted() {
        return aVoid -> startAuthenticationActivity();
    }

    private void startAuthenticationActivity() {
        finish();
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }

    //BOTTOM MENU
    @SuppressLint("NonConstantResourceId")
    private void pageSelected() {
        initFragment();
        setFragment(mapsFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.map_page:
                    toolbar.setTitle(getString(R.string.toolbar_text_1));
                    searchButton.setVisibility(View.VISIBLE);
                    if (searchBar.isShown()) {
                        searchBar.setVisibility(View.GONE);
                        searchText.getText().clear();
                    }
                    setFragment(mapsFragment);
                    break;
                case R.id.restaurant_page:
                    toolbar.setTitle(getString(R.string.toolbar_text_1));
                    searchButton.setVisibility(View.VISIBLE);
                    if (searchBar.isShown()) {
                        searchBar.setVisibility(View.GONE);
                        searchText.getText().clear();
                    }
                    setFragment(restaurantListFragment);
                    break;
                case R.id.worker_page:
                    toolbar.setTitle(getString(R.string.toolbar_text_2));
                    searchButton.setVisibility(View.GONE);
                    if (searchBar.isShown()) {
                        searchBar.setVisibility(View.GONE);
                    }
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
        this.restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);
    }
}
