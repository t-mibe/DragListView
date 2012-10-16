package com.mibe.draglistview;

import com.tim.draglistviewsample.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * DragListViewを表示するActivity
 * @author mibe
 *
 */
public class DragListActivity extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//DragListView dlv = (DragListView) findViewById(R.id.dragListView);
	}
	
	public void setListAdapter(){
		
	}
}
