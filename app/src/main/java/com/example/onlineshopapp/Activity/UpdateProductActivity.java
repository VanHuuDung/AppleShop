package com.example.onlineshopapp.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {

    ImageView imgUpload;
    Button btnSave, btnAddSpec;
    EditText edtProName, edtProQuantity, edtProPrice, edtProDesc;
    CheckBox chkIsFeatured;
    Spinner spnCategories;
    String key, catId, oldImageURL, imageURL, createdAt;
    Float rating;
    Uri uri;
    private LinearLayout layoutTechnicalSpecs;
    ArrayList<Categories> categoriesArrayList;
    CategorySpinnerAdapter categorySpinnerAdapter;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        addControls();
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
                            Toast.makeText(UpdateProductActivity.this, "Không có ảnh nào được chọn.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateProductActivity.this).load(bundle.getString("Image")).into(imgUpload);
            edtProName.setText(bundle.getString("Name"));
            edtProQuantity.setText(String.valueOf(bundle.getInt("Quantity")));
            edtProPrice.setText(String.valueOf(bundle.getInt("Price")));
            edtProDesc.setText(bundle.getString("Description"));
            chkIsFeatured.setChecked(bundle.getBoolean("IsFeatured"));
            rating = bundle.getFloat("Rating");
            key = bundle.getString("Id");
            catId = bundle.getString("CatId");
            createdAt = bundle.getString("CreatedAt");
            oldImageURL = bundle.getString("Image");

            HashMap<String, String> specifications = (HashMap<String, String>) bundle.getSerializable("Specifications");
            if (specifications != null) {
                displaySpecifications(specifications);
            }
        }
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
                categorySpinnerAdapter = new CategorySpinnerAdapter(UpdateProductActivity.this, categoriesArrayList);
                spnCategories.setAdapter(categorySpinnerAdapter);

                if (catId != null && !catId.isEmpty()) {
                    for (int i = 0; i < categoriesArrayList.size(); i++) {
                        if (categoriesArrayList.get(i).getId().equals(catId)) {
                            spnCategories.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProductActivity.this, "Failed to load categories.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEvents() {
        btnAddSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTechnicalSpecField("", "");
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
                            Toast.makeText(UpdateProductActivity.this, "Không có ảnh nào được chọn", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(UpdateProductActivity.this, AdminProductListActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveData(){
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

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProductActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        if (uri != null) {
            storageReference = FirebaseStorage.getInstance().getReference().child("Products").child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri urlImage = uriTask.getResult();
                    imageURL = urlImage.toString();
                    uploadData(imageURL, dialog);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(UpdateProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            uploadData(oldImageURL, dialog);
        }
    }

    public void uploadData(String imageUrl, AlertDialog dialog){
        String name = edtProName.getText().toString().trim();
        int quantity = Integer.parseInt(edtProQuantity.getText().toString().trim());
        int price = Integer.parseInt(edtProPrice.getText().toString().trim());
        String description = edtProDesc.getText().toString().trim();
        Boolean isFeatured = chkIsFeatured.isChecked();

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

        Products product = new Products(true, categoryId, createdAt, description, imageUrl, isFeatured, name, price, quantity, rating, technicalSpecs);

        FirebaseDatabase.getInstance().getReference("Products").child(key)
                .setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (uri != null) {
                                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                                reference.delete();
                            }
                            Toast.makeText(UpdateProductActivity.this, "Cập nhật sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(UpdateProductActivity.this, "Cập nhật sản phẩm không thành công.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(UpdateProductActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displaySpecifications(HashMap<String, String> specifications) {
        for (Map.Entry<String, String> entry : specifications.entrySet()) {
            addTechnicalSpecField(entry.getKey(), entry.getValue());
        }
    }

    private void addTechnicalSpecField(String key, String value) {
        LinearLayout specLayout = new LinearLayout(this);
        specLayout.setOrientation(LinearLayout.HORIZONTAL);

        EditText editTextKey = new EditText(this);
        editTextKey.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));
        editTextKey.setText(key);
        editTextKey.setHint("Tên thông số");
        specLayout.addView(editTextKey);

        EditText editTextValue = new EditText(this);
        editTextValue.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        ));
        editTextValue.setText(value);
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

        layoutTechnicalSpecs.addView(specLayout);
    }

}