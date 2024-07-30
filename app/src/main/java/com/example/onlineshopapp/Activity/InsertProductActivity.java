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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onlineshopapp.Adapter.CategorySpinnerAdapter;
import com.example.onlineshopapp.Model.Categories;
import com.example.onlineshopapp.Model.Products;
import com.example.onlineshopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class InsertProductActivity extends AppCompatActivity {

    ImageView imgUpload;
    Button btnSave, btnAddSpec;
    EditText edtProName, edtProQuantity, edtProPrice, edtProDesc;
    CheckBox chkIsFeatured;
    Spinner spnCategories;
    String imageURL;
    Uri uri;
    private LinearLayout layoutTechnicalSpecs;
    ArrayList<Categories> categoriesArrayList;
    CategorySpinnerAdapter categorySpinnerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);
        addControls();
        loadCategories();
        addEvents();
    }

    private void addControls() {
        imgUpload = findViewById(R.id.imgUpload);
        btnAddSpec = findViewById(R.id.btnAddSpec);
        btnSave = findViewById(R.id.btnSave);
        edtProName = findViewById(R.id.edtProName);
        edtProQuantity = findViewById(R.id.edtProQuantity);
        edtProPrice = findViewById(R.id.edtProPrice);
        edtProDesc = findViewById(R.id.edtProDesc);
        chkIsFeatured = findViewById(R.id.chkIsFeatured);
        spnCategories = findViewById(R.id.spnCategories);
        layoutTechnicalSpecs = findViewById(R.id.layoutTechnicalSpecs);
    }

    private void loadCategories() {
        categoriesArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Categories");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Categories category = dataSnapshot.getValue(Categories.class);
                    category.setId(dataSnapshot.getKey());
                    categoriesArrayList.add(category);
                }
                categorySpinnerAdapter = new CategorySpinnerAdapter(InsertProductActivity.this, categoriesArrayList);
                spnCategories.setAdapter(categorySpinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InsertProductActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvents() {
        btnAddSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTechnicalSpecField();
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
                            imgUpload.setImageURI(uri);
                        } else {
                            Toast.makeText(InsertProductActivity.this, "Không có ảnh nào được chọn", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    public void saveData(){
        if (uri == null) {
            Toast.makeText(InsertProductActivity.this, "Vui lòng chọn một ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (edtProName.getText().toString().isEmpty()) {
            edtProName.setError("Tên sản phẩm không được bỏ trống.");
            return;
        }
        if (edtProQuantity.getText().toString().isEmpty()) {
            edtProQuantity.setError("Số lượng sản phẩm không được bỏ trống.");
            return;
        }
        if (edtProPrice.getText().toString().isEmpty()) {
            edtProPrice.setError("Giá sản phẩm không được bỏ trống.");
            return;
        }
        if (edtProDesc.getText().toString().isEmpty()) {
            edtProDesc.setError("Mô tả không được bỏ trống.");
            return;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Products")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(InsertProductActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(InsertProductActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadData(){
        String name = edtProName.getText().toString().trim();
        int quantity = Integer.parseInt(edtProQuantity.getText().toString().trim());
        int price = Integer.parseInt(edtProPrice.getText().toString().trim());
        String description = edtProDesc.getText().toString().trim();
        Boolean isFeatured = chkIsFeatured.isChecked();

        Date currentDate = Calendar.getInstance().getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("vi", "VN"));

        String formattedDate = sdf.format(currentDate);

        // Get selected category
        Categories selectedCategory = (Categories) spnCategories.getSelectedItem();
        String categoryId = selectedCategory.getId();

        HashMap<String, String> technicalSpecs = new HashMap<>();
        for (int i = 0; i < layoutTechnicalSpecs.getChildCount(); i++) {
            LinearLayout specLayout = (LinearLayout) layoutTechnicalSpecs.getChildAt(i);
            EditText editTextKey = (EditText) specLayout.getChildAt(0);
            EditText editTextValue = (EditText) specLayout.getChildAt(1);

            String key = editTextKey.getText().toString().trim();
            String value = editTextValue.getText().toString().trim();

            if (!key.isEmpty() && !value.isEmpty()) {
                technicalSpecs.put(key, value);
            }
        }

        Products product = new Products(true, categoryId, formattedDate, description, imageURL, isFeatured, name, price, quantity, 0, technicalSpecs);

        FirebaseDatabase.getInstance().getReference("Products").push()
                .setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(InsertProductActivity.this, "Thêm sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                            clearForm();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InsertProductActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearForm() {
        edtProName.setText("");
        edtProQuantity.setText("");
        edtProPrice.setText("");
        edtProDesc.setText("");
        chkIsFeatured.setChecked(false);
        layoutTechnicalSpecs.removeAllViews();
    }

    private void addTechnicalSpecField() {
        // Create a new horizontal LinearLayout to hold the specification key, value, and delete button
        LinearLayout specLayout = new LinearLayout(this);
        specLayout.setOrientation(LinearLayout.HORIZONTAL);
        specLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText editTextKey = new EditText(this);
        editTextKey.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));
        editTextKey.setHint("Tên thông số");
        specLayout.addView(editTextKey);

        EditText editTextValue = new EditText(this);
        editTextValue.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));
        editTextValue.setHint("Giá trị");
        specLayout.addView(editTextValue);

        ImageButton btnDeleteSpec = new ImageButton(this);
        btnDeleteSpec.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        btnDeleteSpec.setImageResource(R.drawable.outline_delete_24);
        btnDeleteSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutTechnicalSpecs.removeView(specLayout);
            }
        });
        specLayout.addView(btnDeleteSpec);

        // Add the specLayout to the parent layout
        layoutTechnicalSpecs.addView(specLayout);
    }

}