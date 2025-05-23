package com.example.magic8ballapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private EditText questionEditText;
    private TextView answerTextView;
    private Vibrator vibrator;
    private RadioGroup modeRadioGroup;
    private RadioButton shakeModeRadioButton, buttonModeRadioButton;
    private CheckBox includeTarotCheckBox;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private com.example.magic8ballapp.PredictionDatabase db;

    private final String[] positiveAnswers = {
            "Biztosan így van", "Határozottan így van", "Kétségtelenül",
            "Igen, határozottan", "Bízhatsz benne", "Ahogy látom, igen",
            "Valószínűleg", "Jó kilátások", "Igen", "Jelek szerint igen"
    };
    private final String[] neutralAnswers = {
            "Homályos válasz, próbáld újra", "Kérdezd később", "Jobb, ha most nem mondom el",
            "Most nem lehet megjósolni", "Koncentrálj és kérdezd újra"
    };
    private final String[] negativeAnswers = {
            "Ne számíts rá", "Válaszom: nem", "Forrásaim szerint nem",
            "Kilátások nem túl jók", "Nagyon kétséges"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionEditText = findViewById(R.id.questionEditText);
        answerTextView = findViewById(R.id.answerTextView);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        modeRadioGroup = findViewById(R.id.modeRadioGroup);
        shakeModeRadioButton = findViewById(R.id.shakeModeRadioButton);
        buttonModeRadioButton = findViewById(R.id.buttonModeRadioButton);
        includeTarotCheckBox = findViewById(R.id.includeTarotCheckBox);
        db = com.example.magic8ballapp.PredictionDatabase.getDatabase(this);

        findViewById(R.id.generateButton).setOnClickListener(v -> generateAnswer());

        findViewById(R.id.micButton).setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mondd a kérdésed...");
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        });

        findViewById(R.id.historyButton).setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.magic8ballapp.HistoryActivity.class));
        });

        findViewById(R.id.statisticsButton).setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.magic8ballapp.StatisticsActivity.class));
        });
    }

    private void generateAnswer() {
        String question = questionEditText.getText().toString().trim();
        String answer;

        int shakeStrength = new Random().nextInt(100);
        if (shakeModeRadioButton.isChecked()) {
            answer = (shakeStrength > 66) ?
                    getRandom(positiveAnswers) : (shakeStrength > 33) ?
                    getRandom(neutralAnswers) :
                    getRandom(negativeAnswers);
        } else {
            answer = getRandom(positiveAnswers);
        }

        answerTextView.setText(answer);
        vibrator.vibrate(500);
        MediaPlayer.create(this, R.raw.magic_sound).start();

        storeQuestionAndAnswer(question, answer);

        if (includeTarotCheckBox.isChecked()) fetchTarotCard();
    }

    private void fetchTarotCard() {
        // API hívás helye - lásd előző Tarot API válasz
    }

    private String getRandom(String[] arr) {
        return arr[new Random().nextInt(arr.length)];
    }

    private void storeQuestionAndAnswer(String question, String answer) {
        new Thread(() -> db.predictionDao().insert(new PredictionItem(question, answer))).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                questionEditText.setText(result.get(0));
            }
        }
    }
}
