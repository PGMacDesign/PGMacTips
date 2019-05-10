package pgmacdesign.pgmactips.samples.activitysamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import com.pgmacdesign.pgmactips.misc.PGMacTipsConstants
import com.pgmacdesign.pgmactips.utilities.DatabaseUtilities
import com.pgmacdesign.pgmactips.utilities.GsonUtilities
import com.pgmacdesign.pgmactips.utilities.L
import com.pgmacdesign.pgmactips.utilities.MiscUtilities
import java.util.HashMap

class SampleDBClassKotlin : AppCompatActivity() {
    private var dbUtilities: DatabaseUtilities? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = DatabaseUtilities.buildRealmConfig(
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
