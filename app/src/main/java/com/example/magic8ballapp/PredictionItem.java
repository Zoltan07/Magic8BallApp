package com.example.magic8ballapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "predictions")
public class PredictionItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String question;
    public String answer;

    public PredictionItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}