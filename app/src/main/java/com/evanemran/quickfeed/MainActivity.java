package com.evanemran.quickfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.evanemran.quickfeed.dialogs.PostDialog;
import com.evanemran.quickfeed.fragments.HomeFragment;
import com.evanemran.quickfeed.fragments.NotificationsFragment;
import com.evanemran.quickfeed.fragments.PostFragment;
import com.evanemran.quickfeed.fragments.ProfileFragment;
import com.evanemran.quickfeed.fragments.SearchFragment;
import com.evanemran.quickfeed.globals.GlobalUser;
import com.evanemran.quickfeed.listeners.ClickListener;
import com.evanemran.quickfeed.listeners.PostListener;
import com.evanemran.quickfeed.models.PostData;
import com.evanemran.quickfeed.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, postsReference;
    TextView textView;
    User user;
    PostData newPost;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        replaceFragment(new HomeFragment());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!uID.isEmpty()){
            getUserData(uID);
        }

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

    private final PostListener postDataClickListener = new PostListener() {
        @Override
        public void onPostClicked(PostData data, Uri imageUri) {
            newPost = data;
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();
            newPost.setPosTTime(format.format(date));
            if (imageUri!=null){
                uploadImage(imageUri);
            }
            else{
                newPost.setPostedBy(user);
                postsReference = firebaseDatabase.getReference("posts");
                String key = postsReference.push().getKey();
                newPost.setPostId(key);
                postsReference.child(key).setValue(newPost);
                Toast.makeText(MainActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void getUserData(String uID) {
        Query dbQuery = databaseReference
                .orderByChild("userId")
                .equalTo(uID);

        dbQuery.addListenerForSingleValueEvent(
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            user = postSnapshot.getValue(User.class);
                            GlobalUser.getInstance().setData(user);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                })
        );
    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + System.currentTimeMillis()+"."+getFileExtension(filePath));

            // adding listeners on upload
            // or failure of image
            ref.putFile(compressImage(filePath))
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            newPost.setImage(uri.toString());
                                            newPost.setPostedBy(user);
                                            postsReference = firebaseDatabase.getReference("posts");
                                            String key = postsReference.push().getKey();
                                            newPost.setPostId(key);
                                            postsReference.child(key).setValue(newPost);
                                            Toast.makeText(MainActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(MainActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private Uri compressImage(Uri uri){
        Uri compressedUri = null;
        try {
            int quality = 70;
            int width = 720;
//            int height = Integer.valueOf(txtHeight.getText().toString());
            File compressed = new Compressor(MainActivity.this)
                    .setMaxWidth(width)
                    .setQuality(quality)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(new File(uri.getPath()));

            compressedUri = Uri.fromFile(compressed);

        } catch (IOException e) {
            e.printStackTrace();
            compressedUri = uri;
        }
        return compressedUri;
    }
}