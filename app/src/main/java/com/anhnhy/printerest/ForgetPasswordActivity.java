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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText txt_email;
    Button btn_updatePassword, btn_back;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        initView();

        btn_updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txt_email.getText().toString().trim().toLowerCase();
                fbAuth = FirebaseAuth.getInstance();
                if (CheckValidate.isNone(email)) {
                    Toast.makeText(ForgetPasswordActivity.this, "Vui lòng nhập lại email", Toast.LENGTH_SHORT).show();
                } else {
                    fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "Kiểm tra email để cập nhập lại mật khẩu.", Toast.LENGTH_LONG).show();
                            } else {

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        txt_email = findViewById(R.id.txt_email);
        btn_updatePassword = findViewById(R.id.btn_updatePassword);
        btn_back = findViewById(R.id.btn_back);
    }
}