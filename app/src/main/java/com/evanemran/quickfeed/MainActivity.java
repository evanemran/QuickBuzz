package com.evanemran.quickfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.evanemran.quickfeed.dialogs.PostDialog;
import com.evanemran.quickfeed.fragments.HomeFragment;
import com.evanemran.quickfeed.fragments.NotificationsFragment;
import com.evanemran.quickfeed.fragments.PostFragment;
import com.evanemran.quickfeed.fragments.ProfileFragment;
import com.evanemran.quickfeed.fragments.SearchFragment;
import com.evanemran.quickfeed.listeners.ClickListener;
import com.evanemran.quickfeed.models.PostData;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, postsReference;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        replaceFragment(new HomeFragment());


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Query query =databaseReference.orderByChild("userMail").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren())
                    {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // No user is signed in
        }


        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavBar);

        bottomNavBar.setOnNavigationItemSelectedListener(listener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.search:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.post:
                    popupPostDialog();
//                    replaceFragment(new PostFragment());
                    break;
                case R.id.notification:
                    replaceFragment(new NotificationsFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }
            return true;
        }
    };

    private void popupPostDialog() {
        PostDialog postDialog = new PostDialog(postDataClickListener);
        postDialog.show(getSupportFragmentManager(), "post");
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();

    }

    private final ClickListener<PostData> postDataClickListener = new ClickListener<PostData>() {
        @Override
        public void onClicked(PostData data) {
            data.setPostedBy(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            postsReference = firebaseDatabase.getReference("posts");
            postsReference.child(postsReference.push().getKey()).setValue(data);
            Toast.makeText(MainActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
        }
    };
}