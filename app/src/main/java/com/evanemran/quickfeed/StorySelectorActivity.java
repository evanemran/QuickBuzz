package com.evanemran.quickfeed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.evanemran.quickfeed.models.StoryData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class StorySelectorActivity extends AppCompatActivity {

    ImageView imageView_story_preview;
    FloatingActionButton button_story_share;
    Uri filePath;
    StoryData storyData = new StoryData();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseStorage storage;
    StorageReference storageReference;

    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_selector);

        imageView_story_preview = findViewById(R.id.imageView_story_preview);
        button_story_share = findViewById(R.id.button_story_share);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("stories");

        SelectImage();

        button_story_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filePath!=null){
                    uploadImage(filePath);
                }

            }
        });


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
                            "stories/"
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
                                            imageUrl = uri.toString();
                                            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                                            Date date = new Date();
                                            storyData.setStoryTime(format.format(date));
                                            storyData.setStoryOwner(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                            String key = databaseReference.push().getKey();
                                            storyData.setStoryId(key);
                                            storyData.setStoryImageUrl(imageUrl);
                                            databaseReference.child(key).setValue(storyData);
                                            progressDialog.dismiss();
                                            Toast.makeText(StorySelectorActivity.this,
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
                                    .makeText(StorySelectorActivity.this,
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
                imageView_story_preview.setImageBitmap(bitmap);
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
            File compressed = new Compressor(StorySelectorActivity.this)
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