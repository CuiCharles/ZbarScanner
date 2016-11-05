package com.example.zbarscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
    }
    
    public void launchScannerActivity(View v)
    {
    	launchNewActivity(ScanDecodeActivity.class);
    }
    
    public void launchImageActivity(View v)
    {
    	launchNewActivity(ImageDecodeActivity.class);
    }
    
    public void launchNewActivity(Class<?> mClass)
    {
    	Intent intent = new Intent(MainActivity.this,mClass);
    	startActivity(intent);
    }

	

   
}
