package com.sbc.bqevernote.task;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Notebook;
import com.sbc.bqevernote.NoteAdapter;

import java.util.ArrayList;


public class FindNotesTask extends AsyncTask<Void,Integer,ArrayList<NoteRef>> {

    private final EvernoteSearchHelper.Search mSearch;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public FindNotesTask(int offset, int maxNotes, Notebook notebook, RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout, int order) {
        this.recyclerView = recyclerView;
        this.swipeRefreshLayout = swipeRefreshLayout;

        NoteFilter noteFilter = new NoteFilter();
        noteFilter.setOrder(order);

        if (notebook != null) {
            noteFilter.setNotebookGuid(notebook.getGuid());
        }

        mSearch = new EvernoteSearchHelper.Search()
                .setOffset(offset)
                .setMaxNotes(maxNotes)
                .setNoteFilter(noteFilter);

        mSearch.addScope(EvernoteSearchHelper.Scope.PERSONAL_NOTES);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected ArrayList<NoteRef> doInBackground(Void... params) {
        ArrayList<NoteRef> list = new ArrayList<>();
        try {
            EvernoteSearchHelper.Result searchResult = EvernoteSession.getInstance()
                    .getEvernoteClientFactory()
                    .getEvernoteSearchHelper()
                    .execute(mSearch);
            list = (ArrayList<NoteRef>) searchResult.getAllAsNoteRef();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<NoteRef> noteRefs) {
        super.onPostExecute(noteRefs);
        if(noteRefs.size()>0){
            ((NoteAdapter)recyclerView.getAdapter()).updateNoteItems(noteRefs);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
