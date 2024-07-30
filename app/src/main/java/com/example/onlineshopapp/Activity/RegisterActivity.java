package com.example.onlineshopapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineshopapp.Utils.InputValidator;
import com.example.onlineshopapp.Model.Users;
import com.example.onlineshopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtPassword;
    Button btnRegister;
    ProgressBar progressBar;
    TextView textView;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        addControls();
        addEvents();
    }

    private void addControls() {
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.tvLoginRedirect);
    }

    private void addEvents() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String fullName = edtFullName.getText().toString().trim();

                if (fullName.isEmpty()) {
                    edtFullName.setError("Họ và tên không được để trống.");
                    return;
                }
                if (email.isEmpty()) {
                    edtEmail.setError("Email không được để trống.");
                    return;
                }
                if (password.isEmpty()) {
                    edtPassword.setError("Password không được để trống.");
                    return;
                }

                if (!InputValidator.isValidName(fullName)) {
                    edtFullName.setError("Họ và tên không được chứa số.");
                    return;
                }
                if (!InputValidator.isValidEmail(email)) {
                    edtEmail.setError("Email không hợp lệ.");
                    return;
                }
                if (!InputValidator.isValidPassword(password)) {
                    progressBar.setVisibility(View.GONE);
                    edtPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Registration success
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        Users newUser = new Users(email, fullName, "", "", "", "customer");
                                        usersRef.child(userId).setValue(newUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
