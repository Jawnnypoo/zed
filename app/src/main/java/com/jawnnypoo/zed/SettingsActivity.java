package com.jawnnypoo.zed;

import com.jawnnypoo.zed.fragment.SettingsFragment;

import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class SettingsActivity extends FragmentActivity {

	private static final String FRAGMENT_TAG = "fragment_tag";
	private SettingsFragment mSettingsFragment; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//No title bar please!
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.single_fragment_container);
		
		//We have not created this screen before
		if (savedInstanceState == null) {
			mSettingsFragment = SettingsFragment.newInstance();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.fragment_container, mSettingsFragment, FRAGMENT_TAG);
			ft.commit();
		}
	}
}
