package com.evanemran.quickfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.evanemran.quickfeed.adapters.PostsAdapter;
import com.evanemran.quickfeed.models.PostData;
import com.evanemran.quickfeed.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {

    RelativeLayout editImageContainer;
    RoundedImageView imageView_profile;
    EditText editText_name, editText_bio;
    Button button_save;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri filePath;

    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editImageContainer = findViewById(R.id.editImageContainer);
        imageView_profile = findViewById(R.id.imageView_profile);
        editText_name = findViewById(R.id.editText_name);
        editText_bio = findViewById(R.id.editText_bio);
        button_save = findViewById(R.id.button_save);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        populateViewsWIthSavedData();

        editImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText_name.getText().toString();
                String bio = editText_bio.getText().toString();
                if (name.equals("") || bio.equals("")){
                    Toast.makeText(EditProfileActivity.this, "Fill up all data", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (filePath!=null){
                    uploadImage(filePath, name, bio);
                }
                else{
                    HashMap user = new HashMap();
                    user.put("userFullName", name);
                    user.put("userBio", bio);
                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(EditProfileActivity.this, "Couldn't update!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    private void populateViewsWIthSavedData() {
        Query dbQuery = databaseReference
                .orderByChild("userId")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbQuery.addListenerForSingleValueEvent(
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            User post = postSnapshot.getValue(User.class);
                            userList.add(post);
                        }

                        editText_name.setText(userList.get(0).getUserFullName());
                        editText_bio.setText(userList.get(0).getUserBio());
                        try {
                            Picasso.get().load(userList.get(0).getUserPhoto()).into(imageView_profile);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                })
        );
    }

    private void uploadImage(Uri filePath, String name, String bio) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Profile Photo..");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "profileImages/"
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

                                            HashMap user = new HashMap();
                                            user.put("userPhoto", uri.toString());
                                            user.put("userFullName", name);
                                            user.put("userBio", bio);
                                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(EditProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Toast.makeText(EditProfileActivity.this, "Couldn't update!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

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
                                    .makeText(EditProfileActivity.this,
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

    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == 100
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                this.getContentResolver(),
                                filePath);
                imageView_profile.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
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
            File compressed = new Compressor(EditProfileActivity.this)
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