package com.mibe.draglistview;

import java.util.ArrayList;
import java.util.Arrays;

import com.mibe.draglistview.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * DragListViewを表示するActivity
 * 現状，データ配列はStringで考える
 * byteとかにしたい場合はDragListViewを使う事
 * @author mibe
 */
public abstract class DragListActivity extends Activity{

	// リストビューに表示するテキスト用のArrayList
	public ArrayList<String> list_view = new ArrayList<String>();

	// 表示用リストと連動してソートされるデータ用のArrayList
	public ArrayList<String> list_data = new ArrayList<String>();

	// 表示するソート可能なListView
	DragListView listView;

	//////////////////////////////
	// Activityクラスのメソッド //
	//////////////////////////////

	// 起動時の処理
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初回起動時のみ行う処理
		if(savedInstanceState == null){
			// 配列の内容を初期設定する
			setArrayList();

			// DragListViewを作成，表示する
			setDragListView();
		}
	}

	// Bundleに状態を保存
	@Override  
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// 配列の内容をBundleに保存する
		saveArrayList(outState);
		
		// スクロール位置を取得する
		DragListView dlv = (DragListView)findViewById(R.id.dragListView);
		int position = dlv.getFirstVisiblePosition();
		outState.putInt("DLV_position", position);
	}

	@Override  
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// 配列の内容をBundleから復元して表示する
		loadArrayList(savedInstanceState);
		
		// スクロール位置を復元する
		int position = savedInstanceState.getInt("DLV_position");
		DragListView dlv = (DragListView)findViewById(R.id.dragListView);
		dlv.setSelectionFromTop(position, 0);
	}

	//////////////////////
	// DragListView関連 //
	//////////////////////
	
	// 配列の内容を初期化する
	public void initArrayList(){
		list_view = new ArrayList<String>();
		list_data = new ArrayList<String>();
	}

	/**
	 * 配列の内容を設定する
	 * 処理は子クラスで定義する
	 */
	public abstract void setArrayList();

	/**
	 * DragListViewを作成，表示する
	 * 		1. Arrayの型を指定する
	 * 		2. オブジェクトIDを指定する
	 */
	public void setDragListView(){

		// ソート可能なListAdapterを作成する
		DragListAdapter<String> adapter = new DragListAdapter<String>(this);

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
	 * 配列の内容をBundleに保存する
	 * @param bundle
	 */
	private void saveArrayList(Bundle bundle){

		// 表示する配列をString[]形式にして保存する
		String[] str_view = list_view.toArray(new String[0]);
		bundle.putStringArray("DLA_list_view", str_view);
		
		// データ配列をString[]形式にして保存する
		String[] str_data = list_data.toArray(new String[0]);
		bundle.putStringArray("DLA_list_data", str_data);
	}

	/**
	 * 配列の内容をBundleから復元して表示する
	 * @param bundle
	 */
	private void loadArrayList(Bundle bundle){

		// 表示する配列をString[]形式で取得する
		String[] str_view = bundle.getStringArray("DLA_list_view");

		// String[]形式をArrayListに変換する
		list_view = new ArrayList<String>(Arrays.asList(str_view));
		

		// データ配列をString[]形式で取得する
		String[] str_data = bundle.getStringArray("DLA_list_data");

		// String[]形式をArrayListに変換する
		list_data = new ArrayList<String>(Arrays.asList(str_data));
		
		// DragListViewを作成，表示する
		setDragListView();
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

	//////////////////
	// メニュー関連 //
	//////////////////

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
		} else {
			ret = super.onOptionsItemSelected(item);
		}

		return ret;
	}

	/**
	 * 追加ボタンの処理
	 */
}
