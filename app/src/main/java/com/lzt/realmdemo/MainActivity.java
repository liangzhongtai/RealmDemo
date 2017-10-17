package com.lzt.realmdemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.lzt.realmdemo.adapter.AnimalAdapter;
import com.lzt.realmdemo.entity.Animal;
import com.lzt.realmdemo.entity.Country;
import com.lzt.realmdemo.entity.Food;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EditText idET;
    private EditText nameET;
    private EditText placeET;
    private EditText countryET;

    private TextView insertTV;
    private TextView deleteTV;
    private TextView queryTV;
    private TextView updateTV;
    private TextView foodTV;
    private TextView syncDeleteTV;

    private AnimalAdapter adapter;
    private Realm realm;
    private RealmAsyncTask transaction;

    private RealmChangeListener<RealmResults<Animal>> listener = new RealmChangeListener<RealmResults<Animal>>() {
        @Override
        public void onChange(RealmResults<Animal> animals) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("haha--","刷新");
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(transaction!=null&&!transaction.isCancelled())transaction.cancel();
        //特点4：Realm对象关闭后，其查询出来的RealmObject不能再被访问.
        if(realm!=null&&realm.isClosed())realm.close();
    }

    private void initView() {
        rv           = (RecyclerView) findViewById(R.id.rv);
        idET         = (EditText) findViewById(R.id.et_id);
        nameET       = (EditText) findViewById(R.id.et_name);
        placeET      = (EditText) findViewById(R.id.et_place);
        countryET    = (EditText) findViewById(R.id.et_country);

        insertTV     = (TextView) findViewById(R.id.tv_insert);
        deleteTV     = (TextView) findViewById(R.id.tv_delete);
        queryTV      = (TextView) findViewById(R.id.tv_query);
        updateTV     = (TextView) findViewById(R.id.tv_update);
        foodTV       = (TextView) findViewById(R.id.tv_food);
        syncDeleteTV = (TextView) findViewById(R.id.tv_delete_synchronization);
    }

    private void initListener() {


        insertTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //主线程事务操作
            //插入一个新的Animal对象
            realm.beginTransaction();

            //当类没有设置主键时，使用realm.createObject(Class<E>)
            //formatAnimal(realm.createObject(Animal.class));
            //当类设置有主键时, 使用createObject(Class<E>, Object)
            formatAnimal(realm.createObject(Animal.class,Integer.valueOf(idET.getText().toString().trim())));

            //通过JSON插入一个Animal
           /* realm.beginTransaction();
            Animal animal = realm.createObjectFromJson(Animal.class
            ,"{aid:100,scientificName:\"熊猫\",place:\"四川\",viviparity:true,like:\"play\"}");
            animal.country= realm.createObjectFromJson(Country.class,"{cid:1000,cname:\"中国\"}");
            realm.commitTransaction();*/


            //子线程事务操作
           /* transaction = realm.executeTransactionAsync(new Realm.Transaction(){
                @Override public void execute(Realm realm) {
                    formatAnimal(realm.createObject(Animal.class));
            }},new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //回调到主线程,需要注意在Activity销毁时,在这里如果有UI操作，需要及时取消事务transaction
                     RealmResults<Animal> list = realm.where(Animal.class).findAll();
                     adapter.animals = list;
                     adapter.notifyDataSetChanged();
                }
            });*/


            //在子线程中执行事务操作
           /* Flowable.create(new FlowableOnSubscribe<List>(){
                @Override
                public void subscribe(FlowableEmitter<List> e) throws Exception {
                    /*//***特点1***Realm在哪个线程创建就只能在哪个线程使用,下面这样插入会报异常.
                   *//* realm.beginTransaction();
                    formatAnimal(realm.createObject(Animal.class));
                    realm.commitTransaction();*//*

                    //正确方式
                    RealmConfiguration config = new RealmConfiguration
                            .Builder()
                            .name("myrealm.realm")
                            .schemaVersion(0)
                            .migration(new MyMigration())
                            .deleteRealmIfMigrationNeeded()
                            .build();
                    Realm realm = Realm.getInstance(config);
                    realm.beginTransaction();
                    formatAnimal(realm.createObject(Animal.class));
                    RealmResults<Animal> list = realm.where(Animal.class).findAll();
                    realm.commitTransaction();

                    /*//***特点2***RealmObject在哪个线程创建就只能在哪个线程使用,下面这样也会报异常
                    *//*e.onNext(list);*//*
                    e.onComplete();
                }
            }, BackpressureStrategy.BUFFER)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                    //subscription = s;
                }
                @Override
                public void onNext(List list) {
                   *//* adapter.animals = list;
                    adapter.notifyDataSetChanged();*//*
                }
                @Override
                public void onError(Throwable t) {
                }
                @Override
                public void onComplete() {
                    realm.beginTransaction();
                    adapter.animals = realm.where(Animal.class).findAll();
                    realm.commitTransaction();
                    adapter.notifyDataSetChanged();
                }
            });*/


            //特点3:Realm支持多线程同时读写同一个数据库,前提是特点1和2
            //另外，官方建议Realm事务操作使用Async后缀的方法,但需要注意以下findAllAsync()方法返回的list
            // ，如果立即使用是有可能为null，需要加入监听
           /* RealmResults<Animal> list = realm.where(Animal.class).findAllAsync();
              list.addChangeListener(listener);
             adapter.animals = list;
             adapter.notifyDataSetChanged()
            */

            RealmResults<Animal> list = realm.where(Animal.class).findAll();
            realm.commitTransaction();

            adapter.animals = list;
            adapter.notifyDataSetChanged();

            }
        });
        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            realm.beginTransaction();
            RealmResults<Animal> list = null;
            if(!TextUtils.isEmpty(idET.getText().toString().trim())){
                realm.where(Animal.class)
                        .equalTo("aid",Integer.valueOf(idET.getText().toString().trim()))
                        .findAll()
                        .deleteAllFromRealm();
                /*for (int i=0;i<list.size();i++){
                    if(list.get(i).aid == Integer.valueOf(idET.getText().toString().trim())){
                        list.deleteFromRealm(i);
                    }
                }*/
            }else if(!TextUtils.isEmpty(nameET.getText().toString().trim())){
                realm.where(Animal.class)
                        .equalTo("scientificName",nameET.getText().toString().trim())
                        .findAll()
                        .deleteAllFromRealm();
            }else if(!TextUtils.isEmpty(placeET.getText().toString().trim())){
                realm.where(Animal.class)
                        .equalTo("place",placeET.getText().toString().trim())
                        .findAll()
                        .deleteAllFromRealm();
            }else{
                realm.where(Animal.class)
                        .equalTo("country.cid",formatCountry(countryET.getText().toString().trim()))
                        .findAll()
                        .deleteAllFromRealm();
            }
            list = realm.where(Animal.class).findAll();
            realm.commitTransaction();
            adapter.animals = list;
            adapter.notifyDataSetChanged();

            }
        });
        queryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            realm.beginTransaction();
            RealmResults<Animal> animals = null;
            RealmResults<Food> foods = realm.where(Food.class).findAll();
            if(!TextUtils.isEmpty(idET.getText().toString().trim())){
                animals = realm.where(Animal.class)
                        .equalTo("aid",Integer.valueOf(idET.getText().toString().trim()))
                        .findAll();
                //只需返回第一个符合查询条件的数据
                /*Animal animal = realm.where(Animal.class)
                        .equalTo("aid",Integer.valueOf(idET.getText().toString().trim()))
                        .findFirst();*/
                //多条件查询
                /*animals = realm.where(Animal.class)
                        .equalTo("aid",Integer.valueOf(idET.getText().toString().trim()))
                        .equalTo("scientificName",nameET.getText().toString().trim())
                        .findAll();*/
                //将查询结果排序
               /* animals = realm.where(Animal.class)
                        .equalTo("aid",Integer.valueOf(idET.getText().toString().trim()))
                        .equalTo("scientificName",nameET.getText().toString().trim())
                        .findAllSorted("aid", Sort.ASCENDING);*/

            }else if(!TextUtils.isEmpty(nameET.getText().toString().trim())){
                animals = realm.where(Animal.class)
                        .equalTo("scientificName",nameET.getText().toString().trim())
                        .findAll();

            }else if(!TextUtils.isEmpty(placeET.getText().toString().trim())){
                animals = realm.where(Animal.class)
                        .equalTo("place",placeET.getText().toString().trim())
                        .findAll();

            }else if(!TextUtils.isEmpty(countryET.getText().toString().trim())){
                //根据Animal中的Country的cid查询Animal
                animals = realm.where(Animal.class)
                        .equalTo("country.cid",formatCountry(countryET.getText().toString().trim()))
                        .findAll();

            }else{
                animals = realm.where(Animal.class).findAll();
            }
            realm.commitTransaction();
            adapter.foods = foods;
            adapter.animals = animals;
            adapter.notifyDataSetChanged();

            }
        });
        updateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            realm.beginTransaction();
            Animal animal = realm.where(Animal.class)
                    .equalTo("aid",Integer.valueOf(idET.getText().toString().trim()))
                    .findFirst();
                formatAnimal(animal);

            RealmResults<Animal> animals = realm.where(Animal.class).findAll();
            realm.commitTransaction();
            adapter.animals = animals;
            adapter.notifyDataSetChanged();

            }
        });

        foodTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //特点3 RelamObject 是不支持序列化的,所以不能直接用Intent传递
                // ,只能传递特征值，进入到下个界面后，再用Realm查询
                Intent intent = new Intent(MainActivity.this,FoodActivity.class);
                //intent.putExtra("animal",adapter.animals.get(0));
                startActivity(intent);
            }
        });


    }

    private Animal formatAnimal(Animal animal) {
        //一次事务中,主键在realm.createObject(Class<Animal>,aid)方法后不可再更改.
        //animal.aid = Integer.valueOf(idET.getText().toString().trim());
        animal.scientificName = nameET.getText().toString().trim();
        animal.place = placeET.getText().toString().trim();
        //在Animal中添加其Country对象
        Country country = realm.createObject(Country.class);
        country.cid = formatCountry(countryET.getText().toString().trim());
        country.cname = countryET.getText().toString();
        animal.country = country;
        return animal;
    }

    private int formatCountry(String country) {
        int cid = 0;
        if("中国".equals(country)){
            cid = 100;
        }else if("美国".equals(country)){
            cid = 200;
        }else if("俄罗斯".equals(country)){
            cid = 300;
        }else if("南极".equals(country)){
            cid = 400;
        }
        return cid;
    }

    private void initData() {
        adapter = new AnimalAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        //只需要在app启动时初始化一次
        Realm.init(this);
        //数据库位置为:/data/data/包名/files/数据库文件
        //默认方式,数据库名字为  default_realm
        /*
        realm = Realm.getDefaultInstance();*/

        //内存级别的持久化方式，app进程销毁后，数据库就会被删除
        /*
        RealmConfiguration config = new RealmConfiguration
                Builder()
                .name("myrealm.realm")
                .inMemory()
                .build();*/

        //指定数据库名的方式,
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name("baserealm.realm")
                .schemaVersion(0)
               /* .migration(new MyMigration())*/
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
    }

    //app由版本1更新2时，迁移版本1的Animal的Table到版本2中,可以指定迁移哪些字段
    static class MyMigration implements RealmMigration {
        @Override public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
            RealmSchema schema = realm.getSchema();
            if (oldVersion == 0 && newVersion == 1) {
                RealmObjectSchema personSchema = schema.get("Animal");
                //新增@Required的like字段
                personSchema.addField("like", String.class, FieldAttribute.REQUIRED)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.set("like", "玩耍");//为like设置默认值
                            }
                        }).removeField("lifeTime");//移除lifeTime字段
                oldVersion++;
            } else if (oldVersion == 1 && newVersion == 2) {
                //创建Country表
               RealmObjectSchema country = schema.create("Country");
                country.addField("cid", int.class);
                country.addField("name", String.class);
               //Animal中添加countries字段
               schema.get("Animal") .addRealmListField("countries", country)
                       .transform(new RealmObjectSchema.Function() {
                           @Override public void apply(DynamicRealmObject obj) {
                               //为已存在的数据设置dogs数据
                               DynamicRealmObject Country = realm.createObject(com.lzt.realmdemo.entity.Country.class.getName());
                               Country.set("cid", 1000);
                               Country.set("name", "中国");
                               obj.getList("countries").add(Country);
                           }
                       });
               oldVersion++; }
        }
    }


}