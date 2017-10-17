package com.lzt.realmdemo.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lzt.realmdemo.R;
import com.lzt.realmdemo.entity.Animal;
import com.lzt.realmdemo.entity.Food;

import java.util.List;


/**
 * Created by liangzhongtai on 2017/9/27.
 */

public class AnimalAdapter extends RecyclerView.Adapter{
    public List<Animal> animals;
    public List<Food> foods;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        AnimalHolder holder = new AnimalHolder(View.inflate(viewGroup.getContext(), R.layout.item_animal,null));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        AnimalHolder holder = (AnimalHolder) viewHolder;
        Animal animal = animals.get(position);
        holder.aid.setText(animal.aid+"");
        holder.sname.setText(animal.scientificName);
        holder.place.setText(animal.place);
        holder.country.setText(animal.country.cname);
        String food ="";
        for (int i=0,len = foods==null?0:foods.size();i<len;i++){
            if(foods.get(i).aid==animal.aid){
                food += foods.get(i).name+" ";
            }
        }
        holder.food.setText(food);
    }

    @Override
    public int getItemCount() {
        return animals==null?0:animals.size();
    }
}
