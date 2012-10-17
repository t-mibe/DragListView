package com.mibe.draglistview;

import java.util.ArrayList;

import com.tim.draglistview.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * DragListViewを表示するActivity
 * 現状，データ配列はString固定
 * @author mibe
 * @param <T>
 */
public abstract class DragListActivity extends Activity{

	// リストビューに表示するテキスト用のArrayList
	public ArrayList<String> list_view;

	// 表示用リストと連動してソートされるデータ用のArrayList
	public ArrayList<Object> list_data;

	// 表示するソート可能なListView
	DragListView listView;

	// 起動時の処理
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 配列の内容を初期設定する
		initArrayList();

		// DragListViewを作成，表示する
		setDragListView();
	}

	/**
	 * 配列の内容を初期設定する
	 * 処理は子クラスで定義する
	 */
	public abstract void initArrayList();

	/**
	 * DragListViewを作成，表示する
	 * 		1. Arrayの型を指定する
	 * 		2. オブジェクトIDを指定する
	 */
	public void setDragListView(){

		// ソート可能なListAdapterを作成する
		DragListAdapter<Object> adapter = 
				new DragListAdapter<Object>(this);

		// 表示用配列と連動配列を設定する
		adapter.list_view = list_view;
		adapter.list_data = list_data;

		// ソート可能なListViewを作成し配列を読み込ませる
		listView = (DragListView) findViewById(R.id.dragListView);

		//ListViewのスクロールバーにつまみをつける
		listView.setFastScrollEnabled(true);

		// ListViewと配列管理アダプタを接続する
		listView.setAdapter(adapter);

		// DragListViewのアイテムがクリックされた時の処理を登録する
		setDragListViewClickListener();
	}

	/**
	 * DragListViewのアイテムがクリックされた時の処理を登録する
	 * 特に変更しないはず
	 * @param listView: 既に作成したDragListView
	 */
	private void setDragListViewClickListener(){

		// リストビューのアイテムがクリックされた時の処理を登録する
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// アイテムがクリックされた時の処理を実行します
				onItemClicked(position);
			}
		});
	}

	/**
	 * アイテムがクリックされた時の処理
	 * 処理は子クラスで定義する
	 */
	public abstract boolean onItemClicked(int position);

	///////////////////////////
	// メニュー関連 ここから //
	///////////////////////////

	/**
	 * メニュー生成
	 * 必要ならこの処理をオーバーライドする（メニューを無効にする時含む）
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 *  メニューが選択された時の処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean ret = true;

		int id = item.getItemId();

		if(id == R.id.menu_add){
			Toast.makeText(this, getString(R.string.menu_add), Toast.LENGTH_SHORT).show();
			ret = true;
		} else if(id == R.id.menu_settings){
			Toast.makeText(this, getString(R.string.menu_settings), Toast.LENGTH_SHORT).show();
			ret = true;
		} else {
			ret = super.onOptionsItemSelected(item);
		}

		return ret;
	}

	/**
	 * 追加ボタンの処理
	 */
}
