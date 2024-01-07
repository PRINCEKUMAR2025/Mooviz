package com.example.cinelix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    EditText etMail;
    Button reset;
    String email;
    FirebaseAuth auth;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        etMail=findViewById(R.id.inputEmail);
        reset=findViewById(R.id.buttonReset);
        progressbar=findViewById(R.id.progressBar);

        auth=FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }

            private void validateData() {
                email=etMail.getText().toString();
                if (email.isEmpty()){
                    etMail.setError("Required");
                }
                else {
                    forgetPass();
                }
            }
        });
    }

    private void forgetPass() {
        progressbar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ForgotPass.this, "Reset instruction sent to your registered email", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ForgotPass.this,SignIn.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(ForgotPass.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}