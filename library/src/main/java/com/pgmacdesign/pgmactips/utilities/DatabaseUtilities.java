package com.pgmacdesign.pgmactips.utilities;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.datamodels.MasterDatabaseObject;
import com.pgmacdesign.pgmactips.misc.CustomAnnotationsBase;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConfig;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.misc.TempString;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
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
import io.realm.annotations.RealmModule;

/**
 * This class serves as a conduit for database functionality. It is utilizing Realm for the
 * database. See the link below for Realm and their Wiki.
 * https://realm.io/docs/java/latest/
 * Created by pmacdowell on 8/18/2016.
 * NOTES:
 * 1) Feel free to cast responses from getPersistedObject even if it is null as null objects
 * can be cast without throwing an error. I caution you against casting them to something
 * else without knowing the response class though. For more info --
 * http://stackoverflow.com/questions/18723596/no-exception-while-type-casting-with-a-null-in-java
 * 2)
 */
@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
		CustomAnnotationsBase.Dependencies.GSON})
public class DatabaseUtilities {
	
	//Misc Type and Typetoken samples
	//final TypeToken MASTER_DB_OBJECT_TYPETOKEN = new TypeToken<List<MasterDatabaseObject>>(){};
	//final Type MASTER_DB_OBJECT_TYPE = MASTER_DB_OBJECT_TYPETOKEN.getType();
	
	//region Static Final Vars
	private static final String CONTEXT_NULL =
			"Context null within DatabaseUtilities. Please call overloaded method to pass in Context";
	private static final String PGMACTIPS_NOT_INITIALIZED =
			"Could not initialize DatabaseUtilities. Either initialize PGMacTipsConfig or call the overloaded constructor to pass in context";
	private static final String COULD_NOT_PERSIST_OBJECT_ILE =
			"Could not persist object into database: ";
	private static final String IF_YOU_WANT_TO_DELETE_ALL_CALL_THIS =
			"If you want to clear all stored / persisted data, please call deleteAllPersistedObjects(true, false)";
	private static final String ATTEMPTING_TO_DELETE =
			"Attempting to delete ";
	private static final String BUT_ITEM_NOT_FOUND =
			", but item not found in DB; nothing to delete.";
	private static final String DELETE_FROM_DB_SUCCEEDED =
			"delete from DB succeeded";
	private static final String DELETE_FAILED =
			"delete failed";
	private static final String DELETING_ENTIRE_DB =
			"Deleting entire database!";
	private static final String DB_EMPTY_UNABLE_TO_MOVE =
			"DB Empty, unable to transfer to encrypted.";
	private static final String ALREADY_CONTAINS_ENCRYPTED_VALUES_BAIL =
			"Database already contains 1 or more encrypted values, unable to bulk transfer.";
	private static final String ENCRYPTION_NOT_ENABLED_UNABLE_TO_MOVE =
			"Encryption is not enabled in the instance you passed. You must initialize a DatabaseUtilities object with a constructor that has a password and salt in order to perform this action.";
	private static final String NO_PRIMARY_KEY_1 =
			"A RealmObject with no PrimaryKey cannot be updated. Does ";
	private static final String NO_PRIMARY_KEY_2 =
			"have a @PrimaryKey designation over a variable?";
	private static final String IO_EXCEPTION_STRING =
			"IOException. Error reading IS";
	private static final String COULD_NOT_ENCRYPT_STRING_OF_TYPE =
			"Could not encrypt String of type: ";
	private static final String QUERY_IS_NULL_CANNOT_DELETE =
			"Query is null, returning false and unable to complete transaction";
	private static final String CLASS_PASSED_WAS_NULL =
			"Class used to write to the DB was null, please check passed params";
	private static final String YOU_CANNOT_MODIFY_THIS_TABLE_FROM_THAT_METHOD =
			"You cannot modify this table from that method. If you want to access the MasterDatabaseObject table, please use the persistObject / dePersistObject / getPersistedObject / getAllPersistedObjects method calls.";
	
	//Defaults. If no configuration is set, these will be used
	private static final String DEFAULT_DB_NAME = PGMacTipsConstants.DB_NAME;
	private static final int DEFAULT_DB_SCHEMA = PGMacTipsConstants.DB_VERSION;
	private static final boolean DEFAULT_DELETE_OPTION = PGMacTipsConstants.DELETE_DB_IF_NEEDED;
	
	//endregion
	
	//region Instance Variables
	private RealmConfiguration realmConfiguration;
	private Context context;
	private boolean loggingEnabled, isEncrypted;
	private TempString password;
	private String salt;
	//endregion
	
	//region Static Vars
	private static Realm queryRealm;
	//endregion
	
	//region Constructors - init
	
	/**
	 * Test constructor
	 */
	private DatabaseUtilities() {
		if (PGMacTipsConfig.getInstance() == null) {
			L.m(PGMACTIPS_NOT_INITIALIZED);
			return;
		}
		this.context = PGMacTipsConfig.getInstance().getContext();
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		this.isEncrypted = false;
		this.salt = null;
		this.password = null;
		this.init(context);
		this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context,
				null, null, null);
	}
	
	/**
	 * Constructor
	 *
	 * @param context Context. Cannot be null
	 */
	@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
			CustomAnnotationsBase.Dependencies.GSON})
	public DatabaseUtilities(@NonNull Context context) {
		this.context = context;
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		this.isEncrypted = false;
		this.salt = null;
		this.password = null;
		this.init(context);
		this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context,
				null, null, null);
	}
	
	/**
	 * Constructor. For more explanation of the input params, see:
	 * {@link DatabaseUtilities#buildRealmConfig(Context, String, Integer, Boolean)}
	 */
	@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
			CustomAnnotationsBase.Dependencies.GSON})
	public DatabaseUtilities(@NonNull Context context, @Nullable String dbName,
	                         @Nullable Integer dbSchemaVersion, @Nullable Boolean deleteDBIfNeeded) {
		this.context = context;
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		this.isEncrypted = false;
		this.salt = null;
		this.password = null;
		this.init(context);
		this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context,
				dbName, dbSchemaVersion, deleteDBIfNeeded);
	}
	
	/**
	 * Constructor using a realm configuration
	 *
	 * @param context            Context
	 * @param realmConfiguration {@link RealmConfiguration} If this is left as null, it will
	 *                           build the default version with hard coded info listed here in
	 *                           this class.
	 */
	@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
			CustomAnnotationsBase.Dependencies.GSON})
	public DatabaseUtilities(@NonNull Context context, RealmConfiguration realmConfiguration) {
		this.context = context;
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		this.isEncrypted = false;
		this.salt = null;
		this.password = null;
		this.init(context);
		this.realmConfiguration = realmConfiguration;
		if (realmConfiguration == null) {
			this.realmConfiguration = DatabaseUtilities.buildRealmConfig(this.context);
		}
	}
	
	/**
	 * Constructor
	 * This constructor requires API level {@link Build.VERSION_CODES#KITKAT}
	 * as it uses Encryption logic that is only available on 19 or higher
	 *
	 * @param context Context. Cannot be null
	 */
	@RequiresApi(value = Build.VERSION_CODES.KITKAT)
	@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
			CustomAnnotationsBase.Dependencies.GSON})
	public DatabaseUtilities(@NonNull Context context, @NonNull String password, @NonNull String salt) {
		this.context = context;
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		if(StringUtilities.isNullOrEmpty(password) || StringUtilities.isNullOrEmpty(salt)){
			this.isEncrypted = false;
			this.salt = null;
			this.password = null;
		} else {
			this.isEncrypted = true;
			this.salt = salt;
			this.password = new TempString(password);
		}
		this.init(context);
		this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context,
				null, null, null);
	}
	
	/**
	 * Constructor. For more explanation of the input params, see:
	 * {@link DatabaseUtilities#buildRealmConfig(Context, String, Integer, Boolean)}
	 * This constructor requires API level {@link Build.VERSION_CODES#KITKAT}
	 * as it uses Encryption logic that is only available on 19 or higher
	 */
	@RequiresApi(value = Build.VERSION_CODES.KITKAT)
	@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
			CustomAnnotationsBase.Dependencies.GSON})
	public DatabaseUtilities(@NonNull Context context, @Nullable String dbName,
	                         @Nullable Integer dbSchemaVersion, @Nullable Boolean deleteDBIfNeeded,
	                         @NonNull String password, @NonNull String salt) {
		this.context = context;
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		if(StringUtilities.isNullOrEmpty(password) || StringUtilities.isNullOrEmpty(salt)){
			this.isEncrypted = false;
			this.salt = null;
			this.password = null;
		} else {
			this.isEncrypted = true;
			this.salt = salt;
			this.password = new TempString(password);
		}
		this.init(context);
		this.realmConfiguration = DatabaseUtilities.buildRealmConfig(context,
				dbName, dbSchemaVersion, deleteDBIfNeeded);
	}
	
	/**
	 * Constructor using a realm configuration
	 * This constructor requires API level {@link Build.VERSION_CODES#KITKAT}
	 * as it uses Encryption logic that is only available on 19 or higher
	 *
	 * @param context            Context
	 * @param realmConfiguration {@link RealmConfiguration} If this is left as null, it will
	 *                           build the default version with hard coded info listed here in
	 *                           this class.
	 */
	@RequiresApi(value = Build.VERSION_CODES.KITKAT)
	@CustomAnnotationsBase.RequiresDependency(requiresDependencies = {CustomAnnotationsBase.Dependencies.Realm,
			CustomAnnotationsBase.Dependencies.GSON})
	public DatabaseUtilities(@NonNull Context context, RealmConfiguration realmConfiguration, @NonNull String password, @NonNull String salt) {
		this.context = context;
		if (this.context == null) {
			L.m(CONTEXT_NULL);
			return;
		}
		if(StringUtilities.isNullOrEmpty(password) || StringUtilities.isNullOrEmpty(salt)){
			this.isEncrypted = false;
			this.salt = null;
			this.password = null;
		} else {
			this.isEncrypted = true;
			this.salt = salt;
			this.password = new TempString(password);
		}
		this.init(context);
		this.realmConfiguration = realmConfiguration;
		if (realmConfiguration == null) {
			this.realmConfiguration = DatabaseUtilities.buildRealmConfig(this.context);
		}
	}
	
	/**
	 * Private initializer
	 * For now, it only handles context
	 * @param context
	 */
	private void init(Context context) {
		Realm.init(context);
	}
	
	/**
	 * Quick method to release memory and close out any open realm instances
	 */
	public void clearInstance(){
		this.closeRealm(DatabaseUtilities.queryRealm);
		this.context = null;
	}
	
	//endregion
	
	//region Insert and Update Methods
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
	public synchronized <T extends RealmObject> boolean executeInsertIntoDB(final T objectToWrite,
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
					if (BoolUtilities.isTrue(appendToObject)) {
						realm.copyToRealmOrUpdate(objectToWrite);
					} else {
						realm.copyToRealm(objectToWrite);
					}
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			L.m(NO_PRIMARY_KEY_1 + myClass.getName() + NO_PRIMARY_KEY_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
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
	public synchronized boolean executeInsertIntoDB(@NonNull final Class myClass,
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
					if (BoolUtilities.isTrue(appendToObject)) {
						realm.createOrUpdateObjectFromJson(myClass, jsonString);
					} else {
						realm.createObjectFromJson(myClass, jsonString);
					}
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			L.m(NO_PRIMARY_KEY_1 + myClass.getName() + NO_PRIMARY_KEY_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
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
	public synchronized boolean executeInsertIntoDB(@NonNull final Class myClass,
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
					if (BoolUtilities.isTrue(appendToObject)) {
						try {
							realm.createOrUpdateObjectFromJson(myClass, is);
						} catch (Exception e) {
							if(loggingEnabled){
								L.m(IO_EXCEPTION_STRING);
								e.printStackTrace();
							}
						}
					} else {
						try {
							realm.createObjectFromJson(myClass, is);
						} catch (IOException e) {
							if(loggingEnabled){
								L.m(IO_EXCEPTION_STRING);
								e.printStackTrace();
							}
						}
					}
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
			
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			L.m(NO_PRIMARY_KEY_1 + myClass.getName() + NO_PRIMARY_KEY_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
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
	public synchronized boolean executeInsertIntoDB(@NonNull final Class myClass,
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
					if (BoolUtilities.isTrue(appendToObject)) {
						realm.createOrUpdateObjectFromJson(myClass, jsonObject);
					} else {
						realm.createObjectFromJson(myClass, jsonObject);
					}
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			L.m(NO_PRIMARY_KEY_1 + myClass.getName() +
					NO_PRIMARY_KEY_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
		}
		return false;
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
	public synchronized boolean persistObject(@NonNull final Class myClass, final Object obj) {
		return (executeInsertIntoDBMaster(myClass, obj));
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized boolean persistObject(@NonNull final TypeToken myClass, final Object obj) {
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
	private synchronized boolean executeInsertIntoDBMaster(@NonNull final Class myClass,
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
		} catch (IllegalArgumentException ile) {
			if(this.loggingEnabled) {
				L.m(COULD_NOT_PERSIST_OBJECT_ILE + ile.getMessage());
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		}
		
		if (jsonString == null) {
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		
		MasterDatabaseObject mdo = new MasterDatabaseObject();
		mdo.setId(className);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(this.isEncryptionEnabled()) {
				try {
					String encryptedString = EncryptionUtilities.encryptString(jsonString, this.password, this.salt);
					if(!StringUtilities.isNullOrEmpty(encryptedString)) {
						mdo.setJsonString(encryptedString);
					} else {
						mdo.setJsonString(jsonString);
					}
				} catch (Exception e) {
					mdo.setJsonString(jsonString);
				}
			} else {
				mdo.setJsonString(jsonString);
			}
		} else {
			mdo.setJsonString(jsonString);
		}
		
		final MasterDatabaseObject mdoFinal = mdo;
		
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(mdoFinal);
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			if(this.loggingEnabled){
				e1.printStackTrace();
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
		}
		return false;
		
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private synchronized boolean executeInsertIntoDBMaster(@NonNull final TypeToken myClass,
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
		} catch (IllegalArgumentException ile) {
			if(this.loggingEnabled){
				L.m(COULD_NOT_PERSIST_OBJECT_ILE + ile.getMessage());
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		}
		
		if (jsonString == null) {
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		
		MasterDatabaseObject mdo = new MasterDatabaseObject();
		mdo.setId(className);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(this.isEncryptionEnabled()) {
				try {
					String encryptedString = EncryptionUtilities.encryptString(jsonString, this.password, this.salt);
					if(!StringUtilities.isNullOrEmpty(encryptedString)) {
						mdo.setJsonString(encryptedString);
					} else {
						mdo.setJsonString(jsonString);
					}
				} catch (Exception e) {
					mdo.setJsonString(jsonString);
				}
			} else {
				mdo.setJsonString(jsonString);
			}
		} else {
			mdo.setJsonString(jsonString);
		}
		final MasterDatabaseObject mdoFinal = mdo;
		
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(mdoFinal);
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			if(this.loggingEnabled){
				e1.printStackTrace();
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
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
	public synchronized boolean persistObjectCustom(@NonNull final Class myClass,
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
		} catch (IllegalArgumentException ile) {
			if(this.loggingEnabled){
				L.m(COULD_NOT_PERSIST_OBJECT_ILE + ile.getMessage());
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		}
		
		if (jsonString == null) {
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		
		MasterDatabaseObject mdo = new MasterDatabaseObject();
		if (customSuffix != null) {
			className = className + customSuffix;
		}
		mdo.setId(className);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(this.isEncryptionEnabled()) {
				try {
					String encryptedString = EncryptionUtilities.encryptString(jsonString, this.password, this.salt);
					if(!StringUtilities.isNullOrEmpty(encryptedString)) {
						mdo.setJsonString(encryptedString);
					} else {
						mdo.setJsonString(jsonString);
					}
				} catch (Exception e) {
					mdo.setJsonString(jsonString);
				}
			} else {
				mdo.setJsonString(jsonString);
			}
		} else {
			mdo.setJsonString(jsonString);
		}
		
		final MasterDatabaseObject mdoFinal = mdo;
		
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(mdoFinal);
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			if(this.loggingEnabled){
				e1.printStackTrace();
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
		}
		return false;
		
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized boolean persistObjectCustom(@NonNull final TypeToken myClass,
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
		String className = myClass.getType().toString();
		String jsonString = null;
		try {
			jsonString = new Gson().toJson(obj, myClass.getType());
		} catch (IllegalArgumentException ile) {
			if(this.loggingEnabled){
				L.m(COULD_NOT_PERSIST_OBJECT_ILE + ile.getMessage());
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		}
		
		if (jsonString == null) {
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		
		MasterDatabaseObject mdo = new MasterDatabaseObject();
		if (customSuffix != null) {
			className = className + customSuffix;
		}
		mdo.setId(className);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(this.isEncryptionEnabled()) {
				try {
					String encryptedString = EncryptionUtilities.encryptString(jsonString, this.password, this.salt);
					if(!StringUtilities.isNullOrEmpty(encryptedString)) {
						mdo.setJsonString(encryptedString);
					} else {
						mdo.setJsonString(jsonString);
					}
				} catch (Exception e) {
					mdo.setJsonString(jsonString);
				}
			} else {
				mdo.setJsonString(jsonString);
			}
		} else {
			mdo.setJsonString(jsonString);
		}
		
		final MasterDatabaseObject mdoFinal = mdo;
		
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.copyToRealmOrUpdate(mdoFinal);
				}
			});
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			if(this.loggingEnabled){
				e1.printStackTrace();
			}
		} catch (Exception e) {
			if(this.loggingEnabled){
				e.printStackTrace();
			}
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
		}
		return false;
		
	}
	//endregion
	
	//region Delete Methods
	
	/**
	 * Delete an object from the Master Table. For deleting persisted objects
	 *
	 * @param myClass Class will be converted to string and used to reference the id / primary
	 *                key to find the item / row.
	 * @return Boolean, true if it succeeded, false if it did not
	 */
	public synchronized boolean dePersistObject(@NonNull final Class myClass) {
		return this.deleteFromMasterDB(myClass);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized boolean dePersistObject(@NonNull final TypeToken myClass) {
		return this.deleteFromMasterDB(myClass);
	}
	
	//Overloaded for naming simplicity since I forget what things are called all the time!
	public synchronized boolean deletePersistedObject(@NonNull final Class myClass) {
		return this.deleteFromMasterDB(myClass);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized boolean deletePersistedObject(@NonNull final TypeToken myClass) {
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
	public synchronized boolean dePersistObjectCustom(@NonNull final Class myClass, final String customSuffix) {
		return this.deleteFromMasterDB(myClass, customSuffix);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized boolean dePersistObjectCustom(@NonNull final TypeToken myClass, final String customSuffix) {
		return this.deleteFromMasterDB(myClass, customSuffix);
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
	public synchronized boolean deletePersistedObjectCustom(@NonNull final Class myClass, final String customSuffix) {
		return this.deleteFromMasterDB(myClass, customSuffix);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized boolean deletePersistedObjectCustom(@NonNull final TypeToken myClass, final String customSuffix) {
		return this.deleteFromMasterDB(myClass, customSuffix);
	}
	
	/**
	 * Delete an object from the Master Table. For deleting persisted objects
	 *
	 * @param myClass Class will be converted to string and used to reference the id / primary
	 *                key to find the item / row.
	 * @return Boolean, true if it succeeded, false if it did not
	 */
	private synchronized boolean deleteFromMasterDB(@NonNull final Class myClass) {
		return this.deleteFromMasterDB(myClass, null);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private synchronized boolean deleteFromMasterDB(@NonNull final TypeToken myClass) {
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
	 *                     master object table with an id (primary key) that matches that custom
	 *                     suffix. Use that same suffix again to delete it from the db.
	 * @param <T>          T extends RealmObject
	 * @return Boolean, true if it succeeded, false if it did not
	 */
	private synchronized <T extends RealmObject> boolean deleteFromMasterDB(@NonNull final Class myClass,
	                                                           final String customSuffix) {
		return this.deleteFromMasterDBOverride(myClass, customSuffix, false);
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
	 *                     master object table with an id (primary key) that matches that custom
	 *                     suffix. Use that same suffix again to delete it from the db.
	 * @param <T>          T extends RealmObject
	 * @return Boolean, true if it succeeded, false if it did not
	 */
	private synchronized <T extends RealmObject> boolean deleteFromMasterDBOverride(@NonNull final Class myClass,
	                                                           final String customSuffix,
	                                                           boolean overrideCheck) {
		if (myClass == null) {
			return false;
		}
		if(!overrideCheck) {
			if (!DatabaseUtilities.isValidWrite(myClass, customSuffix)) {
				L.m(IF_YOU_WANT_TO_DELETE_ALL_CALL_THIS);
				return false;
			}
		}
		//Class name String
		final String myClassName = myClass.getName();
		//Returned object from the master search
		try {
			Object obj = this.queryDatabaseMasterSingle(myClass);
			if (obj == null) {
				Object obj2 = this.getPersistedObjectCustom(myClass, customSuffix);
				if (obj2 == null) {
					//IF it is null, it has already been deleted, return true and move on
					if (this.loggingEnabled) {
						L.m(ATTEMPTING_TO_DELETE + myClassName +
								((StringUtilities.isNullOrEmpty(customSuffix) ? "" : customSuffix)) + BUT_ITEM_NOT_FOUND);
					}
					return true;
				}
			}
		} catch (IllegalStateException ile){
			if(this.loggingEnabled){
				ile.printStackTrace();
			}
		}
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		final RealmQuery<MasterDatabaseObject> query;
		try {
			query = realm.where(MasterDatabaseObject.class);
		} catch (IllegalStateException il) {
			if(this.loggingEnabled){
				il.printStackTrace();
			}
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		if(query == null){
			if(this.loggingEnabled){
				L.m(QUERY_IS_NULL_CANNOT_DELETE);
			}
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				//Start transaction
				RealmResults<MasterDatabaseObject> results = query.findAll();
				if (results == null) {
					return;
				}
				for (MasterDatabaseObject mdo : results) {
					if (mdo != null) {
						String id = mdo.getId();
						if (!StringUtilities.isNullOrEmpty(id)) {
							if (!StringUtilities.isNullOrEmpty(customSuffix)) {
								String customId = myClassName + customSuffix;
								//Check if ID Matches package name
								if (customId.equals(id)) {
									try {
										mdo.deleteFromRealm();
										if(loggingEnabled){
											L.m(DELETE_FROM_DB_SUCCEEDED);
										}
										return;
									} catch (Exception e) {
										if(loggingEnabled){
											L.m(DELETE_FAILED);
											e.printStackTrace();
										}
									}
								}
							} else {
								//Check if ID Matches package name
								if (myClassName.equals(id)) {
									try {
										mdo.deleteFromRealm();
										if(loggingEnabled){
											L.m(DELETE_FROM_DB_SUCCEEDED);
										}
										return;
									} catch (Exception e) {
										if(loggingEnabled){
											L.m(DELETE_FAILED);
											e.printStackTrace();
										}
									}
								}
							}
							
						}
					}
				}
			}
		});
		
		DatabaseUtilities.this.closeRealm(realm);
		return true;
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private synchronized <T extends RealmObject> boolean deleteFromMasterDB(@NonNull final TypeToken myClass,
	                                                           final String customSuffix) {
		return deleteFromMasterDBOverride(myClass, customSuffix, false);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private synchronized <T extends RealmObject> boolean deleteFromMasterDBOverride(@NonNull final TypeToken myClass,
	                                                           final String customSuffix,
	                                                           boolean overrideCheck) {
		
		if (myClass == null) {
			return false;
		}
		if(!overrideCheck) {
			if (!DatabaseUtilities.isValidWrite(myClass, customSuffix)) {
				L.m(IF_YOU_WANT_TO_DELETE_ALL_CALL_THIS);
				return false;
			}
		}
		//Class name String
		final String myClassName = myClass.getType().toString();
		
		//Returned object from the master search
		try {
			Object obj = this.queryDatabaseMasterSingle(myClass);
			if (obj == null) {
				Object obj2 = this.getPersistedObjectCustom(myClass, customSuffix);
				if (obj2 == null) {
					//IF it is null, it has already been deleted, return true and move on
					if (this.loggingEnabled) {
						L.m(ATTEMPTING_TO_DELETE + myClassName +
								((StringUtilities.isNullOrEmpty(customSuffix) ? "" : customSuffix)) + BUT_ITEM_NOT_FOUND);
					}
					return true;
				}
			}
		} catch (IllegalStateException ile){
			if(this.loggingEnabled){
				ile.printStackTrace();
			}
		}
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		final RealmQuery<MasterDatabaseObject> query;
		try {
			query = realm.where(MasterDatabaseObject.class);
		} catch (IllegalStateException il) {
			if(loggingEnabled){
				il.printStackTrace();
			}
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		if(query == null){
			DatabaseUtilities.this.closeRealm(realm);
			return false;
		}
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				//Start transaction
				RealmResults<MasterDatabaseObject> results = query.findAll();
				if (results == null) {
					return;
				}
				for (MasterDatabaseObject mdo : results) {
					if (mdo != null) {
						String id = mdo.getId();
						if (!StringUtilities.isNullOrEmpty(id)) {
							if (!StringUtilities.isNullOrEmpty(customSuffix)) {
								String customId = myClassName + customSuffix;
								//Check if ID Matches package name
								if (customId.equals(id)) {
									try {
										mdo.deleteFromRealm();
										if(loggingEnabled){
											L.m(DELETE_FROM_DB_SUCCEEDED);
										}
										return;
									} catch (Exception e) {
										if(loggingEnabled){
											L.m(DELETE_FAILED);
											e.printStackTrace();
										}
									}
								}
							} else {
								//Check if ID Matches package name
								if (myClassName.equals(id)) {
									try {
										mdo.deleteFromRealm();
										if(loggingEnabled){
											L.m(DELETE_FROM_DB_SUCCEEDED);
										}
										return;
									} catch (Exception e) {
										if(loggingEnabled){
											L.m(DELETE_FAILED);
											e.printStackTrace();
										}
									}
								}
							}
							
						}
					}
				}
			}
		});
		
		DatabaseUtilities.this.closeRealm(realm);
		return true;
		
	}
	
	/**
	 * DANGER ZONE!
	 * This will wipe the entire database (Of the configuration set. If you have
	 * multiple RealmConfigurations, you will need to create a DatabaseUtilities Object for
	 * each one and loop this method to delete them all).
	 *
	 * @param areYouSure    Pass true to confirm wipe
	 * @param areYouNotSure Pass false to confirm wipe
	 * @return Boolean of success or not
	 */
	public synchronized <T extends  RealmObject>  boolean deleteEntireDB(boolean areYouSure, boolean areYouNotSure) {
		if (!areYouSure) {
			return false;
		}
		if (areYouNotSure) {
			return false;
		}
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		try {
			if(loggingEnabled){
				L.m(DELETING_ENTIRE_DB);
			}
			final RealmResults<MasterDatabaseObject> results = realm.where(MasterDatabaseObject.class).findAll();
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					boolean b = results.deleteAllFromRealm();
				}
			});
		} catch (Exception e){
			if(loggingEnabled){
				e.printStackTrace();
			}
		}
		try {
			this.closeRealm(realm);
			DatabaseUtilities.clearRealmQueryInstance();
			return (deleteRealmFileInStorage(realmConfiguration));
		} catch (Exception e) {
			if(loggingEnabled){
				e.printStackTrace();
			}
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
	public synchronized <T extends RealmObject> boolean deleteAllPersistedObjects(boolean areYouSure,
	                                                                 boolean areYouNotSure) {
		
		if (!areYouSure) {
			return false;
		}
		if (areYouNotSure) {
			return false;
		}
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		//final RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class); //Old version, 3.0.0
		final RealmQuery<MasterDatabaseObject> query;
		try {
			query = realm.where(MasterDatabaseObject.class);
		} catch (IllegalStateException il) {
			il.printStackTrace();
			return false;
		}
		if(query == null){
			return false;
		}
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				//Start transaction
				RealmResults<MasterDatabaseObject> results = query.findAll();
				for (MasterDatabaseObject t : results) {
					if (t != null) {
						try {
							t.deleteFromRealm();
						} catch (Exception e) {
							if(loggingEnabled){
								L.m(DELETE_FAILED);
							}
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
	public synchronized <T extends RealmObject> boolean executeDeleteFromDB(RealmQuery query,
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
			DatabaseUtilities.this.closeRealm(realm);
			return true;
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			L.m(NO_PRIMARY_KEY_1 + myClass.getName() + NO_PRIMARY_KEY_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
		}
		return false;
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized <T extends RealmObject> boolean executeDeleteFromDB(RealmQuery query,
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
			return true;
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			L.m(NO_PRIMARY_KEY_1 + myClass.getType().toString() +
					NO_PRIMARY_KEY_2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtilities.this.closeRealm(realm);
		}
		return false;
	}
	
	//endregion
	
	//region Query Methods
	
	/**
	 * Query the master table in the database
	 *
	 * @param myClass Class (row / id) to query
	 * @return returns an Object that was pulled from the DB. If nothing found, it will return null.
	 */
	public synchronized Object getPersistedObject(@NonNull Class myClass) {
		return this.queryDatabaseMasterSingle(myClass);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized Object getPersistedObject(@NonNull TypeToken myClass) {
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
	public synchronized Map<String, String> getAllPersistedObjects() {
		List<MasterDatabaseObject> masterDatabaseObjects = this.queryDatabaseMasterAll();
		if (MiscUtilities.isListNullOrEmpty(masterDatabaseObjects)) {
			return new HashMap<>();
		}
		Map<String, String> myMap = new HashMap<>();
		for (MasterDatabaseObject mdo : masterDatabaseObjects) {
			String id = mdo.getId();
			String json = DatabaseUtilities.this.cleanMDOString(mdo.getJsonString());
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
	public synchronized Object getPersistedObjectCustom(@NonNull final Class myClass,
	                                       final String customSuffix) {
		return this.queryDatabaseMasterSingle(myClass, customSuffix);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized Object getPersistedObjectCustom(@NonNull final TypeToken myClass,
	                                       final String customSuffix) {
		return this.queryDatabaseMasterSingle(myClass, customSuffix);
	}
	
	/**
	 * Query the master table in the database
	 *
	 * @param myClass Class (row / id) to query
	 * @return returns an Object that was pulled from the DB. If nothing found, it will return null.
	 */
	private synchronized Object queryDatabaseMasterSingle(@NonNull final Class myClass) {
		
		return this.queryDatabaseMasterSingle(myClass, null);
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private synchronized Object queryDatabaseMasterSingle(@NonNull final TypeToken myClass) {
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
	private synchronized Object queryDatabaseMasterSingle(@NonNull final Class myClass,
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
				String str = DatabaseUtilities.this.cleanMDOString(jsonString);
				Object obj = new Gson().fromJson(str, myClass);
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
	private synchronized Object queryDatabaseMasterSingle(@NonNull final TypeToken myClass,
	                                         final String customSuffix) {
		String className = myClass.getType().toString();
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
				String str = DatabaseUtilities.this.cleanMDOString(jsonString);
				Object obj = new Gson().fromJson(str, myClass.getType());
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
				//In case they pass the wrong class name
				return null;
			}
		}
	}
	
	/**
	 * Query the master table in the database for all rows.
	 * Note! This was causing errors because the Realm object was being instantiated and
	 * simultaneously closing when a full Master DB wipe happened within nanoseconds. As a result,
	 * I switched the Realm object to a singleton for this method only and it is cleared whenever
	 * the deleteDB is called; values in milliseconds was: 294863303301582 vs 294863303147936,
	 * a difference of 0.153646 milliseconds
	 *
	 * @param <T> T extends RealmModel
	 * @return returns a list of Objects that was pulled from the DB. If nothing found,
	 * it will return an initialized, but empty, list.
	 */
	private synchronized <T extends RealmModel> List<MasterDatabaseObject> queryDatabaseMasterAll() {
		final RealmQuery<MasterDatabaseObject> query;
		try {
			query = DatabaseUtilities.buildRealmQueryOnly(this.realmConfiguration).where(MasterDatabaseObject.class);
		} catch (IllegalStateException il) {
			il.printStackTrace();
			return new ArrayList<>();
		}
		if(query == null){
			return new ArrayList<>();
		}
		//Start transaction
		RealmResults<MasterDatabaseObject> results = query.findAll();
		if (results.size() <= 0) {
			return new ArrayList<>();
		}
		try {
			List<MasterDatabaseObject> masterDatabaseObjectList = new ArrayList<MasterDatabaseObject>();
			for (MasterDatabaseObject mdo : results) {
				if (mdo != null) {
					// TODO: 2019-11-05 add cleanMDOString
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
	public synchronized <T extends RealmModel> Object queryDatabaseSingle(RealmQuery<T> passedQuery,
	                                                         @NonNull Class myClass) {
		
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		if (passedQuery == null) {
			passedQuery = this.buildRealmQuery(realm, myClass);
		}
		
		//Start transaction
		RealmResults<T> results = passedQuery.findAll();
		
		if (results != null) {
			Object object = results.get(0);
			DatabaseUtilities.this.closeRealm(realm);
			return object;
		}
		
		return null;
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized <T extends RealmModel> Object queryDatabaseSingle(RealmQuery<T> passedQuery,
	                                                         @NonNull TypeToken myClass) {
		
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		if (passedQuery == null) {
			passedQuery = this.buildRealmQuery(realm, myClass);
		}
		
		//Start transaction
		RealmResults<T> results = passedQuery.findAll();
		
		if (results != null) {
			Object object = results.get(0);
			DatabaseUtilities.this.closeRealm(realm);
			return object;
		}
		
		DatabaseUtilities.this.closeRealm(realm);
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
	public synchronized <T extends RealmModel> List<Object> queryDatabaseList(RealmQuery<T> passedQuery,
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
		DatabaseUtilities.this.closeRealm(realm);
		return objects;
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	public synchronized <T extends RealmModel> List<Object> queryDatabaseList(RealmQuery<T> passedQuery,
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
		DatabaseUtilities.this.closeRealm(realm);
		return objects;
	}
	
	//endregion
	
	//region Realm Object, Configuration, and Query Utilities
	
	/**
	 * This method will build the Realm object using pre-programmed hard-coded info for the
	 * version and name. If you want to make your own custom version, please use the overloaded
	 * method which takes in a RealmConfiguration parameter
	 *
	 * @param context Context
	 * @return Realm object
	 */
	public static synchronized Realm buildRealm(Context context) {
		RealmConfiguration config = DatabaseUtilities.buildRealmConfig(context);
		Realm realm = Realm.getInstance(config);
		return realm;
	}
	
	/**
	 * Build a Realm object and return it
	 * Not making a singleton version as per the docs:
	 * "Realm instances cannot be used across different threads. This means that you have to open an instance on each thread
	 * you want to use Realm" {@link Realm}
	 *
	 * @param realmConfiguration {@link RealmConfiguration}
	 * @return Realm object {@link Realm}
	 */
	public static synchronized Realm buildRealm(@NonNull RealmConfiguration realmConfiguration) {
		Realm realm = null;
		if (realmConfiguration != null) {
			realm = Realm.getInstance(realmConfiguration);
		}
		return realm;
	}
	
	/**
	 * Build a Realm object and return it
	 * Not making a singleton version as per the docs:
	 * "Realm instances cannot be used across different threads. This means that you have to open an instance on each thread
	 * you want to use Realm" {@link Realm}
	 *
	 * @param realmConfiguration {@link RealmConfiguration}
	 * @return Realm object {@link Realm}
	 */
	private static synchronized Realm buildRealmQueryOnly(@NonNull RealmConfiguration realmConfiguration) {
		try {
			if (DatabaseUtilities.queryRealm == null) {
				queryRealm = Realm.getInstance(realmConfiguration);
			}
			if (DatabaseUtilities.queryRealm != null) {
				if (DatabaseUtilities.queryRealm.isClosed()) {
					DatabaseUtilities.queryRealm = Realm.getInstance(realmConfiguration);
				}
			}
		} catch (IllegalStateException ile){
			//https://github.com/realm/realm-java/issues/3806 && https://github.com/realm/realm-java/issues/4409
			return DatabaseUtilities.buildRealm(realmConfiguration);
		}
		return DatabaseUtilities.queryRealm;
	}
	
	/**
	 * Clear the query realm active instance
	 */
	private static synchronized void clearRealmQueryInstance(){
		try {
			if (DatabaseUtilities.queryRealm != null) {
				DatabaseUtilities.queryRealm.close();
			}
			DatabaseUtilities.queryRealm = null;
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will build the Realm configuration object using pre-programmed hard-coded
	 * info for the version and name. If you want to make your own custom version,
	 * please use the overloaded method which takes in multiple parameters
	 *
	 * @param context Context
	 * @return RealmConfiguration object
	 */
	public static synchronized RealmConfiguration buildRealmConfig(Context context) {
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
	public static synchronized RealmConfiguration buildRealmConfig(@NonNull Context context,
	                                                  @Nullable String dbName,
	                                                  @Nullable Integer schemaVersion,
	                                                  @Nullable Boolean deleteIfNeeded) {
		if (context == null) {
			L.m(CONTEXT_NULL);
			return null;
		}
		Realm.init(context);
		if (StringUtilities.isNullOrEmpty(dbName)) {
			boolean setNameSuccess = false;
			try {
				String s = PGMacTipsConfig.getInstance().getDefaultDatabaseName();
				if (!StringUtilities.isNullOrEmpty(s)) {
					dbName = s;
					setNameSuccess = true;
				}
			} catch (Exception e) {
			}
			if (!setNameSuccess) {
				try {
					String packageName = SystemUtilities.getPackageName(context);
					if (!StringUtilities.isNullOrEmpty(packageName)) {
						dbName = packageName + ".realm";
						setNameSuccess = true;
					}
				} catch (Exception e1) {
				}
			}
			if (!setNameSuccess) {
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
		builder.name(dbName);
		if (deleteIfNeeded) {
			builder.deleteRealmIfMigrationNeeded();
		}
		builder.schemaVersion(schemaVersion);
		builder.modules(new DatabaseUtilities.PGMacTipsModule());
		//Realm Config
		RealmConfiguration config = builder.build();
		
		return config;
	}
	
	/**
	 * Simple getter for the realm configuration being used. If it does not exist, it will
	 * build it.
	 *
	 * @return
	 */
	public synchronized RealmConfiguration getRealmConfiguration() {
		try {
			if (this.realmConfiguration == null) {
				this.realmConfiguration = DatabaseUtilities.buildRealmConfig(
						context, null, null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.realmConfiguration;
	}
	
	//endregion
	
	//region Private Misc Utilities
	
	/**
	 * Quick checker for whether encryption is enabled.
	 * @return True if it is, false if it is not
	 */
	private synchronized boolean isEncryptionEnabled(){
		if(!this.isEncrypted){
			return false;
		}
		if(StringUtilities.isNullOrEmpty(this.salt)){
			return false;
		}
		if(this.password == null){
			return false;
		}
		if(StringUtilities.isNullOrEmpty(this.password.getTempStringData())){
			return false;
		}
		return true;
	}
	
	/**
	 * Clean the String and return the decrypted String or the current one if it is already
	 * decrypted or does not have it enabled.
	 * @param jsonString String to decrypt or return
	 * @return Clean String to use
	 */
	private synchronized String cleanMDOString(String jsonString){
		if(StringUtilities.isNullOrEmpty(jsonString)){
			return jsonString;
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			if(this.isEncryptionEnabled()){
				try {
					String s = EncryptionUtilities.decryptString(jsonString, this.password, this.salt);
					if(!StringUtilities.isNullOrEmpty(s)){
						return s;
					}
				} catch (Exception e){}
				return jsonString;
			} else {
				return jsonString;
			}
		} else {
			return jsonString;
		}
	}
	
	/**
	 * Simple method to close the realm instance
	 * @param realm
	 */
	private synchronized void closeRealm(Realm realm){
		if(realm == null){
			return;
		}
		try {
			realm.close();
		} catch (Exception e) {
			if(loggingEnabled){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * If no query is set, this will build a query with the class sent in
	 *
	 * @param realm
	 * @param myClass
	 * @return
	 */
	private synchronized RealmQuery buildRealmQuery(Realm realm, @NonNull Class myClass) {
		if (realm == null) {
			return null;
		}
		try {
			return realm.where(myClass);
		} catch (IllegalStateException il) {
			il.printStackTrace();
			return null;
		}
		//return RealmQuery.createQuery(realm, myClass); //Old version, 3.0.0
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private synchronized RealmQuery buildRealmQuery(Realm realm, @NonNull TypeToken myClass) {
		if (realm == null) {
			return null;
		}
		try {
			return realm.where(myClass.getRawType());
		} catch (IllegalStateException il) {
			il.printStackTrace();
			return null;
		}
		//return RealmQuery.createQuery(realm, myClass.getRawType()); //Old version, 3.0.0
	}
	
	/**
	 * Checking for a valid class
	 *
	 * @param myClass
	 * @return
	 */
	private static synchronized boolean isClassValid(@NonNull Class myClass) {
		if (myClass == null) {
			L.m(CLASS_PASSED_WAS_NULL);
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
	private static synchronized boolean isValidWrite(@NonNull Class myClass) {
		if (myClass == null) {
			L.m(CLASS_PASSED_WAS_NULL);
			return false;
		}
		String className = myClass.getName();
		String masterDBObjectName = MasterDatabaseObject.class.getName();
		if (!StringUtilities.isNullOrEmpty(className) &&
				!StringUtilities.isNullOrEmpty(masterDBObjectName)) {
			if (masterDBObjectName.equalsIgnoreCase(className)) {
				L.m(YOU_CANNOT_MODIFY_THIS_TABLE_FROM_THAT_METHOD);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private static synchronized boolean isValidWrite(@NonNull TypeToken myClass) {
		if (myClass == null) {
			L.m(CLASS_PASSED_WAS_NULL);
			return false;
		}
		String className = myClass.toString();
		String masterDBObjectName = MasterDatabaseObject.class.getName();
		if (!StringUtilities.isNullOrEmpty(className) &&
				!StringUtilities.isNullOrEmpty(masterDBObjectName)) {
			if (masterDBObjectName.equalsIgnoreCase(className)) {
				L.m(YOU_CANNOT_MODIFY_THIS_TABLE_FROM_THAT_METHOD);
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
	private static synchronized boolean isValidWrite(@NonNull Class myClass, String customSuffix) {
		if (myClass == null) {
			L.m(CLASS_PASSED_WAS_NULL);
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
				L.m(YOU_CANNOT_MODIFY_THIS_TABLE_FROM_THAT_METHOD);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Overloaded to allow for {@link TypeToken}
	 */
	private static synchronized boolean isValidWrite(@NonNull TypeToken myClass, String customSuffix) {
		if (myClass == null) {
			L.m(CLASS_PASSED_WAS_NULL);
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
				L.m(YOU_CANNOT_MODIFY_THIS_TABLE_FROM_THAT_METHOD);
				return false;
			}
		}
		return true;
	}
	
	//endregion
	
	//region Public Misc Utilities
	
	/**
	 * Checks if the values in the DB are already encrypted
	 * @return
	 */
	public synchronized boolean areDBValuesEncrypted(){
		List<MasterDatabaseObject> masterDatabaseObjects = this.queryDatabaseMasterAll();
		if(MiscUtilities.isListNullOrEmpty(masterDatabaseObjects)){
			return false;
		}
		boolean isAlreadyEncrypted = false;
		for(MasterDatabaseObject mdo : masterDatabaseObjects){
			if(mdo == null){
				continue;
			}
			if(StringUtilities.isNullOrEmpty(mdo.getJsonString())){
				continue;
			}
			String str = mdo.getJsonString();
			if(EncryptionUtilities.isHexString(str)){
				isAlreadyEncrypted = true;
				break;
			}
		}
		return isAlreadyEncrypted;
	}
	
	/**
	 * In case you want to know the name of your db file, this will print the
	 * file name in the logcat
	 */
	public synchronized void printDatabaseName() {
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
		DatabaseUtilities.this.closeRealm(realm);
	}
	
	/**
	 * Returns a set of the classes (that extends RealmModel) that make up the current table
	 * with the realmConfiguration. If the realmconfig is null, it will build one.
	 *
	 * @return
	 */
	public synchronized Set<Class<? extends RealmModel>> getDBTableTypes() {
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
	public static synchronized boolean copyDBToDownloadDirectory(Context context, String dbName) {
		File file = null;
		String packageName = SystemUtilities.getPackageName();
		try {
			file = new File(context.getFilesDir().getPath() + "/" + packageName + "/files/" + dbName);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				file = new File(Environment.getExternalStorageDirectory() + "/" + packageName + "/" + dbName);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
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
	 * Move the database from a non-encrypted version to an encrypted version.
	 * Some important notes: If any or all of the values are already
	 * encrypted, it will not complete the transfer so as to prevent values from
	 * being encrypted twice and therefore become unreadable.
	 *
	 * @param dbUtils
	 * @return
	 */
	@RequiresApi(value = Build.VERSION_CODES.KITKAT)
	public static synchronized boolean moveDBToEncryptedVersion(@NonNull DatabaseUtilities dbUtils){
		if(dbUtils == null){
			return false;
		}
		if(!dbUtils.isEncryptionEnabled()){
			L.m(ENCRYPTION_NOT_ENABLED_UNABLE_TO_MOVE);
			return false;
		}
		List<MasterDatabaseObject> masterDatabaseObjects = dbUtils.queryDatabaseMasterAll();
		if(MiscUtilities.isListNullOrEmpty(masterDatabaseObjects)){
			L.m(DB_EMPTY_UNABLE_TO_MOVE);
			return false;
		}
		boolean isAlreadyEncrypted = false;
		for(MasterDatabaseObject mdo : masterDatabaseObjects){
			if(mdo == null){
				continue;
			}
			if(StringUtilities.isNullOrEmpty(mdo.getJsonString())){
				continue;
			}
			String str = mdo.getJsonString();
			if(EncryptionUtilities.isHexString(str)){
				isAlreadyEncrypted = true;
				break;
			}
		}
		if(isAlreadyEncrypted){
			L.m(ALREADY_CONTAINS_ENCRYPTED_VALUES_BAIL);
			return false;
		}
		List<MasterDatabaseObject> toWrite = new ArrayList<>();
		for(MasterDatabaseObject mdo : masterDatabaseObjects){
			if(mdo == null){
				continue;
			}
			if(StringUtilities.isNullOrEmpty(mdo.getId())){
				continue;
			}
			String str = mdo.getJsonString();
			if(StringUtilities.isNullOrEmpty(str)){
				continue;
			}
			MasterDatabaseObject mdo1 = new MasterDatabaseObject();
			mdo1.setId(mdo.getId());
			String sstr = null;
			try {
				sstr = EncryptionUtilities.encryptString(str, dbUtils.password, dbUtils.salt);
			} catch (Exception e){}
			if(StringUtilities.isNullOrEmpty(sstr)){
				L.m(COULD_NOT_ENCRYPT_STRING_OF_TYPE + mdo.getId());
				continue;
			}
			mdo1.setJsonString(sstr);
			toWrite.add(mdo1);
		}
		if(MiscUtilities.isListNullOrEmpty(toWrite)){
			return false;
		}
		Realm realm = DatabaseUtilities.buildRealm(dbUtils.realmConfiguration);
		try {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.delete(MasterDatabaseObject.class);
				}
			});
		} catch (IllegalArgumentException e1) {
			if(dbUtils.loggingEnabled){
				e1.printStackTrace();
			}
		} catch (Exception e) {
			if(dbUtils.loggingEnabled){
				e.printStackTrace();
			}
		}
		for(final MasterDatabaseObject mdoFinal : toWrite){
			try {
				realm.executeTransaction(new Realm.Transaction() {
					@Override
					public void execute(Realm realm) {
						realm.copyToRealmOrUpdate(mdoFinal);
					}
				});
			} catch (IllegalArgumentException e1) {
				if(dbUtils.loggingEnabled){
					e1.printStackTrace();
				}
			} catch (Exception e) {
				if(dbUtils.loggingEnabled){
					e.printStackTrace();
				}
			}
		}
		dbUtils.closeRealm(realm);
		return true;
	}
	
	/**
	 * Prints out the database in the logcat. Useful for checking what is happening
	 * if you are receiving weird results.
	 *
	 */
	public synchronized void printOutDatabase() {
		printOutDatabase(false);
	}
	
	/**
	 * Prints out the database in the logcat. Useful for checking what is happening
	 * if you are receiving weird results.
	 *
	 * @param decryptEncryptedValuesForPrint If true, will decrypt and print the encrypted
	 *                                       values in the DB, else, will not decrypt them
	 *                                       and will instead print the encrypted string.
	 */
	public synchronized void printOutDatabase(final boolean decryptEncryptedValuesForPrint) {
		L.m("Begin Printout of full Database\n");
		if(decryptEncryptedValuesForPrint){
			L.m("Note! Decrypting and printing encrypted values");
		}
		Realm realm = DatabaseUtilities.buildRealm(this.realmConfiguration);
		//final RealmQuery query = RealmQuery.createQuery(realm, MasterDatabaseObject.class); //Old version, 3.0.0
		final RealmQuery<MasterDatabaseObject> query;
		try {
			query = realm.where(MasterDatabaseObject.class);
		} catch (IllegalStateException il) {
			il.printStackTrace();
			DatabaseUtilities.this.closeRealm(realm);
			return;
		}
		if(query == null){
			DatabaseUtilities.this.closeRealm(realm);
			return;
		}
		realm.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				//Start transaction
				RealmResults<MasterDatabaseObject> results = query.findAll();
				if (results == null) {
					L.m("Nothing in the Database");
					return;
				}
				for (MasterDatabaseObject mdo : results) {
					if (mdo != null) {
						String str;
						if(decryptEncryptedValuesForPrint){
							str = DatabaseUtilities.this.cleanMDOString(mdo.getJsonString());
						} else {
							str = mdo.getJsonString();
						}
						L.m("Object: " + mdo.getId() + ", JSON == "
								+ str + "\n");
					}
				}
			}
		});
		DatabaseUtilities.this.closeRealm(realm);
		L.m("\nEnd Printout of full Database");
	}
	
	//endregion
	
	//region Public Misc Methods
	
	/**
	 * Enable logging (Defaults to disabled)
	 */
	public void enableLogging(){
		this.setLogging(true);
	}
	
	/**
	 * Disable logging (Defaults to disabled)
	 */
	public void disableLogging(){
		this.setLogging(false);
	}
	
	/**
	 * Set the logging functionality
	 * @param bool
	 */
	private void setLogging(boolean bool){
		this.loggingEnabled = bool;
	}
	
	//endregion
	
	//region Modules
	
	/*
	From this issue:
	https://github.com/realm/realm-java/issues/1721

	Using logic here:
	https://realm.io/docs/java/latest/#schemas
	 */
	@RealmModule(library = true, allClasses = true)
	public static class PGMacTipsModule {}
	
	//endregion
}