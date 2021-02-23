package com.sharon.edusoft.OTP_Receiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sharon.edusoft.LoginActivity;
import com.sharon.edusoft.R;
import com.sharon.edusoft.SetupAccount.SetupAccountImageActivity;
import com.sharon.edusoft.StudDashboard;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity {
   EditText countryCode, phoneNo,otpCode;
   TextView admin;
   Button sendOtp, verifyOtp, resendOtp;
   String userPhoneNumber,verificationId;
   FirebaseAuth mAuth;
   BroadcastReceiver receiver;
   PhoneAuthProvider.ForceResendingToken token;
   PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://edusoft-1b8b7.appspot.com");

        countryCode=findViewById(R.id.cCode);
        phoneNo=findViewById(R.id.phone);
        sendOtp=findViewById(R.id.sendOtp);
        otpCode=findViewById(R.id.otpCode);
        verifyOtp=findViewById(R.id.verifyCode);
        resendOtp=findViewById(R.id.resendOtp);
        admin=findViewById(R.id.admin);
        resendOtp.setEnabled(false);
        new Otp_Receiver().setEditText(otpCode);
        requestSmsPermission();
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait as we verify your phone number.");
        pd.setCancelable(false);
       admin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(OtpVerification.this, LoginActivity.class);
               startActivity(intent);
           }
       });
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryCode.getText().toString().isEmpty()){
                    countryCode.setError("Required");
                    return;
                }
                if (phoneNo.getText().toString().isEmpty()){
                    phoneNo.setError("Phone Number is required");
                }
                pd.show();
                userPhoneNumber="+"+countryCode.getText().toString()+phoneNo.getText().toString();
                verifyphoneNumber(userPhoneNumber);
            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyphoneNumber(userPhoneNumber);
                resendOtp.setEnabled(false);
            }
        });
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpCode.getText().toString().isEmpty()){
                    otpCode.setError("Enter Otp First");
                    return;
                }
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,otpCode.getText().toString());
                authenticateUser(credential);
            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
             authenticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OtpVerification.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId=s;
                token=forceResendingToken;

                countryCode.setVisibility(View.GONE);
                phoneNo.setVisibility(View.GONE);
                sendOtp.setVisibility(View.GONE);
                admin.setVisibility(View.GONE);

                otpCode.setVisibility(View.VISIBLE);
                verifyOtp.setVisibility(View.VISIBLE);
                resendOtp.setVisibility(View.VISIBLE);
                resendOtp.setEnabled(false);
                pd.dismiss();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendOtp.setEnabled(true);
            }
        };
//        private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equalsIgnoreCase("otp")){
//                    final String otpCode=intent.getStringExtra("otpCode");
//                    otpCode.set(otpCode);
//                }
//            }
//        }
    }

    private void requestSmsPermission() {
        String permission= Manifest.permission.RECEIVE_SMS;
        int grant= ContextCompat.checkSelfPermission(this, permission);
        if (grant!= PackageManager.PERMISSION_GRANTED){
            String[] permission_list=new String[1];
            permission_list[0]=permission;
            ActivityCompat.requestPermissions(this,permission_list,1);
        }
    }

    public void verifyphoneNumber(String phoneNum){
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(mAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNum)
                 .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }
    public void authenticateUser(PhoneAuthCredential credential){
     mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                Intent intent=new Intent(OtpVerification.this,SetupAccountImageActivity.class);

                startActivity(intent);
                String login_id = mDatabase.child("users").child(task.getResult().getUser().getUid()).push().getKey();
                String user_id = task.getResult().getUser().getUid();
                try {
                    PackageInfo pInfo = OtpVerification.this.getPackageManager().getPackageInfo(getPackageName(), 0);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


                HashMap<String, Object> mLoginInfoDataMap = new HashMap<>();
                mLoginInfoDataMap.put("login_id", login_id);
                mLoginInfoDataMap.put("user_id", user_id);
                mLoginInfoDataMap.put("timestamp", System.currentTimeMillis());


                mDatabase.child("users").child(user_id).child("login").child(login_id).updateChildren(mLoginInfoDataMap);
            }
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(OtpVerification.this,e.getMessage(), Toast.LENGTH_SHORT).show();
         }
     });
    }
//    @Override
//    public void onResume() {
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
//        super.onResume();
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),StudDashboard.class));
            finish();
        }
    }


}
