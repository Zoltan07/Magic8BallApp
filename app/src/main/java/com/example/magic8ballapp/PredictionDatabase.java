package com.example.magic8ballapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PredictionItem.class}, version = 1, exportSchema = false)
public abstract class PredictionDatabase extends RoomDatabase {
    public abstract PredictionDao predictionDao();

    private static volatile PredictionDatabase INSTANCE;

    public static PredictionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PredictionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PredictionDatabase.class, "prediction_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public PredictionDao PredictionDao() {
        return null;
    }
}
