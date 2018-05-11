package com.sumdroid.localcomplaint.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.sumdroid.localcomplaint.R;
import com.sumdroid.localcomplaint.model.Complain;

import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.CustomViewHolder> {
    private Context context;
    public ArrayList<Complain> complainList;



    public DoctorListAdapter(Context context, ArrayList<Complain> complainList) {
        this.context = context;
        this.complainList = complainList;

    }




    class CustomViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ArrayList<Complain> complainList;
        private TextView txtTitle, txtDescription;
        private ImageView imgProfile;


        public CustomViewHolder(View itemView, Context context, ArrayList<Complain> complainList) {
            super(itemView);
            this.context = context;
            this.complainList = complainList;

            txtTitle = itemView.findViewById(R.id.textView_title_complain);
            txtDescription = itemView.findViewById(R.id.textView_description_complain);
            imgProfile=itemView.findViewById(R.id.image_complain);

        }

    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_complain, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view, context, complainList);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {

        holder.txtTitle.setText(complainList.get(position).getTitle());
        holder.txtDescription.setText(complainList.get(position).getDescription());

        //Loading image from Glide library.
        Glide.with(context).load(complainList.get(position).getImgUrl()).into(holder.imgProfile);

    }


    @Override
    public int getItemCount() {
        return complainList.size();
    }


}
