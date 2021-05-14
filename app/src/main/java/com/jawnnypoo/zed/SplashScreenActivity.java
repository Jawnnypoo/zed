package com.jawnnypoo.zed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

public class SplashScreenActivity extends FragmentActivity implements OnClickListener, AnimationListener {
	private static final int STATE_CREDIT_GROW = 0;
	private static final int STATE_CREDIT_SHRINK = 1;
	private static final int STATE_TITLE_GROW = 2;
	private static final int STATE_TITLE_SHRINK = 3;
	private static final int TRANSITION_TIME = 1000; //ms
	private RelativeLayout mRootLayout;
	private ImageView mTitle;
	private ImageView mCredits;
	private Animation mShrinkAnim;
	private Animation mGrowAnim;
	private int mAnimationState = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//No title bar please!
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Fullscreen, plz
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		init();
	}
	
	private void init() {
		//Load in our silly animation from XML
		mGrowAnim = AnimationUtils.loadAnimation(this, R.anim.stupid_newspaper_spin_grow);
		mGrowAnim.setAnimationListener(this);
		mShrinkAnim = AnimationUtils.loadAnimation(this, R.anim.stupid_newspaper_spin_shrink);
		mShrinkAnim.setAnimationListener(this);
		mRootLayout = (RelativeLayout) findViewById(R.id.layout_root);
		mRootLayout.setOnClickListener(this);
		mCredits = (ImageView) findViewById(R.id.credits);
		mTitle = (ImageView) findViewById(R.id.title);
		startAnimation();
	}

	private void startAnimation() {
		mCredits.startAnimation(mGrowAnim);
	}

	private void goToStartScreen() {
		Intent i = new Intent(this, StartScreenActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onClick(View v) {
		goToStartScreen();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		switch (mAnimationState) {
			case STATE_CREDIT_GROW:
				//Start the next animation after a delay
				mRootLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						mCredits.startAnimation(mShrinkAnim);
					}
					
				}, TRANSITION_TIME);
				break;
			case STATE_CREDIT_SHRINK:
				mCredits.setVisibility(View.INVISIBLE);
				mTitle.startAnimation(mGrowAnim);
				break;
			case STATE_TITLE_GROW:
				mRootLayout.postDelayed(new Runnable() {

					@Override
					public void run() {
						mTitle.startAnimation(mShrinkAnim);
					}
					
				}, TRANSITION_TIME);
				break;
			case STATE_TITLE_SHRINK:
				mTitle.setVisibility(View.GONE);
				goToStartScreen();
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) { }

	@Override
	public void onAnimationStart(Animation animation) {
		mAnimationState++;
		if (mAnimationState < STATE_TITLE_GROW) {
			mCredits.setVisibility(View.VISIBLE);
		} else {
			mTitle.setVisibility(View.VISIBLE);
		}
	}
}
