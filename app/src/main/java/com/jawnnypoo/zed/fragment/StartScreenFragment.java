package com.jawnnypoo.zed.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.jawnnypoo.zed.GameScreenActivity;
import com.jawnnypoo.zed.R;
import com.jawnnypoo.zed.SettingsActivity;

/***
 * Fragment for the interface on the main screen. We use fragments
 * not just because Google tells us to, but due to their re-usability
 * @author Jawn
 *
 */
public class StartScreenFragment extends Fragment {
	//Stuff related to UI
	private View mRootView;
	private Button mStartButton;
	private Button mSettingsButton;
	
	public static StartScreenFragment newInstance() {
		StartScreenFragment f = new StartScreenFragment();
		
		Bundle args = new Bundle();
		f.setArguments(args);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//We were passed an inflater in this method, and we use it
		//to inflate the layout that we wish to show for this fragment.
		//Here we get all the references to the layout elements that we will
		//need to modify in code
		mRootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
		mStartButton = (Button) mRootView.findViewById(R.id.button_start);
		mSettingsButton = (Button) mRootView.findViewById(R.id.button_settings);
		mStartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startClick();
			}
		});
		mSettingsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				settingsClick();
			}
		});
		return mRootView;
	}

	private void startClick() {
		Intent intent = new Intent(getActivity(), GameScreenActivity.class);
		startActivity(intent);
	}
	
	protected void settingsClick() {
		Intent intent = new Intent(getActivity(), SettingsActivity.class);
		startActivity(intent);
	}
}
