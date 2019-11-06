package pgmacdesign.pgmactips.samples.activitysamples

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import com.pgmacdesign.pgmactips.datamodels.SamplePojo
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities
import com.pgmacdesign.pgmactips.utilities.GsonUtilities
import com.pgmacdesign.pgmactips.utilities.L
import com.pgmacdesign.pgmactips.utilities.MiscUtilities
import io.realm.RealmConfiguration
import io.realm.RealmObject
import java.util.*

class SampleDBClassKotlin : AppCompatActivity() {
    private var dbUtilities: DatabaseUtilities? = null
    private var config: RealmConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = DatabaseUtilities.buildRealmConfig(
                applicationContext,
                //Replace me with your DB Name
                "PatTestDB.db",
                //Replace me with your DB Version
                PGMacTipsConstants.DB_VERSION,
                //Replace me with your DB boolean flag
                PGMacTipsConstants.DELETE_DB_IF_NEEDED
        )
        this.dbUtilities = DatabaseUtilities(applicationContext, config)

        //CRUD Operations on Simple object
        this.saveSimpleObject()
        this.printSimpleObject()
        this.updateSimpleObject()
        this.deleteSimpleObject()

        L.m("\n---Separator---\n")

        //CRUD Operations on a HashMap<String, String>
        this.saveMap()
        this.printMap()
        this.updateMap()
        this.deleteMap()

        L.m("\n---Separator---\n")

        //CRUD Operations on a Customized Simple Object
        this.saveSimpleObjectCustom()
        this.printSimpleObjectCustom()
        this.updateSimpleObjectCustom()
        this.deleteSimpleObjectCustom()

        L.m("\n---Separator---\n")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.performAdvancedOperations()
        }
        val bool = this.dbUtilities!!.deleteEntireDB<RealmObject>(true, false)
        L.m("Successfully cleared entire db? $bool")
    }

    //region CRUD Operations on a simple object

    /**
     * Save a simple object
     */
    private fun saveSimpleObject() {
        val s = SimpleObject()
        s.age = 21
        s.name = "Pat"
        s.title = "Developer"
        s.isAdmin = true
        val success = this.dbUtilities!!.persistObject(SimpleObject::class.java, s)
        L.m("Successfully saved object? $success")
    }

    /**
     * Print the obtained Simple object
     */
    private fun printSimpleObject() {
        val s = this.dbUtilities!!.getPersistedObject(SimpleObject::class.java) as SimpleObject
        if (s == null) {
            L.m("Error! Could not retrieve object")
            return
        }
        //Print the object out in Json Format:
        L.m("Record successfully obtained: " + GsonUtilities.convertObjectToJson(s, SimpleObject::class.java)!!)
    }

    /**
     * Update the object
     */
    private fun updateSimpleObject() {
        val s = this.dbUtilities!!.getPersistedObject(SimpleObject::class.java) as SimpleObject
        if (s == null) {
            L.m("Error! Could not retrieve object")
            return
        }
        ++s.age
        val success = this.dbUtilities!!.persistObject(SimpleObject::class.java, s)
        L.m("Successfully updated object: $success")

        val s2 = this.dbUtilities!!.getPersistedObject(SimpleObject::class.java) as SimpleObject
        L.m("Updated Object: " + GsonUtilities.convertObjectToJson(s2, SimpleObject::class.java)!!)
    }

    /**
     * Delete the object
     */
    private fun deleteSimpleObject() {
        val success = this.dbUtilities!!.dePersistObject(SimpleObject::class.java)
        L.m("Successfully deleted object? $success")
    }

    //endregion

    //region CRUD Operations on a HashMap<String, Object>

    /**
     * Save the map
     */
    private fun saveMap() {
        val map = HashMap<String, Any>()
        map["test name 1"] = "PG Mac"
        map["Directions to make cookies"] = arrayOf("Mix flour, baking soda, salt", "Diff bowl, mix in sugars, butter, eggs", "Add in dry mixture", "Complete the process")
        map["cookiesAreGood"] = true
        map["ageOfEarthInYears"] = 4543000000000.1
        map["avgLifeExpectancyInUS"] = 78.69
        val success = this.dbUtilities!!.persistObject(MAP_TYPE, map)
        L.m("Successfully saved object? $success")
    }

    /**
     * Print the obtained map
     */
    private fun printMap() {
        val map = this.dbUtilities!!.getPersistedObject(MAP_TYPE) as HashMap<String, Any>
        if (map == null) {
            L.m("Error! Could not retrieve object")
            return
        }
        //Print the object out in Json Format:
        L.m("Record successfully obtained: ")
        MiscUtilities.printOutHashMap(map)
    }

    /**
     * Update the map
     */
    private fun updateMap() {
        val map = this.dbUtilities!!.getPersistedObject(MAP_TYPE) as HashMap<String, Any>
        if (map == null) {
            L.m("Error! Could not retrieve object")
            return
        }
        var lifeExpectancy: Double? = map["avgLifeExpectancyInUS"] as Double
        if (lifeExpectancy != null) {
            lifeExpectancy = lifeExpectancy + 1
            map.put("avgLifeExpectancyInUS", lifeExpectancy)
        }
        val success = this.dbUtilities!!.persistObject(MAP_TYPE, map)
        L.m("Successfully updated object: $success")

        val map2 = this.dbUtilities!!.getPersistedObject(MAP_TYPE) as HashMap<String, Any>
        L.m("Updated Object: ")
        MiscUtilities.printOutHashMap(map2)
    }

    /**
     * Delete the map
     */
    private fun deleteMap() {
        val success = this.dbUtilities!!.dePersistObject(MAP_TYPE)
        L.m("Successfully deleted object? $success")
    }

    //endregion

    //region CRUD Operations on a simple object custom

    /**
     * Save a simple object
     */
    private fun saveSimpleObjectCustom() {
        val s = SimpleObject()
        s.age = 31
        s.name = "Pat 2"
        s.title = "Developer 2"
        s.isAdmin = false
        val success = this.dbUtilities!!.persistObjectCustom(SimpleObject::class.java, s, CUSTOM_OBJECT_SAMPLE_STRING)
        L.m("Successfully saved object? $success")
    }

    /**
     * Print the obtained Simple object
     */
    private fun printSimpleObjectCustom() {
        val s = this.dbUtilities!!.getPersistedObjectCustom(SimpleObject::class.java, CUSTOM_OBJECT_SAMPLE_STRING) as SimpleObject
        if (s == null) {
            L.m("Error! Could not retrieve object")
            return
        }
        //Print the object out in Json Format:
        L.m("Record successfully obtained: " + GsonUtilities.convertObjectToJson(s, SimpleObject::class.java)!!)
    }

    /**
     * Update the object
     */
    private fun updateSimpleObjectCustom() {
        val s = this.dbUtilities!!.getPersistedObjectCustom(SimpleObject::class.java, CUSTOM_OBJECT_SAMPLE_STRING) as SimpleObject
        if (s == null) {
            L.m("Error! Could not retrieve object")
            return
        }
        --s.age
        val success = this.dbUtilities!!.persistObjectCustom(SimpleObject::class.java, s, CUSTOM_OBJECT_SAMPLE_STRING)
        L.m("Successfully updated object: $success")

        val s2 = this.dbUtilities!!.getPersistedObjectCustom(SimpleObject::class.java, CUSTOM_OBJECT_SAMPLE_STRING) as SimpleObject
        L.m("Updated Object: " + GsonUtilities.convertObjectToJson(s2, SimpleObject::class.java)!!)
    }

    /**
     * Delete the object
     */
    private fun deleteSimpleObjectCustom() {
        val success = this.dbUtilities!!.dePersistObjectCustom(SimpleObject::class.java, CUSTOM_OBJECT_SAMPLE_STRING)
        L.m("Successfully deleted object? $success")
    }
    //endregion

    //region Advanced Methods

    /**
     * Performs some advanced operations that utilize encryption
     */
    @RequiresApi(value = Build.VERSION_CODES.KITKAT)
    private fun performAdvancedOperations() {

        applicationContext
        if (dbUtilities == null) {
            dbUtilities = DatabaseUtilities(applicationContext, config)
        }
        val CORRECT_PW = "myPw"
        val CORRECT_SALT = "mySalt"
        val INCORRECT_PW = "myPw2"
        val INCORRECT_SALT = "mySalt2"

        dbUtilities!!.enableLogging()

        L.m("Initial print before process: ")
        L.m("\nPrinting out DB without decryption")
        dbUtilities!!.printOutDatabase()
        L.m("are db values encrypted? " + dbUtilities!!.areDBValuesEncrypted())
        L.m("This should fail and return false. Value: " + DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities!!))
        dbUtilities!!.deleteEntireDB<RealmObject>(true, false)
        L.m("This should fail and return false. Value: " + DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities!!))

        dbUtilities!!.clearInstance()
        dbUtilities = null
        dbUtilities = DatabaseUtilities(applicationContext, config, CORRECT_PW, CORRECT_SALT)
        L.m("This should fail and return false. Value: " + DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities!!))

        dbUtilities!!.clearInstance()
        dbUtilities = null
        dbUtilities = DatabaseUtilities(applicationContext, config)

        try {
            //running this to prevent issues
            //	    	Thread.sleep(50);
        } catch (e: Exception) {
        }

        val TYPE_MAP_STRING_STRING = object : TypeToken<Map<String, String>>() {

        }
        val MAP_STRING_CUSTOM_SUFFIX = "-custom1"
        val MAP_STRING_CUSTOM_SUFFIX2 = "-custom2"
        L.m("TEST DB2 HERE")
        dbUtilities!!.printOutDatabase()
        val itemOne = HashMap<String, String>()
        itemOne["this is a diff object"] = "neato!"
        itemOne["stuff"] = "can go here and whatnot"
        itemOne["age"] = "11123123123123 (old)"
        dbUtilities!!.persistObject(TYPE_MAP_STRING_STRING, itemOne)
        dbUtilities!!.persistObjectCustom(TYPE_MAP_STRING_STRING, itemOne, MAP_STRING_CUSTOM_SUFFIX)
        val samplePojo = SamplePojo()
        samplePojo.age = 2
        samplePojo.gender = "apache-attack-helicopter"
        samplePojo.id = 123123
        samplePojo.name = "name"
        samplePojo.strs = Arrays.asList("test1", "test2", "test3", "okiedokie")
        samplePojo.fauxEnums = Arrays.asList(SamplePojo.MyFauxTestEnum.One,
                SamplePojo.MyFauxTestEnum.Two, SamplePojo.MyFauxTestEnum.Three)
        dbUtilities!!.persistObject(SamplePojo::class.java, samplePojo)
        dbUtilities!!.persistObjectCustom(SamplePojo::class.java, samplePojo, MAP_STRING_CUSTOM_SUFFIX)

        L.m("Finished all writes, printing out entire DB")
        dbUtilities!!.printOutDatabase()
        val dePersisted = dbUtilities!!.dePersistObject(SamplePojo::class.java)
        L.m("Successfully de-persisted one object? (Non-Custom) == $dePersisted")
        val dePersisted2 = dbUtilities!!.dePersistObjectCustom(SamplePojo::class.java, MAP_STRING_CUSTOM_SUFFIX)
        L.m("Successfully de-persisted one object? (Custom) == $dePersisted2")
        L.m("Printing entire db after delete of 2 items: ")
        dbUtilities!!.printOutDatabase()
        dbUtilities!!.persistObject(SamplePojo::class.java, samplePojo)
        dbUtilities!!.persistObjectCustom(SamplePojo::class.java, samplePojo, MAP_STRING_CUSTOM_SUFFIX)
        L.m("are db values encrypted? " + dbUtilities!!.areDBValuesEncrypted())

        dbUtilities!!.clearInstance()
        dbUtilities = null
        dbUtilities = DatabaseUtilities(applicationContext, config, CORRECT_PW, CORRECT_SALT)

        val bool = DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities!!)
        L.m("Did transfer work? $bool")

        L.m("This should not successfully move items as they are already encrypted.")
        val boolq = DatabaseUtilities.moveDBToEncryptedVersion(dbUtilities!!)
        L.m("Value should be False:  $boolq")

        L.m("\nPrinting out DB without decryption")
        dbUtilities!!.printOutDatabase()

        L.m("\nPrinting out DB with decryption")
        dbUtilities!!.printOutDatabase(true)

        L.m("Adding a new, encrypted value")
        val itemTwo = HashMap<String, String>()
        itemTwo["demoing encrypted key"] = "demoing encrypted value"
        itemTwo["Liam"] = "Is an excellent name"
        itemTwo["Tristan"] = "Is also an excellent name!"
        itemTwo["droidId"] = "IG-88"
        val added = dbUtilities!!.persistObjectCustom(TYPE_MAP_STRING_STRING, itemTwo, MAP_STRING_CUSTOM_SUFFIX2)
        L.m("Successfully added new encrypted value to DB? $added")

        L.m("\nPrinting out DB without decryption")
        dbUtilities!!.printOutDatabase()

        L.m("\nPrinting out DB with decryption")
        dbUtilities!!.printOutDatabase(true)

        dbUtilities!!.clearInstance()
        dbUtilities = null
        dbUtilities = DatabaseUtilities(applicationContext, config, INCORRECT_PW, INCORRECT_SALT)
        L.m("This should throw decryption exceptions and not be able to decrypt values:")
        dbUtilities!!.printOutDatabase(true)

        dbUtilities!!.clearInstance()
        dbUtilities = null
        dbUtilities = DatabaseUtilities(applicationContext, config, CORRECT_PW, CORRECT_SALT)
        L.m("This should now be able to decrypt values:")
        dbUtilities!!.printOutDatabase(true)
    }

    //endregion

    //region POJO
    /**
     * Simple Object for testing
     */
    class SimpleObject {
        internal var name: String? = null
        internal var title: String? = null
        internal var age: Int = 0
        internal var isAdmin: Boolean = false
    }

    companion object {

        private val MAP_TYPE = object : TypeToken<HashMap<String, Any>>() {}
        private val CUSTOM_OBJECT_SAMPLE_STRING = "-c"
    }
    //endregion
}
