package com.example.birdsworld;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
 
public class SearchActivity extends Activity {
 
    private TextView txtQuery;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
 
        // get the action bar
        ActionBar actionBar = getActionBar();
 
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
 
        txtQuery = (TextView) findViewById(R.id.txtQuery);
 
        String[] result = handleIntent(getIntent());
        onListTrigger(result);
    }
 
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String[] result = handleIntent(intent);
        onListTrigger(result);
    }
 
    /**
     * Handling intent data
     */
    private String[] handleIntent(Intent intent)
    {
    	String[] result={null};
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            
            DataBaseHelper searchResult = new DataBaseHelper(this);
            
            try 
    		{ 
            	searchResult.createDataBase();
    		} 
    		catch (IOException ioe) 
    		{
    			throw new Error("Unable to create database");
    		}
    		try 
    		{
    			searchResult.openDataBase();
    		}
    		catch(SQLException sqle)
    		{
    			throw sqle;
    		}
    		
    		Cursor c = searchResult.rawQuery ("SELECT * FROM Family WHERE engName LIKE \"%" + query + "%\"", null);
    		String[] list = new String[c.getCount()];
    	 	txtQuery.setText(" You searched for: " + query);
    	 	
    	
    	 	int counter = 0;
    	 	for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
    	 	{
    	 		list[counter] = c.getString(c.getColumnIndex("engName"));
    	 		counter++;
    	 	}
    	 	
    	 	ListView listView = (ListView)findViewById(R.id.resultView1);
    	 	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
    	 	listView.setAdapter(adapter);
    		result = list;
    		/**
             * Use this query to display search results like 
             * 1. Getting the data from SQLite and showing in listview 
             * 2. Making webrequest and displaying the data 
             * For now we just display the query only
             */
            
 
        }
		return result;
 
    }
    
    private void onListTrigger(String[] list)
	{
		final ListView listview = (ListView)findViewById(R.id.resultView1);
		listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , list));
		listview.setTextFilterEnabled(true);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id)
			{
				MainActivity.birdName = (String) listview.getItemAtPosition(position);
				Intent intent=new Intent(SearchActivity.this, BirdDetails.class);
				startActivity(intent);
			}
		});
				
	}
}