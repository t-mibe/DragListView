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
 * @param <T> : Array配列に入れる型
 */
public class DragListAdapter<T> extends BaseAdapter {
	
	// 表示用アイテムをArray<String>に変更
	public ArrayList<String> list_view;
	
	// 表示itemと連動して並び替える配列
	public ArrayList<T> list_data;
	
	private Context context;
	private int currentPosition = -1;

	public DragListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		//return items.length;
		return list_view.size();
	}

	@Override
	public Object getItem(int position) {
		//return items[position];
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
		// View作成
		if (convertView == null) {
			convertView = new TextView(context);
		}
		TextView textView = (TextView) convertView;
		textView.setTextSize(30);

		// データ設定
		textView.setText((String) getItem(position));

		// ドラッグ対象項目は、ListView側で別途描画するため、非表示にする。
		if (position == currentPosition) {
			textView.setVisibility(View.INVISIBLE);
		} else {
			textView.setVisibility(View.VISIBLE);
		}
		return textView;
	}

	/**
	 * ドラッグ開始
	 *
	 * @param position
	 */
	public void startDrag(int position) {
		this.currentPosition = position;
	}

	/**XXX ArrayListの並び替えを追加
	 * ドラッグに従ってデータを並び替える
	 *
	 * @param newPosition
	 */
	public void doDrag(int newPosition) {
		//String item = items[currentPosition];
		String temp1 = list_view.get(currentPosition);
		T temp2 = list_data.get(currentPosition);
		if (currentPosition < newPosition) {
			// リスト項目を下に移動している場合
			for (int i = currentPosition; i < newPosition; i++) {
				//items[i] = items[i + 1];
				list_view.set(i, list_view.get(i + 1)); //XXX
				list_data.set(i, list_data.get(i + 1)); //XXX
			}
		} else if (currentPosition > newPosition) {
			// リスト項目を上に移動している場合
			for (int i = currentPosition; i > newPosition; i--) {
				//items[i] = items[i - 1];
				list_view.set(i, list_view.get(i - 1)); //XXX
				list_data.set(i, list_data.get(i - 1)); //XXX
			}
		}
		//items[newPosition] = item;
		list_view.set(newPosition, temp1);
		list_data.set(newPosition, temp2);
		
		currentPosition = newPosition;
	}

	/**
	 * ドラッグ終了
	 */
	public void stopDrag() {
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