package com.example.blooddonar1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText EtLoginPhone;
    EditText EtLoginPassword;
    Button LoginButton;
    TextView TvAccountCreate;

    FirebaseAuth mainAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainAuth = FirebaseAuth.getInstance();

        EtLoginPhone = (EditText)findViewById(R.id.login_ph_num_et);
        EtLoginPassword = (EditText)findViewById(R.id.login_password_et);
        LoginButton = (Button)findViewById(R.id.login_btn);
        TvAccountCreate = (TextView)findViewById(R.id.account_create_tv);

        LoginButton.setOnClickListener(this);

        TvAccountCreate.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mainAuth.getCurrentUser()!= null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    //    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mainAuth.getCurrentUser()!=null){
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.login_btn){
            final String email = EtLoginPhone.getText().toString().trim();
            final String password = EtLoginPassword.getText().toString().trim();

            if (email.isEmpty()){
                EtLoginPhone.setError("Enter Email...");
                EtLoginPhone.requestFocus();
                return;
            }
            if (password.isEmpty()){
                EtLoginPassword.setError("Enter Password...");
                EtLoginPassword.requestFocus();
                return;
            }

            mainAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Enter valid Email or Password....", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (v.getId()==R.id.account_create_tv){
            Intent intent = new Intent(MainActivity.this, RegWithEmailActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
