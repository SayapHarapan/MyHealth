package com.ichiban.kelompok1.myhealth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class EditDataActivity extends AppCompatActivity {
    private final String TAG = "myTag";
    private String mGender = "Gender";
    private EditText mBeratBadanEditText;
    private EditText mTinggiBadanEditText;
    private EditText mLingkarPerutEditText;
    private TextView mResetSucces;
    private Button mSimpanButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mBBIRef;
    private DatabaseReference mIMTRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUserRef = mDatabase.getReference("users");
        mBBIRef = mDatabase.getReference("bbi");
        mIMTRef = mDatabase.getReference("imt");

        mResetSucces = (TextView) findViewById(R.id.resetPasswordSucces);

        mBeratBadanEditText = (EditText) findViewById(R.id.ubahDataBBeditText);
        mTinggiBadanEditText = (EditText) findViewById(R.id.ubahDataTTeditText);
        mLingkarPerutEditText = (EditText) findViewById(R.id.ubahDataLPeditText);

        mSimpanButton = (Button) findViewById(R.id.simpanDataButton);

        FirebaseUser user = mAuth.getCurrentUser();
        String mUserId = user.getUid();

        readUserData(mUserId);

    }

    public void readUserData(final String mUserId){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User mUser = dataSnapshot.getValue(User.class);
                String mTinggiBadan = mUser.getTinggibadan().toString();
                String mBeratBadan = mUser.getBeratbadan().toString();
                String mJenisKelamin = mUser.getJeniskelamin().toString();
                String mLingkarPerut = mUser.getLingkarperut();
                mGender = mJenisKelamin;

                mBeratBadanEditText.setText(mBeratBadan);
                mTinggiBadanEditText.setText(mTinggiBadan);
                mLingkarPerutEditText.setText(mLingkarPerut);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                // ...
            }
        };
        mUserRef.child(mUserId).addValueEventListener(userListener);
    }

    public void simpanData(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        String mUserId = user.getUid();

        String mTinggiBadan = mTinggiBadanEditText.getText().toString();
        String mBeratBadan = mBeratBadanEditText.getText().toString();
        String mLingkarPerut = mLingkarPerutEditText.getText().toString();

        mUserRef.child(mUserId).child("beratbadan").setValue(mBeratBadan);
        mUserRef.child(mUserId).child("tinggibadan").setValue(mTinggiBadan);
        mUserRef.child(mUserId).child("lingkarperut").setValue(mLingkarPerut);

        hitungBBI(mUserId, parseDouble(mTinggiBadan), mGender);
        hitungIMT(mUserId, parseDouble(mTinggiBadan),parseDouble(mBeratBadan));

        startActivity(new Intent(this, MainActivity.class));
        finish();
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

    public void resetPassword(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        String emailAddress = user.getEmail().toString();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mResetSucces.setText("Reset password email is sent!");
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}

