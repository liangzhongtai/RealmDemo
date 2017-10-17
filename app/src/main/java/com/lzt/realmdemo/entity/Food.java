package com.lzt.realmdemo.entity;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by liangzhongtai on 2017/10/11.
 */

@RealmClass
public class Food implements RealmModel/*extends RealmObject*/{
    @PrimaryKey
    public int fId;

    public String name;

    public String animal;

    public int aid;
}
