package com.example.birdsworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		final Button button_about_ok=(Button)findViewById(R.id.button_about_ok);
		
		button_about_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	int btn=v.getId();
            	if(btn==R.id.button_about_ok){
            		onBackPressed();
            	}
            }
		});
	}
}
