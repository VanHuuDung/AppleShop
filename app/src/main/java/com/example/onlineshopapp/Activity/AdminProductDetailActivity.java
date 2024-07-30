package com.example.onlineshopapp.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AdminProductDetailActivity extends AppCompatActivity {

    TextView detailName, detailQuantity, detailPrice, detailDescription, tvCreatedAt;
    RatingBar ratingBar;
    ImageView detailImage;
    CheckBox chkIsFeatured;
    LinearLayout specificationsLayout;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";
    String catId = "";
    HashMap<String, String> specifications;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_admin);
        addControls();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailName.setText(bundle.getString("Name"));
            detailQuantity.setText(String.valueOf(bundle.getInt("Quantity")));
            detailPrice.setText(String.valueOf(bundle.getInt("Price")));
            detailDescription.setText(bundle.getString("Description"));
            tvCreatedAt.setText(bundle.getString("CreatedAt"));
            chkIsFeatured.setChecked(bundle.getBoolean("IsFeatured"));
            ratingBar.setRating((float) bundle.getDouble("Rating"));
            if (specifications != null) {
                displaySpecifications(specifications);
            }
            key = bundle.getString("Id");
            catId = bundle.getString("CatId");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);

            specifications = (HashMap<String, String>) bundle.getSerializable("Specifications");
            if (specifications != null) {
                displaySpecifications(specifications);
            }
        }

        addEvents();
    }

    private void addControls() {
        detailName = findViewById(R.id.tvDetailName);
        detailImage = findViewById(R.id.imgDetailImage);
        detailQuantity = findViewById(R.id.tvDetailQuantity);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailPrice = findViewById(R.id.tvDetailPrice);
        detailDescription = findViewById(R.id.tvDetailDescription);
        specificationsLayout = findViewById(R.id.specificationsLayout);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        chkIsFeatured = findViewById(R.id.chkIsFeatured);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void displaySpecifications(HashMap<String, String> specifications) {
        for (Map.Entry<String, String> entry : specifications.entrySet()) {
            TextView textView = new TextView(this);
            textView.setText(entry.getKey() + ": " + entry.getValue());
            specificationsLayout.addView(textView);
        }
    }

    private void addEvents() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AdminProductDetailActivity.this)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        reference.child(key).removeValue();
                                        Toast.makeText(AdminProductDetailActivity.this, "Xóa sản phẩm thành công.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AdminProductListActivity.class));
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProductDetailActivity.this, UpdateProductActivity.class)
                        .putExtra("Name", detailName.getText().toString())
                        .putExtra("Quantity", Integer.parseInt(detailQuantity.getText().toString()))
                        .putExtra("Price", Integer.parseInt(detailPrice.getText().toString()))
                        .putExtra("Image", imageUrl)
                        .putExtra("Description", detailDescription.getText().toString())
                        .putExtra("Id", key)
                        .putExtra("CatId", catId)
                        .putExtra("CreatedAt", tvCreatedAt.getText().toString())
                        .putExtra("IsFeatured", chkIsFeatured.isChecked())
                        .putExtra("Rating", ratingBar.getRating())
                        .putExtra("Specifications", specifications);
                startActivity(intent);
            }
        });
    }

}