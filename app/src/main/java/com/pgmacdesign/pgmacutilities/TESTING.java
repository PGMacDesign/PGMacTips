package com.pgmacdesign.pgmacutilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.utilities.ContactUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.PermissionUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class TESTING extends AppCompatActivity {

    private DatabaseUtilities dbUtilities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_layout);

        init();
    }

    private void init(){
        //Custom stuff here
        dbUtilities = new DatabaseUtilities(this);


        writeDBStuff();
        //moveDBFile();
        queryDB();
        //contactQuery();
        //temp();
        //temp2();
        deleteStuff();
    }

    private void moveDBFile(){
        PermissionUtilities.getStoragePermissions(this);
        dbUtilities.copyDBToDownloadDirectory(null);
    }
    private void writeDBStuff(){
        PermissionUtilities.getStoragePermissions(this);
        Gson gson = new Gson();
        TESTINGPOJO testingpojo = new TESTINGPOJO();
        testingpojo.setAge(300);
        testingpojo.setName("Patrick Was here");
        testingpojo.setGender("MMMMMMMM");

        TESTINGPOJO2 testingpojo2 = new TESTINGPOJO2();
        testingpojo2.setAge(11100);
        testingpojo2.setName("Patrick Wasn't here");
        testingpojo2.setGender("Nooooo!");
        testingpojo2.setOkie("oh hai!");

        TESTINGPOJO3 testingpojo3 = new TESTINGPOJO3();
        testingpojo3.setX(22);

        boolean bool = dbUtilities.executeInsertIntoDBMaster(TESTINGPOJO.class, testingpojo);
        boolean bool2 = dbUtilities.executeInsertIntoDBMaster(TESTINGPOJO2.class, testingpojo2);
        boolean bool3 = dbUtilities.executeInsertIntoDBMaster(TESTINGPOJO3.class, testingpojo3);
        L.m("insert success = " + bool);
        L.m("insert success = " + bool2);
        L.m("insert success = " + bool3);
        /*
        String jsonToTest = gson.toJson(testingpojo, TESTINGPOJO.class);
        if(DatabaseUtilities.executeInsertIntoDB(TESTINGPOJO.class, jsonToTest, null, true)){
            L.m("success");
        } else {
            L.m("Failure");
        }
        */
    }
    private void temp(){
        L.m(MiscUtilities.getPackageName());

    }
    private void temp2(){
        L.m("further package testing");
        //4 - test
        ArrayList<PackageInfo> res = new ArrayList<PackageInfo>();
        int counter = 0;
        for(PackageInfo packageInfo : res){
            L.m("position " + counter + " - " + packageInfo.packageName);
            // TODO: 8/18/2016 read through package info in docs sometime. tons of info
        }
        //PackageManager pm = getApplicationContext().getPackageManager();
        //List<PackageInfo> packs = pm.getInstalledPackages(0);
    }
    public String getPackageName(Context context) {
        return context.getPackageName();
    }
    private void contactQuery(){
        ContactUtilities.ContactQueryAsync async = new ContactUtilities.ContactQueryAsync(
                new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(Object result, int customTag) {
                        List<ContactUtilities.Contact> myContacts =
                                (List<ContactUtilities.Contact>) result;
                        L.m("result size = " + myContacts.size());
                    }
                }, this, null, null,
                new ContactUtilities.SearchTypes[]{
                        ContactUtilities.SearchTypes.NAME, ContactUtilities.SearchTypes.PHONE,
                        ContactUtilities.SearchTypes.EMAIL},
                new ContactUtilities.SearchQueryFlags[]{
                        ContactUtilities.SearchQueryFlags.ADD_ALPHABET_HEADERS,
                        ContactUtilities.SearchQueryFlags.USE_ALL_ALPHABET_LETTERS,
                        ContactUtilities.SearchQueryFlags.MOVE_FAVORITES_TO_TOP_OF_LIST}
        );
        async.execute();
        L.m("async started");
    }
    private void queryDB(){
        //RealmConfiguration realmConfiguration = DatabaseUtilities.buildDefaultRealmConfig(this);
        //Realm realm = DatabaseUtilities.buildRealm(realmConfiguration);
        /*
        Object obj = DatabaseUtilities.queryDatabaseSingle(null, TESTINGPOJO.class, realm);
        try {
            L.m(obj.toString());
            TESTINGPOJO pojo = (TESTINGPOJO) obj;
            L.m("name = " + pojo.getName());
            L.m("age = " + pojo.getAge());
            L.m("gender = " + pojo.getGender());
        } catch (Exception e){}
        */

        //Realm realm = DatabaseUtilities.buildRealm(this);
        /*
        List<Object> objects = DatabaseUtilities.queryDatabaseList(null, TESTINGPOJO.class, realm);
        try {
            for(Object object : objects){
                L.m(object.toString());
                TESTINGPOJO pojo = (TESTINGPOJO) object;
                L.m("name = " + pojo.getName());
                L.m("age = " + pojo.getAge());
                L.m("gender = " + pojo.getGender());
            }
        } catch (Exception e){}
        */
        TESTINGPOJO obj = (TESTINGPOJO) dbUtilities.queryDatabaseMasterSingle(TESTINGPOJO.class);
        L.m("TESTING POJO name = " + obj.getName());
        L.m("TESTING POJO gender = " + obj.getGender());
        L.m("TESTING POJO age = " + obj.getAge());
        L.m("TESTING POJO id = " + obj.getId());

        TESTINGPOJO2 obj2 = (TESTINGPOJO2) dbUtilities.queryDatabaseMasterSingle(TESTINGPOJO2.class);
        L.m("TESTING POJO2 name = " + obj2.getName());
        L.m("TESTING POJO2 gender = " + obj2.getGender());
        L.m("TESTING POJO2 age = " + obj2.getAge());
        L.m("TESTING POJO2 id = " + obj2.getId());
        L.m("TESTING POJO2 okie = " + obj2.getOkie());

        TESTINGPOJO3 obj3 = (TESTINGPOJO3) dbUtilities.queryDatabaseMasterSingle(TESTINGPOJO3.class);
        if(obj3 != null) {
            L.m("X = " + obj3.getX());
        }
        //List<DatabaseUtilities.MasterDatabaseObject> mObjects = dbUtilities.queryDatabaseMasterAll();
        //L.m("size = " + mObjects.size());
    }

    private void deleteStuff(){
        dbUtilities.deleteFromMasterDB(TESTINGPOJO3.class);

        L.m("pause");

        TESTINGPOJO3 obj3 = (TESTINGPOJO3) dbUtilities.queryDatabaseMasterSingle(TESTINGPOJO3.class);
        if(obj3 != null) {
            L.m("X = " + obj3.getX());
        } else {
            L.m("delete worked");
        }
    }
}
