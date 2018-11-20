package com.example.kesha.blog.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kesha.blog.R;


public class AdapterInfoFragment extends RecyclerView.Adapter<AdapterInfoFragment.InformationViewHolder> {
    @NonNull
    @Override
    public InformationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.information_recykler, viewGroup, false);
        InformationViewHolder holder = new InformationViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InformationViewHolder informationViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class InformationViewHolder extends RecyclerView.ViewHolder{
        private TextView infoTextView;
        public InformationViewHolder(View view){
            super(view);
            infoTextView = view.findViewById(R.id.data);
        }
    }
}
