package com.lzt.realmdemo.entity;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by liangzhongtai on 2017/10/11.
 */

@RealmClass
public class Country implements RealmModel,Serializable/*extends RealmObject */{
    public int cid;

    public String cname;
}
