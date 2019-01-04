package com.ichiban.kelompok1.myhealth;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class HitungLemakActivity extends AppCompatActivity {
    private final String TAG = "myTag";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference mLemakRef;
    private String mUserId;
    private DatabaseReference mRefWanita;
    private DatabaseReference mRefPria;
    private List<DataWanita> mDataWanitaList;
    private List<DataPria> mDataPriaList;

    private double mMeanBBRingan;
    private double mSDeviasiBBRingan;
    private double mMeanBBNormal;
    private double mSDeviasiBBNormal;
    private double mMeanBBTinggi;
    private double mSDeviasiBBTinggi;
    private double mMeanBBSangatTinggi;
    private double mSDeviasiBBSangatTinggi;

    private double mMeanTBRingan;
    private double mSDeviasiTBRingan;
    private double mMeanTBNormal;
    private double mSDeviasiTBNormal;
    private double mMeanTBTinggi;
    private double mSDeviasiTBTinggi;
    private double mMeanTBSangatTinggi;
    private double mSDeviasiTBSangatTinggi;

    private double mMeanLPRingan;
    private double mSDeviasiLPRingan;
    private double mMeanLPNormal;
    private double mSDeviasiLPNormal;
    private double mMeanLPTinggi;
    private double mSDeviasiLPTinggi;
    private double mMeanLPSangatTinggi;
    private double mSDeviasiLPSangatTinggi;

    private double mJumlahRingan;
    private double mJumlahNormal;
    private double mJumlahTinggi;
    private double mJumlahSangatTinggi;

    private TextView mTextView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitung_lemak);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_lemak);
        // Membuat Divider antar item di list
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        new FireBaseLemakHelper().readLemak(new FireBaseLemakHelper.DataStatusLemak() {
            @Override
            public void DataIsLoaded(List<KadarLemak> kadarLemaks, List<String> keys) {
                new LemakRecyclerView().setConfig(mRecyclerView, HitungLemakActivity.this, kadarLemaks, keys);
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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mTextView = (TextView) findViewById(R.id.NilaiLemakTextView);
        mDataWanitaList = new ArrayList<>();
        mDataPriaList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance();
        mUserRef = mDatabase.getReference("users");
        mLemakRef = mDatabase.getReference("kadarlemak");
        mRefWanita = mDatabase.getReference("datadummyperempuan");
        mRefPria = mDatabase.getReference("datadummylaki");

        mUserId = user.getUid();

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
                String mTinggiBadan = mUser.getTinggibadan().toString();
                String mBeratBadan = mUser.getBeratbadan().toString();
                String mLingkarPerut = mUser.getLingkarperut().toString();
                String mJenisKelamin = mUser.getJeniskelamin().toString();

                hitungLemak(mJenisKelamin,mTinggiBadan,mBeratBadan,mLingkarPerut);

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

    public void hitungLemak(String jenisKelamin, final String tinggiBadan, final String beratBadan, final String lingkarPerut){
        if (jenisKelamin.equals("Pria")){
            mRefPria.orderByChild("kadarlemak").equalTo("RINGAN").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataPriaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataPria dataPria = snapshot.getValue(DataPria.class);
                            mDataPriaList.add(dataPria);
                            //TinggiBadan
                            mTB = parseInt(mDataPriaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataPriaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataPriaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahRingan++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBRingan = SumTB/(i);
                        mSDeviasiTBRingan = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBRingan = Math.sqrt(mSDeviasiTBRingan);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBRingan = SumBB/(i);
                        mSDeviasiBBRingan = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBRingan = Math.sqrt(mSDeviasiBBRingan);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPRingan = SumLP/(i);
                        mSDeviasiLPRingan = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPRingan = Math.sqrt(mSDeviasiLPRingan);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefPria.orderByChild("kadarlemak").equalTo("NORMAL").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataPriaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataPria dataPria = snapshot.getValue(DataPria.class);
                            mDataPriaList.add(dataPria);
                            //TinggiBadan
                            mTB = parseInt(mDataPriaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataPriaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataPriaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahNormal++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBNormal = SumTB/(i);
                        mSDeviasiTBNormal = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBNormal = Math.sqrt(mSDeviasiTBNormal);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBNormal = SumBB/(i);
                        mSDeviasiBBNormal = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBNormal = Math.sqrt(mSDeviasiBBNormal);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPNormal = SumLP/(i);
                        mSDeviasiLPNormal= ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPNormal = Math.sqrt(mSDeviasiLPNormal);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefPria.orderByChild("kadarlemak").equalTo("TINGGI").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataPriaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataPria dataPria = snapshot.getValue(DataPria.class);
                            mDataPriaList.add(dataPria);
                            //TinggiBadan
                            mTB = parseInt(mDataPriaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataPriaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataPriaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahTinggi++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBTinggi= SumTB/(i);
                        mSDeviasiTBTinggi = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBTinggi = Math.sqrt(mSDeviasiTBTinggi);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBTinggi = SumBB/(i);
                        mSDeviasiBBTinggi = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBTinggi = Math.sqrt(mSDeviasiBBTinggi);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPTinggi = SumLP/(i);
                        mSDeviasiLPTinggi = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPTinggi = Math.sqrt(mSDeviasiLPTinggi);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefPria.orderByChild("kadarlemak").equalTo("SANGAT TINGGI").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataPriaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataPria dataPria = snapshot.getValue(DataPria.class);
                            mDataPriaList.add(dataPria);
                            //TinggiBadan
                            mTB = parseInt(mDataPriaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataPriaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataPriaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahSangatTinggi++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBSangatTinggi = SumTB/(i);
                        mSDeviasiTBSangatTinggi = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBSangatTinggi = Math.sqrt(mSDeviasiTBSangatTinggi);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBSangatTinggi = SumBB/(i);
                        mSDeviasiBBSangatTinggi = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBSangatTinggi = Math.sqrt(mSDeviasiBBSangatTinggi);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPSangatTinggi = SumLP/(i);
                        mSDeviasiLPSangatTinggi = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPSangatTinggi = Math.sqrt(mSDeviasiLPSangatTinggi);

                        double mTinggiBadan = parseDouble(tinggiBadan);
                        double mBeratBadan = parseDouble(beratBadan);
                        double mLingkarPerut = parseDouble(lingkarPerut);

                        //f(TinggiBadan)
                        double mPangkatETiBaRingan = -Math.pow((mTinggiBadan - mMeanTBRingan), 2) / (2 * Math.pow(mSDeviasiTBRingan, 2));
                        double mPangkatETiBaNormal = -Math.pow((mTinggiBadan - mMeanTBNormal), 2) / (2 * Math.pow(mSDeviasiTBNormal, 2));
                        double mPangkatETiBaTinggi = -Math.pow((mTinggiBadan - mMeanTBTinggi), 2) / (2 * Math.pow(mSDeviasiTBTinggi, 2));
                        double mPangkatETiBaSangatTinggi = -Math.pow((mTinggiBadan - mMeanTBSangatTinggi), 2) / (2 * Math.pow(mSDeviasiTBSangatTinggi, 2));
                        double FTiBaRingan = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBRingan)) * Math.pow(Math.E, mPangkatETiBaRingan);
                        double FTiBaNormal = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBNormal)) * Math.pow(Math.E, mPangkatETiBaNormal);
                        double FTiBaTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBTinggi)) * Math.pow(Math.E, mPangkatETiBaTinggi);
                        double FTiBaSangatTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBSangatTinggi)) * Math.pow(Math.E, mPangkatETiBaSangatTinggi);
                        //f(BeratBadan)
                        double mPangkatEBeBaRingan = -Math.pow((mBeratBadan - mMeanBBRingan), 2) / (2 * Math.pow(mSDeviasiBBRingan, 2));
                        double mPangkatEBeBaNormal = -Math.pow((mBeratBadan - mMeanBBNormal), 2) / (2 * Math.pow(mSDeviasiBBNormal, 2));
                        double mPangkatEBeBaTinggi = -Math.pow((mBeratBadan - mMeanBBTinggi), 2) / (2 * Math.pow(mSDeviasiBBTinggi, 2));
                        double mPangkatEBeBaSangatTinggi = -Math.pow((mBeratBadan - mMeanBBSangatTinggi), 2) / (2 * Math.pow(mSDeviasiBBSangatTinggi, 2));
                        double FBeBaRingan = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBRingan)) * Math.pow(Math.E, mPangkatEBeBaRingan);
                        double FBeBaNormal = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBNormal)) * Math.pow(Math.E, mPangkatEBeBaNormal);
                        double FBeBaTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBTinggi)) * Math.pow(Math.E, mPangkatEBeBaTinggi);
                        double FBeBaSangatTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBSangatTinggi)) * Math.pow(Math.E, mPangkatEBeBaSangatTinggi);
                        //f(LingkarPerut)
                        double mPangkatELingPerRingan = -Math.pow((mLingkarPerut - mMeanLPRingan), 2) / (2 * Math.pow(mSDeviasiLPRingan, 2));
                        double mPangkatELingPerNormal = -Math.pow((mLingkarPerut - mMeanLPNormal), 2) / (2 * Math.pow(mSDeviasiLPNormal, 2));
                        double mPangkatELingPerTinggi = -Math.pow((mLingkarPerut - mMeanLPTinggi), 2) / (2 * Math.pow(mSDeviasiLPTinggi, 2));
                        double mPangkatELingPerSangatTinggi = -Math.pow((mLingkarPerut - mMeanLPSangatTinggi), 2) / (2 * Math.pow(mSDeviasiLPSangatTinggi, 2));
                        double FLingPerRingan = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiLPRingan)) * Math.pow(Math.E, mPangkatELingPerRingan);
                        double FLingPerNormal = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiLPNormal)) * Math.pow(Math.E, mPangkatELingPerNormal);
                        double FLingPerTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiLPTinggi)) * Math.pow(Math.E, mPangkatELingPerTinggi);
                        double FLingPerSangatTinggi = (1 / (Math.sqrt(2 * Math.PI * mSDeviasiLPSangatTinggi))) * Math.pow(Math.E, mPangkatELingPerSangatTinggi);

                        double mJumlahSemua = mJumlahNormal+mJumlahRingan+mJumlahTinggi+mJumlahSangatTinggi;

                        double LikelihoodRendah = FTiBaRingan * FBeBaRingan * FLingPerRingan * (mJumlahRingan/mJumlahSemua);
                        double LikelihoodNormal = FTiBaNormal* FBeBaNormal * FLingPerNormal * (mJumlahNormal/mJumlahSemua);
                        double LikelihoodTinggi = FTiBaTinggi * FBeBaTinggi * FLingPerTinggi * (mJumlahTinggi/mJumlahSemua);
                        double LikelihoodSangatTinggi = FTiBaSangatTinggi * FBeBaSangatTinggi * FLingPerSangatTinggi * (mJumlahSangatTinggi/mJumlahSemua);

                        double ProRendah = LikelihoodRendah / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);
                        double ProNormal = LikelihoodNormal / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);
                        double ProTinggi = LikelihoodTinggi / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);
                        double ProSangatTinggi = LikelihoodSangatTinggi / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);

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

                        if (ProSangatTinggi > ProTinggi && ProSangatTinggi > ProNormal && ProSangatTinggi > ProRendah) {
                            mTextView.setText("SANGAT TINGGI");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("SANGAT TINGGI");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefPria.push().getKey();
                            mRefPria.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefPria.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefPria.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefPria.child(mKey).child("kadarlemak").setValue("SANGAT TINGGI");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("SANGAT TINGGI", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        } else if (ProTinggi > ProSangatTinggi && ProTinggi > ProNormal && ProTinggi > ProRendah) {
                            mTextView.setText("TINGGI");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("TINGGI");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefPria.push().getKey();
                            mRefPria.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefPria.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefPria.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefPria.child(mKey).child("kadarlemak").setValue("TINGGI");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("TINGGI", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        } else if (ProNormal > ProSangatTinggi && ProNormal > ProTinggi && ProNormal > ProRendah) {
                            mTextView.setText("NORMAL");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("NORMAL");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefPria.push().getKey();
                            mRefPria.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefPria.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefPria.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefPria.child(mKey).child("kadarlemak").setValue("NORMAL");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("NORMAL", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        } else {
                            mTextView.setText("RINGAN");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("RINGAN");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefPria.push().getKey();
                            mRefPria.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefPria.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefPria.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefPria.child(mKey).child("kadarlemak").setValue("RINGAN");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("RINGAN", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        else if (jenisKelamin.equals("Wanita")){
            mRefWanita.orderByChild("kadarlemak").equalTo("RINGAN").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataWanitaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataWanita dataWanita = snapshot.getValue(DataWanita.class);
                            mDataWanitaList.add(dataWanita);
                            //TinggiBadan
                            mTB = parseInt(mDataWanitaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataWanitaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataWanitaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahRingan++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBRingan = SumTB/(i);
                        mSDeviasiTBRingan = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBRingan = Math.sqrt(mSDeviasiTBRingan);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBRingan = SumBB/(i);
                        mSDeviasiBBRingan = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBRingan = Math.sqrt(mSDeviasiBBRingan);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPRingan = SumLP/(i);
                        mSDeviasiLPRingan = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPRingan = Math.sqrt(mSDeviasiLPRingan);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefWanita.orderByChild("kadarlemak").equalTo("NORMAL").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataWanitaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataWanita dataWanita = snapshot.getValue(DataWanita.class);
                            mDataWanitaList.add(dataWanita);
                            //TinggiBadan
                            mTB = parseInt(mDataWanitaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataWanitaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataWanitaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahNormal++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBNormal = SumTB/(i);
                        mSDeviasiTBNormal = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBNormal = Math.sqrt(mSDeviasiTBNormal);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBNormal = SumBB/(i);
                        mSDeviasiBBNormal = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBNormal = Math.sqrt(mSDeviasiBBNormal);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPNormal = SumLP/(i);
                        mSDeviasiLPNormal = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPNormal = Math.sqrt(mSDeviasiLPNormal);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefWanita.orderByChild("kadarlemak").equalTo("TINGGI").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataWanitaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataWanita dataWanita = snapshot.getValue(DataWanita.class);
                            mDataWanitaList.add(dataWanita);
                            //TinggiBadan
                            mTB = parseInt(mDataWanitaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataWanitaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataWanitaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahTinggi++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBTinggi = SumTB/(i);
                        mSDeviasiTBTinggi = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBTinggi = Math.sqrt(mSDeviasiTBTinggi);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBTinggi = SumBB/(i);
                        mSDeviasiBBTinggi = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBTinggi = Math.sqrt(mSDeviasiBBTinggi);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPTinggi = SumLP/(i);
                        mSDeviasiLPTinggi = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPTinggi = Math.sqrt(mSDeviasiLPTinggi);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mRefWanita.orderByChild("kadarlemak").equalTo("SANGAT TINGGI").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mDataWanitaList.clear();
                    if (dataSnapshot.exists()){
                        double SumBB = 0;
                        double SumBB1 = 0;
                        double SumBB2 = 0;
                        double mBB = 0;
                        double mBB1 = 0;
                        double SumTB = 0;
                        double SumTB1 = 0;
                        double SumTB2 = 0;
                        double mTB = 0;
                        double mTB1 = 0;
                        double SumLP = 0;
                        double SumLP1 = 0;
                        double SumLP2 = 0;
                        double mLP = 0;
                        double mLP1 = 0;
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            DataWanita dataWanita = snapshot.getValue(DataWanita.class);
                            mDataWanitaList.add(dataWanita);
                            //TinggiBadan
                            mTB = parseInt(mDataWanitaList.get(i).getTinggibadan());
                            mTB1 = mTB* mTB;
                            SumTB += mTB;
                            SumTB1 += mTB1;
                            //BeratBadan
                            mBB = parseInt(mDataWanitaList.get(i).getBeratbadan());
                            mBB1 = mBB *mBB;
                            SumBB += mBB;
                            SumBB1 += mBB1;
                            //LingkarPerut
                            mLP = parseInt(mDataWanitaList.get(i).getLingkarperut());
                            mLP1 = mLP *mLP;
                            SumLP += mLP;
                            SumLP1 += mLP1;
                            i++;
                            mJumlahSangatTinggi++;
                        }
                        SumTB2 = SumTB*SumTB;
                        mMeanTBSangatTinggi = SumTB/(i);
                        mSDeviasiTBSangatTinggi = ((i*SumTB1)-(SumTB2))/(i*(i-1));
                        mSDeviasiTBSangatTinggi = Math.sqrt(mSDeviasiTBSangatTinggi);
                        SumBB2 = SumBB*SumBB;
                        mMeanBBSangatTinggi = SumBB/(i);
                        mSDeviasiBBSangatTinggi = ((i*SumBB1)-(SumBB2))/(i*(i-1));
                        mSDeviasiBBSangatTinggi = Math.sqrt(mSDeviasiBBSangatTinggi);
                        SumLP2 = SumLP*SumLP;
                        mMeanLPSangatTinggi = SumLP/(i);
                        mSDeviasiLPSangatTinggi = ((i*SumLP1)-(SumLP2))/(i*(i-1));
                        mSDeviasiLPSangatTinggi = Math.sqrt(mSDeviasiLPSangatTinggi);

                        double mTinggiBadan = parseDouble(tinggiBadan);
                        double mBeratBadan = parseDouble(beratBadan);
                        double mLingkarPerut = parseDouble(lingkarPerut);

                        //f(TinggiBadan)
                        double mPangkatETiBaRingan = -Math.pow((mTinggiBadan - mMeanTBRingan), 2) / (2 * Math.pow(mSDeviasiTBRingan, 2));
                        double mPangkatETiBaNormal = -Math.pow((mTinggiBadan - mMeanTBNormal), 2) / (2 * Math.pow(mSDeviasiTBNormal, 2));
                        double mPangkatETiBaTinggi = -Math.pow((mTinggiBadan - mMeanTBTinggi), 2) / (2 * Math.pow(mSDeviasiTBTinggi, 2));
                        double mPangkatETiBaSangatTinggi = -Math.pow((mTinggiBadan - mMeanTBSangatTinggi), 2) / (2 * Math.pow(mSDeviasiTBSangatTinggi, 2));
                        double FTiBaRingan = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBRingan)) * Math.pow(Math.E, mPangkatETiBaRingan);
                        double FTiBaNormal = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBNormal)) * Math.pow(Math.E, mPangkatETiBaNormal);
                        double FTiBaTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBTinggi)) * Math.pow(Math.E, mPangkatETiBaTinggi);
                        double FTiBaSangatTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiTBSangatTinggi)) * Math.pow(Math.E, mPangkatETiBaSangatTinggi);
                        //f(BeratBadan)
                        double mPangkatEBeBaRingan = -Math.pow((mBeratBadan - mMeanBBRingan), 2) / (2 * Math.pow(mSDeviasiBBRingan, 2));
                        double mPangkatEBeBaNormal = -Math.pow((mBeratBadan - mMeanBBNormal), 2) / (2 * Math.pow(mSDeviasiBBNormal, 2));
                        double mPangkatEBeBaTinggi = -Math.pow((mBeratBadan - mMeanBBTinggi), 2) / (2 * Math.pow(mSDeviasiBBTinggi, 2));
                        double mPangkatEBeBaSangatTinggi = -Math.pow((mBeratBadan - mMeanBBSangatTinggi), 2) / (2 * Math.pow(mSDeviasiBBSangatTinggi, 2));
                        double FBeBaRingan = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBRingan)) * Math.pow(Math.E, mPangkatEBeBaRingan);
                        double FBeBaNormal = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBNormal)) * Math.pow(Math.E, mPangkatEBeBaNormal);
                        double FBeBaTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBTinggi)) * Math.pow(Math.E, mPangkatEBeBaTinggi);
                        double FBeBaSangatTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiBBSangatTinggi)) * Math.pow(Math.E, mPangkatEBeBaSangatTinggi);
                        //f(LingkarPerut)
                        double mPangkatELingPerRingan = -Math.pow((mLingkarPerut - mMeanLPRingan), 2) / (2 * Math.pow(mSDeviasiLPRingan, 2));
                        double mPangkatELingPerNormal = -Math.pow((mLingkarPerut - mMeanLPNormal), 2) / (2 * Math.pow(mSDeviasiLPNormal, 2));
                        double mPangkatELingPerTinggi = -Math.pow((mLingkarPerut - mMeanLPTinggi), 2) / (2 * Math.pow(mSDeviasiLPTinggi, 2));
                        double mPangkatELingPerSangatTinggi = -Math.pow((mLingkarPerut - mMeanLPSangatTinggi), 2) / (2 * Math.pow(mSDeviasiLPSangatTinggi, 2));
                        double FLingPerRingan = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiLPRingan)) * Math.pow(Math.E, mPangkatELingPerRingan);
                        double FLingPerNormal = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiLPNormal)) * Math.pow(Math.E, mPangkatELingPerNormal);
                        double FLingPerTinggi = (1 / (Math.sqrt(2 * Math.PI) * mSDeviasiLPTinggi)) * Math.pow(Math.E, mPangkatELingPerTinggi);
                        double FLingPerSangatTinggi = (1 / (Math.sqrt(2 * Math.PI * mSDeviasiLPSangatTinggi))) * Math.pow(Math.E, mPangkatELingPerSangatTinggi);

                        double mJumlahSemua = mJumlahNormal+mJumlahRingan+mJumlahTinggi+mJumlahSangatTinggi;

                        double LikelihoodRendah = FTiBaRingan * FBeBaRingan * FLingPerRingan * (mJumlahRingan/mJumlahSemua);
                        double LikelihoodNormal = FTiBaNormal* FBeBaNormal * FLingPerNormal * (mJumlahNormal/mJumlahSemua);
                        double LikelihoodTinggi = FTiBaTinggi * FBeBaTinggi * FLingPerTinggi * (mJumlahTinggi/mJumlahSemua);
                        double LikelihoodSangatTinggi = FTiBaSangatTinggi * FBeBaSangatTinggi * FLingPerSangatTinggi * (mJumlahSangatTinggi/mJumlahSemua);

                        double ProRendah = LikelihoodRendah / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);
                        double ProNormal = LikelihoodNormal / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);
                        double ProTinggi = LikelihoodTinggi / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);
                        double ProSangatTinggi = LikelihoodSangatTinggi / (LikelihoodRendah + LikelihoodNormal + LikelihoodTinggi + LikelihoodSangatTinggi);

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


                        if (ProSangatTinggi > ProTinggi && ProSangatTinggi > ProNormal && ProSangatTinggi > ProRendah) {
                            mTextView.setText("SANGAT TINGGI");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("SANGAT TINGGI");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefWanita.push().getKey();
                            mRefWanita.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefWanita.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefWanita.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefWanita.child(mKey).child("kadarlemak").setValue("SANGAT TINGGI");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("SANGAT TINGGI", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        } else if (ProTinggi > ProSangatTinggi && ProTinggi > ProNormal && ProTinggi > ProRendah) {
                            mTextView.setText("TINGGI");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("TINGGI");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefWanita.push().getKey();
                            mRefWanita.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefWanita.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefWanita.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefWanita.child(mKey).child("kadarlemak").setValue("TINGGI");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("TINGGI", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        } else if (ProNormal > ProSangatTinggi && ProNormal > ProTinggi && ProNormal > ProRendah) {
                            mTextView.setText("NORMAL");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("NORMAL");
                            Double d = new Double(mJumlahSemua);
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefWanita.push().getKey();
                            mRefWanita.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefWanita.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefWanita.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefWanita.child(mKey).child("kadarlemak").setValue("NORMAL");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("NORMAL", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        } else {
                            mTextView.setText("RINGAN");
                            mUserRef.child(mUserId).child("kadarlemak").setValue("RINGAN");
                            Double beratBadan = new Double(mBeratBadan);
                            Double tinggiBadan = new Double(mTinggiBadan);
                            Double lingkarPerut = new Double(mLingkarPerut);
                            int beratBadan1 = beratBadan.intValue();
                            int tinggiBadan1 = tinggiBadan.intValue();
                            int lingkarPerut1 = lingkarPerut.intValue();
                            String mKey = mRefWanita.push().getKey();
                            mRefWanita.child(mKey).child("beratbadan").setValue(String.valueOf(beratBadan1));
                            mRefWanita.child(mKey).child("tinggibadan").setValue(String.valueOf(tinggiBadan1));
                            mRefWanita.child(mKey).child("lingkarperut").setValue(String.valueOf(lingkarPerut1));
                            mRefWanita.child(mKey).child("kadarlemak").setValue("RINGAN");
                            String key = mLemakRef.push().getKey();
                            KadarLemak kadarLemak = new KadarLemak("RINGAN", date);
                            mLemakRef.child(mUserId).child(key).setValue(kadarLemak);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void hitungLemak(View view) {
        readUserData(mUserId);
    }
}
