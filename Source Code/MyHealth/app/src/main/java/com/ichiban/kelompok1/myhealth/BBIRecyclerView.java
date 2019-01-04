package com.ichiban.kelompok1.myhealth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BBIRecyclerView {
    private Context mContext;
    private BBIsAdapter mBBIAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<BBI> bbis, List<String> keys){
        mContext = context;
        mBBIAdapter = new BBIsAdapter(bbis, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mBBIAdapter);
    }

    class BBIItemView extends RecyclerView.ViewHolder{
        private TextView mNilai;
        private TextView mTanggal;

        private String key;

        public BBIItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext)
                    .inflate(R.layout.bbi_list_item, parent, false));

            mNilai = (TextView) itemView.findViewById(R.id.nilaiListBBITextView);
            mTanggal = (TextView) itemView.findViewById(R.id.tanggalListBBITextView);
        }

        public void bind(BBI bbi, String key){
            mNilai.setText(bbi.getNilai());
            mTanggal.setText(bbi.getTanggal());
            this.key = key;
        }
    }

    class BBIsAdapter extends RecyclerView.Adapter<BBIItemView>{
        private List<BBI> mBBIList;
        private List<String> mKeys;

        public BBIsAdapter(List<BBI> mBBIList, List<String> mKeys) {
            this.mBBIList = mBBIList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public BBIItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BBIItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BBIItemView holder, int position) {
            holder.bind(mBBIList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mBBIList.size();
        }
    }
}
