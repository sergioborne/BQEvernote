package com.sbc.bqevernote.tasks;

import android.os.AsyncTask;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;


public class CreateNewNoteTask extends AsyncTask<Void,Integer,Integer> {

    private String noteTitle;
    private String noteContent;
    private OnCompleteListener completeListener;

    public CreateNewNoteTask(String title, String content, OnCompleteListener listener) {
        this.noteTitle = title;
        this.noteContent = content;
        this.completeListener = listener;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(EvernoteUtil.NOTE_PREFIX + noteContent + EvernoteUtil.NOTE_SUFFIX);
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        try {
            noteStoreClient.createNote(note);
            //All goes ok
            return 0;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        //Some error occur
        return -1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(completeListener!=null){
            completeListener.onComplete(result);
        }

    }

    public interface OnCompleteListener {
        void onComplete(int result);
    }
}
