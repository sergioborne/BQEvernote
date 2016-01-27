package com.sbc.bqevernote;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity {

    private boolean twoPane = false;

    private RecyclerView recyclerView;

    private ArrayList<String> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }else{
            twoPane = false;
        }

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        setupRecyclerView(recyclerView, this);
    }

    private void setupRecyclerView(RecyclerView recyclerView, Context context) {
        recyclerView.setAdapter(new NoteAdapter(notes, context, R.layout.note_list_content));

        //We can setup layout manager here or in xml
//        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLinearLayoutManager);

        recyclerView.addOnItemTouchListener(new NoteTouchListenerAdapter(
                recyclerView,
                new NoteTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent,
                                            View clickedView, int position) {

                    }

                    @Override
                    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {}

                }));
    }
}
