package com.example.Conekto.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Conekto.Models.UsersDetailsModel;
import com.example.Conekto.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView profile_name;
    TextView profile_email;
    CircleImageView profile_image, edt_image;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    StorageReference storageReference;
    private static final int PROFILE_IMG_REQUEST = 1;
    private Uri ImageUri;
    private StorageTask<UploadTask.TaskSnapshot> ImageUpload;
    ProgressDialog custom_progress_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        custom_progress_dialog = new ProgressDialog(ProfileActivity.this);
        custom_progress_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ProfileActivity.this.getColor(android.R.color.white));
        setContentView(R.layout.activity_profile);
        profile_email = findViewById(R.id.profile_email);
        profile_image = findViewById(R.id.img);
        edt_image = findViewById(R.id.edt_image);
        profile_name = findViewById(R.id.profile_name);
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                UsersDetailsModel usersDetailsModel = snapshot.getValue(UsersDetailsModel.class);
                assert usersDetailsModel != null;
                profile_name.setText(usersDetailsModel.getFullname());
                profile_email.setText(usersDetailsModel.getEmail());
                if (usersDetailsModel.getImage().equals("default")) {
                    profile_image.setImageResource(R.drawable.profile);
                } else {
                    Glide.with(getApplicationContext()).load(usersDetailsModel.getImage()).into(profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        edt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageSelection();
            }
        });
    }

    private void ImageSelection() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PROFILE_IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PROFILE_IMG_REQUEST && data != null && data.getData() != null) {
            ImageUri = data.getData();
            if (ImageUpload != null && ImageUpload.isInProgress()) {
                Toast.makeText(this, "Upload in Progress", Toast.LENGTH_SHORT).show();

            } else {
                setImageUpload();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void setImageUpload() {
        custom_progress_dialog.show();
        custom_progress_dialog.setContentView(R.layout.custom_progress_dialog);
        custom_progress_dialog.setCancelable(false);
        if (ImageUri != null) {
            final StorageReference storageRef = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(ImageUri));

            ImageUpload = storageRef.putFile(ImageUri);
            ImageUpload.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        String vUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", vUri);
                        reference.updateChildren(map);
                        custom_progress_dialog.dismiss();
                    } else {
                        custom_progress_dialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed.Some error occured", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    custom_progress_dialog.dismiss();
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
}