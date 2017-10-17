package com.lzt.realmdemo.entity;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * Created by liangzhongtai on 2017/10/11.
 * @RealmClass 注解和实现RealmModel接口，  或者直接继承自RealmObject
 * @PrimaryKey 主键
 * @Required   声明字段不能为null，基础数据类型除外，因为他们本身会默认初始化赋值
 * @Ignore     忽略注解,注解的字段不会存储到数据库中
 * @Index      索引注解,可以在查询添加了索引注解的字段的速度更快,但数据库表会变大,可以在数据量大而复杂的情况下使用.
 *             支持除char类型外的基本类型字段
 * RealmList<Country> countries          RealmList替换集合类型
 */

@RealmClass
public class Animal implements RealmModel,Serializable/*extends RealmObject*/{
    @PrimaryKey
    public int aid;

    @Required
    public String scientificName;

    public String place;

    public int lifeTime;

    public boolean viviparity;

    @Ignore
    public String icon;

    public Country country;

    @Required
    public String like;

    //public RealmList<Country> countries;
}
