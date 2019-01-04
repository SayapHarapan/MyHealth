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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class HitungKaloriActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mSpinnerLabel = "";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mKaloriRef;
    private RecyclerView mRecyclerView;

    private double mUsia;
    private String mTinggiBadan = "";
    private String mBeratBadan = "";
    private String mJenisKelamin = "";
    private double mNilaiAktif;

    private TextView mKeteranganLevelKeaktifan;
    private TextView mNilaiKalori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitung_kalori);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_kalori);
        // Membuat Divider antar item di list
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        new FireBaseKaloriHelper().readKalori(new FireBaseKaloriHelper.DataStatusKalori() {
            @Override
            public void DataIsLoaded(List<Kalori> kaloris, List<String> keys) {
                new KaloriRecyclerView().setConfig(mRecyclerView, HitungKaloriActivity.this, kaloris, keys);
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
        mKaloriRef = mDatabase.getReference("kalori");

        mKeteranganLevelKeaktifan = (TextView) findViewById(R.id.LevelAktifKetTextView);
        mNilaiKalori = (TextView) findViewById(R.id.NilaiKalTextView);

        // Create the spinner.
        Spinner spinner = (Spinner) findViewById(R.id.label_spinner);
        if (spinner != null) {
            spinner.setOnItemSelectedListener(this);
        }
        // Create ArrayAdapter using the string array and default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.labels_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        if (spinner != null) {
            spinner.setAdapter(adapter);
        }

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
                String mNama = mUser.getNama().toString();
                mTinggiBadan = mUser.getTinggibadan().toString();
                mBeratBadan = mUser.getBeratbadan().toString();
                mJenisKelamin = mUser.getJeniskelamin().toString();
                String mTanggalLahir = mUser.getTanggallahir().toString();
                String mKalori = mUser.getKalori().toString();
                mNilaiKalori.setText(mKalori);

                mUsia = hitungUsia(mTanggalLahir);
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

    public double hitungUsia (String tanggal){
        Calendar now=  Calendar.getInstance();
        Calendar tanggallahir = Calendar.getInstance();

        String[] parts = tanggal.split("-");
        String mDay = parts[0];
        String mMonth = parts[1];
        String mYear = parts[2];

        tanggallahir.set(parseInt(mYear), parseInt(mMonth), parseInt(mDay));
        int years = now.get(Calendar.YEAR) - tanggallahir.get(Calendar.YEAR);

        return years;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mSpinnerLabel = adapterView.getItemAtPosition(i).toString();
        switch (mSpinnerLabel) {
            case "Tidak Aktif" :
                mKeteranganLevelKeaktifan.setText(R.string.tidakaktif);
                mNilaiAktif = 1.200;
                break;
            case "Aktivitas Ringan" :
                mKeteranganLevelKeaktifan.setText(R.string.aktivitasringan);
                mNilaiAktif = 1.375;
                break;
            case "Aktivitas Sedang" :
                mKeteranganLevelKeaktifan.setText(R.string.aktivitassedang);
                mNilaiAktif = 1.550;
                break;
            case "Aktivitas Berat" :
                mKeteranganLevelKeaktifan.setText(R.string.aktivitasberat);
                mNilaiAktif = 1.725;
                break;
            case "Aktivitas Sangat Berat" :
                mKeteranganLevelKeaktifan.setText(R.string.aktivitassangatberat);
                mNilaiAktif = 1.900;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "onNothingSelected: ");
    }

    public void hitungKebKalori(View view) {
        double BB = parseDouble(mBeratBadan);
        double TB = parseDouble(mTinggiBadan);
        double usia = mUsia;
        FirebaseUser user = mAuth.getCurrentUser();
        String mUserId = user.getUid();

        DecimalFormat df = new DecimalFormat("#.##");

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

        switch (mJenisKelamin){
            case "Pria" :
                double BMR = 66.473+(13.7516*BB)+(5.0033*TB)-(6.7550*usia);
                double Kalori = BMR * mNilaiAktif;
                mNilaiKalori.setText(String.valueOf(df.format(Kalori)));
                mRef.child(mUserId).child("kalori").setValue(String.valueOf(df.format(Kalori)));
                String key = mKaloriRef.push().getKey();
                Kalori kalori = new Kalori(String.valueOf(df.format(Kalori)), date);
                mKaloriRef.child(mUserId).child(key).setValue(kalori);
                break;
            case "Wanita" :
                double BMR1 = 655.0955+(9.5634*BB)+(1.8496*TB)-(4.6756*usia);
                double Kalori1 = BMR1 * mNilaiAktif;
                mNilaiKalori.setText(String.valueOf(df.format(Kalori1)));
                mRef.child(mUserId).child("kalori").setValue(String.valueOf(df.format(Kalori1)));
                String key1 = mKaloriRef.push().getKey();
                Kalori kalori1 = new Kalori(String.valueOf(df.format(Kalori1)), date);
                mKaloriRef.child(mUserId).child(key1).setValue(kalori1);
                break;
        }
    }
}
