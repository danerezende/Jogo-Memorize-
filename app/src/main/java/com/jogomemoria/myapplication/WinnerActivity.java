package com.jogomemoria.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


public class WinnerActivity extends AppCompatActivity {

    private TextView timeTextView, scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        // Recupera os dados da Intent
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        int score = intent.getIntExtra("score", 0);

        // Referências para os TextViews
        //timeTextView = findViewById(R.id.timeTextView);
        scoreTextView = findViewById(R.id.scoreTextView);

        // Atualiza os TextViews com tempo e pontuação
        //timeTextView.setText("Tempo: " + time);
        scoreTextView.setText("Placar: " + score + " Pontos");
    }

    // Método para iniciar um novo jogo
    public void startNewGame(View view) {
        Intent intent = new Intent(WinnerActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Fecha a activity para que não seja possível voltar para ela
    }
}
