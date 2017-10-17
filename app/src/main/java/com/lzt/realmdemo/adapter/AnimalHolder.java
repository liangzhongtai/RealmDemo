package com.lzt.realmdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzt.realmdemo.R;


/**
 * Created by liangzhongtai on 2017/9/27.
 */

public class AnimalHolder extends RecyclerView.ViewHolder{
    public TextView aid;
    public TextView sname;
    public TextView place;
    public TextView country;
    public TextView food;
    public AnimalHolder(View itemView) {
        super(itemView);
        aid = (TextView) itemView.findViewById(R.id.tv_aid);
        sname = (TextView) itemView.findViewById(R.id.tv_sname);
        place = (TextView) itemView.findViewById(R.id.tv_place);
        country   = (TextView) itemView.findViewById(R.id.tv_country);
        food  = (TextView) itemView.findViewById(R.id.tv_food);
    }
}
