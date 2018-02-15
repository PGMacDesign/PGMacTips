package com.pgmacdesign.pgmactips.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.datamodels.MasterDatabaseObject;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * This class serves as a conduit for database functionality. It is utilizing Realm for the
 * database. See the link below for Realm and their Wiki.
 * https://realm.io/docs/java/latest/
 * Created by pmacdowell on 8/18/2016.
 * NOTES:
 * 1) Feel free to cast responses from getPersistedObject even if it is null as null objects
 *    can be cast without throwing an error. I caution you against casting them to something
 *    else without knowing the response class though. For more info --
 *    http://stackoverflow.com/questions/18723596/no-exception-while-type-casting-with-a-null-in-java
 * 2)
 */
public class DatabaseUtilities {

    //Misc Type and Typetoken samples
    //final TypeToken MASTER_DB_OBJECT_TYPETOKEN = new TypeToken<List<MasterDatabaseObject>>(){};
    //final Type MASTER_DB_OBJECT_TYPE = MASTER_DB_OBJECT_TYPETOKEN.getType();

    //Global Vars
    private RealmConfiguration realmConfiguration;
    private Context context;

    //Defaults. If no configuration is set, these will be used
    private static final String DEFAULT_DB_NAME = PGMacTipsConstants.DB_NAME;
    private static final int DEFAULT_DB_SCHEMA = PGMacTipsConstants.DB_VERSION;
    private static final boolean DEFAULT_DELETE_OPTION = PGMacTipsConstants.DELETE_DB_IF_NEEDED;

    ////////////////////////
    //Constructors - init //
    ////////////////////////

    /**
     * Test constructor
     */
//    public DatabaseUtilities() {
//        L.m("attempting to get context for app from context provider");
//        this.context = PGMacTipsConfig.getInstance().getContext();
//        L.m("getting contact for db utilities from content provider");
//        this.init(context);
//        this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context, null, null, null);
//    }

    /**
     * Constructor
     *
     * @param context Context. Cannot be null
     */
    public DatabaseUtilities(@NonNull Context context) {
        this.context = context;
        this.init(context);
        this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context, null, null, null);
    }

    /**
     * Constructor using a realm configuration
     *
     * @param context            Context
     * @param realmConfiguration {@link RealmConfiguration} If this is left as null, it will
     *                           build the default version with hard coded info listed here in
     *                           this class.
     */
    public DatabaseUtilities(@NonNull Context context, RealmConfiguration realmConfiguration) {
        this.context = context;
        this.init(context);
        this.realmConfiguration = realmConfiguration;
        if (realmConfiguration == null) {
            this.realmConfiguration = DatabaseUtilities.buildRealmConfig(this.context);
        }
    }

    private void init(Context context) {
        Realm.init(context);
    }

    //////////////////
    //Insert Methods//
    //////////////////

    /**
     * Standard Database Insertion method with an object.
     *
     * @param objectToWrite  Object to write. Note, the class must extend RealmObject and
     *                       must also have a valid, designated Primary key declared.
     * @param appendToObject Boolean, if true, this will call 'updateOrWrite'. If it is false,
     *                       it will call 'write'. The idea being that if you want the object
     *                       already existing in the db to be update, set this
     *                       to true, else, set it to false for an overwrite.
     * @param <T>            T extends {@link RealmObject}
     * @return Boolean. True if the insert succeeded, false if it did not
     */
    public <T extends RealmObject> boolean executeInsertIntoDB(final T objectToWrite,
                                                               final Boolean appendToObject) {
        if (objectToWrite == null) {
            return false;
        }
        Class myClass = objectToWrite.getClass();
        if (myClass == null) {
            return false;
        }
        if (!isValidWrite(myClass) || !isClassValid(myClass)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (MiscUtilities.isBooleanNullTrueFalse(appendToObject)) {
                        realm.copyToRealmOrUpdate(objectToWrite);
                    } else {
                        realm.copyToRealm(objectToWrite);
                    }
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Standard Database Insertion method.
     *
     * @param myClass        Class will serve as the 'table' in the database
     * @param jsonString     The json String (Standard JSON format) to put into the db.
     * @param appendToObject Boolean, if true, this will call 'updateOrWrite'. If it is false,
     *                       it will call 'write'. The idea being that if you want the object
     *                       already existing in the db to be update, set this
     *                       to true, else, set it to false for an overwrite.
     * @return Boolean. True if the insert succeeded, false if it did not
     */
    public boolean executeInsertIntoDB(@NonNull final Class myClass,
                                       final String jsonString,
                                       final Boolean appendToObject) {
        if (jsonString == null || myClass == null) {
            return false;
        }
        if (!isValidWrite(myClass)) {
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
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Standard Database Insertion method.
     *
     * @param myClass        Class will serve as the 'table' in the database.
     *                       Note! The class must extend RealmObject for it to be written into the db
     * @param is             The InputStream (IS) to put into the db. Useful for fileWriting from a buffer.
     * @param appendToObject Boolean, if true, this will call 'updateOrWrite'. If it is false,
     *                       it will call 'write'. The idea being that if you want the object
     *                       already existing in the db to be update, set this
     *                       to true, else, set it to false for an overwrite.
     * @return Boolean. True if the insert succeeded, false if it did not
     */
    public boolean executeInsertIntoDB(@NonNull final Class myClass,
                                       final InputStream is,
                                       final Boolean appendToObject) {
        if (is == null || myClass == null) {
            return false;
        }
        if (!isValidWrite(myClass) || !isClassValid(myClass)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(realmConfiguration);
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (MiscUtilities.isBooleanNullTrueFalse(appendToObject)) {
                        try {
                            realm.createOrUpdateObjectFromJson(myClass, is);
                        } catch (IOException e) {
                            L.m("IOException. Error reading file");
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            realm.createObjectFromJson(myClass, is);
                        } catch (IOException e) {
                            L.m("IOException. Error reading file");
                            e.printStackTrace();
                        }
                    }
                }
            });
            realm.close();
            return true;

        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Standard Database Insertion method.
     *
     * @param myClass        Class will serve as the 'table' in the database
     *                       Note! The class must extend RealmObject for it to be written into the db
     * @param jsonObject     The json Object (Standard JSONObject format) to put into the db.
     * @param appendToObject Boolean, if true, this will call 'updateOrWrite'. If it is false,
     *                       it will call 'write'. The idea being that if you want the object
     *                       already existing in the db to be update, set this
     *                       to true, else, set it to false for an overwrite.
     * @return Boolean. True if the insert succeeded, false if it did not
     */
    public boolean executeInsertIntoDB(@NonNull final Class myClass,
                                       final JSONObject jsonObject,
                                       final Boolean appendToObject) {
        if (jsonObject == null || myClass == null) {
            return false;
        }
        if (!isValidWrite(myClass) || !isClassValid(myClass)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (MiscUtilities.isBooleanNullTrueFalse(appendToObject)) {
                        realm.createOrUpdateObjectFromJson(myClass, jsonObject);
                    } else {
                        realm.createObjectFromJson(myClass, jsonObject);
                    }
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public boolean executeInsertIntoDB(@NonNull final TypeToken myClass,
                                       final JSONObject jsonObject,
                                       final Boolean appendToObject) {
        return executeInsertIntoDB(myClass.getClass(), jsonObject, appendToObject);
    }

    /**
     * This method will put objects (converted to JSON Strings using Gson) into a custom table.
     * This works in that if you send an object, it WILL OVERRIDE the
     * one in the database for it. This is used for persisting objects while using their
     * class names as the primary key. If you send null as the obj, it will delete the
     * persisted item in the DB. This entire method is mainly used for persisting 1 object of
     * different classes. You can insert items into the DB normally, using other methods in this
     * class if the goal is to persist more than one object
     * NOTE: For the class objects being sent in, the fully qualified name of the object (in
     * String form) will be used as its id. For example, it will look something like this:
     * com.pgmacdesign.pgmactips.pojos.SampleObject
     * NOTE: The json String being pulled will be in JSON format, but will match the object
     * model as per matching the class passed in. An example would be:
     * {"age":30,"gender":"M","id":0,"name":"Patrick"}
     *
     * @param myClass Class that is of the object you are sending
     * @param obj     Object to persist. If null is passed, it will delete any object matching
     *                that class name
     * @return Return a boolean, true if suceeded, false if not
     */
    public boolean persistObject(@NonNull final Class myClass, final Object obj) {
        return (executeInsertIntoDBMaster(myClass, obj));
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public boolean persistObject(@NonNull final TypeToken myClass, final Object obj) {
        return (executeInsertIntoDBMaster(myClass, obj));
    }

    /**
     * Insert an object into the Master Table. For persisting objects
     *
     * @param myClass Class, in this method, the class will represent the ID in the table. If
     *                another object is sent with the same class, it will overwrite it.
     * @param obj     The object to put into the db. (Will be converted to JSON using Gson).
     *                Note, passing null will delete it from the Table.
     * @return Boolean, true if it succeeded, false if it did not
     */
    private boolean executeInsertIntoDBMaster(@NonNull final Class myClass,
                                              final Object obj) {

        if (myClass == null) {
            return false;
        }
        if (obj == null) {
            //This means they want to delete the item from the DB. Remove then leave
            return (this.deleteFromMasterDB(myClass));
        }

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        String className = myClass.getName();
        String jsonString = null;
        try {
            jsonString = new Gson().toJson(obj, myClass);
        } catch (Exception e) {
        }

        if (jsonString == null) {
            try {
                realm.close();
            } catch (Exception e) {
            }
            return false;
        }

        MasterDatabaseObject mdo = new MasterDatabaseObject();
        mdo.setId(className);
        mdo.setJsonString(jsonString);

        final MasterDatabaseObject mdoFinal = mdo;

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(mdoFinal);
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;

    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private boolean executeInsertIntoDBMaster(@NonNull final TypeToken myClass,
                                              final Object obj) {

        if (myClass == null) {
            return false;
        }
        if (obj == null) {
            //This means they want to delete the item from the DB. Remove then leave
            return (this.deleteFromMasterDB(myClass));
        }

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        String className = myClass.getType().toString();
        String jsonString = null;
        try {
            jsonString = new Gson().toJson(obj, myClass.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (jsonString == null) {
            try {
                realm.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        MasterDatabaseObject mdo = new MasterDatabaseObject();
        mdo.setId(className);
        mdo.setJsonString(jsonString);
        final MasterDatabaseObject mdoFinal = mdo;

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(mdoFinal);
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Insert an object into the Master Table. For persisting objects
     *
     * @param myClass      Class, in this method, the class will represent the ID in the table. If
     *                     another object is sent with the same class, it will overwrite it.
     * @param obj          The object to put into the db. (Will be converted to JSON using Gson).
     *                     Note, passing null will delete it from the Table.
     * @param customSuffix String of a custom suffix to be appended to the class name. This is
     *                     used in the event that you want to have a secondary persisted object
     *                     of the same type in the master table. An example would be that you have
     *                     2 user objects and want to persist both for X long. To do that, just
     *                     add a custom suffix string (ie -user2) and it will be written into the
     *                     masterobject table with an id (primary key) that matches that custom
     *                     suffix. Use that same suffix again to delete it from the db.
     * @return Boolean, true if it succeeded, false if it did not
     */
    public boolean persistObjectCustom(@NonNull final Class myClass,
                                       final Object obj,
                                       final String customSuffix) {

        if (myClass == null) {
            return false;
        }
        if (obj == null) {
            //This means they want to delete the item from the DB. Remove then leave
            return (this.deleteFromMasterDB(myClass, customSuffix));
        }
        if (!isValidWrite(myClass, customSuffix)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        String className = myClass.getName();
        String jsonString = null;
        try {
            jsonString = new Gson().toJson(obj, myClass);
        } catch (Exception e) {
        }

        if (jsonString == null) {
            try {
                realm.close();
            } catch (Exception e) {
            }
            return false;
        }

        MasterDatabaseObject mdo = new MasterDatabaseObject();
        if (customSuffix != null) {
            className = className + customSuffix;
        }
        mdo.setId(className);
        mdo.setJsonString(jsonString);

        final MasterDatabaseObject mdoFinal = mdo;

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(mdoFinal);
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;

    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public boolean persistObjectCustom(@NonNull final TypeToken myClass,
                                       final Object obj,
                                       final String customSuffix) {
        if (myClass == null) {
            return false;
        }
        if (obj == null) {
            //This means they want to delete the item from the DB. Remove then leave
            return (this.deleteFromMasterDB(myClass, customSuffix));
        }

        if (!isValidWrite(myClass, customSuffix)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        String className = myClass.toString();
        String jsonString = null;
        try {
            jsonString = new Gson().toJson(obj, myClass.getType());
        } catch (Exception e) {
        }

        if (jsonString == null) {
            try {
                realm.close();
            } catch (Exception e) {
            }
            return false;
        }

        MasterDatabaseObject mdo = new MasterDatabaseObject();
        if (customSuffix != null) {
            className = className + customSuffix;
        }
        mdo.setId(className);
        mdo.setJsonString(jsonString);

        final MasterDatabaseObject mdoFinal = mdo;

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(mdoFinal);
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;

    }

    //////////////////
    //Delete Methods//
    //////////////////

    /**
     * Delete an object from the Master Table. For deleting persisted objects
     *
     * @param myClass Class will be converted to string and used to reference the id / primary
     *                key to find the item / row.
     * @return Boolean, true if it succeeded, false if it did not
     */
    public boolean dePersistObject(@NonNull final Class myClass) {
        return this.deleteFromMasterDB(myClass);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public boolean dePersistObject(@NonNull final TypeToken myClass) {
        return this.deleteFromMasterDB(myClass);
    }

    //Overloaded for naming simplicity since I forget what things are called all the time!
    public boolean deletePersistedObject(@NonNull final Class myClass) {
        return this.deleteFromMasterDB(myClass);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public boolean deletePersistedObject(@NonNull final TypeToken myClass) {
        return this.deleteFromMasterDB(myClass);
    }

    /**
     * Delete an object from the Master Table. For deleting persisted objects
     *
     * @param myClass      Class will be converted to string and used to reference the id / primary
     *                     key to find the item / row.
     * @param customSuffix String of a custom suffix to be appended to the class name. This is
     *                     used in the event that you want to have a secondary persisted object
     *                     of the same type in the master table. An example would be that you have
     *                     2 user objects and want to persist both for X long. To do that, just
     *                     add a custom suffix string (ie -user2) and it will be written into the
     *                     masterobject table with an id (primary key) that matches that custom
     *                     suffix. Use that same suffix again to delete it from the db.
     * @return Boolean, true if it succeeded, false if it did not
     */
    public boolean dePersistObjectCustom(@NonNull final Class myClass, final String customSuffix) {
        return this.deleteFromMasterDB(myClass, customSuffix);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public boolean dePersistObjectCustom(@NonNull final TypeToken myClass, final String customSuffix) {
        return this.dePersistObjectCustom(myClass.getClass(), customSuffix);
    }

    /**
     * Delete an object from the Master Table. For deleting persisted objects
     *
     * @param myClass Class will be converted to string and used to reference the id / primary
     *                key to find the item / row.
     * @return Boolean, true if it succeeded, false if it did not
     */
    private boolean deleteFromMasterDB(@NonNull final Class myClass) {
        return this.deleteFromMasterDB(myClass, null);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private boolean deleteFromMasterDB(@NonNull final TypeToken myClass) {
        return this.deleteFromMasterDB(myClass, null);
    }

    /**
     * Delete an object from the Master Table. For deleting persisted objects
     *
     * @param myClass      Class will be converted to string and used to reference the id / primary
     *                     key to find the item / row.
     * @param customSuffix String of a custom suffix to be appended to the class name. This is
     *                     used in the event that you want to have a secondary persisted object
     *                     of the same type in the master table. An example would be that you have
     *                     2 user objects and want to persist both for X long. To do that, just
     *                     add a custom suffix string (ie -user2) and it will be written into the
     *                     masterobject table with an id (primary key) that matches that custom
     *                     suffix. Use that same suffix again to delete it from the db.
     * @param <T>          T extends RealmObject
     * @return Boolean, true if it succeeded, false if it did not
     */
    private <T extends RealmObject> boolean deleteFromMasterDB(@NonNull final Class myClass,
                                                               final String customSuffix) {
        if (myClass == null) {
            return false;
        }
        if (!DatabaseUtilities.isValidWrite(myClass, customSuffix)) {
            L.m("If you want to clear all stored / persisted data, please call " +
                    "deleteAllPersistedObjects(true, false)");
            return false;
        }
        //Class name String
        final String myClassName = myClass.getName();

        //Returned object from the master search
        Object obj = this.queryDatabaseMasterSingle(myClass);
        if (obj == null) {
            Object obj2 = this.getPersistedObjectCustom(myClass, customSuffix);
            if (obj2 == null) {
                // TODO: 2017-04-18 may need one more check on customSuffix outside of this if statement
                //IF it is null, it has already been deleted, return true and move on
                return true;
            }
        }

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        final RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //Start transaction
                RealmResults<T> results = query.findAll();
                if (results == null) {
                    return;
                }
                for (T t : results) {
                    if (t != null) {
                        MasterDatabaseObject mdo = (MasterDatabaseObject) t;
                        String id = mdo.getId();
                        if (!StringUtilities.isNullOrEmpty(id)) {
                            if (!StringUtilities.isNullOrEmpty(customSuffix)) {
                                String customId = myClassName + customSuffix;
                                //Check if ID Matches package name
                                if (customId.equals(id)) {
                                    try {
                                        t.deleteFromRealm();
                                        L.m("delete from DB succeeded");
                                        return;
                                    } catch (Exception e) {
                                        L.m("delete failed");
                                    }
                                }
                            } else {
                                //Check if ID Matches package name
                                if (myClassName.equals(id)) {
                                    try {
                                        t.deleteFromRealm();
                                        L.m("delete succeeded");
                                        return;
                                    } catch (Exception e) {
                                        L.m("delete failed");
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });

        try {
            realm.close();
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private <T extends RealmObject> boolean deleteFromMasterDB(@NonNull final TypeToken myClass,
                                                               final String customSuffix) {

        if (myClass == null) {
            return false;
        }
        if (!DatabaseUtilities.isValidWrite(myClass, customSuffix)) {
            L.m("If you want to clear all stored / persisted data, please call " +
                    "deleteAllPersistedObjects(true, false)");
            return false;
        }
        //Class name String
        final String myClassName = myClass.toString();

        //Returned object from the master search
        Object obj = this.queryDatabaseMasterSingle(myClass);
        if (obj == null) {
            Object obj2 = this.getPersistedObjectCustom(myClass, customSuffix);
            if (obj2 == null) {
                // TODO: 2017-04-18 may need one more check on customSuffix outside of this if statement
                //IF it is null, it has already been deleted, return true and move on
                return true;
            }
        }

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        final RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //Start transaction
                RealmResults<T> results = query.findAll();
                if (results == null) {
                    return;
                }
                for (T t : results) {
                    if (t != null) {
                        MasterDatabaseObject mdo = (MasterDatabaseObject) t;
                        String id = mdo.getId();
                        if (!StringUtilities.isNullOrEmpty(id)) {
                            if (!StringUtilities.isNullOrEmpty(customSuffix)) {
                                String customId = myClassName + customSuffix;
                                //Check if ID Matches package name
                                if (customId.equals(id)) {
                                    try {
                                        t.deleteFromRealm();
                                        L.m("delete from DB succeeded");
                                        return;
                                    } catch (Exception e) {
                                        L.m("delete failed");
                                    }
                                }
                            } else {
                                //Check if ID Matches package name
                                if (myClassName.equals(id)) {
                                    try {
                                        t.deleteFromRealm();
                                        L.m("delete succeeded");
                                        return;
                                    } catch (Exception e) {
                                        L.m("delete failed");
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });

        try {
            realm.close();
        } catch (Exception e) {
        }
        return true;

    }

    /**
     * DANGER ZONE! This will wipe the entire database (Of the configuration set. If you have
     * multiple RealmConfigurations, you will need to create a DatabaseUtilities Object for
     * each one and loop this method to delete them all).
     *
     * @param areYouSure    Pass true to confirm wipe
     * @param areYouNotSure Pass false to confirm wipe
     * @return Boolean of success or not
     */
    public boolean deleteEntireDB(boolean areYouSure, boolean areYouNotSure) {
        if (!areYouSure) {
            return false;
        }
        if (areYouNotSure) {
            return false;
        }
        try {
            L.m("Deleting entire database!");
            Set<Class<? extends RealmModel>> allClasses = getDBTableTypes();
            if(allClasses != null){
                if(allClasses.size()>= 0){
                    for(Class<? extends RealmModel> c : allClasses){
                        try {
                            deleteFromMasterDB(c);
                        } catch (Exception e){
                            L.m("A class could not be deleted: " + c.getCanonicalName());
                        }
                    }
                }
            }
            return (deleteRealmFileInStorage(realmConfiguration));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Only to be called if the realm instance needs to be deleted
     *
     * @return boolean of completion, true if success.
     */
    private static boolean deleteRealmFileInStorage(RealmConfiguration realmConfiguration) {
        return (Realm.deleteRealm(realmConfiguration));
    }

    /**
     * This will delete all persisted objects in the MasterDatabaseObject table
     *
     * @param areYouSure    If you want to delete everything, send true here
     * @param areYouNotSure If you want to delete everything, send false here
     *                      (Adding in '2 factor authentication' to prevent disasters)
     * @param <T>           T extends RealmObject
     * @return Returns a boolean, true if deletes succeeded, false if they did not
     */
    public <T extends RealmObject> boolean deleteAllPersistedObjects(boolean areYouSure,
                                                                     boolean areYouNotSure) {

        if (!areYouSure) {
            return false;
        }
        if (areYouNotSure) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        final RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //Start transaction
                RealmResults<T> results = query.findAll();
                for (T t : results) {
                    if (t != null) {
                        try {
                            t.deleteFromRealm();
                        } catch (Exception e) {
                            L.m("delete failed");
                        }
                    }
                }
            }
        });
        return true;
    }

    /**
     * @param query   Query to delete. Keep in mind here, if you send no query, it will delete
     *                everything in that table.
     * @param myClass Class to reference (Table pulling from)
     * @param <T>     T extends RealmObject
     * @return return a boolean, true if it succeeded, false if it did not
     */
    public <T extends RealmObject> boolean executeDeleteFromDB(RealmQuery query,
                                                               @NonNull final Class myClass) {
        if (myClass == null) {
            return false;
        }
        if (!isValidWrite(myClass)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if (query == null) {
            query = this.buildRealmQuery(realm, myClass);
        }
        final RealmResults<T> results = query.findAll();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (T t : results) {
                        t.deleteFromRealm();
                    }
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.getName() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public <T extends RealmObject> boolean executeDeleteFromDB(RealmQuery query,
                                                               @NonNull final TypeToken myClass) {
        if (myClass == null) {
            return false;
        }
        if (!isValidWrite(myClass)) {
            return false;
        }
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if (query == null) {
            query = this.buildRealmQuery(realm, myClass);
        }
        final RealmResults<T> results = query.findAll();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (T t : results) {
                        t.deleteFromRealm();
                    }
                }
            });
            realm.close();
            return true;
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            L.m("A RealmObject with no PrimaryKey cannot be updated. Does " + myClass.toString() +
                    "have a @PrimaryKey designation over something?");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!realm.isClosed()) {
                    realm.close();
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    /////////////////
    //Query Methods//
    /////////////////

    /**
     * Query the master table in the database
     *
     * @param myClass Class (row / id) to query
     * @return returns an Object that was pulled from the DB. If nothing found, it will return null.
     */
    public Object getPersistedObject(@NonNull Class myClass) {
        return this.queryDatabaseMasterSingle(myClass);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public Object getPersistedObject(@NonNull TypeToken myClass) {
        return this.queryDatabaseMasterSingle(myClass);
    }

    /**
     * Query the MasterDatabaseObject table (persisted objects) in the database for all rows
     *
     * @return returns a Map of type <String, String>. The first String represents the
     * key used in the MasterDatabaseObject table and the second String is the
     * object converted into a String using Gson.toJson(). If there are no objects
     * to return whatsoever, an empty map will be returned.
     * <p>
     * NOTE: For the class objects being retrieved, the fully qualified name of the object (in
     * String form) will be used as its id. For example, it will look something like this:
     * com.pgmacdesign.pgmactips.pojos.SampleObject
     * NOTE: The json String being pulled will be in JSON format, but will match the object
     * model as per matching the class passed in. An example would be:
     * {"age":30,"gender":"M","id":0,"name":"Patrick"}
     **/
    public Map<String, String> getAllPersistedObjects() {
        List<MasterDatabaseObject> masterDatabaseObjects = this.queryDatabaseMasterAll();
        if (MiscUtilities.isListNullOrEmpty(masterDatabaseObjects)) {
            return new HashMap<>();
        }
        Map<String, String> myMap = new HashMap<>();
        for (MasterDatabaseObject mdo : masterDatabaseObjects) {
            String id = mdo.getId();
            String json = mdo.getJsonString();
            if (id != null && json != null) {
                myMap.put(id, json);
            }
        }
        return myMap;
    }

    /**
     * Query the master table in the database
     *
     * @param myClass      Class (row / id) to query
     * @param customSuffix Custom String suffix added (in the insert method) to add more
     *                     space (more rows) for persisted objects.
     * @return returns an Object that was pulled from the DB. If nothing found, it will return null.
     */
    public Object getPersistedObjectCustom(@NonNull final Class myClass,
                                           final String customSuffix) {
        return this.queryDatabaseMasterSingle(myClass, customSuffix);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public Object getPersistedObjectCustom(@NonNull final TypeToken myClass,
                                           final String customSuffix) {
        return this.queryDatabaseMasterSingle(myClass, customSuffix);
    }

    /**
     * Query the master table in the database
     *
     * @param myClass Class (row / id) to query
     * @return returns an Object that was pulled from the DB. If nothing found, it will return null.
     */
    private Object queryDatabaseMasterSingle(@NonNull final Class myClass) {

        return this.queryDatabaseMasterSingle(myClass, null);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private Object queryDatabaseMasterSingle(@NonNull final TypeToken myClass) {
        return this.queryDatabaseMasterSingle(myClass, null);
    }

    /**
     * Query the master table in the database
     *
     * @param myClass      Class (row / id) to query
     * @param customSuffix Custom String suffix added (in the insert method) to add more
     *                     space (more rows) for persisted objects.
     * @return returns an Object that was pulled from the DB. If nothing found, it will return null.
     */
    private Object queryDatabaseMasterSingle(@NonNull final Class myClass,
                                             final String customSuffix) {

        String className = myClass.getName();
        if (customSuffix != null) {
            className = className + customSuffix;
        }
        List<MasterDatabaseObject> fullList = this.queryDatabaseMasterAll();

        if (fullList == null) {
            return null;
        }
        MasterDatabaseObject pulledObject = null;
        //Loop through to check if the String matches the ID
        for (MasterDatabaseObject mdo : fullList) {
            String id = mdo.getId();
            if (!StringUtilities.isNullOrEmpty(id)) {
                if (id.equals(className)) {
                    pulledObject = mdo;
                    break;
                }
            }
        }

        if (pulledObject == null) {
            return null;
        } else {
            try {
                String jsonString = pulledObject.getJsonString();
                Object obj = new Gson().fromJson(jsonString, myClass);
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
                //In case they pass the wrong class name
                return null;
            }
        }
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private Object queryDatabaseMasterSingle(@NonNull final TypeToken myClass,
                                             final String customSuffix) {
        String className = myClass.toString();
        if (customSuffix != null) {
            className = className + customSuffix;
        }
        List<MasterDatabaseObject> fullList = this.queryDatabaseMasterAll();

        if (fullList == null) {
            return null;
        }
        MasterDatabaseObject pulledObject = null;
        //Loop through to check if the String matches the ID
        for (MasterDatabaseObject mdo : fullList) {
            String id = mdo.getId();
            if (!StringUtilities.isNullOrEmpty(id)) {
                if (id.equals(className)) {
                    pulledObject = mdo;
                    break;
                }
            }
        }

        if (pulledObject == null) {
            return null;
        } else {
            try {
                String jsonString = pulledObject.getJsonString();
                Object obj = new Gson().fromJson(jsonString, myClass.getType());
                return obj;
            } catch (Exception e) {
                e.printStackTrace();
                //In case they pass the wrong class name
                return null;
            }
        }
    }

    /**
     * Query the master table in the database for all rows
     *
     * @param <T> T extends RealmModel
     * @return returns a list of Objects that was pulled from the DB. If nothing found,
     * it will return an initialized, but empty, list.
     */
    private <T extends RealmModel> List<MasterDatabaseObject> queryDatabaseMasterAll() {

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class);

        //Start transaction
        RealmResults<T> results = query.findAll();
        if (results.size() <= 0) {
            return new ArrayList<>();
        }
        try {
            List<MasterDatabaseObject> masterDatabaseObjectList = new ArrayList<MasterDatabaseObject>();
            for (T t : results) {
                if (t != null) {
                    MasterDatabaseObject mdo = (MasterDatabaseObject) t;
                    if (!StringUtilities.isNullOrEmpty(mdo.getId()) &&
                            !StringUtilities.isNullOrEmpty(mdo.getJsonString())) {
                        masterDatabaseObjectList.add(mdo);
                    }
                }
            }

            return masterDatabaseObjectList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Query the database
     *
     * @param passedQuery The query to search with. If null is passed here, it will build a default
     *                    query in which it searches everything.
     * @param myClass     Class (table) that is being searched
     * @param <T>         T extends RealmModel (RealmResults)
     * @return An object from the database (one from that table)
     */
    public <T extends RealmModel> Object queryDatabaseSingle(RealmQuery<T> passedQuery,
                                                             @NonNull Class myClass) {

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if (passedQuery == null) {
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

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public <T extends RealmModel> Object queryDatabaseSingle(RealmQuery<T> passedQuery,
                                                             @NonNull TypeToken myClass) {

        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if (passedQuery == null) {
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

    /**
     * Query the database
     *
     * @param passedQuery The query to search with. If null is passed here, it will build a default
     *                    query in which it searches everything.
     * @param myClass     Class (table) that is being searched
     * @param <T>         T extends RealmModel (RealmResults)
     * @return An list of objects from the database (all in that table)
     */
    public <T extends RealmModel> List<Object> queryDatabaseList(RealmQuery<T> passedQuery,
                                                                 @NonNull Class myClass) {
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if (passedQuery == null) {
            passedQuery = this.buildRealmQuery(realm, myClass);
        }

        //Start transaction
        RealmResults<T> results = passedQuery.findAll();
        List<Object> objects = new ArrayList<>();
        //<T extends RealmModel>
        if (results != null) {
            for (T t : results) {
                Object object = (Object) t;
                if (object != null) {
                    objects.add(object);
                }
            }
        }
        return objects;
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    public <T extends RealmModel> List<Object> queryDatabaseList(RealmQuery<T> passedQuery,
                                                                 @NonNull TypeToken myClass) {
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        if (passedQuery == null) {
            passedQuery = this.buildRealmQuery(realm, myClass);
        }

        //Start transaction
        RealmResults<T> results = passedQuery.findAll();
        List<Object> objects = new ArrayList<>();
        //<T extends RealmModel>
        if (results != null) {
            for (T t : results) {
                Object object = (Object) t;
                if (object != null) {
                    objects.add(object);
                }
            }
        }
        return objects;
    }

    ////////////////////////////////////////////////////
    //Realm Object, Configuration, and Query Utilities//
    ////////////////////////////////////////////////////

    /**
     * This method will build the Realm object using pre-programmed hard-coded info for the
     * version and name. If you want to make your own custom version, please use the overloaded
     * method which takes in a RealmConfiguration parameter
     *
     * @param context Context
     * @return Realm object
     */
    public static Realm buildRealm(Context context) {
        RealmConfiguration config = DatabaseUtilities.buildRealmConfig(context);
        Realm realm = Realm.getInstance(config);
        return realm;
    }

    /**
     * Build a Realm object and return it
     *
     * @param realmConfiguration
     * @return Realm object
     */
    public static Realm buildRealm(RealmConfiguration realmConfiguration) {
        Realm realm = null;
        if (realmConfiguration != null) {
            realm = Realm.getInstance(realmConfiguration);
        }
        return realm;
    }

    /**
     * This method will build the Realm configuration object using pre-programmed hard-coded
     * info for the version and name. If you want to make your own custom version,
     * please use the overloaded method which takes in multiple parameters
     *
     * @param context Context
     * @return RealmConfiguration object
     */
    public static RealmConfiguration buildRealmConfig(Context context) {
        return (DatabaseUtilities.buildRealmConfig(context, null, null, null));
    }

    /**
     * Build a realm configuration file
     *
     * @param context        Context, cannot be null
     * @param dbName         The database name. If null, will default to a pre-set value
     * @param schemaVersion  The schema version. Generally you want to increment these by one each
     *                       time you change the schema. If left null, it will default to 1.
     * @param deleteIfNeeded Boolean, if true, the database will be wiped when updated if it is
     *                       needed in order to complete the update. Set this to false if you want
     *                       to manually take care of db updates/ concurrency issues.
     * @return
     */
    public static RealmConfiguration buildRealmConfig(@NonNull Context context,
                                                      @Nullable String dbName,
                                                      @Nullable Integer schemaVersion,
                                                      @Nullable Boolean deleteIfNeeded) {
//        if (context == null) {
//            context = PGMacTipsConfig.getInstance().getContext();
//        }
        if (context == null) {
            return null;
        }
        Realm.init(context);
        if (StringUtilities.isNullOrEmpty(dbName)) {
            try {
                String packageName = MiscUtilities.getPackageName(context);
                if (!StringUtilities.isNullOrEmpty(packageName)) {
                    dbName = packageName + ".db";
                } else {
                    dbName = DEFAULT_DB_NAME;
                }
            } catch (Exception e) {
                dbName = DEFAULT_DB_NAME;
            }
        }
        if (schemaVersion == null) {
            schemaVersion = DEFAULT_DB_SCHEMA;
        }
        if (deleteIfNeeded == null) {
            deleteIfNeeded = DEFAULT_DELETE_OPTION;
        }
        //Builder
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder();
        //RealmConfiguration.Builder builder = new RealmConfiguration.Builder(context);
        builder.name(dbName);
        if (deleteIfNeeded) {
            builder.deleteRealmIfMigrationNeeded();
        }
        builder.schemaVersion(schemaVersion);

        //Realm Config
        RealmConfiguration config = builder.build();

        return config;
    }

    /**
     * If no query is set, this will build a query with the class sent in
     *
     * @param realm
     * @param myClass
     * @return
     */
    private RealmQuery buildRealmQuery(Realm realm, @NonNull Class myClass) {
        return RealmQuery.createQuery(realm, myClass);
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private RealmQuery buildRealmQuery(Realm realm, @NonNull TypeToken myClass) {
        return RealmQuery.createQuery(realm, myClass.getRawType());
    }

    //////////////////
    //Misc Utilities//
    //////////////////

    /**
     * Checking for a valid class
     *
     * @param myClass
     * @return
     */
    private static boolean isClassValid(@NonNull Class myClass) {
        if (myClass == null) {
            L.m("Class used to write to the DB was null, please check passed params");
            return false;
        }
        if (!isValidWrite(myClass)) {
            return false;
        }
        if (myClass == RealmObject.class) {
            return true;
        }
        try {
            return myClass.isAssignableFrom(RealmObject.class);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * For checking on valid write / update / delete
     *
     * @param myClass Class Class object being used
     * @return boolean. False if it invalid and should be aborted, true if it is ok to write
     */
    private static boolean isValidWrite(@NonNull Class myClass) {
        if (myClass == null) {
            L.m("Class used to write to the DB was null, please check passed params");
            return false;
        }
        String className = myClass.getName();
        String masterDBObjectName = MasterDatabaseObject.class.getName();
        if (!StringUtilities.isNullOrEmpty(className) &&
                !StringUtilities.isNullOrEmpty(masterDBObjectName)) {
            if (masterDBObjectName.equalsIgnoreCase(className)) {
                L.m("You cannot modify this table from that method. If you want to access the " +
                        "MasterDatabaseObject table, please use the persistObject / dePersistObject /" +
                        " getPersistedObject / getAllPersistedObjects method calls.");
                return false;
            }
        }
        return true;
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private static boolean isValidWrite(@NonNull TypeToken myClass) {
        if (myClass == null) {
            L.m("Class used to write to the DB was null, please check passed params");
            return false;
        }
        String className = myClass.toString();
        String masterDBObjectName = MasterDatabaseObject.class.getName();
        if (!StringUtilities.isNullOrEmpty(className) &&
                !StringUtilities.isNullOrEmpty(masterDBObjectName)) {
            if (masterDBObjectName.equalsIgnoreCase(className)) {
                L.m("You cannot modify this table from that method. If you want to access the " +
                        "MasterDatabaseObject table, please use the persistObject / dePersistObject /" +
                        " getPersistedObject / getAllPersistedObjects method calls.");
                return false;
            }
        }
        return true;
    }

    /**
     * For checking on valid write / update / delete
     *
     * @param myClass Class Class object being used
     * @return boolean. False if it invalid and should be aborted, true if it is ok to write
     */
    private static boolean isValidWrite(@NonNull Class myClass, String customSuffix) {
        if (myClass == null) {
            L.m("Class used to write to the DB was null, please check passed params");
            return false;
        }
        String className = myClass.getName();
        if (customSuffix != null) {
            className = className + customSuffix;
        }
        String masterDBObjectName = MasterDatabaseObject.class.getName();
        if (!StringUtilities.isNullOrEmpty(className) &&
                !StringUtilities.isNullOrEmpty(masterDBObjectName)) {
            if (masterDBObjectName.equalsIgnoreCase(className)) {
                L.m("You cannot modify this table from that method. If you want to access the " +
                        "MasterDatabaseObject table, please use the persistObject / dePersistObject /" +
                        " getPersistedObject / getAllPersistedObjects method calls.");
                return false;
            }
        }
        return true;
    }

    /**
     * Overloaded to allow for {@link TypeToken}
     */
    private static boolean isValidWrite(@NonNull TypeToken myClass, String customSuffix) {
        if (myClass == null) {
            L.m("Class used to write to the DB was null, please check passed params");
            return false;
        }
        String className = myClass.toString();
        if (customSuffix != null) {
            className = className + customSuffix;
        }
        String masterDBObjectName = MasterDatabaseObject.class.getName();
        if (!StringUtilities.isNullOrEmpty(className) &&
                !StringUtilities.isNullOrEmpty(masterDBObjectName)) {
            if (masterDBObjectName.equalsIgnoreCase(className)) {
                L.m("You cannot modify this table from that method. If you want to access the " +
                        "MasterDatabaseObject table, please use the persistObject / dePersistObject /" +
                        " getPersistedObject / getAllPersistedObjects method calls.");
                return false;
            }
        }
        return true;
    }

    /**
     * In case you want to know the name of your db file, this will print the
     * file name in the logcat
     */
    public void printDatabaseName() {
        Realm realm;
        if (realmConfiguration != null) {
            realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        } else {
            realm = buildRealm(context);
        }
        String str = realm.getConfiguration().getRealmFileName();
        if (!StringUtilities.isNullOrEmpty(str)) {
            L.m("Database Name: " + str);
        }
    }

    /**
     * Simple getter for the realm configuration being used. If it does not exist, it will
     * build it.
     *
     * @return
     */
    public RealmConfiguration getRealmConfiguration() {
        try {
            if (this.realmConfiguration == null) {
                this.realmConfiguration = DatabaseUtilities
                        .buildRealmConfig(context, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.realmConfiguration;
    }

    /**
     * Returns a set of the classes (that extends RealmModel) that make up the current table
     * with the realmConfiguration. If the realmconfig is null, it will build one.
     *
     * @return
     */
    public Set<Class<? extends RealmModel>> getDBTableTypes() {
        try {
            if (this.realmConfiguration == null) {
                this.realmConfiguration = DatabaseUtilities
                        .buildRealmConfig(context, null, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (realmConfiguration == null) {
            return null;
        }
        return realmConfiguration.getRealmObjectClasses();
    }

    /**
     * Make a copy of the database and move it to the Download Folder
     *
     * @param dbName The name of the database. NOTE! MUST INCLUDE THE EXTENSION! (IE 'myDB.db')
     * @return boolean, true if it succeeded, false if it did not
     */
    public static boolean copyDBToDownloadDirectory(String dbName) {
        File file = null;
        String packageName = MiscUtilities.getPackageName();
        try {
            file = new File("/data/data/" + packageName + "/files/" + dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;
        try {
            if (file == null) {
                return false;
            }
            fis = new FileInputStream(file);
            long currTime = DateUtilities.getCurrentDateLong();
            fos = new FileOutputStream(FileUtilities.getFileWriteDirectory() +
                    "/Download/" + dbName + "_copy_" + currTime + ".db");
            while (true) {
                int i = fis.read();
                if (i != -1) {
                    fos.write(i);
                } else {
                    break;
                }
            }
            fos.flush();
            success = true;

        } catch (Exception e) {
            e.printStackTrace();
            success = false;

        } finally {
            try {
                fos.close();
                fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (NullPointerException e1) {
                //Null
                success = false;
            }
        }
        return success;
    }

    /**
     * Prints out the database in the logcat. Useful for checking what is happening
     * if you are receiving weird results.
     *
     * @param <T>
     */
    public <T extends RealmObject> void printOutDatabase() {
        L.m("Begin Printout of full Database\n");
        Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
        final RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //Start transaction
                RealmResults<T> results = query.findAll();
                if (results == null) {
                    L.m("Nothing in the Database");
                    return;
                }
                for (T t : results) {
                    if (t != null) {
                        MasterDatabaseObject mdo = (MasterDatabaseObject) t;
                        L.m("Object: " + mdo.getId() + ", JSON == "
                                + mdo.getJsonString() + "\n");
                    }
                }
            }
        });

        try {
            realm.close();
        } catch (Exception e) {
        }
        L.m("\nEnd Printout of full Database");
    }
}
