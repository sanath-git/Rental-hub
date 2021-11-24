package com.example.tourguide2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {
    private TextInputEditText fullName,email,phoneNumber;
    private Button saveButton;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    private CircleImageView profileImage;
    private ImageView backIconButton;
    private ProgressBar progressBar,profilePicProgressBar;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        reference = FirebaseDatabase.getInstance().getReference("User");
        Log.i("User reference",reference.toString());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("current user id",currentUser.getUid());
        Log.i("current user email",currentUser.getEmail());
        Log.i("current user name",currentUser.getDisplayName());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        fullName = findViewById(R.id.profileFullName);
        email = findViewById(R.id.profileEmail);
        phoneNumber = findViewById(R.id.profilePhoneNumber);
        saveButton = findViewById(R.id.saveProfileButton);
        profileImage = findViewById(R.id.profilePic);
        backIconButton = findViewById(R.id.backButtonIconProfile);
        progressBar = findViewById(R.id.progressBarProfile);
        profilePicProgressBar = findViewById(R.id.profilePicProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        profilePicProgressBar.setVisibility(View.VISIBLE);
        getUserData();
        getProfileImage();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                checkPhoneNumber();
                Toast.makeText(profileActivity.this,"Profile Updated",Toast.LENGTH_LONG).show();
            }

        });
        backIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profileActivity.this,homeActivity.class));
            }
        });

    }

    private void getProfileImage() {
        storageReference.child("user/"+currentUser.getUid().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null)
                {
                    Log.i("download Url",uri.toString());
                    Picasso.get().load(uri.toString()).into(profileImage);
                }
                profilePicProgressBar.setVisibility(View.INVISIBLE);

            }
        });

    }


    private void setTextViews(String sEmail, String sFullName, String sPhoneNumber) {
        fullName.setText(sFullName);
        email.setText(sEmail);
        if (sPhoneNumber!="")
        {
            phoneNumber.setText(sPhoneNumber);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void checkPhoneNumber() {
        String sPhoneNumber;
        sPhoneNumber = String.valueOf(phoneNumber.getText());
        if((sPhoneNumber.length() == 10) && (sPhoneNumber.charAt(0) >= 7))
        {
            updateUserDB(sPhoneNumber);
        }
        else {
            phoneNumber.setError("Enter a valid Phone Number");
            phoneNumber.requestFocus();
            return;
        }
    }

    private void updateUserDB(String sPhoneNumber) {
        reference.child(currentUser.getUid()).child("Phone Number").setValue(sPhoneNumber);
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void getUserData()
    {
        Log.i("on get user data","working");
        reference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email, fullName,phoneNumber;
                phoneNumber = "";
                Log.i("on data change","working");
                email = snapshot.child("mEmail").getValue().toString();
                fullName = snapshot.child("mFullName").getValue().toString();
                if(snapshot.child("Phone Number").getValue() != null)
                {
                    phoneNumber = snapshot.child("Phone Number").getValue().toString();
                }
                setTextViews(email,fullName,phoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        / checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }

    private void uploadImage()
    {
        if (filePath!=null)
        {
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("user/"+currentUser.getUid().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast
                            .makeText(profileActivity.this,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_LONG)
                            .show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.child(currentUser.getUid()).child("Profile Image").setValue(uri.toString());
                            getProfileImage();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast
                            .makeText(profileActivity.this,
                                    "Failed " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress
                            = (100.0
                            * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded "
                                    + (int)progress + "%");
                }
            });
        }
    }
}