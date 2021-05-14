package com.jawnnypoo.zed;

import android.os.Bundle;
import android.view.Window;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.jawnnypoo.zed.fragment.StartScreenFragment;

/***
 * The start screen for the game. Although this is an activity, it is 
 * really just a place where we load up a fragment and display it.
 * Think of it like an activity is a canvas, and a fragment is what 
 * you place on the canvas. The canvas is still nessisary, and can perform
 * actions that a fragment couldnt, but it is good practice to use fragments
 * as much as possible
 * @author Jawn
 *
 */
public class StartScreenActivity extends FragmentActivity {
	private static final String FRAGMENT_TAG = "fragment_tag";
	private StartScreenFragment mStartScreenFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//No title bar please!
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//After this line, we can access Views and other elements of 
		//our XML, since the layout has been set on the activity
		setContentView(R.layout.activity_start_screen);
		
		//A saved instance state is something that we get each time
		//we start an activity from a state where it had already been
		//created once before, such as after an onPause, or onStop
		if (savedInstanceState == null) {
			//We first create the fragment
			mStartScreenFragment = StartScreenFragment.newInstance(); 
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			//Here, we are adding the fragment to the activity. We need a place to add it in, so 
			//in the XML for this activity, we have a blank layout to place this fragment into that
			//will take up the whole screen
			ft.add(R.id.fragment_container, mStartScreenFragment, FRAGMENT_TAG);
			ft.commit();
		} else {
			mStartScreenFragment = (StartScreenFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
		}
	}
}
