package com.example.cinelix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Username extends AppCompatActivity {

    TextView username_tv;
    EditText username_et;
    Button button;
    DatabaseReference databaseReference;
    Member member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();


        member = new Member();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Username");
        username_tv = findViewById(R.id.username_textview);
        username_et=findViewById(R.id.username_edittext);
        button=findViewById(R.id.username_save_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = username_et.getText().toString();

                if (username.isEmpty()){
                    username_et.setError("Please enter your username");
                }else {
                    member.setName(username);
                    databaseReference.child(userId).setValue(member);
                }
            }
        });
    }

    @Override
    protected void onStart() {

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String userId=user.getUid();

        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String username=snapshot.child("name").getValue().toString();
                    username_tv.setText("Your username is\n"+username);
                    username_et.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }else {
                    Toast.makeText(Username.this, "No Username", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        super.onStart();
    }
}