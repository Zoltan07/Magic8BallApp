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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private EditText questionEditText;
    private TextView answerTextView;
    private Vibrator vibrator;
    private RadioGroup modeRadioGroup;
    private RadioButton shakeModeRadioButton, buttonModeRadioButton;
    private CheckBox includeTarotCheckBox;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private PredictionDatabase db;

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
        db = PredictionDatabase.getDatabase(this);

        findViewById(R.id.generateButton).setOnClickListener(v -> generateAnswer());

        findViewById(R.id.micButton).setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mondd a kérdésed...");
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        });

        findViewById(R.id.historyButton).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        findViewById(R.id.statisticsButton).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
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
        String url = "https://rws-cards-api.herokuapp.com/api/v1/cards/random?n=1";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject card = response
                                .getJSONArray("cards")
                                .getJSONObject(0);

                        String name = card.getString("name");
                        String meaning = card.getString("meaning_up");

                        String tarotResult = "\n\n🃏 " + name + "\n🔮 " + meaning;
                        answerTextView.setText(answerTextView.getText() + tarotResult);

                    } catch (Exception e) {
                        e.printStackTrace();
                        answerTextView.setText(answerTextView.getText() + "\n⚠️ Tarot értelmezési hiba.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    answerTextView.setText(answerTextView.getText() + "\n❌ Nem sikerült Tarot kártyát lekérni.");
                }
        );

        queue.add(jsonRequest);
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
