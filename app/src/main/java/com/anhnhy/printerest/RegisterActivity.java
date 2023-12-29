package com.anhnhy.printerest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anhnhy.printerest.helper.CheckValidate;
import com.anhnhy.printerest.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText txt_email, txt_name, txt_password, txt_re_password;
    Button btn_register, btn_back;
    protected FirebaseAuth fbAuth;
    private DatabaseReference dbRef;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        fbAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txt_email.getText().toString().trim().toLowerCase();
                String name = txt_name.getText().toString().trim();
                String password = txt_password.getText().toString();
                String re_password = txt_re_password.getText().toString();

                // TODO: Kiểm tra email đã đăng ký trên firebase chưa, nếu có thông báo chọn email khác
                if (CheckValidate.isNone(email) ||
                        CheckValidate.isNone(name) ||
                        CheckValidate.isNone(password) ||
                        CheckValidate.isNone(re_password)) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đủ các trường", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(re_password)) {
                        fbAuth.createUserWithEmailAndPassword(email, password).
                                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        User userRegister = new User(name, email);
                                        UID = fbAuth.getCurrentUser().getUid();
                                        dbRef.child(UID).setValue(userRegister);

                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Đăng ký không thành công", Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        txt_email = findViewById(R.id.txt_email);
        txt_name = findViewById(R.id.txt_name);
        txt_password = findViewById(R.id.txt_password);
        txt_re_password = findViewById(R.id.txt_re_password);
        btn_register = findViewById(R.id.btn_register);
        btn_back = findViewById(R.id.btn_back);
    }
}