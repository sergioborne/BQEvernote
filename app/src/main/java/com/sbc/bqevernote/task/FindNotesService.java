package com.sbc.bqevernote.task;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.NoteSortOrder;
import com.sbc.bqevernote.NoteListActivity;

import java.util.ArrayList;

/**
 * Created by Sergio on 29/01/2016.
 */
public class FindNotesService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FindNotesService(String name) {
        super(name);
    }

    public FindNotesService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver rec = intent.getParcelableExtra(NoteListActivity.RECEIVER_TAG);
        int order = intent.getIntExtra(NoteListActivity.ORDER_TAG, NoteSortOrder.UPDATED.getValue());
        int maxNotes = intent.getIntExtra(NoteListActivity.MAX_NOTES_TAG, 100);

        NoteFilter noteFilter = new NoteFilter();
        noteFilter.setOrder(order);

        //We need to modify this search object if we want for example paginate results ("play" with offset and maxNotes)
        EvernoteSearchHelper.Search mSearch = new EvernoteSearchHelper.Search()
                .setOffset(0)
                .setMaxNotes(maxNotes)
                .setNoteFilter(noteFilter);

        mSearch.addScope(EvernoteSearchHelper.Scope.PERSONAL_NOTES);


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

        //This lines are used to test device screen rotation chase, because request is as fast enough to
        //not give time to recreate.
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Bundle b= new Bundle();
        b.putParcelableArrayList(NoteListActivity.NOTES_TAG, list);
        rec.send(0, b);
    }
}
