package com.example.onlineshopapp.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Utils.InputValidator;
import com.example.onlineshopapp.Model.Users;
import com.example.onlineshopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PersonalInfoActivity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtEmail, edtAddress;
    Button btnLuu;
    ImageView imgAvatar, imgChangePassword;
    String imageURL, role, key, oldImageURL = "";
    Uri uri;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userRef;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addControls();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        addEvents();
    }

    private void addControls() {
        imgAvatar = findViewById(R.id.imgAvatar);
        imgChangePassword = findViewById(R.id.imgChangePassword);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        btnLuu = findViewById(R.id.btnLuu);
    }

    private void addEvents() {
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users currentUser = snapshot.getValue(Users.class);
                    if (currentUser != null) {
                        edtFullName.setText(currentUser.getFull_name());
                        edtPhone.setText(currentUser.getPhone());
                        edtEmail.setText(currentUser.getEmail());
                        edtAddress.setText(currentUser.getAddress());
                        role = currentUser.getRole();
                        key = user.getUid();
                        if (currentUser.getImage_url() != null && !currentUser.getImage_url().isEmpty()) {
                            Glide.with(PersonalInfoActivity.this).load(currentUser.getImage_url()).into(imgAvatar);
                            oldImageURL = currentUser.getImage_url();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            imgAvatar.setImageURI(uri);
                        } else {
                            Toast.makeText(PersonalInfoActivity.this, "Không có ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        imgChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalInfoActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveData(){

        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (!InputValidator.isValidName(fullName)) {
            edtFullName.setError("Họ và tên không được chứa số");
            dialog.dismiss();
            return;
        }
        if (!InputValidator.isValidPhone(phone)) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            dialog.dismiss();
            return;
        }

        if (uri != null) {
            storageReference = FirebaseStorage.getInstance().getReference().child("Avatars").child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri urlImage = uriTask.getResult();
                    imageURL = urlImage.toString();
                    uploadData(imageURL, dialog, email, fullName, phone, address);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(PersonalInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            uploadData(oldImageURL, dialog, email, fullName, phone, address);
        }
    }

    public void uploadData(String imageUrl, AlertDialog dialog, String email, String fullName, String phone, String address){
        Users user = new Users(email, fullName, phone, address, imageUrl, role);

        FirebaseDatabase.getInstance().getReference("Users").child(key)
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            updateFirebaseUserProfile(fullName, imageUrl);
                            if (uri != null && oldImageURL != null && !oldImageURL.isEmpty()) {
                                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                                reference.delete();
                            }
                            Toast.makeText(PersonalInfoActivity.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(PersonalInfoActivity.this, "Cập nhật thông tin không thành công.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PersonalInfoActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }


    private void updateFirebaseUserProfile(String fullName, String photoUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(Uri.parse(photoUrl))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}