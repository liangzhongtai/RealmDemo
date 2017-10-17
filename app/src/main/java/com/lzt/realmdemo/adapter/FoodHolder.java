package com.lzt.realmdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzt.realmdemo.R;


/**
 * Created by liangzhongtai on 2017/9/27.
 */

public class FoodHolder extends RecyclerView.ViewHolder{
    public TextView fid;
    public TextView name;
    public TextView animal;
    public TextView aid;
    public FoodHolder(View itemView) {
        super(itemView);
        fid = (TextView) itemView.findViewById(R.id.tv_fid);
        name = (TextView) itemView.findViewById(R.id.tv_name);
        animal = (TextView) itemView.findViewById(R.id.tv_animal);
        aid   = (TextView) itemView.findViewById(R.id.tv_aid);
    }
}
