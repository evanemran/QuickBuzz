package com.evanemran.quickfeed;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evanemran.quickfeed.R;
import com.evanemran.quickfeed.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText editTextPassword, editTextMail, editTextPasswordConfirm, editTextName;
    Button button_signup;
    TextView textView_login;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        button_signup = findViewById(R.id.button_signup);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextMail = findViewById(R.id.editTextMail);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        editTextName = findViewById(R.id.editTextName);
        textView_login = findViewById(R.id.textView_login);

        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                SignupActivity.this.finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextMail.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();
                if (isValidInputs(email, password, name)){
                    signup(email, password, name);
                }
            }
        });
    }

    private boolean isValidInputs(String email, String password, String name) {
        if (email.isEmpty()){
            editTextMail.setError("Enter email");
            return false;
        }
        else if (password.isEmpty()){
            editTextPassword.setError("Enter password");
            return false;
        }
        else if (name.isEmpty()){
            editTextName.setError("Enter name");
            return false;
        }
        else return true;
    }

    private void signup(String email, String password, String name) {
//        Toast.makeText(this, "Signup", Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createNewUser(task.getResult().getUser(), name);

                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
                            // Sign in success, update UI with the signed-in user's information
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }

    private void createNewUser(FirebaseUser user, String name) {
        addUser(new User(user.getUid(),
                name,
                name,
                user.getEmail(),
                "",
                ""
        ));
    }

    private void addUser(User user) {
        databaseReference.child(user.getUserId()).setValue(user);
        Toast.makeText(this, "Info Added to database", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            reload();
        }
    }
}