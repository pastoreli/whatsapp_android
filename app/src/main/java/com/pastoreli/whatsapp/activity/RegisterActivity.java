package com.pastoreli.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.config.FirebaseConfig;
import com.pastoreli.whatsapp.controller.UserController;
import com.pastoreli.whatsapp.helpers.CustomBase64;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth authentication;
    private UserController userController = new UserController();

    private TextInputEditText editName, editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editProfileName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);

    }

    public void handleRegisterUser (View view) {

        if(validateForm()) {

            User user = new User();
            user.setName(editName.getText().toString());
            user.setEmail(editEmail.getText().toString());
            user.setPassword(editPassword.getText().toString());

            registerUser(user);

        }

    }

    public void registerUser (User user) {

        authentication = FirebaseConfig.getFirebaseAuthentication();
        authentication.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    saveUserData(user);
                    UserFirebaseHelper.updateUserName(user.getName());
                    showToast("Sucesso ao cadastrar usuário");
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        showToast("Digite uma senha mais forte.");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        showToast("Por favor, digite um e-mail válido.");
                    } catch (FirebaseAuthUserCollisionException e) {
                        showToast("Esta conta já foi cadastrada.");
                    } catch (Exception e) {
                        showToast("Erro ao cadastrar usuário: "+ e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void saveUserData (User user) {
        userController.saveUserData(user);
    }

    public boolean validateForm () {
        String textName = editName.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textPassword = editPassword.getText().toString();

        if( !textName.isEmpty() ) {
            if( !textEmail.isEmpty() ) {
                if( !textPassword.isEmpty() )
                    return true;
                else
                    showToast("Preencha a senha!");
            } else
                showToast("Preencha o email!");
        } else
            showToast("Preencha o nome!");

        return false;

    }

    public void showToast (String text) {
        Toast.makeText(
                RegisterActivity.this,
                text,
                Toast.LENGTH_SHORT
        ).show();
    }
}