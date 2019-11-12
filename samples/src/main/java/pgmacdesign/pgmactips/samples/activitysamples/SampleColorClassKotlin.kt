package pgmacdesign.pgmactips.samples.activitysamples

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.pgmacdesign.pgmactips.utilities.ColorUtilities
import com.pgmacdesign.pgmactips.utilities.L
import com.pgmacdesign.pgmactips.utilities.MiscUtilities
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import pgmacdesign.pgmactips.samples.R
import java.util.ArrayList


/**
 * Simple class to show how to use the [ColorUtilities] class
 */
class SampleColorClassKotlin : AppCompatActivity() {


    @BindView(R.id.colorPickerView)
    internal var colorPickerView: ColorPickerView? = null
    @BindView(R.id.sample_color_results)
    internal var sample_color_results: TextView? = null
    @BindView(R.id.sample_color_lv)
    internal var sample_color_lv: ListView? = null

    //Vars
    private var lvAdapter: MySimpleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.sample_color_class_activity)
        this.initVariables()
        this.initUI()
    }

    private fun initVariables() {
        this.lvAdapter = MySimpleAdapter(this, android.R.layout.simple_list_item_1)
    }

    private fun initUI() {
        ButterKnife.bind(this)
        val cc = ColorEnvelopeListener { envelope, fromUser ->
            userPickedColor(envelope)
        }
        this.colorPickerView!!.setColorListener(cc)
        this.sample_color_lv!!.adapter = this.lvAdapter
    }

    private fun userPickedColor(envelope: ColorEnvelope?) {
        if (envelope == null) {
            return
        }
        try {
            val fullColorPalette = ColorUtilities.createFullColorPalette(envelope.color)
            if (MiscUtilities.isMapNullOrEmpty(fullColorPalette)) {
                this.sample_color_results!!.text = "Could not parse color"
                return
            }
            var str = envelope.hexCode
            try {
                if (str.startsWith("FF")) {
                    str = str.substring(2)
                }
            } catch (e: Exception) {
            }

            this.sample_color_results!!.text = "Color Picked: $str, Full Palette:"
            val color1 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_LIGHT_50]!!
            val color2 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_LIGHT_100]!!
            val color3 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_LIGHT_200]!!
            val color4 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_LIGHT_300]!!
            val color5 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_LIGHT_400]!!
            val color6 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_NEUTRAL_500]!!
            val color7 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_DARK_600]!!
            val color8 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_DARK_700]!!
            val color9 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_DARK_800]!!
            val color10 = fullColorPalette[ColorUtilities.MATERIAL_PALETTE_DARK_900]!!
            val simpleObjects = ArrayList<SimpleObject>()
            val s1 = SimpleObject(color1, ColorUtilities.convertColorToHex(color1))
            val s2 = SimpleObject(color2, ColorUtilities.convertColorToHex(color2))
            val s3 = SimpleObject(color3, ColorUtilities.convertColorToHex(color3))
            val s4 = SimpleObject(color4, ColorUtilities.convertColorToHex(color4))
            val s5 = SimpleObject(color5, ColorUtilities.convertColorToHex(color5))
            val s6 = SimpleObject(color6, ColorUtilities.convertColorToHex(color6))
            val s7 = SimpleObject(color7, ColorUtilities.convertColorToHex(color7))
            val s8 = SimpleObject(color8, ColorUtilities.convertColorToHex(color8))
            val s9 = SimpleObject(color9, ColorUtilities.convertColorToHex(color9))
            val s10 = SimpleObject(color10, ColorUtilities.convertColorToHex(color10))
            simpleObjects.add(s1)
            simpleObjects.add(s2)
            simpleObjects.add(s3)
            simpleObjects.add(s4)
            simpleObjects.add(s5)
            simpleObjects.add(s6)
            simpleObjects.add(s7)
            simpleObjects.add(s8)
            simpleObjects.add(s9)
            simpleObjects.add(s10)
            this.lvAdapter!!.updateData(simpleObjects)
            this.lvAdapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
            L.Toast<String>(this, e.message)
        }

    }


    internal class SimpleObject(var color: Int, var hex: String) {
        var isDarkColor: Boolean = false

        init {
            this.isDarkColor = ColorUtilities.isColorDark(this.color)
        }
    }

    internal inner class MySimpleAdapter(context: Context, resource: Int) : ArrayAdapter<SimpleObject>(context, resource) {

        private val MY_LAYOUT = R.layout.simple_lv_item
        private var tv: TextView? = null
        private var rootview: LinearLayout? = null
        private val layoutInflater: LayoutInflater
        private val colorWhite: Int
        private val colorBlack: Int

        private var data: List<SimpleObject>? = null

        init {
            this.layoutInflater = LayoutInflater.from(context)
            this.colorBlack = ContextCompat.getColor(context, R.color.black)
            this.colorWhite = ContextCompat.getColor(context, R.color.white)
        }

        fun updateData(data: List<SimpleObject>) {
            this.data = data
            this.notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return if (MiscUtilities.isListNullOrEmpty(data)) 0 else data!!.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val s = (if (MiscUtilities.isValidPosition(data, position)) data!![position] else null)
                    ?: return super.getView(position, convertView, parent)
            val view = layoutInflater.inflate(MY_LAYOUT, parent, false)
                    ?: return super.getView(position, convertView, parent)
            tv = view.findViewById<View>(R.id.tv) as TextView
            rootview = view.findViewById<View>(R.id.rootview) as LinearLayout
            if (tv != null) {
                tv!!.text = "Color: " + s.hex
                if (s.isDarkColor) {
                    tv!!.setTextColor(colorWhite)
                } else {
                    tv!!.setTextColor(colorBlack)
                }
            }
            if (rootview != null) {
                rootview!!.setBackgroundColor(s.color)
            }
            return view
        }
    }
}
