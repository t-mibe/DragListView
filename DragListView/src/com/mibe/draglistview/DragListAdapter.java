package com.mibe.draglistview;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 並び替えできるListViewの配列管理
 * 改変元: http://goo.gl/1rkuy
 * @author mibe
 * @param <T> : Array配列に入れる型，当分Stringのみ考える
 */
public class DragListAdapter<T> extends BaseAdapter {
	
	// 表示用アイテムをArray<String>に変更
	public ArrayList<String> list_view;
	
	// 表示itemと連動して並び替える配列
	public ArrayList<T> list_data;
	
	private Context context;
	
	// 長押しでドラッグモードにしたメンバ番号を保存する（-1で無効）
	private int currentPosition = -1;

	// 呼び出された時の処理
	public DragListAdapter(Context context) {
		
		// 親のContextを保存する
		this.context = context;
	}

	@Override
	public int getCount() {
		return list_view.size();
	}

	/**
	 * 指定した番号のアイテムを取得する
	 */
	@Override
	public Object getItem(int position) {
		
		// 表示用の配列から該当する値を返す
		return list_view.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * リスト項目のViewを取得する
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Viewが無い場合作成する
		if (convertView == null) {
			convertView = new TextView(context);
		}
		
		// TextViewとして保持しておく
		TextView textView = (TextView) convertView;
		
		// テキストのサイズを指定する
		textView.setTextSize(30);

		// データ設定
		textView.setText((String) getItem(position));

		// ドラッグ対象項目は、ListView側で別途描画するため、非表示にする。
		// ドラッグ対象は別処理でフロートして表示する
		if (position == currentPosition) {
			// ドラッグ中の対象が元の位置にいるとき
			
			// メンバを非表示にする
			textView.setVisibility(View.INVISIBLE);
		} else {
			// ドラッグ中の対象が
			
			textView.setVisibility(View.VISIBLE);
		}
		return textView;
	}

	/**
	 * ドラッグ開始
	 * @param position
	 */
	public void startDrag(int position) {
		
		// 今選択しているメンバ番号を保存する
		this.currentPosition = position;
	}

	/**
	 * ドラッグに従ってデータを並び替える
	 * @param newPosition : 移動先の番号
	 */
	public void doDrag(int newPosition) {
		
		// 選択した表示テキストを保存する
		String temp_view = list_view.get(currentPosition);
		
		// 連動させるデータを保存する
		T temp_data = list_data.get(currentPosition);
		
		
		if (currentPosition < newPosition) {
			// リスト項目を下に移動している場合
			
			// 下にあったデータ達を1つ上にずらす
			for (int i = currentPosition; i < newPosition; i++) {
				list_view.set(i, list_view.get(i + 1)); // 表示データ
				list_data.set(i, list_data.get(i + 1)); // 内部データ
			}
			
		} else if (currentPosition > newPosition) {
			// リスト項目を上に移動している場合
			
			// 上にあったデータ達を1つ下にずらす
			for (int i = currentPosition; i > newPosition; i--) {
				list_view.set(i, list_view.get(i - 1)); // 表示データ
				list_data.set(i, list_data.get(i - 1)); // 内部データ
			}
		}
		
		// 移動先に保存していたデータを書き込む
		list_view.set(newPosition, temp_view);
		list_data.set(newPosition, temp_data);
		
		// 選択している場所を移動先に変更する
		currentPosition = newPosition;
	}

	/**
	 * ドラッグ終了
	 */
	public void stopDrag() {
		
		// ドラッグ中のメンバ番号を消去する
		this.currentPosition = -1;
	}
	
	/**
	 * XXX list_dataへのアイテム追加
	 */
	private void addlist_data(T t){
		list_data.add(t);
	}
	
	/**
	 * XXX list_dataへの文字列アイテム追加
	 * 型チェックをどうにかしたい
	 */
	public void addStringlist_data(String text){
		
		@SuppressWarnings("unchecked") //FIXME 出来れば消したい
		T t = (T) text;
		
		addlist_data(t);
	}
}