package com.example.magic8ballapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private com.example.magic8ballapp.PredictionDatabase db;
    private RecyclerView recyclerView;
    private com.example.magic8ballapp.PredictionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = PredictionDatabase.getDatabase(this);
        adapter = new PredictionAdapter();
        recyclerView.setAdapter(adapter);

        db.predictionDao().getAll().observe(this, new Observer<List<com.example.magic8ballapp.PredictionItem>>() {
            @Override
            public void onChanged(List<PredictionItem> items) {
                adapter.setItems(items);
            }
        });
    }
}