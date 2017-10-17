package com.lzt.realmdemo.entity;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by liangzhongtai on 2017/10/11.
 */

@RealmClass
public class Pojo implements RealmModel{
    public int aid;
    public String descPlace;
}
