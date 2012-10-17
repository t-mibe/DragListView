package com.mibe.draglistview;

import java.util.ArrayList;
import java.util.Arrays;

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
 * 現状，データ配列はbute[]で考える
 * @author mibe
 */
public abstract class DragListActivity extends Activity{

	// リストビューに表示するテキスト用のArrayList
	public ArrayList<String> list_view;

	// 表示用リストと連動してソートされるデータ用のArrayList
	public ArrayList<byte[]> list_data;

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
			initArrayList();

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
		DragListAdapter<byte[]> adapter = new DragListAdapter<byte[]>(this);

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

		// 表示する配列をString[]形式にする
		String[] str_view = list_view.toArray(new String[0]);
		bundle.putStringArray("DLA_list_view", str_view);

		// 配列の長さを取得する
		int len = list_data.size();

		// 連動する配列を1つずつ保存する
		for(int i = 0; i < len; i++){

			// 該当するデータ配列の内容を保存する
			byte[] data = list_data.get(i);
			bundle.putByteArray("DLA_list_data_".concat(Integer.toString(i)), data);
		}
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

		// データ配列を初期化する
		list_data = new ArrayList<byte[]>();

		// 配列の長さを取得する
		int len = list_view.size();

		// 連動する配列の情報を1つずつ取得する
		for(int i = 0; i < len; i++){

			// 該当するデータ配列の内容を取得する
			byte[] data =bundle.getByteArray("DLA_list_data_".concat(Integer.toString(i)));
			list_data.add(data);
		}
		
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
