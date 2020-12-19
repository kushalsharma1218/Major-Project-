package com.example.expressblog.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expressblog.dao.DraftDao;
import com.example.expressblog.entities.Draft;

@Database(entities = Draft.class, version = 2, exportSchema = false)
public abstract class DraftDatabase extends RoomDatabase {

    private static DraftDatabase draftDatabase;

    public static synchronized DraftDatabase getDatabase(Context context){
        if(draftDatabase == null)
        {
            draftDatabase = Room.databaseBuilder(
                    context,
                    DraftDatabase.class,
                    "drafts_db"
            ).build();
        }
        return draftDatabase;

    }
    public abstract DraftDao draftDao();
}
