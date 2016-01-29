package com.sbc.bqevernote.task;

import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.type.NoteRef;
import com.squareup.okhttp.Response;

import java.io.IOException;


public class GetNoteDetailsTask extends AsyncTask<Void,Integer,String> {

    private NoteRef mNoteRef;
    private TextView contentTextView;

    public GetNoteDetailsTask(NoteRef noteRef, TextView contentTextView) {
        mNoteRef = noteRef;
        this.contentTextView = contentTextView;
    }

    @Override
    protected String doInBackground(Void... params) {
        EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();
        EvernoteHtmlHelper htmlHelper = clientFactory.getHtmlHelperDefault();

        try {
            Response response = htmlHelper.downloadNote(mNoteRef.getGuid());
            if(response!=null) {
                return htmlHelper.parseBody(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String noteContent) {
        super.onPostExecute(noteContent);

        if(noteContent!=null){
            contentTextView.setText(Html.fromHtml(noteContent));
        }

    }
}
