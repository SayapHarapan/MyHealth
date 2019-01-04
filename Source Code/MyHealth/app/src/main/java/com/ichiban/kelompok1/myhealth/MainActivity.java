package com.ichiban.kelompok1.myhealth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "myTag";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mBBIRef;
    private FirebaseAuth.AuthStateListener authListener;

    private TextView mBeratBadanIdeal;
    private TextView mIndeksMasaTubuh;
    private TextView mNamaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setLogo(R.drawable.ic_action_name);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance();
        mUserRef = mDatabase.getReference("users");
        mBBIRef = mDatabase.getReference("bbi");

        mNamaTextView = (TextView) findViewById(R.id.namaTextView);
        mBeratBadanIdeal = (TextView) findViewById(R.id.BBItextView);
        mIndeksMasaTubuh = (TextView) findViewById(R.id.IMTtextView);


        String mUserId = user.getUid();

        readUserData(mUserId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ubahDataMenu :
                startActivity(new Intent(this, EditDataActivity.class));
                return true;
            case R.id.signOutMenu :
                mAuth.signOut();
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void readUserData(final String mUserId){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User mUser = dataSnapshot.getValue(User.class);
                String mNama = mUser.getNama().toString();
                String mTinggiBadan = mUser.getTinggibadan().toString();
                String mBeratBadan = mUser.getBeratbadan().toString();
                String mJenisKelamin = mUser.getJeniskelamin();

                mNamaTextView.setText("Hello, " + mNama);

                hitungBBI(parseInt(mTinggiBadan), mJenisKelamin);
                hitungIMT(parseInt(mTinggiBadan), parseInt(mBeratBadan));
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


    public void hitungBBI(double tinggiBadan, String jenisKelamin){
        double hasilHitung =0;
        hasilHitung = (tinggiBadan-100);
        if (jenisKelamin.trim().equals("Pria")){
            if (tinggiBadan < 160){
            } else {
                hasilHitung = 0.9 * (tinggiBadan-100);
            }
            DecimalFormat df = new DecimalFormat("#.#");
            mBeratBadanIdeal.setText(String.valueOf(df.format(hasilHitung)));
        }
        else if (jenisKelamin.trim().equals("Wanita")) {
            if (tinggiBadan < 150){
                hasilHitung = (tinggiBadan-100);
            } else {
                hasilHitung = 0.9 * (tinggiBadan-100);
            }
            DecimalFormat df = new DecimalFormat("#.#");
            mBeratBadanIdeal.setText(String.valueOf(df.format(hasilHitung)));
        }
    }

    public void hitungIMT(double tinggiBadan, double beratBadan){

        tinggiBadan = tinggiBadan/100;

        DecimalFormat df = new DecimalFormat("#.##");
        double hasilHitung = beratBadan/(tinggiBadan*tinggiBadan);

        mIndeksMasaTubuh.setText(String.valueOf(df.format(hasilHitung)));
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public void detailBBI(View view) {
        startActivity(new Intent(this, DetailBBIActivity.class));
    }

    public void detailIMT(View view) {
        startActivity(new Intent(this, DetailIMTActivity.class));
    }

    public void hitungKalori(View view) {
        startActivity(new Intent(this, HitungKaloriActivity.class));
    }

    public void goToMenuMakan(View view) {
        startActivity(new Intent(this, HitungLemakActivity.class));
    }

    public void goToKadarLemak(View view) {
        startActivity(new Intent(this, HitungLemakActivity.class));
    }
}
