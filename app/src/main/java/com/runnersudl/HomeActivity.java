package com.runnersudl;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.runnersudl.estadisticas.EstadisticasFragment;
import com.runnersudl.estadisticas.HistorialSteps;
import com.runnersudl.login.LoginActivity;
import com.runnersudl.rutas.RutasFragment;
import com.runnersudl.settings.SettingsActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    CircleImageView imageView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imageView = (CircleImageView)findViewById(R.id.image_profile);
        imageView.callOnClick();
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().getLastPathSegment());

            // Load the image using Glide
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(imageView);
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        setInitialFragment();
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.estadisticas:
                fragment = new EstadisticasFragment();
                break;
            case R.id.retos:
                fragment = new RutasFragment();
                break;
        }
        replaceFragment(fragment);
        return true;
    }

    private void setInitialFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_fragment, new EstadisticasFragment());
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_historial){
            Intent intent = new Intent(HomeActivity.this, HistorialSteps.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("SelectedItemId", bottomNavigationView.getSelectedItemId());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemId = savedInstanceState.getInt("SelectedItemId");
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }
}
