package com.example.blooddonar1;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegWithEmailActivity extends AppCompatActivity implements View.OnClickListener {
    EditText EregNameEt;
    EditText EregEmailEt;
    EditText EregPhoneEt;
    EditText EregPassEt;
    Spinner spinnerDropDownView;
    String[] spinnerValueHoldValue = {"select blood group ","A+", "A-", "AB+", "AB-", "B+", "B-", "O+", "O-"};
    public String spinnerData;
    ProgressBar progressBar;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_with_email);

        EregNameEt = (EditText)findViewById(R.id.ereg_name_et);
        EregEmailEt = (EditText)findViewById(R.id.ereg_emai_et);
        EregPhoneEt = (EditText)findViewById(R.id.ereg_phone_et);
        EregPassEt = (EditText)findViewById(R.id.ereg_password_et);
        progressBar = (ProgressBar) findViewById(R.id.ereg_progress);
        progressBar.setVisibility(View.GONE);
        spinnerDropDownView =(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RegWithEmailActivity.this, R.layout.spinner_item, spinnerValueHoldValue);
        spinnerDropDownView.setAdapter(adapter);

        spinnerDropDownView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                spinnerData = spinnerDropDownView.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.ereg_btn).setOnClickListener(this);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mAuth.getCurrentUser()!= null){
//            Intent intent = new Intent(RegWithEmailActivity.this, HomeActivity.class);
//            startActivity(intent);
//        }
//    }

    private void registerUser(){
        final String name = EregNameEt.getText().toString().trim();
        final String email = EregEmailEt.getText().toString().trim();
        final String phone = EregPhoneEt.getText().toString().trim();
        final String pass = EregPassEt.getText().toString().trim();
        final String sp = spinnerDropDownView.getSelectedItem().toString();

        if (name.isEmpty()){
            EregNameEt.setError("Name is required...");
            EregNameEt.requestFocus();
            return;
        }
        if (email.isEmpty()){
            EregEmailEt.setError("Email is required...");
            EregEmailEt.requestFocus();
            return;
        }
        if (phone.isEmpty()){
            EregPhoneEt.setError("Phone Number is required...");
            EregPhoneEt.requestFocus();
            return;
        }
        if (phone.length() < 10){
            EregPhoneEt.setError("Please Enter a Valid Phone Number");
            EregPhoneEt.requestFocus();
            return;
        }
        if (pass.isEmpty()){
            EregPassEt.setError("Password is required...");
            EregPassEt.requestFocus();
            return;
        }
        if (pass.length()<8){
            EregPassEt.setError("Password is required...");
            EregPassEt.requestFocus();
            return;
        }
        if (sp.length()>4){
            TextView errorText = (TextView)spinnerDropDownView.getSelectedView();
            errorText.setError("Select Blood Group");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Blood Group");//changes the selected item text to this
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            BloodDonar bloodDonar = new BloodDonar(
                                    name,
                                    email,
                                    phone,
                                    sp,
                                    pass
                            );
                            FirebaseDatabase.getInstance().getReference("BloodDonarEmailUsers")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(bloodDonar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegWithEmailActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(RegWithEmailActivity.this, "Error Creating User...", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegWithEmailActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ereg_btn:
                registerUser();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegWithEmailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
