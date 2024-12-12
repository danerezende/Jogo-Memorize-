package com.jogomemoria.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton easyButton = findViewById(R.id.imageButton3);
        ImageButton mediumButton = findViewById(R.id.imageButton4);
        ImageButton hardButton = findViewById(R.id.imageButton5);

        easyButton.setOnClickListener(v -> startGame(4, 3, "facil")); // 4x3
        mediumButton.setOnClickListener(v -> startGame(4, 4, "medio")); // 4x4
        hardButton.setOnClickListener(v -> startGame(5, 4, "dificil")); // 5x4
    }

    private void startGame(int rows, int cols, String nivel) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.putExtra("rows", rows);  // Número de linhas
        intent.putExtra("cols", cols);  // Número de colunas
        intent.putExtra("nivel", nivel); // Nível de dificuldade
        startActivity(intent);
    }
}

