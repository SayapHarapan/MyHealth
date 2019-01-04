package com.ichiban.kelompok1.myhealth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LemakRecyclerView {
    private Context mContext;
    private LemakRecyclerView.LemaksAdapter mLemakAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<KadarLemak> kadarLemaks, List<String> keys){
        mContext = context;
        mLemakAdapter = new LemakRecyclerView.LemaksAdapter(kadarLemaks, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mLemakAdapter);
    }

    class LemakItemView extends RecyclerView.ViewHolder{
        private TextView mNilai;
        private TextView mTanggal;

        private String key;

        public LemakItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext)
                    .inflate(R.layout.lemak_list_item, parent, false));

            mNilai = (TextView) itemView.findViewById(R.id.nilaiListLemakTextView);
            mTanggal = (TextView) itemView.findViewById(R.id.tanggalListLemakTextView);
        }

        public void bind(KadarLemak kadarLemak, String key){
            mNilai.setText(kadarLemak.getNilai());
            mTanggal.setText(kadarLemak.getTanggal());
            this.key = key;
        }
    }

    class LemaksAdapter extends RecyclerView.Adapter<LemakRecyclerView.LemakItemView>{
        private List<KadarLemak> mLemakList;
        private List<String> mKeys;

        public LemaksAdapter(List<KadarLemak> mLemakList, List<String> mKeys) {
            this.mLemakList = mLemakList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public LemakRecyclerView.LemakItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LemakRecyclerView.LemakItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull LemakRecyclerView.LemakItemView holder, int position) {
            holder.bind(mLemakList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mLemakList.size();
        }
    }
}
