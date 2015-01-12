package com.example.birdsworld;

import java.io.IOException;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class BirdDetails extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bird_details);
		
		DataBaseHelper myDbHelper = new DataBaseHelper(this);
		try 
		{ 
        	myDbHelper.createDataBase();
		} 
		catch (IOException ioe) 
		{
			throw new Error("Unable to create database");
		}
		try 
		{
			myDbHelper.openDataBase();
		}
		catch(SQLException sqle)
		{
			throw sqle;
		}
		
		int birdID;
		birdID = onChangeBirdDetail(myDbHelper);

		onPlaySoundTrigger(myDbHelper, birdID);
		
        // get action bar  
        ActionBar actionBar = getActionBar();
 
        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
		//onChangeBirdDetail(myDbHelper);
		
	}

	    //Change bird details to be display
		public int onChangeBirdDetail(DataBaseHelper myDbHelper)
		{
			TextView birdNameField = (TextView)findViewById(R.id.birdNameField);
			TextView sciNameField = (TextView)findViewById(R.id.sciNameField);
			TextView localNameField = (TextView)findViewById(R.id.localNameField);
			TextView birdSizeField = (TextView)findViewById(R.id.birdSizeField);
			TextView habitatField = (TextView)findViewById(R.id.habitatField);
			TextView distributionField1 = (TextView)findViewById(R.id.distributionField1);
			TextView distributionField2 = (TextView)findViewById(R.id.distributionField2);
			TextView distributionField3 = (TextView)findViewById(R.id.distributionField3);
			TextView detailField = (TextView)findViewById(R.id.detailField);
			TextView referenceField = (TextView)findViewById(R.id.referenceField);
			ImageView birdImage = (ImageView)findViewById(R.id.birdPic);
			
			Cursor familyC = myDbHelper.rawQuery ("select * from Family where engName=\"" + MainActivity.birdName + "\"", null);
			familyC.moveToFirst();
			int familyId = familyC.getInt(familyC.getColumnIndex("_id"));
			
			birdNameField.setText(familyC.getString(familyC.getColumnIndex("engName")));
			sciNameField.setText(familyC.getString(familyC.getColumnIndex("sciName")));
			
			Cursor speciesC = myDbHelper.rawQuery ("select * from Species where familyId=" + familyId, null);
			speciesC.moveToFirst();
			int birdID = speciesC.getInt(speciesC.getColumnIndex("_id"));
			
			localNameField.setText(speciesC.getString(speciesC.getColumnIndex("localName")));
			
			Cursor detailsC = myDbHelper.rawQuery ("select * from Details where _id=" + birdID, null);
			detailsC.moveToFirst();
						
			distributionField1.setText(detailsC.getString(detailsC.getColumnIndex("distpen")));
			distributionField2.setText(detailsC.getString(detailsC.getColumnIndex("distSar")));
			distributionField3.setText(detailsC.getString(detailsC.getColumnIndex("distSab")));
			referenceField.setText(detailsC.getString(detailsC.getColumnIndex("reference")));
			birdSizeField.setText(detailsC.getString(detailsC.getColumnIndex("size")));
			habitatField.setText(detailsC.getString(detailsC.getColumnIndex("habitat")));
			
			Cursor aliasesC = myDbHelper.rawQuery ("select * from Aliases where _id=" + birdID, null);
			aliasesC.moveToFirst();
			detailField.setText(aliasesC.getString(aliasesC.getColumnIndex("Alias")));
			
			Cursor imageC = myDbHelper.rawQuery ("select * from Image where _id=" + birdID , null);
			imageC.moveToFirst();
			
			AssetManager assetManager = getAssets();
	        InputStream istr = null;
	        try {
	            istr = assetManager.open(imageC.getString(imageC.getColumnIndex("pathName")));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        Bitmap bitmap = BitmapFactory.decodeStream(istr);
			birdImage.setImageBitmap(bitmap);
			
			return birdID;
		}
		
		//PlaySound button click action
		private void onPlaySoundTrigger(final DataBaseHelper myDbHelper, final int birdID)
		{
			findViewById(R.id.button_playsound).setOnClickListener(new OnClickListener(){
				
	        	@Override
				public void onClick(View v) {
					int btn=v.getId();
					if(btn==R.id.button_playsound){
						
						Cursor audioC = myDbHelper.rawQuery ("select * from Audio where _id=" + birdID , null);
						audioC.moveToFirst();
						String path = audioC.getString(audioC.getColumnIndex("pathName"));
						Toast.makeText(getBaseContext(), "Playing bird's sound", Toast.LENGTH_SHORT).show();
						audioC.moveToFirst();
						
						AssetFileDescriptor afd;
						try {
							afd = getAssets().openFd(path);
						} catch (IOException e) {
							afd = null;
						}
						
						
						MediaPlayer player = new MediaPlayer();
						try {
							player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							player.prepare();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						player.start();
						
						//Release Media Player and display message when playback ended
						player.setOnCompletionListener(new
							    OnCompletionListener() {        
							        @Override
							        public void onCompletion(MediaPlayer player) {
							        	Toast.makeText(getBaseContext(), "Playback completed", Toast.LENGTH_SHORT).show();    
							        	player.release();
							        	player = null;
							    }
							});
						
					}
					
				}
			});
		}
		
}
