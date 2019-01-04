package com.ichiban.kelompok1.myhealth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class KaloriRecyclerView {
    private Context mContext;
    private KaloriRecyclerView.KalorisAdapter mKaloriAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<Kalori> kaloris, List<String> keys){
        mContext = context;
        mKaloriAdapter = new KaloriRecyclerView.KalorisAdapter(kaloris, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mKaloriAdapter);
    }

    class KaloriItemView extends RecyclerView.ViewHolder{
        private TextView mNilai;
        private TextView mTanggal;

        private String key;

        public KaloriItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext)
                    .inflate(R.layout.kalori_list_item, parent, false));

            mNilai = (TextView) itemView.findViewById(R.id.nilaiListKaloriTextView);
            mTanggal = (TextView) itemView.findViewById(R.id.tanggalListKaloriTextView);
        }

        public void bind(Kalori kalori, String key){
            mNilai.setText(kalori.getNilai());
            mTanggal.setText(kalori.getTanggal());
            this.key = key;
        }
    }

    class KalorisAdapter extends RecyclerView.Adapter<KaloriRecyclerView.KaloriItemView>{
        private List<Kalori> mKaloriList;
        private List<String> mKeys;

        public KalorisAdapter(List<Kalori> mKaloriList, List<String> mKeys) {
            this.mKaloriList = mKaloriList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public KaloriRecyclerView.KaloriItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new KaloriRecyclerView.KaloriItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull KaloriRecyclerView.KaloriItemView holder, int position) {
            holder.bind(mKaloriList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mKaloriList.size();
        }
    }
}
