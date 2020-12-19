package com.example.expressblog.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.expressblog.R;
import com.example.expressblog.database.DraftDatabase;
import com.example.expressblog.entities.Draft;

import java.util.List;

public class Drafts extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        getDrafts();
    }

    private void getDrafts(){

        @SuppressLint("StaticFieldLeak")
        class GetDraftsTask extends AsyncTask<Void, Void, List<Draft>>{

            @Override
            protected List<Draft> doInBackground(Void... voids) {
                return DraftDatabase
                        .getDatabase(getApplicationContext())
                        .draftDao().getAllDraft();
            }

            @Override
            protected void onPostExecute(List<Draft> drafts) {
                super.onPostExecute(drafts);
                Log.d("my draft1111111111" , drafts.toString());
            }
        }

        new GetDraftsTask().execute();
    }

}