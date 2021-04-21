package com.example.database;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.database.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class bigmain extends AppCompatActivity implements ValueEventListener {
    TextView user, email;
    Button crowdfunding,forum;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    String key = databaseReference.child("users").push().getKey();
    private DatabaseReference username = databaseReference.child("users").child(mFirebaseUser.getUid());
    private DatabaseReference useremail = databaseReference.child("users").child(mFirebaseUser.getUid());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigmain);



        user = (TextView) findViewById(R.id.profileName);
        email = (TextView) findViewById(R.id.profileEmail);
        crowdfunding = (Button) findViewById(R.id.crowdfunding);
        forum = findViewById(R.id.forum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        crowdfunding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }






    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User first = dataSnapshot.getValue(User.class);
       user.setText(first.username);
        email.setText(first.email);


    }

    public void onCancelled(DatabaseError databaseError) {

    }

    public void onStart(){
        super.onStart();
        username.addValueEventListener(this);
        useremail.addValueEventListener(this);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }
}
