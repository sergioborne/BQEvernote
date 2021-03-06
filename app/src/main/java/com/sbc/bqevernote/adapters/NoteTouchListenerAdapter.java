/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package com.sbc.bqevernote.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * As RecyclerView does not have standard way to add click listeners to the
 * items, this RecyclerView.OnNoteTouchListener intercepts touch events and
 * translates them to simple onItemClick and onItemLongClick callbacks.
 */
public class NoteTouchListenerAdapter extends
		GestureDetector.SimpleOnGestureListener implements
		RecyclerView.OnItemTouchListener {

	public interface RecyclerViewOnItemClickListener {
		void onItemClick(RecyclerView parent, View clickedView, int position);

		void onItemLongClick(RecyclerView parent, View clickedView, int position);
	}

	private RecyclerViewOnItemClickListener listener;
	private RecyclerView recyclerView;
	private GestureDetector gestureDetector;

	public NoteTouchListenerAdapter(RecyclerView recyclerView,
									RecyclerViewOnItemClickListener listener) {
		if (recyclerView == null || listener == null) {
			throw new IllegalArgumentException(
					"RecyclerView and Listener arguments can not be null");
		}
		this.recyclerView = recyclerView;
		this.listener = listener;
		this.gestureDetector = new GestureDetector(recyclerView.getContext(),
				this);
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView,
			MotionEvent motionEvent) {
		gestureDetector.onTouchEvent(motionEvent);
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
	}

	@Override
	public void onShowPress(MotionEvent e) {
		View view = getChildViewUnder(e);
		if (view != null) {
			view.setPressed(true);
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		View view = getChildViewUnder(e);
		if (view == null)
			return false;

		view.setPressed(false);
//		view.setPressed(true);
		int position = recyclerView.getChildAdapterPosition(view);
		listener.onItemClick(recyclerView, view, position);
		return true;
	}

	public void onLongPress(MotionEvent e) {
		View view = getChildViewUnder(e);
		if (view == null)
			return;
		int position = recyclerView.getChildPosition(view);
		listener.onItemLongClick(recyclerView, view, position);
		view.setPressed(false);
	}

	@Nullable
	private View getChildViewUnder(MotionEvent e) {
		return recyclerView.findChildViewUnder(e.getX(), e.getY());
	}

}
