package com.ichiban.kelompok1.myhealth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;

import static java.lang.Double.parseDouble;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "myTag";
    private String mGender = "Gender";
    private EditText mEmailEditText;
    private EditText mPassWordEditText;
    private EditText mPassWord1EditText;
    private EditText mNamaEditText;
    private EditText mBeratBadan;
    private EditText mTinggiBadan;
    private EditText mLingkarPerut;
    private EditText mTanggalLahir;
    private Button mRegisterButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mBBIRef;
    private DatabaseReference mIMTRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("users");
        mBBIRef = mDatabase.getReference("bbi");
        mIMTRef = mDatabase.getReference("imt");

        mEmailEditText = (EditText) findViewById(R.id.fillemailText);
        mPassWordEditText = (EditText) findViewById(R.id.fillpasswordText);
        mPassWord1EditText = (EditText) findViewById(R.id.fillpassword1Text);
        mNamaEditText = (EditText) findViewById(R.id.fillnamaText);
        mBeratBadan = (EditText) findViewById(R.id.fillberatbadanText);
        mTinggiBadan = (EditText) findViewById(R.id.filltinggibadanText);
        mLingkarPerut = (EditText) findViewById(R.id.filllingkarperutText);
        mTanggalLahir = (EditText) findViewById(R.id.tgllahirText);

        mRegisterButton = (Button) findViewById(R.id.registerButton);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        mTanggalLahir.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String mDay = "";
                String mMonth = "";
                if (day <10){
                    mDay = "0" + day;
                } else {
                    mDay = String.valueOf(day);
                }
                if (month < 10) {
                    mMonth = "0" + month;
                } else {
                    mMonth = String.valueOf(month);
                }
                String date =  mDay + "-" + mMonth + "-" + year;
                mTanggalLahir.setText( date );
            }
        };

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmpty()) {
                    return;
                } else {
                    if(isEqual()){
                        inProgress(true);
                        mAuth.createUserWithEmailAndPassword(mEmailEditText.getText().toString(),
                                mPassWordEditText.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String key = user.getUid();
                                        String nama = mNamaEditText.getText().toString();
                                        String tinggibadan = mTinggiBadan.getText().toString();
                                        String beratbadan = mBeratBadan.getText().toString();
                                        String lingkarperut = mLingkarPerut.getText().toString();
                                        String tanggallahir = mTanggalLahir.getText().toString();
                                        String jeniskelamin = mGender;
                                        User userBaru = new User(nama, beratbadan, tinggibadan, lingkarperut, tanggallahir, jeniskelamin, "0", "0");
                                        hitungBBI(key, parseDouble(tinggibadan), jeniskelamin);
                                        hitungIMT(key, parseDouble(tinggibadan),parseDouble(beratbadan));
                                        mRef.child(key).setValue(userBaru);


                                        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                inProgress(false);
                                Toast.makeText(RegisterActivity.this,"Registration failed!"+e.getMessage(),Toast.LENGTH_LONG);
                                mEmailEditText.setError("Email sudah digunakan");
                            }
                        });
                    } else{
                        mPassWord1EditText.setError("Password tidak sama");
                    }
                }
            }
        });
    }

    private void inProgress(boolean x){
        if (x) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRegisterButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRegisterButton.setEnabled(true);
        }
    }

    private boolean isEqual(){
        if(TextUtils.equals(mPassWordEditText.getText().toString(),mPassWord1EditText.getText().toString())){
            return true;
        }
        return false;
    }
    private boolean isEmpty() {
        if (TextUtils.isEmpty(mEmailEditText.getText().toString())){
            mEmailEditText.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mPassWordEditText.getText().toString())){
            mPassWordEditText.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mPassWord1EditText.getText().toString())){
            mPassWord1EditText.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mNamaEditText.getText().toString())){
            mNamaEditText.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mBeratBadan.getText().toString())){
            mBeratBadan.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mTinggiBadan.getText().toString())){
            mTinggiBadan.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mLingkarPerut.getText().toString())){
            mLingkarPerut.setError("Required!!");
            return true;
        }
        if (TextUtils.isEmpty(mTanggalLahir.getText().toString())){
            mTanggalLahir.setError("Required!!");
            return true;
        }
        if (mGender.equals("Gender")){
            return  true;
        }
        return false;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.genderPriaButton:
                if (checked)
                    mGender = "Pria";
                    break;
            case R.id.genderWanitaButton:
                if (checked)
                    mGender = "Wanita";
                    break;
        }
    }

    public void hitungBBI(String userID, double tinggiBadan, String jenisKelamin){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        String mDay = "";
        String mMonth = "";
        if (day <10){
            mDay = "0" + day;
        } else {
            mDay = String.valueOf(day);
        }
        if (month < 10) {
            mMonth = "0" + month;
        } else {
            mMonth = String.valueOf(month);
        }
        String date =  mDay + "-" + mMonth + "-" + year;

        double hasilHitung =0;
        hasilHitung = (tinggiBadan-100);
        if (jenisKelamin.trim().equals("Pria")){
            if (tinggiBadan < 160){
            } else {
                hasilHitung = 0.9 * (tinggiBadan-100);
            }
            DecimalFormat df = new DecimalFormat("#.#");
            String key = mBBIRef.push().getKey();
            BBI mbbi = new BBI(String.valueOf(df.format(hasilHitung)), date);
            mBBIRef.child(userID).child(key).setValue(mbbi);
        }
        else if (jenisKelamin.trim().equals("Wanita")) {
            if (tinggiBadan < 150){
                hasilHitung = (tinggiBadan-100);
            } else {
                hasilHitung = 0.9 * (tinggiBadan-100);
            }
            DecimalFormat df = new DecimalFormat("#.#");
            String key = mBBIRef.push().getKey();
            BBI mbbi = new BBI(String.valueOf(df.format(hasilHitung)), date);
            mBBIRef.child(userID).child(key).setValue(mbbi);
        }

    }

    public void hitungIMT(String userID, double tinggiBadan, double beratBadan){

        tinggiBadan = tinggiBadan/100;

        DecimalFormat df = new DecimalFormat("#.##");
        double hasilHitung = beratBadan/(tinggiBadan*tinggiBadan);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        String mDay = "";
        String mMonth = "";
        if (day <10){
            mDay = "0" + day;
        } else {
            mDay = String.valueOf(day);
        }
        if (month < 10) {
            mMonth = "0" + month;
        } else {
            mMonth = String.valueOf(month);
        }
        String date =  mDay + "-" + mMonth + "-" + year;

        String key = mBBIRef.push().getKey();
        IMT imt = new IMT(String.valueOf(df.format(hasilHitung)), date);
        mIMTRef.child(userID).child(key).setValue(imt);
    }
}
