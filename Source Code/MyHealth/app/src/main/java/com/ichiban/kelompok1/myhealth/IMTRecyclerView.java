package com.ichiban.kelompok1.myhealth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class IMTRecyclerView {
    private Context mContext;
    private IMTRecyclerView.IMTsAdapter mIMTAdapter;
    public void setConfig(RecyclerView recyclerView, Context context, List<IMT> imts, List<String> keys){
        mContext = context;
        mIMTAdapter = new IMTRecyclerView.IMTsAdapter(imts, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mIMTAdapter);
    }

    class IMTItemView extends RecyclerView.ViewHolder{
        private TextView mNilai;
        private TextView mTanggal;

        private String key;

        public IMTItemView(ViewGroup parent){
            super(LayoutInflater.from(mContext)
                    .inflate(R.layout.imt_list_item, parent, false));

            mNilai = (TextView) itemView.findViewById(R.id.nilaiListIMTTextView);
            mTanggal = (TextView) itemView.findViewById(R.id.tanggalListIMTTextView);
        }

        public void bind(IMT imt, String key){
            mNilai.setText(imt.getNilai());
            mTanggal.setText(imt.getTanggal());
            this.key = key;
        }
    }

    class IMTsAdapter extends RecyclerView.Adapter<IMTRecyclerView.IMTItemView>{
        private List<IMT> mIMTList;
        private List<String> mKeys;

        public IMTsAdapter(List<IMT> mIMTList, List<String> mKeys) {
            this.mIMTList = mIMTList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public IMTRecyclerView.IMTItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IMTRecyclerView.IMTItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull IMTRecyclerView.IMTItemView holder, int position) {
            holder.bind(mIMTList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mIMTList.size();
        }
    }
}
