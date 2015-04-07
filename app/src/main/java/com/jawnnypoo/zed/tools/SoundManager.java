/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.jawnnypoo.zed.tools;

import java.util.Comparator;

import com.jawnnypoo.zed.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Sound effects are stored in the manager in a soundpool, which
 * is efficient for small size sound effects such as explosions.
 * For larger, more persistent sounds such as music, it is best
 * to create a MediaPlayer objects
 * @author Jawn
 *
 */
public class SoundManager {
    private static final int MAX_STREAMS = 8;
    private static final int MAX_SOUNDS = 32;
    private static final SoundComparator sSoundComparator = new SoundComparator();
    
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_MUSIC = 3;

    //Sound pool appropriate for sound effects, like explosions
    //Not for background music.
    private SoundPool mSoundPool;
    private Context mContext;
    private FixedSizeArray<Sound> mSounds;
    private MediaPlayer mBackgroundMusic;
    private Sound mSearchDummy;
    private boolean mSoundEnabled;
    private int[] mLoopingStreams;
    
    public SoundManager(Context context) {
        super();
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mSounds = new FixedSizeArray<Sound>(MAX_SOUNDS, sSoundComparator);
        mSearchDummy = new Sound();
        mLoopingStreams = new int[MAX_STREAMS];
        mSoundEnabled = true;
        mContext = context;
        for (int x = 0; x < mLoopingStreams.length; x++) {
        	mLoopingStreams[x] = -1;
        }
    }
    
    public void reset() {
        mSoundPool.release();
        mSounds.clear();
        mSoundEnabled = true;
        for (int x = 0; x < mLoopingStreams.length; x++) {
        	mLoopingStreams[x] = -1;
        }
    }
    
    public void loadAndPlayBackgroundMusic(int resource) {
    	mBackgroundMusic = MediaPlayer.create(mContext, resource);
    	mBackgroundMusic.setLooping(true);
    	mBackgroundMusic.start();
    }

    public Sound load(int resource) {
        final int index = findSound(resource);
        Sound result = null;
        if (index < 0) {
            // new sound.
               result = new Sound();
               result.resource = resource;
               result.soundId = mSoundPool.load(mContext, resource, 1);
               mSounds.add(result);
               mSounds.sort(false);
        } else {
            result = mSounds.get(index);
        }
        
        return result;
    }
    
    synchronized public final int play(Sound sound, boolean loop, int priority) {
    	int stream = -1;
    	if (mSoundEnabled) {
    		stream = mSoundPool.play(sound.soundId, 1.0f, 1.0f, priority, loop ? -1 : 0, 1.0f);
    		if (loop) {
    			addLoopingStream(stream);
    		}
    	} 
    	
    	return stream;
    }
    
    synchronized public final int play(Sound sound, boolean loop, int priority, float volume, float rate) {
    	int stream = -1;
    	if (mSoundEnabled) {
    		stream = mSoundPool.play(sound.soundId, volume, volume, priority, loop ? -1 : 0, rate);
    		if (loop) {
    			addLoopingStream(stream);
    		}
    	} 
    	
    	return stream;
    }
    
    public final void stop(int stream) {
        mSoundPool.stop(stream);
        removeLoopingStream(stream);
    }
    
    public final void pause(int stream) {
        mSoundPool.pause(stream);
    }
    
    public final void resume(int stream) {
       mSoundPool.resume(stream);
    }
    
    public final void stopAll() {
    	final int count = mLoopingStreams.length;
    	for (int x = count - 1; x >= 0; x--) {
    		if (mLoopingStreams[x] >= 0) {
    			stop(mLoopingStreams[x]);
    		}
    	}
    }
    
    public void stopBackground() {
    	if (mBackgroundMusic != null) {
    		mBackgroundMusic.reset();
    	}
    }
    
    // HACK: There's no way to pause an entire sound pool, but if we
    // don't do something when our parent activity is paused, looping
    // sounds will continue to play.  Rather that reproduce all the bookkeeping
    // that SoundPool does internally here, I've opted to just pause looping
    // sounds when the Activity is paused.
    public void pauseAll() {
    	final int count = mLoopingStreams.length;
    	for (int x = 0; x < count; x++) {
    		if (mLoopingStreams[x] >= 0) {
    			pause(mLoopingStreams[x]);
    		}
    	}
    	
    	if (mBackgroundMusic != null) {
	    	if (mBackgroundMusic.isPlaying()) {
	    		mBackgroundMusic.pause();
	    	}
    	}
    }
    
    private void addLoopingStream(int stream) {
    	final int count = mLoopingStreams.length;
    	for (int x = 0; x < count; x++) {
    		if (mLoopingStreams[x] < 0) {
    			mLoopingStreams[x] = stream;
    			break;
    		}
    	}
    }
    
    private void removeLoopingStream(int stream) {
    	final int count = mLoopingStreams.length;
    	for (int x = 0; x < count; x++) {
    		if (mLoopingStreams[x] == stream) {
    			mLoopingStreams[x] = -1;
    			break;
    		}
    	}
    }
    
    private final int findSound(int resource) {
        mSearchDummy.resource = resource;
        return mSounds.find(mSearchDummy, false);
    }

	synchronized public final void setSoundEnabled(boolean soundEnabled) {
		mSoundEnabled = soundEnabled;
	}
	
	public final boolean getSoundEnabled() {
		return mSoundEnabled;
	}
	
	public void resumeBackground() {
		if (mBackgroundMusic != null) {
			mBackgroundMusic.start();
		}
	}
	
	public void playExplosion() {
		play(load(R.raw.explosion), false, SoundManager.PRIORITY_NORMAL);
	}
	
	public void playHurt() {
		play(load(R.raw.hurt), false, SoundManager.PRIORITY_NORMAL);
	}
	
    public class Sound {
        public int resource;
        public int soundId;
    }
    
    /** Comparator for sounds. */
    private final static class SoundComparator implements Comparator<Sound> {
        public int compare(final Sound object1, final Sound object2) {
            int result = 0;
            if (object1 == null && object2 != null) {
                result = 1;
            } else if (object1 != null && object2 == null) {
                result = -1;
            } else if (object1 != null && object2 != null) {
                result = object1.resource - object2.resource;
            }
            return result;
        }
    }

    
}
