package com.mibe.draglistactivitysample;

import com.mibe.draglistview.DragListActivity;

import android.content.res.Configuration;
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

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		Toast.makeText(this, "ori", Toast.LENGTH_SHORT).show();
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * 配列の内容を初期設定する
	 */
	@Override
	public void setArrayList(){
		// とりあえずダミーデータを配置する
		
		// 配列の初期化
		initArrayList();

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
		String data = new String(list_data.get(position));
		Toast.makeText(this, data, Toast.LENGTH_LONG).show();

		return false;
	}
}
