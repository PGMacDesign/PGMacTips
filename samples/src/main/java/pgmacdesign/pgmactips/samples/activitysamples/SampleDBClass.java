package pgmacdesign.pgmactips.samples.activitysamples;

import android.os.Build;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.pgmacdesign.pgmactips.datamodels.SamplePojo;
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants;
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities;
import com.pgmacdesign.pgmactips.utilities.GsonUtilities;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.RealmConfiguration;

public class SampleDBClass extends AppCompatActivity {
	
	private static final TypeToken MAP_TYPE = new TypeToken<Map<String, Object>>(){};
	private static final String CUSTOM_OBJECT_SAMPLE_STRING = "-c";
	private DatabaseUtilities dbUtilities;
	private RealmConfiguration config;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		config = DatabaseUtilities.buildRealmConfig(
				getApplicationContext(),
				//Replace me with your DB Name
				"PatTestDB.db",
				//Replace me with your DB Version
				PGMacTipsConstants.DB_VERSION,
				//Replace me with your DB boolean flag
				PGMacTipsConstants.DELETE_DB_IF_NEEDED
		);
//		this.dbUtilities = new DatabaseUtilities(getApplicationContext(), config);
		this.dbUtilities = new DatabaseUtilities(getApplicationContext(), config);
		
		//CRUD Operations on Simple object
		this.saveSimpleObject();
		this.printSimpleObject();
		this.updateSimpleObject();
		this.deleteSimpleObject();

		L.m("\n---Separator---\n");
		
		//CRUD Operations on a HashMap<String, String>
		this.saveMap();
		this.printMap();
		this.updateMap();
		this.deleteMap();
		
		L.m("\n---Separator---\n");
		
		//CRUD Operations on a Customized Simple Object
		this.saveSimpleObjectCustom();
		this.printSimpleObjectCustom();
		this.updateSimpleObjectCustom();
		this.deleteSimpleObjectCustom();
		
		L.m("\n---Separator---\n");
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			this.performAdvancedOperations();
		}
		boolean bool = this.dbUtilities.deleteEntireDB(true, false);
		L.m("Successfully cleared entire db? " + bool);
	}
	
	//region CRUD Operations on a simple object
	
	/**
	 * Save a simple object
	 */
	private void saveSimpleObject(){
		SimpleObject s = new SimpleObject();
		s.age = 21;
		s.name = "Pat";
		s.title = "Developer";
		s.isAdmin = true;
		boolean success = this.dbUtilities.persistObject(SimpleObject.class, s);
		L.m("Successfully saved object? " + success);
	}
	
	/**
	 * Print the obtained Simple object
	 */
	private void printSimpleObject(){
		SimpleObject s = (SimpleObject) this.dbUtilities.getPersistedObject(SimpleObject.class);
		if(s == null){
			L.m("Error! Could not retrieve object");
			return;
		}
		//Print the object out in Json Format:
		L.m("Record successfully obtained: " + GsonUtilities.convertObjectToJson(s, SimpleObject.class));
	}
	
	/**
	 * Update the object
	 */
	private void updateSimpleObject(){
		SimpleObject s = (SimpleObject) this.dbUtilities.getPersistedObject(SimpleObject.class);
		if(s == null){
			L.m("Error! Could not retrieve object");
			return;
		}
		++s.age;
		boolean success = this.dbUtilities.persistObject(SimpleObject.class, s);
		L.m("Successfully updated object: " + success);
		
		SimpleObject s2 = (SimpleObject) this.dbUtilities.getPersistedObject(SimpleObject.class);
		L.m("Updated Object: " + GsonUtilities.convertObjectToJson(s2, SimpleObject.class));
	}
	
	/**
	 * Delete the object
	 */
	private void deleteSimpleObject(){
		boolean success = this.dbUtilities.dePersistObject(SimpleObject.class);
		L.m("Successfully deleted object? " + success);
	}
	
	//endregion
	
	//region CRUD Operations on a Map<String, Object>
	
	/**
	 * Save the map
	 */
	private void saveMap(){
		Map<String, Object> map = new HashMap<>();
		map.put("test name 1", "PG Mac");
		map.put("Directions to make cookies", new String[]{
				"Mix flour, baking soda, salt", "Diff bowl, mix in sugars, butter, eggs",
				"Add in dry mixture", "Complete the process"});
		map.put("cookiesAreGood", (Boolean)true);
		map.put("ageOfEarthInYears", (Double)4543000000000.1);
		map.put("avgLifeExpectancyInUS", (Double)78.69);
		boolean success = this.dbUtilities.persistObject(MAP_TYPE, map);
		L.m("Successfully saved object? " + success);
	}
	
	/**
	 * Print the obtained map
	 */
	private void printMap(){
		Map<String, Object> map = (Map<String, Object>) this.dbUtilities.getPersistedObject(MAP_TYPE);
		if(map == null){
			L.m("Error! Could not retrieve object");
			return;
		}
		//Print the object out in Json Format:
		L.m("Record successfully obtained: ");
		MiscUtilities.printOutHashMap(map);
	}
	
	/**
	 * Update the map
	 */
	private void updateMap(){
		Map<String, Object> map = (Map<String, Object>) this.dbUtilities.getPersistedObject(MAP_TYPE);
		if(map == null){
			L.m("Error! Could not retrieve object");
			return;
		}
		Double lifeExpectancy = (Double)(map.get("avgLifeExpectancyInUS"));
		if(lifeExpectancy != null){
			lifeExpectancy = lifeExpectancy + 1;
			map.put("avgLifeExpectancyInUS", lifeExpectancy);
		}
		boolean success = this.dbUtilities.persistObject(MAP_TYPE, map);
		L.m("Successfully updated object: " + success);
		
		Map<String, Object> map2 = (Map<String, Object>) this.dbUtilities.getPersistedObject(MAP_TYPE);
		L.m("Updated Object: ");
		MiscUtilities.printOutHashMap(map2);
	}
	
	/**
	 * Delete the map
	 */
	private void deleteMap(){
		boolean success = this.dbUtilities.dePersistObject(MAP_TYPE);
		L.m("Successfully deleted object? " + success);
	}
	
	//endregion
	
	//region CRUD Operations on a simple object custom
	
	/**
	 * Save a simple object
	 */
	private void saveSimpleObjectCustom(){
		SimpleObject s = new SimpleObject();
		s.age = 31;
		s.name = "Pat 2";
		s.title = "Developer 2";
		s.isAdmin = false;
		boolean success = this.dbUtilities.persistObjectCustom(SimpleObject.class, s, CUSTOM_OBJECT_SAMPLE_STRING);
		L.m("Successfully saved object? " + success);
	}
	
	/**
	 * Print the obtained Simple object
	 */
	private void printSimpleObjectCustom(){
		SimpleObject s = this.dbUtilities.getPersistedObjectCustom(SimpleObject.class, CUSTOM_OBJECT_SAMPLE_STRING);
		if(s == null){
			L.m("Error! Could not retrieve object");
			return;
		}
		//Print the object out in Json Format:
		L.m("Record successfully obtained: " + GsonUtilities.convertObjectToJson(s, SimpleObject.class));
	}
	
	/**
	 * Update the object
	 */
	private void updateSimpleObjectCustom(){
		SimpleObject s = (SimpleObject) this.dbUtilities.getPersistedObjectCustom(SimpleObject.class, CUSTOM_OBJECT_SAMPLE_STRING);
		if(s == null){
			L.m("Error! Could not retrieve object");
			return;
		}
		--s.age;
		boolean success = this.dbUtilities.persistObjectCustom(SimpleObject.class, s, CUSTOM_OBJECT_SAMPLE_STRING);
		L.m("Successfully updated object: " + success);
		
		SimpleObject s2 = (SimpleObject) this.dbUtilities.getPersistedObjectCustom(SimpleObject.class, CUSTOM_OBJECT_SAMPLE_STRING);
		L.m("Updated Object: " + GsonUtilities.convertObjectToJson(s2, SimpleObject.class));
	}
	
	/**
	 * Delete the object
	 */
	private void deleteSimpleObjectCustom(){
		boolean success = this.dbUtilities.dePersistObjectCustom(SimpleObject.class, CUSTOM_OBJECT_SAMPLE_STRING);
		L.m("Successfully deleted object? " + success);
	}
	//endregion 
	
	//region Advanced Methods
	
	/**
	 * Performs some advanced operations that utilize encryption
	 */
	@RequiresApi(value = Build.VERSION_CODES.KITKAT)
	private void performAdvancedOperations(){
		
		getApplicationContext();
		if(dbUtilities == null) {
			dbUtilities = new DatabaseUtilities(getApplicationContext(), config);
		}
		final String CORRECT_PW = "myPw";
		final String CORRECT_SALT = "mySalt";
		final String INCORRECT_PW = "myPw2";
		final String INCORRECT_SALT = "mySalt2";
		
		dbUtilities.enableLogging();
		
		L.m("Initial print before process: ");
		L.m("\nPrinting out DB without decryption");
		dbUtilities.printOutDatabase();
		L.m("are db values encrypted? " + dbUtilities.areDBValuesEncrypted());
		L.m("This should fail and return false. Value: " + DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities));
		dbUtilities.deleteEntireDB(true, false);
		L.m("This should fail and return false. Value: " + DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities));
		
		dbUtilities.clearInstance();
		dbUtilities = null;
		dbUtilities = new DatabaseUtilities(getApplicationContext(), config, CORRECT_PW, CORRECT_SALT);
		L.m("This should fail and return false. Value: " + DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities));
		
		dbUtilities.clearInstance();
		dbUtilities = null;
		dbUtilities = new DatabaseUtilities(getApplicationContext(), config);
		
		try {
			//running this to prevent issues
//	    	Thread.sleep(50);
		} catch (Exception e){}
		final TypeToken TYPE_MAP_STRING_STRING = new TypeToken<Map<String, String>>() {};
		final String MAP_STRING_CUSTOM_SUFFIX = "-custom1";
		final String MAP_STRING_CUSTOM_SUFFIX2 = "-custom2";
		L.m("TEST DB2 HERE");
		dbUtilities.printOutDatabase();
		Map<String, String> itemOne = new HashMap<>();
		itemOne.put("this is a diff object", "neato!");
		itemOne.put("stuff", "can go here and whatnot");
		itemOne.put("age", "11123123123123 (old)");
		dbUtilities.persistObject(TYPE_MAP_STRING_STRING, itemOne);
		dbUtilities.persistObjectCustom(TYPE_MAP_STRING_STRING, itemOne, MAP_STRING_CUSTOM_SUFFIX);
		SamplePojo samplePojo = new SamplePojo();
		samplePojo.setAge(2);
		samplePojo.setGender("apache-attack-helicopter");
		samplePojo.setId(123123);
		samplePojo.setName("name");
		samplePojo.setStrs(Arrays.asList("test1", "test2", "test3", "okiedokie"));
		samplePojo.setFauxEnums(Arrays.asList(SamplePojo.MyFauxTestEnum.One,
				SamplePojo.MyFauxTestEnum.Two, SamplePojo.MyFauxTestEnum.Three));
		dbUtilities.persistObject(SamplePojo.class, samplePojo);
		dbUtilities.persistObjectCustom(SamplePojo.class, samplePojo, MAP_STRING_CUSTOM_SUFFIX);
		
		L.m("Finished all writes, printing out entire DB");
		dbUtilities.printOutDatabase();
		boolean dePersisted = dbUtilities.dePersistObject(SamplePojo.class);
		L.m("Successfully de-persisted one object? (Non-Custom) == " + dePersisted);
		boolean dePersisted2 = dbUtilities.dePersistObjectCustom(SamplePojo.class, MAP_STRING_CUSTOM_SUFFIX);
		L.m("Successfully de-persisted one object? (Custom) == " + dePersisted2);
		L.m("Printing entire db after delete of 2 items: " );
		dbUtilities.printOutDatabase();
		dbUtilities.persistObject(SamplePojo.class, samplePojo);
		dbUtilities.persistObjectCustom(SamplePojo.class, samplePojo, MAP_STRING_CUSTOM_SUFFIX);
		L.m("are db values encrypted? " + dbUtilities.areDBValuesEncrypted());
		
		dbUtilities.clearInstance();
		dbUtilities = null;
		dbUtilities = new DatabaseUtilities(getApplicationContext(), config, CORRECT_PW, CORRECT_SALT);
		
		boolean bool = DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities);
		L.m("Did transfer work? " + bool);
		
		L.m("This should not successfully move items as they are already encrypted.");
		boolean boolq = DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities);
		L.m("Value should be False:  " + boolq);
		
		L.m("\nPrinting out DB without decryption");
		dbUtilities.printOutDatabase();
		
		L.m("\nPrinting out DB with decryption");
		dbUtilities.printOutDatabase(true);
		
		L.m("Adding a new, encrypted value");
		Map<String, String> itemTwo = new HashMap<>();
		itemTwo.put("demoing encrypted key", "demoing encrypted value");
		itemTwo.put("Liam", "Is an excellent name");
		itemTwo.put("Tristan", "Is also an excellent name!");
		itemTwo.put("droidId", "IG-88");
		boolean added = dbUtilities.persistObjectCustom(TYPE_MAP_STRING_STRING, itemTwo, MAP_STRING_CUSTOM_SUFFIX2);
		L.m("Successfully added new encrypted value to DB? " + added);
		
		L.m("\nPrinting out DB without decryption");
		dbUtilities.printOutDatabase();
		
		L.m("\nPrinting out DB with decryption");
		dbUtilities.printOutDatabase(true);
		
		dbUtilities.clearInstance();
		dbUtilities = null;
		dbUtilities = new DatabaseUtilities(getApplicationContext(), config, INCORRECT_PW, INCORRECT_SALT);
		L.m("This should throw decryption exceptions and not be able to decrypt values:");
		dbUtilities.printOutDatabase(true);
		
		dbUtilities.clearInstance();
		dbUtilities = null;
		dbUtilities = new DatabaseUtilities(getApplicationContext(), config, CORRECT_PW, CORRECT_SALT);
		L.m("This should now be able to decrypt values:");
		dbUtilities.printOutDatabase(true);
	}
	
	//endregion
	
	//region POJO
	/**
	 * Simple Object for testing
	 */
	public static class SimpleObject {
		String name;
		String title;
		int age;
		boolean isAdmin;
	}
	//endregion 
}
