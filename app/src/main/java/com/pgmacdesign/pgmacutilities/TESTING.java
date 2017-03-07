package com.pgmacdesign.pgmacutilities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pgmacdesign.pgmacutilities.adaptersandlisteners.OnTaskCompleteListener;
import com.pgmacdesign.pgmacutilities.nonutilities.PGMacCustomProgressBar;
import com.pgmacdesign.pgmacutilities.pojos.MasterDatabaseObject;
import com.pgmacdesign.pgmacutilities.utilities.CameraMediaUtilities;
import com.pgmacdesign.pgmacutilities.utilities.ContactUtilities;
import com.pgmacdesign.pgmacutilities.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmacutilities.utilities.L;
import com.pgmacdesign.pgmacutilities.utilities.MiscUtilities;
import com.pgmacdesign.pgmacutilities.utilities.PermissionUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pmacdowell on 8/12/2016.
 */
public class TESTING extends AppCompatActivity implements View.OnClickListener {

    private DatabaseUtilities dbUtilities;
    private CameraMediaUtilities cam;
    private Button button;
    private static final String CUSTOM_STRING = "-PAT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_layout);
        button = (Button) this.findViewById(R.id.button);
        button.setTag("button");
        button.setOnClickListener(this);
        init();
    }

    private void init(){

        //contactQuery();
        //temp();
        //temp2();



        //Custom stuff here
        dbUtilities = new DatabaseUtilities(this);

        //writeDBStuff();
        //moveDBFile();
        //queryDB();
        //deleteStuff();
        //deleteCustom();
        //deleteAll();
        //superDeleteEverything();
    }

    private void moveDBFile(){
        //Check camera permissions
        PermissionUtilities perm = PermissionUtilities.getInstance(this);
        if(perm.startPermissionsRequest(new PermissionUtilities.permissionsEnum[]{
                PermissionUtilities.permissionsEnum.WRITE_EXTERNAL_STORAGE,
                PermissionUtilities.permissionsEnum.READ_EXTERNAL_STORAGE})) {
            dbUtilities.copyDBToDownloadDirectory(null);
        }
    }
    private void writeDBStuff(){
        PermissionUtilities perm = PermissionUtilities.getInstance(this);
        if(perm.startPermissionsRequest(new PermissionUtilities.permissionsEnum[]{
                PermissionUtilities.permissionsEnum.WRITE_EXTERNAL_STORAGE,
                PermissionUtilities.permissionsEnum.READ_EXTERNAL_STORAGE})) {
        }

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

        TESTINGPOJO3 testingpojo4 = new TESTINGPOJO3();
        testingpojo4.setX(221111111);

        boolean bool = dbUtilities.persistObject(TESTINGPOJO.class, testingpojo);
        boolean bool2 = dbUtilities.persistObject(TESTINGPOJO2.class, testingpojo2);
        boolean bool3 = dbUtilities.persistObject(TESTINGPOJO3.class, testingpojo3);
        boolean bool4 = dbUtilities.persistObjectCustom(TESTINGPOJO3.class, testingpojo4, CUSTOM_STRING);
        L.m("insert success = " + bool);
        L.m("insert success = " + bool2);
        L.m("insert success = " + bool3);
        L.m("insert success = " + bool4);

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

        Map<String, String> allObjects = dbUtilities.getAllPersistedObjects();
        L.m("total number of allObjects = " + allObjects.size());
        L.m("begin map \n\n");
        int counter = 0;
        for(Map.Entry<String, String> myMap : allObjects.entrySet()){
            String key = myMap.getKey();
            String json = myMap.getValue();
            L.m("ITEM NUMBER " + counter);
            counter++;
            L.m("key = " + key);
            L.m("json = " + json);
        }
        L.m("end map \n\n");
        TESTINGPOJO obj = (TESTINGPOJO) dbUtilities.getPersistedObject(TESTINGPOJO.class);
        if(obj != null) {
            L.m("TESTING POJO name = " + obj.getName());
            L.m("TESTING POJO gender = " + obj.getGender());
            L.m("TESTING POJO age = " + obj.getAge());
            L.m("TESTING POJO id = " + obj.getId());
        } else {
            L.m("TESTINGPOJO.class" + " is not in DB");
        }

        TESTINGPOJO2 obj2 = (TESTINGPOJO2) dbUtilities.getPersistedObject(TESTINGPOJO2.class);
        if(obj2 != null) {
            L.m("TESTING POJO2 name = " + obj2.getName());
            L.m("TESTING POJO2 gender = " + obj2.getGender());
            L.m("TESTING POJO2 age = " + obj2.getAge());
            L.m("TESTING POJO2 id = " + obj2.getId());
            L.m("TESTING POJO2 okie = " + obj2.getOkie());
        } else {
            L.m("TESTINGPOJO2.class" + " is not in DB");
        }

        TESTINGPOJO3 obj3 = (TESTINGPOJO3) dbUtilities.getPersistedObject(TESTINGPOJO3.class);
        if(obj3 != null) {
            L.m("X = " + obj3.getX());
        } else {
            L.m("TESTINGPOJO3.class" + " is not in DB");
        }

        L.m("testing custom now");
        TESTINGPOJO3 obj4 = (TESTINGPOJO3) dbUtilities.getPersistedObjectCustom(TESTINGPOJO3.class, CUSTOM_STRING);
        if(obj4 != null) {
            L.m("X = " + obj4.getX());
        } else {
            L.m("TESTINGPOJO3 + " + CUSTOM_STRING + " is not in DB");
        }
        //List<DatabaseUtilities.MasterDatabaseObject> mObjects = dbUtilities.queryDatabaseMasterAll();
        //L.m("size = " + mObjects.size());
    }

    private void deleteAll(){
        dbUtilities.deleteAllPersistedObjects(true, false);
    }
    private void deleteStuff(){
        //dbUtilities.executeDeleteFromDB(null, TESTINGPOJO3.class);

        dbUtilities.dePersistObject(MasterDatabaseObject.class);
        L.m("pause");

        TESTINGPOJO3 obj3 = (TESTINGPOJO3) dbUtilities.getPersistedObject(TESTINGPOJO3.class);
        if(obj3 != null) {
            L.m("X = " + obj3.getX());
        } else {
            L.m("delete worked");
        }
    }
    private void deleteCustom(){
        L.m("It was a " + dbUtilities.dePersistObjectCustom(TESTINGPOJO3.class, CUSTOM_STRING));
    }
    private void superDeleteEverything(){
        dbUtilities.deleteEntireDB(true, false);
    }

    private void testPhoto(){
        cam = new CameraMediaUtilities(this, this, new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(Object result, int customTag) {
                L.m("custom tag = " + customTag);
            }
        });
        cam.startPhotoProcess(CameraMediaUtilities.SourceType.CAMERA_SELF_PHOTO);
    }

    private void testLoadingAnimation(){
        Dialog progressDialog = PGMacCustomProgressBar.buildCaliforniaSVGDialog(this, true);
        progressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(CameraMediaUtilities.doesCodeBelongToUtility(requestCode)){
            cam.afterOnActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        String id = null;
        try {
            id = (String) view.getTag();
        } catch (Exception e){
            id = "";
        }
        if(id.equals("button")){
            //writeDBStuff();
            testLoadingAnimation();
        }
    }
}
