package com.sbc.bqevernote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<String> mItems;
	// Allows to remember the last item shown on screen
	private int lastPosition = -1;

	private int mResLayout;

	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView title;
		TextView date;
		// We need to retrieve the container because it's the view that will be animated
		RelativeLayout container;

		public ViewHolder(View itemView) {
			super(itemView);
			container = (RelativeLayout) itemView
					.findViewById(R.id.item_content_container);
			title = (TextView) itemView.findViewById(R.id.titleTextView);
			date = (TextView) itemView.findViewById(R.id.dateTextView);
		}

		public void bindNote(String note) {
			if (!note.equals("")) {
				title.setText(note);
			} else {
				title.setText("");
			}
		}
	}

	public NoteAdapter(ArrayList<String> mItems, Context context, int resLayout) {
		this.mItems = mItems;
		this.mContext = context;

		this.mResLayout = resLayout;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(mResLayout,
				parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		String note = mItems.get(position);
		if (note != null) {
			holder.bindNote(note);
		}
		setAnimation(holder.itemView, position);
	}

	/**
	 * Here is the key method to apply the animation
	 */
	private void setAnimation(View viewToAnimate, int position) {
		// If the bound view wasn't previously displayed on screen, it's
		// animated
		if (position > lastPosition) {
			Animation animation = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_from_bottom);
			animation.setStartOffset(position*30);
			viewToAnimate.startAnimation(animation);
			lastPosition = position;
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

    public void updateNoteItems(ArrayList<String> items){
        this.mItems = items;
		notifyDataSetChanged();
    }
}