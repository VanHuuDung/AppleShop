package com.example.onlineshopapp.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlineshopapp.Activity.ChangePasswordActivity;
import com.example.onlineshopapp.Activity.LoginActivity;
import com.example.onlineshopapp.Activity.AdminProductListActivity;
import com.example.onlineshopapp.Activity.PersonalInfoActivity;
import com.example.onlineshopapp.Model.Users;
import com.example.onlineshopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AccountFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference userRef;
    TextView tvEmail, tvFullName;
    ImageView imgAvatar;
    ProgressBar progressBar;
    String imageURL;
    LinearLayout layoutInfoAndLogin, layoutOrderTracking, layoutPersonalInfo,
            layoutCategoryManagement, layoutProductManagement,
            layoutUserManagement, layoutReceiptManagement,
            layoutOrderManagement, layoutChangePassword,
            layoutLogout, layoutDeleteAccount, managementLayout, parentLayout;
    public AccountFragment() {
        // Required empty public constructor
    }
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvEmail = view.findViewById(R.id.tvEmail);
        tvFullName = view.findViewById(R.id.tvFullName);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        progressBar = view.findViewById(R.id.progressBar);
        parentLayout = view.findViewById(R.id.parentLayout);
        managementLayout = view.findViewById(R.id.managementLayout);
        layoutInfoAndLogin = view.findViewById(R.id.layoutInfoAndLogin);
        layoutOrderTracking = view.findViewById(R.id.layoutOrderTracking);
        layoutPersonalInfo = view.findViewById(R.id.layoutPersonalInfo);
        layoutCategoryManagement = view.findViewById(R.id.layoutCategoryManagement);
        layoutProductManagement = view.findViewById(R.id.layoutProductManagement);
        layoutUserManagement = view.findViewById(R.id.layoutUserManagement);
        layoutReceiptManagement = view.findViewById(R.id.layoutReceiptManagement);
        layoutOrderManagement = view.findViewById(R.id.layoutOrderManagement);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        layoutDeleteAccount = view.findViewById(R.id.layoutDeleteAccount);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Ẩn chức năng nếu người dùng chưa đăng nhập
        if (user == null) {
            tvEmail.setText("Đăng nhập");
            tvFullName.setText("Khách");
            parentLayout.setVisibility(View.GONE);
        } else {
            parentLayout.setVisibility(View.VISIBLE);
            refreshUserData();
        }

        // Mở Activity Login hoặc xem thông tin cá nhân
        layoutInfoAndLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (user == null) {
                    intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent = new Intent(getContext(), PersonalInfoActivity.class);
                }
                startActivity(intent);
            }
        });

        // Đăng xuất
        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

//        layoutOrderTracking.setOnClickListener(v -> startActivity(new Intent(getActivity(), OrderTrackingActivity.class)));

        // Xem thông tin cá nhân
        layoutPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

        // Mở Activity đổi mật khẩu
        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

//        layoutCategoryManagement.setOnClickListener(v -> startActivity(new Intent(getActivity(), CategoryManagementActivity.class)));

        // Mở AdminProductListActivity
        layoutProductManagement.setOnClickListener(v -> startActivity(new Intent(getActivity(), AdminProductListActivity.class)));

//        layoutUserManagement.setOnClickListener(v -> startActivity(new Intent(getActivity(), UserManagementActivity.class)));

        // Gọi showDeleteAccountDialog()
        layoutDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAccountDialog();
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserData();
    }

    // Làm mới dữ liệu user ở fragment account
    private void refreshUserData() {
        if (user != null) {
            userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Users currentUser = snapshot.getValue(Users.class);
                        if (currentUser != null) {
                            tvFullName.setText(currentUser.getFull_name());
                            tvEmail.setText(currentUser.getEmail());
                            Glide.with(getContext()).load(currentUser.getImage_url()).into(imgAvatar);
                            imageURL = currentUser.getImage_url();
                            if (currentUser.getRole() != null && Objects.equals(currentUser.getRole(), "admin")) {
                                managementLayout.setVisibility(View.VISIBLE);
                            } else {
                                managementLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }

    // Mở hộp thoại xóa tài khoản
    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận xóa tài khoản");
        builder.setMessage("Xóa tài khoản sẽ xóa toàn bộ dữ liệu về đơn hàng và các dữ liệu khác. Bạn có chắc chắn muốn xóa không?");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Nhập lại mật khẩu");
        builder.setView(input);

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    reauthenticateAndDelete(password);
                }
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Xóa tài khoản trên Firebase Authentication
    private void reauthenticateAndDelete(String password) {
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            deleteUserAccount();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Xác thực thất bại. Vui lòng kiểm tra lại mật khẩu.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Xóa ảnh đại diện từ Firebase Storage và xóa user trên Realtime Database
    private void deleteUserAccount() {
        if (!imageURL.isEmpty()) {
            StorageReference avatarRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
            avatarRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Không thể xóa ảnh đại diện. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        userRef.child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Tài khoản đã được xóa thành công", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Không thể xóa tài khoản. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Không thể xóa dữ liệu người dùng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}