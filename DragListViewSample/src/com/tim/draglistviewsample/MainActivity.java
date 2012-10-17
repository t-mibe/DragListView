package com.tim.draglistviewsample;

import java.util.ArrayList;

import com.mibe.draglistview.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


/**
 * 自作ライブラリのDragListViewを使うサンプルアプリ
 * 
 * 準備
 * プロジェクトのプロパティの"Android"項目
 * ライブラリーの追加ボタンでDragListActivityを追加
 * 
 * コーディング手順
 * 1. 表示用と追従用のArrayListを作成する
 * 2. DragListAdapterを作成し，2つの配列を登録する
 * 3. 
 * 
 * @author mibe
 *
 */
public class MainActivity extends Activity {

	// リストビューに表示する用のArrayList
	ArrayList<String> list_view;

	// 表示用リストと連動してソートされるArrayList
	ArrayList<String> list_data;

	// 表示するソート可能なListView
	DragListView listView;

	// 起動時の処理
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ArrayListを設定する
		setArrayList();

		// DragListViewを作成し表示する
		setDragListView();

		// DragListViewのアイテムがクリックされた時の処理を登録する
		setDragListViewClickListener();
	}

	/**
	 * ArrayListを設定する
	 * サンプル用に2つの配列にダミーデータを入れている
	 */
	private void setArrayList(){

		// 配列の初期化
		list_view = new ArrayList<String>();
		list_data = new ArrayList<String>();

		// ダミーデータの設定
		for(int i = 0; i < 100; i++){
			list_view.add("v".concat(Integer.toString(i)));
			list_data.add("f".concat(Integer.toString(i)));
		}
	}

	/**
	 * DragListViewを作成，表示する
	 * 		1. Arrayの型を指定する
	 * 		2. オブジェクトIDを指定する
	 */
	private void setDragListView(){

		// ソート可能なListAdapterを作成する
		DragListAdapter<String> adapter = 
				new DragListAdapter<String>(this);

		// 表示用配列と連動配列を設定する
		adapter.list_view = list_view;
		adapter.list_data = list_data;

		// ソート可能なListViewを作成し配列を読み込ませる
		listView = (DragListView) findViewById(R.id.dragListView);

		//ListViewのスクロールバーにつまみをつける
		listView.setFastScrollEnabled(true);

		// ListViewと配列管理アダプタを接続する
		listView.setAdapter(adapter);
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
	 * 
	 * アイテムがクリックされた時の処理
	 * とりあえずトースト表示
	 * @param position	: 指定したアイテムの順番（数値処理等に使う）
	 * @return          : クリック処理の成否を返す（とりあえずfalse）
	 */
	private boolean onItemClicked(int position){

		// 成否保存用
		boolean result = false;

		// トースト出力
		// ArrayList.get(id)を利用して配列から文字列を取得する
		// 今回は追従配列から取得してみた
		Toast.makeText(this, list_data.get(position), Toast.LENGTH_LONG).show();

		return result;
	}

}
