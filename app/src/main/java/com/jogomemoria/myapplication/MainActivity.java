package com.jogomemoria.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private int[][] palavrasSyllables = {
            {R.drawable.vaca1, R.drawable.vaca2},
            {R.drawable.bolo1, R.drawable.bolo2},
            {R.drawable.bola1, R.drawable.bola2},
            {R.drawable.pipa1, R.drawable.pipa2},
            {R.drawable.fada1, R.drawable.fada2},
            {R.drawable.rato1, R.drawable.rato2},
            {R.drawable.uva1, R.drawable.uva2},
            {R.drawable.sapo1, R.drawable.sapo2},
            {R.drawable.fogo1, R.drawable.fogo2},
            {R.drawable.gato1, R.drawable.gato2},
            {R.drawable.vela1, R.drawable.vela2},
            {R.drawable.pato1, R.drawable.pato2}
    };

    private int cardBack;
    private ImageView[] buttons;
    private int[] shuffledImages;

    private ImageView firstCard, secondCard;
    private int firstCardIndex, secondCardIndex;
    private boolean isSecondCardFlipped = false;
    private boolean isBusy = false;

    private TextView scoreTextView, timerTextView;
    private int score = 0;
    private int maxScore;
    private int pairsFound = 0;  // Variável para contar os pares encontrados

    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (elapsedMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nivel = getIntent().getStringExtra("nivel");
        if (nivel != null) {
            switch (nivel) {
                case "facil":
                    cardBack = R.drawable.estrela;
                    break;
                case "medio":
                    cardBack = R.drawable.aventureiro;
                    break;
                case "dificil":
                    cardBack = R.drawable.superheroi;
                    break;
            }
        }

        int rows = getIntent().getIntExtra("rows", 4);
        int cols = getIntent().getIntExtra("cols", 4);
        maxScore = (rows * cols) / 2;

        gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(cols);

        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);

        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        setupGame(rows, cols);
    }

    private void setupGame(int rows, int cols) {
        int totalImages = rows * cols;
        shuffledImages = new int[totalImages];

        List<Integer> syllableList = new ArrayList<>();
        for (int i = 0; i < totalImages / 2; i++) {
            syllableList.add(palavrasSyllables[i][0]);
            syllableList.add(palavrasSyllables[i][1]);
        }
        Collections.shuffle(syllableList);
        for (int i = 0; i < totalImages; i++) {
            shuffledImages[i] = syllableList.get(i);
        }

        Map<Integer, Integer> syllablePairs = new HashMap<>();
        Map<Integer, Boolean> isFirstSyllable = new HashMap<>();
        for (int[] par : palavrasSyllables) {
            syllablePairs.put(par[0], par[1]);
            isFirstSyllable.put(par[0], true);
            isFirstSyllable.put(par[1], false);
        }

        buttons = new ImageView[totalImages];
        for (int i = 0; i < totalImages; i++) {
            buttons[i] = new ImageView(this);
            buttons[i].setTag(i);
            buttons[i].setImageResource(cardBack);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);

            buttons[i].setLayoutParams(params);

            buttons[i].setOnClickListener(v -> {
                if (!isBusy) {
                    handleCardClick((ImageView) v, syllablePairs, isFirstSyllable);
                }
            });

            gridLayout.addView(buttons[i]);
        }
    }

    private void handleCardClick(ImageView card, Map<Integer, Integer> syllablePairs, Map<Integer, Boolean> isFirstSyllable) {
        int tag = (int) card.getTag();

        if (card.getDrawable() != null && card.getDrawable().getConstantState() == getResources().getDrawable(cardBack).getConstantState()) {
            card.setImageResource(shuffledImages[tag]);

            if (!isSecondCardFlipped) {
                firstCard = card;
                firstCardIndex = tag;
                isSecondCardFlipped = true;
            } else {
                secondCard = card;
                secondCardIndex = tag;
                isBusy = true;

                boolean isValidPair = syllablePairs.containsKey(shuffledImages[firstCardIndex])
                        && syllablePairs.get(shuffledImages[firstCardIndex]) == shuffledImages[secondCardIndex]
                        && isFirstSyllable.get(shuffledImages[firstCardIndex]);

                if (isValidPair) {
                    updateScore();
                    firstCard.setClickable(false);
                    secondCard.setClickable(false);
                    pairsFound++;  // Incrementa o número de pares encontrados

                    // Identificar a palavra do par encontrado
                    String palavra = getPalavraCorrespondente(shuffledImages[firstCardIndex]);

                    // Tocar o som correspondente à palavra
                    playSound(palavra);

                    // Verifica se todos os pares foram encontrados
                    if (pairsFound == maxScore) {
                        checkForWin();
                    }

                    isSecondCardFlipped = false;
                    isBusy = false;
                } else {
                    new Handler().postDelayed(() -> {
                        firstCard.setImageResource(cardBack);
                        secondCard.setImageResource(cardBack);
                        isSecondCardFlipped = false;
                        isBusy = false;
                    }, 1000);
                }
            }
        }
    }

    private void updateScore() {
        long elapsedMillis = SystemClock.uptimeMillis() - startTime;
        int seconds = (int) (elapsedMillis / 1000);

        if (seconds < 30) {
            score += 10;
        } else if (seconds < 60) {
            score += 5;
        } else {
            score += 2;
        }

        scoreTextView.setText("Score: " + score);
    }

    private void checkForWin() {
        timerHandler.removeCallbacks(timerRunnable);
        Intent intent = new Intent(MainActivity.this, WinnerActivity.class);
        intent.putExtra("time", timerTextView.getText().toString());
        intent.putExtra("score", score);
        startActivity(intent);
        finish();
    }

    private void playSound(String palavra) {
        // Cria o nome do arquivo de áudio com base na palavra
        String soundFile = palavra.toLowerCase(); // ".mp3";  // Garante que o nome do arquivo seja minúsculo
        // Recupera o ID do recurso de áudio a partir do nome
        int soundResId = getResources().getIdentifier(soundFile, "raw", getPackageName());

        if (soundResId != 0) {  // Verifica se o arquivo existe
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResId);
            mediaPlayer.start();

            // Libera o MediaPlayer após a reprodução
            mediaPlayer.setOnCompletionListener(mp -> mp.release());
        } else {
            // Caso o arquivo de som não seja encontrado
            System.out.println("Áudio não encontrado para a palavra: " + palavra);
        }
    }



    private String getPalavraCorrespondente(int imageResource) {
        // Mapeamento das imagens para palavras
        if (imageResource == R.drawable.vaca1 || imageResource == R.drawable.vaca2) {
            return "vaca";
        } else if (imageResource == R.drawable.bolo1 || imageResource == R.drawable.bolo2) {
            return "bolo";
        } else if (imageResource == R.drawable.bola1 || imageResource == R.drawable.bola2) {
            return "bola";
        } else if (imageResource == R.drawable.pipa1 || imageResource == R.drawable.pipa2) {
            return "pipa";
        } else if (imageResource == R.drawable.fada1 || imageResource == R.drawable.fada2) {
            return "fada";
        } else if (imageResource == R.drawable.rato1 || imageResource == R.drawable.rato2) {
            return "rato";
        } else if (imageResource == R.drawable.uva1 || imageResource == R.drawable.uva2) {
            return "uva";
        } else if (imageResource == R.drawable.sapo1 || imageResource == R.drawable.sapo2) {
            return "sapo";
        } else if (imageResource == R.drawable.fogo1 || imageResource == R.drawable.fogo2) {
            return "fogo";
        } else if (imageResource == R.drawable.gato1 || imageResource == R.drawable.gato2) {
            return "gato";
        } else if (imageResource == R.drawable.vela1 || imageResource == R.drawable.vela2) {
            return "vela";
        } else if (imageResource == R.drawable.pato1 || imageResource == R.drawable.pato2) {
            return "pato";
        }
        return "";
    }
}
