package com.pastoreli.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.model.User;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth authentication;

    private TextInputEditText editEmail, editPassword;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        textRegister = findViewById(R.id.textRegister);

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward(RegisterActivity.class);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkAuth();
    }

    public void checkAuth () {
        authentication = FirebaseConfig.getFirebaseAuthentication();
        if(authentication.getCurrentUser() != null)
            forward(MainActivity.class);
    }

    public void handleLogin(View view) {
        if(validateForm()) {
            User user = new User();
            user.setEmail(editEmail.getText().toString());
            user.setPassword(editPassword.getText().toString());

            loginUser(user);
        }
    }

    public void loginUser(User user) {

        authentication = FirebaseConfig.getFirebaseAuthentication();
        authentication.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    forward(MainActivity.class);
                else {
                    try {
                        throw task.getException();
                    } catch ( FirebaseAuthInvalidUserException e ) {
                        showToast("Usuário não está cadastado.");
                    } catch ( FirebaseAuthInvalidCredentialsException e ) {
                        showToast("E-mail e senha não correspondem a um usuário cadastrado.");
                    } catch ( Exception e ) {
                        showToast("Erro ao fazer login.");
                    }
                }
            }
        });

    }

    public void forward (Class classRef) {
        Intent intent = new Intent(LoginActivity.this, classRef);
        startActivity(intent);
    }

    public boolean validateForm() {
        String textEmail = editEmail.getText().toString();
        String textPassword = editPassword.getText().toString();


        if (!textEmail.isEmpty()) {
            if (!textPassword.isEmpty())
                return true;
            else
                showToast("Preencha a senha!");
        } else
            showToast("Preencha o email!");

        return false;

    }

    public void showToast (String text) {
        Toast.makeText(
                LoginActivity.this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

}