package com.example.birdsworld;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static String birdName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);

        //create database and populate list with bird name
        String[] list;
        list = createDatabase();
        //trigger list to bird detail page
        onListTrigger(list);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
       
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            Intent intent=new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
            return true;
        case R.id.action_settings:
        	onSettingsButtonClick();
            return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	//Settings button action
	private void onSettingsButtonClick()
	{
		findViewById(R.id.action_settings).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View view) {

				PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
				popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

	                @Override
	                public boolean onMenuItemClick(MenuItem item) {
	                	int btn=item.getItemId();
	                	if(btn==R.id.action1){
	                		Toast.makeText(getBaseContext(), "You selected the " + item.getTitle(), Toast.LENGTH_SHORT).show();
	                		Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
	                		startActivity(intent);
	                	}
	                	else if(btn==R.id.action2){
	                		Intent intent = new Intent(MainActivity.this, About.class);
	                        startActivity(intent);
	                	}
	                    return true;
	                }
	            });
				popupMenu.inflate(R.menu.popup);
				popupMenu.show();
			}
		});
	}
	
	//Create database
	private String[] createDatabase()
	{
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
	 	
	     //rawQuery declared as public in DataBaseHelper class before can use, need simplify
	 	Cursor c = myDbHelper.rawQuery ("select engName from Family", null);
	 	String[] list = new String[c.getCount()];
	 	int iName = 0;
	 	int counter = 0;
	 
	 	for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
	 		list[counter] = c.getString(iName);
	 	    counter++;
	 	}
	 	
	 	ListView listView = (ListView)findViewById(R.id.birdList);
	 	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
	 	listView.setAdapter(adapter);
	 	
	 	return list;
	 	 
	}
	
	//trigger list to bird detail
	private void onListTrigger(String[] list)
	{
		final ListView listview = (ListView)findViewById(R.id.birdList);
		listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , list));
		listview.setTextFilterEnabled(true);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id)
			{
				birdName = (String) listview.getItemAtPosition(position);
				Intent intent=new Intent(MainActivity.this, BirdDetails.class);
				startActivity(intent);
			}
		});
				
	}
}