package com.lzt.realmdemo;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.lzt.realmdemo.adapter.FoodAdapter;
import com.lzt.realmdemo.entity.Animal;
import com.lzt.realmdemo.entity.Country;
import com.lzt.realmdemo.entity.Food;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * Created by liangzhongtai on 2017/10/9.
 */

public class FoodActivity extends AppCompatActivity {
    private RecyclerView rv;
    private EditText idET;
    private EditText nameET;
    private EditText animalET;

    private TextView insertTV;
    private TextView deleteTV;
    private TextView queryTV;
    private TextView updateTV;
    private TextView syncDeleteTV;
    private TextView insertJsonTV;
    private TextView insertJsonArrayTV;

    private FoodAdapter adapter;
    private Realm realm;

    //监听RealmObject数据更新
    private RealmChangeListener<RealmResults<Animal>> listener = new RealmChangeListener<RealmResults<Animal>>() {
        @Override
        public void onChange(RealmResults<Animal> animals) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        initView();
        initListener();
        initData();
        //Animal animal = (Animal) getIntent().getSerializableExtra("aniaml");
    }

    private void initView() {
        rv                = (RecyclerView) findViewById(R.id.rv);
        idET              = (EditText) findViewById(R.id.et_id);
        nameET            = (EditText) findViewById(R.id.et_name);
        animalET          = (EditText) findViewById(R.id.et_animal);

        insertTV          = (TextView) findViewById(R.id.tv_insert);
        deleteTV          = (TextView) findViewById(R.id.tv_delete);
        queryTV           = (TextView) findViewById(R.id.tv_query);
        updateTV          = (TextView) findViewById(R.id.tv_update);
        syncDeleteTV      = (TextView) findViewById(R.id.tv_delete_synchronization);
        insertJsonTV      = (TextView) findViewById(R.id.tv_insert_json);
        insertJsonArrayTV = (TextView) findViewById(R.id.tv_insert_jsonarray);
    }

    private void initListener() {

        insertTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果设置了主键，则插入主键相同的对象时，会报异常。
                realm.beginTransaction();
                formatFood(realm.createObject(Food.class,Integer.valueOf(idET.getText().toString().trim())));
                RealmResults<Food> list = realm.where(Food.class).findAll();
                realm.commitTransaction();

                adapter.foods = list;
                adapter.notifyDataSetChanged();

            }
        });
        deleteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                RealmResults<Food> list = null;
                if(!TextUtils.isEmpty(idET.getText().toString().trim())){
                    realm.where(Food.class)
                            .equalTo("fid",idET.getText().toString().trim())
                            .findAll()
                            .deleteAllFromRealm();
                /*for (int i=0;i<list.size();i++){
                    if(list.get(i).aid == Integer.valueOf(idET.getText().toString().trim())){
                        list.deleteFromRealm(i);
                    }
                }*/
                }else if(!TextUtils.isEmpty(nameET.getText().toString().trim())){
                    realm.where(Food.class)
                            .equalTo("name",nameET.getText().toString().trim())
                            .findAll()
                            .deleteAllFromRealm();
                /*for (int i=0;i<list.size();i++){
                    if(list.get(i).scientificName.equals(nameET.getText().toString().trim())){
                        list.deleteFromRealm(i);
                    }
                }*/
                }else if(!TextUtils.isEmpty(animalET.getText().toString().trim())){
                    realm.where(Food.class)
                            .equalTo("animal",animalET.getText().toString().trim())
                            .findAll()
                            .deleteAllFromRealm();
              /*  for (int i=0;i<list.size();i++){
                    if(list.get(i).place.equals(placeET.getText().toString().trim())){
                        list.deleteFromRealm(i);
                    }
                }*/
                }
                if(list==null)list = realm.where(Food.class).findAll();
                realm.commitTransaction();
                adapter.foods = list;
                adapter.notifyDataSetChanged();
            }
        });
        queryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                RealmResults<Food> foods;
                if(!TextUtils.isEmpty(idET.getText().toString().trim())){
                    foods = realm.where(Food.class)
                            .equalTo("fid",Integer.valueOf(idET.getText().toString().trim()))
                            .findAll();

                }else if(!TextUtils.isEmpty(nameET.getText().toString().trim())){
                    foods = realm.where(Food.class)
                            .equalTo("name",nameET.getText().toString().trim())
                            .findAll();

                }else if(!TextUtils.isEmpty(animalET.getText().toString().trim())){
                    foods = realm.where(Food.class)
                            .equalTo("animal",animalET.getText().toString().trim())
                            .findAll();

                }else{
                    foods = realm.where(Food.class).findAll();
                }
                realm.commitTransaction();
                adapter.foods = foods;
                adapter.notifyDataSetChanged();
            }
        });
        updateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                RealmResults<Food> foods = realm.where(Food.class)
                        .equalTo("fid",Integer.valueOf(idET.getText().toString().trim()))
                        .findAll();
                if(foods!=null&&foods.size()>0){
                    formatFood(foods.get(0));
                }
                foods = realm.where(Food.class).findAll();
                realm.commitTransaction();
                adapter.foods = foods;
                adapter.notifyDataSetChanged();
            }
        });

        syncDeleteTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //特点5：Relam数据库支持自动更新数据对象RealmObject,比如在FoodActivity中删除animal表的第一行数据后
                // ，返回MainActivity,直接调用adapter.notifyDataSetChanged(),可以发现RecyclerView中的视图也已经更新
                // ，而之前并没有重新查询animal表后再更新AnimalAdapter的animals的的代码
                // ，这个特点在开发中是非常有用的，因为页面交错跳转之后，这些页面间的数据同步是个很头疼的问题。
                realm.beginTransaction();
                realm.where(Animal.class).findAll().deleteFirstFromRealm();
                realm.commitTransaction();


                //如果需要监听查询的RealmObject何时有更新操作，可以用以下方法
                //需要注意,list集合如果立即使用，数据有可能为空，Realm只有全部查询完毕整个Animal表，list才会有数据
                //使用Async后缀的方法，不需要再调用beginTransaction()和commitTransaction()，否则会报错
                //realm.beginTransaction();
               /* RealmResults<Animal> list = realm.where(Animal.class).findAllAsync();
                list.addChangeListener(listener);*/
                //realm.commitTransaction();
            }
        });

        insertJsonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果设置有主键，则JSON中必须包含主键字段
                realm.beginTransaction();
                realm.createObjectFromJson(Food.class
                        ,"{fId:10,name:\"竹子\",animal:\"熊猫\",aid:100}");
                RealmResults<Food> list = realm.where(Food.class).findAll();
                realm.commitTransaction();

                adapter.foods = list;
                adapter.notifyDataSetChanged();
            }
        });

        insertJsonArrayTV.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                realm.createAllFromJson(Food.class
                        ,"[{fId:20,name:\"鳕鱼\",animal:\"白头海雕\",aid:200},{fId:30,name:\"海豹\",animal:\"北极熊\",aid:300}]");
                RealmResults<Food> list = realm.where(Food.class).findAll();
                realm.commitTransaction();

                adapter.foods = list;
                adapter.notifyDataSetChanged();
            }
        });
    }

    private Food formatFood(Food food) {
        food.name = nameET.getText().toString().trim();
        food.animal = animalET.getText().toString().trim();
        food.aid = formatAnimal(animalET.getText().toString().trim());
        return food;
    }

    private int formatAnimal(String animal) {
        int aid = 0;
        if("熊猫".equals(animal)){
            aid = 100;
        }else if("白头海雕".equals(animal)){
            aid = 200;
        }else if("北极熊".equals(animal)){
            aid = 300;
        }else if("企鹅".equals(animal)){
            aid = 400;
        }
        return aid;
    }

    private void initData() {
        adapter = new FoodAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .name("baserealm.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
    }
}
