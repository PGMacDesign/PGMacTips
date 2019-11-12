package pgmacdesign.pgmactips.samples.activitysamples;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pgmacdesign.pgmactips.utilities.ColorUtilities;
import com.pgmacdesign.pgmactips.utilities.L;
import com.pgmacdesign.pgmactips.utilities.MiscUtilities;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pgmacdesign.pgmactips.samples.R;

/**
 * Simple class to show how to use the {@link ColorUtilities} class
 */
public class SampleColorClass extends AppCompatActivity {
	
	
	@BindView(R.id.colorPickerView)
	ColorPickerView colorPickerView;
	@BindView(R.id.sample_color_results)
	TextView sample_color_results;
	@BindView(R.id.sample_color_lv)
	ListView sample_color_lv;
	
	//Vars
	private MySimpleAdapter lvAdapter;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sample_color_class_activity);
		this.initVariables();
		this.initUI();
	}
	
	private void initVariables(){
		this.lvAdapter = new MySimpleAdapter(this, android.R.layout.simple_list_item_1);
	}
	
	private void initUI(){
		ButterKnife.bind(this);
		ColorEnvelopeListener cc = new ColorEnvelopeListener() {
			@Override
			public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
				//dostuff
			}
		};
		this.colorPickerView.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
			userPickedColor(envelope);
		});
		this.sample_color_lv.setAdapter(this.lvAdapter);
	}
	
	private void userPickedColor(ColorEnvelope envelope){
		if(envelope == null){
			return;
		}
		try {
			Map<String, Integer> fullColorPalette = ColorUtilities.createFullColorPalette(envelope.getColor());
			if(MiscUtilities.isMapNullOrEmpty(fullColorPalette)){
				this.sample_color_results.setText("Could not parse color");
				return;
			}
			String str = envelope.getHexCode();
			try {
				if(str.startsWith("FF")){
					str = str.substring(2);
				}
			} catch (Exception e){ }
			this.sample_color_results.setText("Color Picked: " + str + ", Full Palette:");
			int color1 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_LIGHT_50);
			int color2 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_LIGHT_100);
			int color3 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_LIGHT_200);
			int color4 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_LIGHT_300);
			int color5 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_LIGHT_400);
			int color6 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_NEUTRAL_500);
			int color7 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_DARK_600);
			int color8 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_DARK_700);
			int color9 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_DARK_800);
			int color10 = fullColorPalette.get(ColorUtilities.MATERIAL_PALETTE_DARK_900);
			List<SimpleObject> simpleObjects = new ArrayList<>();
			SimpleObject s1 = new SimpleObject(color1, ColorUtilities.convertColorToHex(color1));
			SimpleObject s2 = new SimpleObject(color2, ColorUtilities.convertColorToHex(color2));
			SimpleObject s3 = new SimpleObject(color3, ColorUtilities.convertColorToHex(color3));
			SimpleObject s4 = new SimpleObject(color4, ColorUtilities.convertColorToHex(color4));
			SimpleObject s5 = new SimpleObject(color5, ColorUtilities.convertColorToHex(color5));
			SimpleObject s6 = new SimpleObject(color6, ColorUtilities.convertColorToHex(color6));
			SimpleObject s7 = new SimpleObject(color7, ColorUtilities.convertColorToHex(color7));
			SimpleObject s8 = new SimpleObject(color8, ColorUtilities.convertColorToHex(color8));
			SimpleObject s9 = new SimpleObject(color9, ColorUtilities.convertColorToHex(color9));
			SimpleObject s10 = new SimpleObject(color10, ColorUtilities.convertColorToHex(color10));
			simpleObjects.add(s1);
			simpleObjects.add(s2);
			simpleObjects.add(s3);
			simpleObjects.add(s4);
			simpleObjects.add(s5);
			simpleObjects.add(s6);
			simpleObjects.add(s7);
			simpleObjects.add(s8);
			simpleObjects.add(s9);
			simpleObjects.add(s10);
			this.lvAdapter.updateData(simpleObjects);
			this.lvAdapter.notifyDataSetChanged();
		} catch (Exception e){
			e.printStackTrace();
			L.Toast(this, e.getMessage());
		}
	}
	
	
	static class SimpleObject {
		int color;
		String hex;
		boolean isDarkColor;
		
		SimpleObject(int color, String hex){
			this.color = color;
			this.hex = hex;
			this.isDarkColor = ColorUtilities.isColorDark(this.color);
		}
	}
	
	class MySimpleAdapter extends ArrayAdapter<SimpleObject> {
		
		private final int MY_LAYOUT = R.layout.simple_lv_item;
		private TextView tv;
		private LinearLayout rootview;
		private LayoutInflater layoutInflater;
		private int colorWhite, colorBlack;
		
		private List<SimpleObject> data;
		
		public MySimpleAdapter(Context context, int resource) {
			super(context, resource);
			this.layoutInflater = LayoutInflater.from(context);
			this.colorBlack = ContextCompat.getColor(context, R.color.black);
			this.colorWhite = ContextCompat.getColor(context, R.color.white);
		}
		
		public void updateData(List<SimpleObject> data){
			this.data = data;
			this.notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return (MiscUtilities.isListNullOrEmpty(data) ? 0 : data.size());
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SimpleObject s = (MiscUtilities.isValidPosition(data, position) ? data.get(position) : null);
			if(s == null){
				return super.getView(position, convertView, parent);
			}
			View view = layoutInflater.inflate(MY_LAYOUT, parent, false);
			if(view == null){
				return super.getView(position, convertView, parent);
			}
			tv = (TextView) view.findViewById(R.id.tv);
			rootview = (LinearLayout) view.findViewById(R.id.rootview);
			if(tv != null){
				tv.setText("Color: " + s.hex);
				if(s.isDarkColor){
					tv.setTextColor(colorWhite);
				} else {
					tv.setTextColor(colorBlack);
				}
			}
			if(rootview != null){
				rootview.setBackgroundColor(s.color);
			}
			return view;
		}
	}
}
