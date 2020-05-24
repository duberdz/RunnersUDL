package com.runnersudl.login;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.runnersudl.HomeActivity;
import com.runnersudl.R;


import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {

    TextInputLayout email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (TextInputLayout) findViewById(R.id.login_textInputLayoutEmail);
        password = (TextInputLayout) findViewById(R.id.login_textInputLayoutPassword);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            goToApp();
        }
    }

    // Log In button
    public void logIn(View view){
        if (!thereAreErrors()){

            final ProgressDialog dialog = ProgressDialog.show(this, "Iniciando sesión", "Cargando. Por favor espera...", true);

            mAuth.signInWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dialog.cancel();

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Show toast login succesfully
                                Toast.makeText(LoginActivity.this, getString(R.string.success_message_login, email.getEditText().getText().toString()), Toast.LENGTH_LONG).show();

                                // Go to HomeActivity
                                goToApp();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

    public void goToApp(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {

                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        // Add a new document with a generated ID
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> map_token = new HashMap<>();
                        Log.w("-------------------", token);
                        map_token.put("token", token);
                        db.collection("Tokens")
                                .document(FirebaseAuth.getInstance().getUid())
                                .set(map_token);
                    }
                });

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // Go to register
    public void goToRegister(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    // Check if there are errors on the fields.
    private boolean thereAreErrors(){
        InputValidator validator = new InputValidator();

        boolean error = false;

        // Email validation
        if (validator.isNullOrEmpty(email.getEditText().getText().toString())){
            email.setErrorEnabled(true);
            email.setError(getString(R.string.email_vacio));
            error = true;
        }
        else if (!validator.isValidEmail(email.getEditText().getText().toString())){
            email.setErrorEnabled(true);
            email.setError(getString(R.string.email_no_valido));
            error = true;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
        }

        // Password validation
        if (validator.isNullOrEmpty(password.getEditText().getText().toString())){
            password.setErrorEnabled(true);
            password.setError(getString(R.string.contraseña_vacia));
            error = true;
        } else if (!validator.isValidPassword(password.getEditText().getText().toString(), false)) {
            password.setErrorEnabled(true);
            password.setError(getString(R.string.contraseña_no_valida));
            error = true;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
        }

        return error;
    }
}
