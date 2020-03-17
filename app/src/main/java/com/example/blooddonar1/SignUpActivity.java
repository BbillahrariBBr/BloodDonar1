package com.example.blooddonar1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText EtSignUpName;
    EditText EtSignUpPhone;
    EditText EtSignUpBloodGroup;
    EditText EtSignUpPassword;
  //  EditText EtSignUpVerificationCode;
    Button EtSignUpBtn;

    FirebaseAuth mAuth;
    String codeSent;

    String myText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        EtSignUpName = (EditText)findViewById(R.id.signup_name_et);
        EtSignUpPhone = (EditText)findViewById(R.id.signup_phone_et);
   //     EtSignUpVerificationCode = (EditText)findViewById(R.id.signup_verification);
        EtSignUpBloodGroup = (EditText) findViewById(R.id.signup_blood_group_et);
        EtSignUpPassword = (EditText)findViewById(R.id.signup_password_et);
        EtSignUpBtn = (Button)findViewById(R.id.signup_btn);

//        List<String> catagories = new ArrayList<String>();
//        catagories.add("Please Select Your Blood Group :  ");
//        catagories.add("A+");
//        catagories.add("A-");
//        catagories.add("B+");
//        catagories.add("B-");
//        catagories.add("O+");
//        catagories.add("O-");
//        catagories.add("AB+");
//        catagories.add("AB-");
//
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, catagories);
//        SpinnerSignUpBloodGroup.setAdapter(dataAdapter);

        EtSignUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.signup_btn){
            String name = EtSignUpName.getText().toString().trim();
            String phone = EtSignUpPhone.getText().toString().trim();
         //   String verificationCode = EtSignUpVerificationCode.getText().toString().trim();
            String blgroup = EtSignUpBloodGroup.getText().toString().trim();
            String password = EtSignUpPassword.getText().toString().trim();

            if (name.isEmpty()){
                EtSignUpName.setError("Name is required...");
                EtSignUpName.requestFocus();
                return;
            }
            if (phone.isEmpty()){
                EtSignUpPhone.setError("Phone Number is required...");
                EtSignUpPhone.requestFocus();
                return;
            }
            if (phone.length() < 10){
                EtSignUpPhone.setError("Please Enter a Valid Phone Number");
                EtSignUpPhone.requestFocus();
                return;
            }
//            if (verificationCode.isEmpty()){
//                EtSignUpVerificationCode.setError("Verification Code is required...");
//                EtSignUpVerificationCode.requestFocus();
//                return;
 //           }
            if (blgroup.isEmpty()){
                EtSignUpBloodGroup.setError("Blood Group is required");
                EtSignUpBloodGroup.requestFocus();
                return;
            }
            if (blgroup.length()>2){
                EtSignUpBloodGroup.setError("Enter like A+, A-, B+ etc... ");
                EtSignUpBloodGroup.requestFocus();
                return;
            }
            if (password.isEmpty()){
                EtSignUpPassword.setError("Password is required...");
                EtSignUpPassword.requestFocus();
                return;
            }

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks

            alertDialogue();
          //  Toast.makeText(SignUpActivity.this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();

        }


    }

    private void alertDialogue() {
        final AlertDialog.Builder verificationCodeDialogue = new AlertDialog.Builder(SignUpActivity.this);
        verificationCodeDialogue.setTitle("Enter Verification Code....");
        final EditText verificationCode = new EditText(SignUpActivity.this);
        verificationCode.setInputType(InputType.TYPE_CLASS_PHONE);
        verificationCodeDialogue.setView(verificationCode);

        verificationCodeDialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myText = verificationCode.getText().toString();
                if (myText.equals(codeSent)){
                    //do nxt activity after verification
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, myText);
                    signInWithPhoneAuthCredential(credential);

                }
                else{
                    Toast.makeText(SignUpActivity.this, "Please enter valid Code", Toast.LENGTH_LONG).show();
                }

            }
        });

        verificationCodeDialogue.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        verificationCodeDialogue.show();

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            //Open new Blood Donar Profile activity
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            }
                        }
                });
    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

}
