package com.mibe.draglistview;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 並び替えができるListView
 * 改変元: http://goo.gl/1rkuy
 * @author mibe
 * @param <T>
 *
 */
public class DragListView extends ListView implements AdapterView.OnItemLongClickListener {

	@SuppressWarnings("unused")
	private static final String TAG = "DragListView";

	private static final int SCROLL_SPEED_FAST = 25;
	private static final int SCROLL_SPEED_SLOW = 8;

	private DragListAdapter<?> adapter;
	private PopupView popupView;
	private MotionEvent downEvent;
	private boolean dragging = false;

	private int view_left = 0;
	private int view_top = 0;

	// ドラッグ中のスクロール制御用ハンドラ
	private Handler handler = new Handler();

	// ドラッグ中のスクロール速度
	private int speed = 0;
	
	// タッチイベントの一時保存
	private MotionEvent me_temp;
	

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// ドラッグ中のスクロールイベント設定
		setDragScrollSchedule();

		popupView = new PopupView(context);
		setOnItemLongClickListener(this);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (adapter instanceof DragListAdapter == false) {
			throw new RuntimeException("引数adapterがDragListAdapterクラスではありません。");
		}
		super.setAdapter(adapter);
		this.adapter = (DragListAdapter<?>) adapter;
	}

	/**
	 * レンダリング終了後にViewのスクリーン座標を取得する
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus){
		super.onWindowFocusChanged(hasWindowFocus);

		int[] location = new int[2];
		getLocationOnScreen(location);

		view_left = location[0];
		view_top = location[1];
	}

	/**
	 * 長押しイベント<br>
	 * ドラッグを開始する。当イベントの前に、タッチイベント（ACTION_DOWN）が呼ばれている前提。
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return startDrag(downEvent);
	}

	/**
	 * タッチイベント
	 * ドラッグしている項目の移動や、ドラッグ終了の制御を行う。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		boolean result = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			storeMotionEvent(event);
			break;
		case MotionEvent.ACTION_MOVE:
			result = doDrag(event);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
			result = stopDrag(event);
			break;
		}
		
		// イベント内容を一時保存する
		me_temp = createTempMotionEvent(event);

		// イベントを処理していなければ、親のイベント処理を呼ぶ。
		// 長押しイベントを発生させるため、ACTION_DOWNイベント時は、親のイベント処理を呼ぶ。
		if (result == false) {
			result = super.onTouchEvent(event);
		}

		//Log.d("test", "onTouchEvent");
		return result;
	}
	
	/**
	 * イベント内容を一時保存する
	 * dxとdyがなくなっているのがポイント
	 * @param e
	 * @return
	 */
	private MotionEvent createTempMotionEvent(MotionEvent event){
		
		// 必要な値を取得する
		long downTime = event.getDownTime();
		long eventTime = event.getEventTime();
		int action = event.getAction();
		int metastate = event.getMetaState();
		
		// タッチ位置の座標を修正
		float x = event.getX();
		float y = event.getY();
		
		return MotionEvent.obtain(downTime, eventTime, action, x, y, metastate);
	}

	/**
	 * 長押しイベント時に、タッチ位置を取得するため、ACTION_DOWN時のMotionEventを保持する。
	 */
	private void storeMotionEvent(MotionEvent event) {
		downEvent = event;
	}

	/**
	 * ドラッグ開始
	 */
	private boolean startDrag(MotionEvent event) {
		//Log.v(TAG, "startDrag");

		dragging = false;

		// スクリーン座標を取得
		int x = (int) event.getX();
		int y = (int) event.getY();

		//Log.d(TAG, "x = " + x + ", y = " + y);

		// イベントから position を取得
		// 取得した position が 0未満＝範囲外の場合はドラッグを開始しない
		int position = eventToPosition(event);
		//Log.d("test", "position = " + position);
		if (position < 0) {
			return false;
		}


		// アダプターにドラッグ対象項目位置を渡す
		adapter.startDrag(position);

		// ドラッグ中のリスト項目の描画を開始する
		popupView.startDrag(x , y, getChildByIndex(position));

		// リストビューを再描画する
		invalidateViews();
		dragging = true;
		return true;
	}

	/**XXX
	 * この時はgetXでローカル座標がとれているっぽい
	 * 描画のみスクリーン座標にして処理する
	 * ドラッグ処理
	 */
	private boolean doDrag(MotionEvent event) {
		//Log.v(TAG, "doDrag");

		if (!dragging) {
			return false;
		}

		int x = (int) event.getX();
		int y = (int) event.getY();
		int position = pointToPosition(x, y);

		//Log.d(TAG, "x = " + x + ", y = " + y);

		// ドラッグの移動先リスト項目が存在する場合
		if (position != AdapterView.INVALID_POSITION) {
			// アダプターのデータを並び替える
			adapter.doDrag(position);
		}

		//XXX スクリーン座標にして送る
		// ドラッグ中のリスト項目の描画を更新する
		//popupView.doDrag(x, y);
		popupView.doDrag(x + view_left, y + view_top);

		// リストビューを再描画する
		invalidateViews();

		// 必要あればスクロールさせる
		// 注意：invalidateViews()後に処理しないとスクロールしなかった
		setScroll(event);
		
		return true;
	}

	/**
	 * ドラッグ終了
	 */
	private boolean stopDrag(MotionEvent event) {
		if (!dragging) {
			return false;
		}

		// スクロールの速度を0に戻す
		speed = 0;

		// アダプターにドラッグ対象なしを渡す
		adapter.stopDrag();

		// ドラッグ中のリスト項目の描画を終了する
		popupView.stopDrag();

		// リストビューを再描画する
		invalidateViews();
		dragging = false;
		return true;
	}

	/**
	 * 必要あればスクロールさせる。
	 * 座標の計算が煩雑になるので当Viewのマージンとパディングはゼロの前提とする。
	 */
	private void setScroll(MotionEvent event) {
		int y = (int) event.getY();
		int height = getHeight();
		int harfHeight = height / 2;
		int harfWidth = getWidth() / 2;

		// スクロール速度の決定
		int fastBound = height / 9;
		int slowBound = height / 4;
		if (event.getEventTime() - event.getDownTime() < 500) {
			// ドラッグの開始から500ミリ秒の間はスクロールしない
			speed = 0;
		} else if (y < slowBound) {
			speed = y < fastBound ? -SCROLL_SPEED_FAST : -SCROLL_SPEED_SLOW;
		} else if (y > height - slowBound) {
			speed = y > height - fastBound ? SCROLL_SPEED_FAST
					: SCROLL_SPEED_SLOW;
		} else {
			// スクロールなしのため処理終了
			speed = 0;
			return;
		}

		// 画面の中央にあるリスト項目位置を求める
		// 横方向はとりあえず考えない
		// 中央がちょうどリスト項目間の境界の場合は、位置が取得できないので、
		// 境界からずらして再取得する。
		int middlePosition = pointToPosition(harfWidth, harfHeight);
		if (middlePosition == AdapterView.INVALID_POSITION) {
			middlePosition = pointToPosition(harfWidth, harfHeight
					+ getDividerHeight());
		}

		// スクロール実施
		final View middleView = getChildByIndex(middlePosition);
		if (middleView != null) {
			setSelectionFromTop(middlePosition, middleView.getTop() - speed);
		}
	}

	// ドラッグ中のスクロールイベント設定
	private void setDragScrollSchedule(){
		// タイマーを生成
		Timer timer = new Timer(false);
		// スケジュールを設定
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(speed == 0) return;
				
				handler.post(new Runnable() {
					@Override
					public void run() {
						// スクロールの定期イベントを実行する
						dragScrollSchedule();
					}
				});
			}
		}, 0, 33);
	}
	
	// ドラッグ中のスクロールイベント内容
	private void dragScrollSchedule(){
		
		onTouchEvent(me_temp);
	}

	/**
	 * MotionEvent から position を取得する
	 */
	private int eventToPosition(MotionEvent event) {
		/**XXX 
		 * フルスクリーンじゃないとY座標がずれる
		 * pointToPositionはローカル座標（枠内の座標）で指定する
		 * 対してgetXやgetRawXはスクリーン座標っぽい
		 */

		//Log.d("test", "x = " + event.getX() + ", y = " + event.getY());
		//return pointToPosition((int) event.getX(), (int) event.getY());
		return pointToPosition((int) event.getRawX() - view_left, (int) event.getRawY() - view_top);
		//return pointToPosition((int) event.getX(), (int) event.getY()-120);
	}

	/**
	 * 指定インデックスのView要素を取得する
	 */
	private View getChildByIndex(int index) {
		return getChildAt(index - getFirstVisiblePosition());
	}
}