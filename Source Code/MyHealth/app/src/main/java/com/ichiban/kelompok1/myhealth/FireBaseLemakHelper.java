package com.ichiban.kelompok1.myhealth;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseLemakHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private List<KadarLemak> kadarLemaks = new ArrayList<>();

    public interface DataStatusLemak{
        void DataIsLoaded(List<KadarLemak> kadarLemaks, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FireBaseLemakHelper(){
        mDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mUserId = user.getUid();
        mRef = mDatabase.getReference("kadarlemak").child(mUserId);
    }

    public void readLemak(final FireBaseLemakHelper.DataStatusLemak dataStatusLemak){
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                kadarLemaks.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    KadarLemak kadarLemak = keyNode.getValue(KadarLemak.class);
                    kadarLemaks.add(kadarLemak);
                }
                dataStatusLemak.DataIsLoaded(kadarLemaks, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
