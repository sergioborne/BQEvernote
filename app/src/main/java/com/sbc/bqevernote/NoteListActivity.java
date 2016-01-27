package com.sbc.bqevernote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.client.android.type.NoteRef;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, EvernoteLoginFragment.ResultCallback{

    private static final int MAX_NOTES = 20;

    private boolean twoPane = false;

    private Activity activity;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ArrayList<NoteRef> notes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_list);

        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }else{
            twoPane = false;
        }

        notes = new ArrayList<NoteRef>();

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        setupRecyclerView(recyclerView, this);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            EvernoteSession.getInstance().authenticate(this);
        }
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if(successful){
            new FindNotesTask(0, MAX_NOTES, null, recyclerView).execute();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Login");
            builder.setMessage("Login required");
            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EvernoteSession.getInstance().authenticate(activity);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }
}
