package com.lightfy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.lightfy.model.json_main.JsonMainSend;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    Intent configScreen, linkedin;
    private URI uri;
    private WebSocket socket;
    TextView copyright;
    ImageView btnLamp, btnScreenConfig;
    MediaPlayer mediaPlayer;
    TinyDB tinyDB;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        configScreen = new Intent(getApplicationContext(), ConfigurationWiFi.class);
        linkedin = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/ramon-valverde/"));
        copyright = findViewById(R.id.lblCopyright);
        mediaPlayer = MediaPlayer.create(this, R.raw.interruptor3); // Cria o MediaPlayer com o som do clique
        btnLamp = findViewById(R.id.btnLamp);
        btnScreenConfig = findViewById((R.id.btnScreenConfig));

        // Funções para esmaecer objetos quando clicados
        fadeEffect(btnScreenConfig, null);
        fadeEffect(btnLamp, mediaPlayer);
        fadeEffect(copyright, null);

        // Inicializa primeira tentatica de conectar com o dispositivo
        tinyDB = new TinyDB(this);
        String ip = tinyDB.get("ip", "192.168.0.120"); // Recupera os dados de ip, se não tiver, retorna 192.168.0.120
        int port = tinyDB.getInt("port", 8266); // Recupera os dados da porta, se não tiver, retorna 8266

        try {
            Log.d("debugWebSocket", "Conectando com: ws://" + ip + ":" + port + "/");
            uri = new URI("ws://" + ip + ":" + port + "/"); // Certifique-se que é a porta certa!
            socket = new WebSocket(uri, this);  // passando a Activity agora usando a variável global
            socket.connect();
            if (socket == null || !socket.isOpen()) {
                assert socket != null;
                socket.startReconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.msgErrorConnecting), Toast.LENGTH_SHORT).show();
            Log.d("debugWebSocket", "Falha ao conectar, MainAcitivity...");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (socket != null) {
            socket.stopReconnect(); // Evita reconectar em segundo plano
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //socket.startReconnect(); // Reinicia a reconexão ao voltar para a tela
    }

    public void screenConfig(View view) {
        startActivity(configScreen);
    }

    public void triggerLamp(View view) {
        Gson gson = new Gson();
        JsonMainSend action = new JsonMainSend("LightFy", "Acionamento");
        String json = gson.toJson(action);
        if (socket != null && socket.isOpen()) { // Verifica se o websocket esta conectado
            socket.send(json);
            Log.d("debugWebSocket", json);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.msgConnectionToDeviceIsNotActive), Toast.LENGTH_SHORT).show();
        }
    }

    public void myLinkedIn(View view) {
        startActivity(linkedin);
    }

    @SuppressLint("ClickableViewAccessibility")
    // Suprime o aviso do Android Lint sobre acessibilidade ao usar OnTouchListener
    public void fadeEffect(View view, @Nullable MediaPlayer mediaPlayer) { // Metodo genérico que aplica efeito de "fade" (transparência) ao pressionar e soltar uma View, podendo também tocar um som opcional com MediaPlayer
        view.setOnTouchListener((v, event) -> { // Define um ouvinte de toque (TouchListener) para a View
            switch (event.getAction()) {      // Verifica o tipo de evento de toque

                case MotionEvent.ACTION_DOWN: // Quando o usuário pressiona a View,
                    // Toca o som ao clicar em um botão
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) { // Se foi fornecido um MediaPlayer e ele não estiver tocando, inicia o som
                        mediaPlayer.start();
                    }
                    v.setAlpha(0.3f);         // ela fica semitransparente
                    return true;  // Indica que o evento foi tratado

                case MotionEvent.ACTION_UP: // Quando o usuário solta a View,
                    v.setAlpha(1.0f);       // ela volta a ficar totalmente opaca
                    v.performClick();       // Dispara o clique da View para fins de acessibilidade (ex: TalkBack)
                    return true;

                case MotionEvent.ACTION_CANCEL: // Se o toque for cancelado (ex: dedo desliza para fora da View),
                    v.setAlpha(1.0f);           // restaura opacidade
                    return true;
            }
            return false; // Retorna false se o evento não for tratado pelos casos acima
        });
    }
}