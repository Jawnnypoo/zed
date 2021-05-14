package com.jawnnypoo.zed.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.jawnnypoo.zed.R;

public class SettingsFragment extends Fragment {
	private SharedPreferences mSharedPreferences;
	private CheckBox mCheckSound;

	public static SettingsFragment newInstance() {
		SettingsFragment f = new SettingsFragment();
		
		Bundle args = new Bundle();
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings_screen, null);
		mCheckSound = (CheckBox) rootView.findViewById(R.id.checkbox_poop);
		return rootView;
	}
	
	//We cannot get to the previous settings until the activity exists, so we 
	//wait until it is created
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = getActivity();
		mSharedPreferences = activity.getSharedPreferences(getString(R.string.SETTING_NAME), 0);
		mCheckSound.setChecked(mSharedPreferences.getBoolean(getString(R.string.SETTING_SOUND), true));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(getString(R.string.SETTING_SOUND), mCheckSound.isChecked());
		editor.commit();
	}
}
