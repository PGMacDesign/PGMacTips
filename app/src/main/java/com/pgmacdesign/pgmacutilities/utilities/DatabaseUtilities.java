package com.pgmacdesign.pgmacutilities.utilities;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * This class serves as a conduit for database functionalities. It is utilizing Realm for the
 * database. See the link below for Realm and their Wiki.
 * https://realm.io/docs/java/latest/
 * Created by pmacdowell on 8/18/2016.
 */
public class DatabaseUtilities {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////dddddddddddddd/////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private RealmConfiguration realmConfiguration;
    private Context context;

    public DatabaseUtilities(@NonNull Context context){
        this.context = context;
        this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context, null, null, null);
    }
    public DatabaseUtilities(@NonNull Context context, RealmConfiguration realmConfiguration){
        this.context = context;
        this.realmConfiguration = realmConfiguration;
        if(realmConfiguration == null){
            this.realmConfiguration = DatabaseUtilities.buildDefaultRealmConfig(this.context);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Insert Methods/////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public <T extends RealmObject> boolean executeInsertIntoDB(final Class myClass,
                                                               final String jsonString,
                                                               final Boolean appendToObject){
        if(jsonString == null || myClass == null){
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (MiscUtilities.isBooleanNullTrueFalse(appendToObject)) {
                        realm.createOrUpdateObjectFromJson(myClass, jsonString);
                    } else {
                        realm.createObjectFromJson(myClass, jsonString);
                    }
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1){
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
            "have a @PrimaryKey designation over something?");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                realm.close();
            } catch (Exception e){}
        }
        return false;
    }

    public <T extends RealmObject> boolean executeInsertIntoDB(final Class myClass,
                                                               final InputStream is,
                                                               final Boolean appendToObject){
        if(is == null || myClass == null){
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(realmConfiguration);
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if(MiscUtilities.isBooleanNullTrueFalse(appendToObject)){
                        try {
                            realm.createOrUpdateObjectFromJson(myClass, is);
                        } catch (IOException e){
                            L.m("IOException. Error reading file");
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            realm.createObjectFromJson(myClass, is);
                        } catch (IOException e){
                            L.m("IOException. Error reading file");
                            e.printStackTrace();
                        }
                    }
                }
            });
            realm.close();
            return true;

        } catch (IllegalArgumentException e1){
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                realm.close();
            } catch (Exception e){}
        }
        return false;
    }

    public <T extends RealmObject> boolean executeInsertIntoDB(final Class myClass,
                                                               final JSONObject jsonObject,
                                                               final Boolean appendToObject){
        if(jsonObject== null || myClass == null){
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if(MiscUtilities.isBooleanNullTrueFalse(appendToObject)){
                        realm.createOrUpdateObjectFromJson(myClass, jsonObject);
                    } else {
                        realm.createObjectFromJson(myClass, jsonObject);
                    }
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1){
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                realm.close();
            } catch (Exception e){}
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Query Methods//////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public <T extends RealmModel> Object queryDatabaseMaster(RealmQuery<T> passedQuery,
                                                             Class myClass){
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if(passedQuery == null){
            passedQuery = this.buildRealmQuery(realm, myClass);
        }

        //Start transaction
        RealmResults<T> results = passedQuery.findAll();

        if (results != null) {
            Object object = results.get(0);
            return object;
        } else {
            return null;
        }
    }
    public <T extends RealmModel> Object queryDatabaseSingle(RealmQuery<T> passedQuery,
                                                             Class myClass){

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if(passedQuery == null){
            passedQuery = this.buildRealmQuery(realm, myClass);
        }

        //Start transaction
        RealmResults<T> results = passedQuery.findAll();

        if (results != null) {
            Object object = results.get(0);
            return object;
        }

        return null;
    }
    public <T extends RealmModel> List<Object> queryDatabaseList(RealmQuery<T> passedQuery,
                                                                 Class myClass){
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if(passedQuery == null){
            passedQuery = this.buildRealmQuery(realm, myClass);
        }

        //Start transaction
        RealmResults<T> results = passedQuery.findAll();
        List<Object> objects = new ArrayList<>();
        //<T extends RealmModel>
        if (results != null) {
            for(T t : results){
                Object object = (Object) t;
                if(object != null){
                    objects.add(object);
                }
            }
        }
        return objects;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Realm Object, Configuration, and Query Utilities///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method will build the Realm object using pre-programmed hard-coded info for the
     * version and name. If you want to make your own custom version, please use the overloaded
     * method which takes in a RealmConfiguration parameter
     * @param context Context
     * @return Realm object
     */
    public static Realm buildRealm(Context context){
        RealmConfiguration config = DatabaseUtilities.buildDefaultRealmConfig(context);
        Realm realm = Realm.getInstance(config);
        return realm;
    }

    /**
     * Build a Realm object and return it
     * @param realmConfiguration
     * @return Realm object
     */
    public static Realm buildRealm(RealmConfiguration realmConfiguration){
        Realm realm = null;
        if(realmConfiguration != null){
            realm = Realm.getInstance(realmConfiguration);
        }
        return realm;
    }
    /**
     * This method will build the Realm configuration object using pre-programmed hard-coded
     * info for the version and name. If you want to make your own custom version,
     * please use the overloaded method which takes in multiple parameters
     * @param context Context
     * @return RealmConfiguration object
     */
    public static RealmConfiguration buildRealmConfig(Context context){
        return (DatabaseUtilities.buildRealmConfig(context, null, null, null));
    }

    /**
     * Build a realm configuration file
     * @param context Context, cannot be null
     * @param dbName The database name. If null, will default to a pre-set value
     * @param schemaVersion The schema version. Generally you want to increment these by one each
     *                      time you change the schema. If left null, it will default to 1.
     * @param deleteIfNeeded Boolean, if true, the database will be wiped when updated if it is
     *                       needed in order to complete the update. Set this to false if you want
     *                       to manually take care of db updates/ concurrency issues.
     * @return
     */
    public static RealmConfiguration buildRealmConfig(Context context, String dbName,
                                                      Integer schemaVersion,
                                                      Boolean deleteIfNeeded){
        if(context == null){
            return null;
        }
        if(dbName == null){
            dbName = "PGMacUtilities.DB";
        }
        if(schemaVersion == null){
            schemaVersion = 1;
        }
        if(deleteIfNeeded == null){
            deleteIfNeeded = true;
        }
        //Builder
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        builder.name(dbName);
        if(deleteIfNeeded){
            builder.deleteRealmIfMigrationNeeded();
        }
        builder.schemaVersion(schemaVersion);

        //Realm Config
        RealmConfiguration config = builder.build();

        return config;
    }

    /**
     * If no query is set, this will build a query with the class sent
     * @param realm
     * @param myClass
     * @return
     */
    private RealmQuery buildRealmQuery(Realm realm, Class myClass){
        RealmQuery query = RealmQuery.createQuery(realm, myClass);
        return query;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////Misc Utilities/////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make a copy of the database and move it to the Download Folder
     * @param dbName The name of the database. NOTE! MUST INCLUDE THE EXTENSION! (IE 'myDB.db')
     * @return boolean, true if it succeeded, false if it did not
     */
    public static boolean copyDBToDownloadDirectory(String dbName){
        File file = null;
        String packageName = MiscUtilities.getPackageName();
        try {
            file = new File("/data/data/" + packageName + "/files/" + dbName);
        } catch (Exception e){
            e.printStackTrace();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;
        try
        {
            if(file == null){
                return false;
            }
            fis = new FileInputStream(file);
            long currTime = DateUtilities.getCurrentDateLong();
            fos = new FileOutputStream(FileUtilities.getFileWriteDirectory() +
                    "/Download/" + dbName + "_copy_" + currTime + ".db");
            while(true)
            {
                int i=fis.read();
                if(i!=-1)
                {fos.write(i);}
                else
                {break;}
            }
            fos.flush();
            success = true;

        } catch(Exception e) {
            e.printStackTrace();
            success = false;

        } finally {
            try {
                fos.close();
                fis.close();
            } catch (IOException ioe) {

            }
            catch (NullPointerException e1){
                //Null
                success = false;
            }
        }
        return success;
    }
}
