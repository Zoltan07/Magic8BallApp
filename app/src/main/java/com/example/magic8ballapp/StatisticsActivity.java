package com.example.magic8ballapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private PredictionDatabase db;
    private TextView statsTextView;

    private final String[] positiveKeywords = {
            "igen", "biztos", "valószínű", "határozottan", "jó kilátás", "bízhatsz"
    };
    private final String[] neutralKeywords = {
            "homályos", "később", "koncentrálj", "nem lehet megjósolni"
    };
    private final String[] negativeKeywords = {
            "nem", "kétséges", "ne számíts", "rossz", "kilátások nem jók"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statsTextView = findViewById(R.id.statsTextView);
        db = PredictionDatabase.getDatabase(this);

        new Thread(() -> {
            List<PredictionItem> items = db.PredictionDao().getAllNow();
            int pos = 0, neu = 0, neg = 0;

            for (PredictionItem item : items) {
                String answer = item.answer.toLowerCase();
                if (containsKeyword(answer, positiveKeywords)) pos++;
                else if (containsKeyword(answer, neutralKeywords)) neu++;
                else if (containsKeyword(answer, negativeKeywords)) neg++;
            }

            final String result = "✅ Igenlő: " + pos +
                    "\n➖ Semleges: " + neu +
                    "\n❌ Tagadó: " + neg;

            runOnUiThread(() -> statsTextView.setText(result));
        }).start();
    }

    private boolean containsKeyword(String text, String[] keywords) {
        for (String word : keywords) {
            if (text.contains(word)) return true;
        }
        return false;
    }
}