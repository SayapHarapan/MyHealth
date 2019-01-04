package com.ichiban.kelompok1.myhealth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

import static java.lang.Integer.parseInt;

public class DetailBBIActivity extends AppCompatActivity {
    private final String TAG = "myTag";
    private RecyclerView mRecyclerView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private TextView mNilaiBBI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bbi);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_bbi);
        // Membuat Divider antar item di list
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        new FireBaseDatabaseHelper().readBBI(new FireBaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<BBI> bbis, List<String> keys) {
                new BBIRecyclerView().setConfig(mRecyclerView, DetailBBIActivity.this, bbis, keys);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("users");

        mNilaiBBI = (TextView) findViewById(R.id.BBIdetailNilaiTextView);

        FirebaseUser user = mAuth.getCurrentUser();
        String mUserId = user.getUid();
        readData(mUserId);
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

    public void readData(final String mUserId){
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object and use the values to update the UI
                User mUser = dataSnapshot.getValue(User.class);
                String mTinggiBadan = mUser.getTinggibadan().toString();
                String mJenisKelamin = mUser.getJeniskelamin().toString();
                hitungBBI(parseInt(mTinggiBadan), mJenisKelamin);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                // ...
            }
        };
        mRef.child(mUserId).addValueEventListener(userListener);
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
            mNilaiBBI.setText(String.valueOf(df.format(hasilHitung)));
        }
        else if (jenisKelamin.trim().equals("Wanita")) {
            if (tinggiBadan < 150){
                hasilHitung = (tinggiBadan-100);
            } else {
                hasilHitung = 0.9 * (tinggiBadan-100);
            }
            DecimalFormat df = new DecimalFormat("#.#");
            mNilaiBBI.setText(String.valueOf(df.format(hasilHitung)));
        }
    }
}
