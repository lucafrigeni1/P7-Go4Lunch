package com.example.go4lunch.Utils;

import android.widget.Toast;

import com.example.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener(){
        return e -> Toast.makeText(getApplicationContext(),
                getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG).show();
    }
}
