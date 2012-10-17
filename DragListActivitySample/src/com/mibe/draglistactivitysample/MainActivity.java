package com.mibe.draglistactivitysample;

import java.util.ArrayList;

import com.mibe.draglistview.DragListActivity;

import android.os.Bundle;
import android.widget.Toast;

/**
 * 自作ライブラリのDragListViewを使うサンプルアプリ
 * その中のDragListActivityを使ったもの
 * @author mibe
 */
public class MainActivity extends DragListActivity {

	// 起動時の処理
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 配列の内容を初期設定する
	 */
	@Override
	public void initArrayList(){
		// とりあえずダミーデータを配置する

		// 配列の初期化
		list_view = new ArrayList<String>();
		list_data = new ArrayList<Object>();

		// ダミーデータの登録
		for(int i = 0; i < 100; i++){
			list_view.add("view_".concat(Integer.toString(i)));
			list_data.add("data_".concat(Integer.toString(i)));
		}
	}
	
	/**
	 * アイテムがクリックされた時の処理
	 * @param position	: 指定したアイテムの順番（数値処理等に使う）
	 * @return          : クリック処理の成否を返す（とりあえずfalse）
	 */
	@Override
	public boolean onItemClicked(int position){
		
		// とりあえずトースト出力
		String data = (String)list_data.get(position);
		Toast.makeText(this, data, Toast.LENGTH_LONG).show();

		return false;
	}
}
