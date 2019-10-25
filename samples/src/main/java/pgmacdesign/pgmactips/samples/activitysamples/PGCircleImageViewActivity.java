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
	
	@BindView(R.id.circle_iv_1)
	PGCircleImageView circle_iv_1;
	@BindView(R.id.circle_iv_2)
	PGCircleImageView circle_iv_2;
	@BindView(R.id.circle_iv_3)
	PGCircleImageView circle_iv_3;
	@BindView(R.id.circle_iv_4)
	PGCircleImageView circle_iv_4;
	@BindView(R.id.circle_iv_5)
	PGCircleImageView circle_iv_5;
	@BindView(R.id.circle_iv_6)
	PGCircleImageView circle_iv_6;
	@BindView(R.id.circle_iv_7)
	PGCircleImageView circle_iv_7;
	@BindView(R.id.circle_iv_8)
	PGCircleImageView circle_iv_8;
	@BindView(R.id.circle_iv_9)
	PGCircleImageView circle_iv_9;
	
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
