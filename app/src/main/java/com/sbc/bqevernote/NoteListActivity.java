package com.sbc.bqevernote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.login.EvernoteLoginFragment;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.NoteSortOrder;
import com.sbc.bqevernote.task.CreateNewNoteTask;
import com.sbc.bqevernote.task.FindNotesService;
import com.sbc.bqevernote.task.MyResultReceiver;

import java.util.ArrayList;

public class NoteListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, EvernoteLoginFragment.ResultCallback, MyResultReceiver.Receiver{

    public static final String NOTES_TAG = "notes";
    public static final String RECEIVER_TAG = "receiverTag";
    public static final String ORDER_TAG = "order";
    public static final String MAX_NOTES_TAG = "maxNotes";
    public static final String REFRESHING_TAG = "refreshing";

    private static final int MAX_NOTES = 20;

    private boolean twoPane = false;

    private Activity activity;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int noteOrder = NoteSortOrder.UPDATED.getValue();

    private ArrayList<NoteRef> notes;
    public MyResultReceiver mReceiver;

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
                new CreateNoteDialogFragment().show(getSupportFragmentManager(), CreateNoteDialogFragment.TAG);
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

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        notes = new ArrayList<>();
        setupRecyclerView(recyclerView, this);
        if(savedInstanceState==null) {
            mReceiver = new MyResultReceiver(new Handler());
            mReceiver.setReceiver(this);
            if (!EvernoteSession.getInstance().isLoggedIn()) {
                EvernoteSession.getInstance().authenticate(this);
            } else {
                loadNotes();
            }
        }else{
            notes = savedInstanceState.getParcelableArrayList(NOTES_TAG);
            //Update recycler items to be the same as before rotating
            ((NoteAdapter)recyclerView.getAdapter()).updateNoteItems(notes);
            mReceiver = savedInstanceState.getParcelable(RECEIVER_TAG);
            //Reasign receiver to receive in this new instance the event
            mReceiver.setReceiver(this);
            if(savedInstanceState.getBoolean(REFRESHING_TAG)){
                setRefreshingEnabled();
            }
        }
    }

    private void setupRecyclerView(final RecyclerView recyclerView, Context context) {
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
                        NoteRef note = ((NoteAdapter) recyclerView.getAdapter()).getItemInPosition(position);
                        if (twoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable(NoteDetailFragment.ARG_NOTE, note);
                            NoteDetailFragment fragment = new NoteDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(activity, NoteDetailActivity.class);
                            intent.putExtra(NoteDetailFragment.ARG_NOTE, note);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemLongClick(RecyclerView parent, View clickedView, int position) {
                    }

                }));
    }

    @Override
    public void onRefresh() {
        loadNotes();
    }

    @Override
    public void onLoginFinished(boolean successful) {
        if(successful){
            loadNotes();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.changeOrder) {
            if(noteOrder == NoteSortOrder.UPDATED.getValue()) {
                noteOrder = NoteSortOrder.TITLE.getValue();
                item.setIcon(R.drawable.ic_action_sort_date);
            }else{
                noteOrder = NoteSortOrder.UPDATED.getValue();
                item.setIcon(R.drawable.ic_action_sort_alphabetically);
            }
            //We reload the whole list looking for changes done, for example an updated note which
            //update time would be modified.
            loadNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNotes(){
//        new FindNotesTask(0, MAX_NOTES, null, recyclerView, swipeRefreshLayout, noteOrder).execute();

            setRefreshingEnabled();

        Intent i = new Intent(this, FindNotesService.class);
        i.putExtra(MAX_NOTES_TAG, MAX_NOTES);
        i.putExtra(ORDER_TAG, noteOrder);
        i.putExtra(RECEIVER_TAG, mReceiver);
        startService(i);
    }

    public void createNewNote(final String title, String content) {
        new CreateNewNoteTask(title, content, new CreateNewNoteTask.OnCompleteListener() {
            @Override
            public void onComplete(int result) {
                switch (result){
                    case 0:{
                        Snackbar.make(swipeRefreshLayout, "Note \"" + title + "\" created", Snackbar.LENGTH_LONG).show();
                        //We get whole notes again to have an updated "online" version of the notes.
                        loadNotes();
                        break;
                    }
                    case 1:{
                        Snackbar.make(swipeRefreshLayout, "Error creating your note", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        }).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RECEIVER_TAG, mReceiver);
        outState.putParcelableArrayList(NOTES_TAG, notes);
        outState.putBoolean(REFRESHING_TAG, swipeRefreshLayout.isRefreshing());
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(MyApplication.LOG_TAG, "onReceiveResult");
        ArrayList<NoteRef> noteRefs = resultData.getParcelableArrayList(NOTES_TAG);
        if(noteRefs.size()>0){
            notes = noteRefs;
            ((NoteAdapter)recyclerView.getAdapter()).updateNoteItems(noteRefs);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    //We need to use this method because setRefreshingEnabled is only launched after
    //onMeasue is completed, but we need before.
    private void setRefreshingEnabled(){
        if(swipeRefreshLayout!=null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }
}
