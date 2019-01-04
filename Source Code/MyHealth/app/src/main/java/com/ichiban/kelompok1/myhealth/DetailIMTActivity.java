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

public class DetailIMTActivity extends AppCompatActivity {

    private final String TAG = "myTag";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private RecyclerView mRecyclerView;

    private TextView mNilaiIMT;
    private TextView mUraian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_imt);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_imt);
        // Membuat Divider antar item di list
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        new FireBaseIMTHelper().readIMT(new FireBaseIMTHelper.DataStatusIMT() {
            @Override
            public void DataIsLoaded(List<IMT> imts, List<String> keys) {
                new IMTRecyclerView().setConfig(mRecyclerView, DetailIMTActivity.this, imts, keys);
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

        mNilaiIMT = (TextView) findViewById(R.id.IMTdetailNilaiTextView);
        mUraian = (TextView) findViewById(R.id.TextViewUraian);

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
                String mBeratBadan = mUser.getBeratbadan().toString();

                hitungIMT(parseInt(mTinggiBadan), parseInt(mBeratBadan));
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

    public void hitungIMT(double tinggiBadan, double beratBadan){

        tinggiBadan = tinggiBadan/100;

        double hasilHitung = beratBadan/(tinggiBadan*tinggiBadan);

        DecimalFormat df = new DecimalFormat("#.##");
        mNilaiIMT.setText(String.valueOf(df.format(hasilHitung)));
        if (hasilHitung < 18.5) {
            mUraian.setText("Anda termasuk kurus (Underweight)");
        }
        else if (hasilHitung >=18.5 && hasilHitung < 25) {
            mUraian.setText("Anda termasuk Normal (Ideal)");
        }
        else if (hasilHitung >=25 && hasilHitung < 30) {
            mUraian.setText("Anda termasuk Kegemukan (Overweight)");
        }
        else if (hasilHitung >=30 && hasilHitung < 35) {
            mUraian.setText("Anda termasuk Obesitas Tingkat I");
        }
        else if (hasilHitung >=35 && hasilHitung < 40) {
            mUraian.setText("Anda termasuk Obesitas Tingkat II");
        }
        else if (hasilHitung >=40) {
            mUraian.setText("Anda termasuk Obesitas Tingkat III");
        }

    }
}
