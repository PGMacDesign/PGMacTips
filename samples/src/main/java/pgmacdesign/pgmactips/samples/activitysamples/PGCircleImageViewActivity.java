package pgmacdesign.pgmactips.samples.activitysamples;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pgmacdesign.pgmactips.customui.PGCircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pgmacdesign.pgmactips.samples.R;

public class PGCircleImageViewActivity extends AppCompatActivity {
	
	private int LAYOUT_RES_ID = R.layout.pg_circle_image_view_activity;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(this.LAYOUT_RES_ID);
		this.initVars();
		this.initUI();
	}
	
	private void initVars(){
	
	}
	
	private void initUI(){
		ButterKnife.bind(this);
	}
}
