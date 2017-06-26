package com.example.sparken02.musicplayer;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sparken02 on 24/6/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Model> myArrayList;
    private View.OnClickListener onClickListener;

    public RecyclerViewAdapter(Context mContext, ArrayList<Model> songArrayList, View.OnClickListener onclikListener) {
        this.mContext = mContext;
        this.myArrayList = songArrayList;
        this.onClickListener = onclikListener;
    }

    public RecyclerViewAdapter(ArrayList<Model> myArrayList) {
        this.myArrayList = myArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        MyViewHolder myholder = new MyViewHolder(view);



        return myholder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Model modelobj = myArrayList.get(position);
        holder.textView.setText(modelobj.getTitle());
        holder.textView.setTag(position);


    }

    @Override
    public int getItemCount() {
        return myArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.txtRecycview);
//            textView.setOnClickListener(this);
        }


    }
}
