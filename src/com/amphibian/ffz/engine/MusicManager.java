package com.amphibian.ffz.engine;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.SparseArray;

import com.amphibian.ffz.App;
import com.amphibian.ffz.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MusicManager {

	private SparseArray<String> library;
	private MediaPlayer mediaPlayer;

	public MusicManager() {
		
		library = new SparseArray<String>();
		
		try {
			
			Reader dataReader = new InputStreamReader(App.getContext()
					.getResources().openRawResource(R.raw.music_library));
			
			Gson gson = new Gson();

			Type collectionType = new TypeToken<List<Music>>(){}.getType();
			List<Music> mList = gson.fromJson(dataReader, collectionType);			
			
			Iterator<Music> i = mList.iterator();
			while (i.hasNext()) {
				Music m = i.next();
				library.put(m.getId(), m.getFilename());
			}
			
		} catch (Exception e) {
			Log.e(App.name, "error reading music library", e);
		}
		
	}
	
	public void playMusic(int id) {
		
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}

		String fn = library.get(id);
		if (fn != null) {

			mediaPlayer = new MediaPlayer();

			try {
				
				AssetFileDescriptor descriptor = App.getContext().getAssets().openFd(fn);
				mediaPlayer.setDataSource(descriptor.getFileDescriptor(),
						descriptor.getStartOffset(), descriptor.getLength());
				descriptor.close();
				
				mediaPlayer.prepare();
				mediaPlayer.setVolume(0.7f, 0.7f);
				mediaPlayer.setLooping(true);
				mediaPlayer.start();
				
			} catch (IOException e) {
				Log.e(App.name, "unable to play music " + fn + " with id " + id, e);
			}

		} else {
			Log.w(App.name, "unknown music with id " + id);
		}
		
	}
	
	public void stopMusic() {
		
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
	}
	
	private class Music {
		
		private int id;
		private String title;
		private String author;
		private String filename;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getAuthor() {
			return author;
		}
		public void setAuthor(String author) {
			this.author = author;
		}
		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
	
}
