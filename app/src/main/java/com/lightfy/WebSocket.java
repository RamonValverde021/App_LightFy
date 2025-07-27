package com.lightfy;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocket extends WebSocketClient { // Essa é a classe personalizada que estende (herda) WebSocketClient, da biblioteca Java-WebSocket
    private final Activity activity; // Armazena a referência da Activity (tela atual) para podermos atualizar a interface gráfica
    private final Handler reconnectHandler = new Handler(Looper.getMainLooper());
    private boolean isReconnecting = false;

    // Construtor da classe MeuWebSocket
    public WebSocket(URI serverUri, Activity activity) { // Recebe o endereço do servidor WebSocket (URI) e a Activity onde será usado
        super(serverUri);                                   // Chama o construtor da classe pai (WebSocketClient) com o endereço do servidor
        this.activity = activity;                           // Armazena a Activity para usar depois dentro dos métodos da classe
    }

    // Metodo que é automaticamente chamado quando o WebSocket recebe uma mensagem do servidor (ESP8266)
    @Override
    public void onMessage(String message) {
        Log.d("debugWebSocket", "WebSocket onMessage() \n    Message: " + message); // Mostra no Logcat a mensagem recebida (útil para depuração)
        MessageWebSocket data = new MessageWebSocket(activity); // 'this' é a Activity,
        data.reciveMessage(message);  // Trata as mensagens que chega do socket
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("debugWebSocket", "WebSocket onOpen() \n    Server Handshake: " + handshakedata);
        send("{\"Id\":\"LightFy\",\"Designação\":\"Status\"}");  // Solicita Status assim que conecta, mas é desnessario
        if (activity instanceof MainActivity) {
            MainActivity main = (MainActivity) activity;
            main.runOnUiThread(() -> {
                ProgressBar iconeStatus = main.findViewById(R.id.iconeStatus); // Pega o obejto do progressBar
                iconeStatus.setVisibility(View.INVISIBLE);                     // Oculta o icone do progressBar
                ImageView lamp = main.findViewById(R.id.btnLamp);              // Pega o obejto do lampada
                lamp.setVisibility(View.VISIBLE);                              // Visualiza a lampada
            });
        } else if (activity instanceof ConfigurationWiFi) {
            ConfigurationWiFi main = (ConfigurationWiFi) activity;
            main.runOnUiThread(() -> {
                // Dentro do onCreate() ou onde for necessário:
                MaterialButton btnConnect = main.findViewById(R.id.btnConnect);
                // Altera a cor de fundo (background)
                btnConnect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(main, R.color.blue_system)));
                // Altera a cor do texto
                btnConnect.setTextColor(ContextCompat.getColor(main, R.color.WHITE));
            });
        }
        stopReconnect(); // conexão feita com sucesso, para tentativas
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.w("debugWebSocket", "WebSocket onClose() \n    Code: " + code + "\n    Reason: " + reason + "\n    Remote: " + remote);
        if (code != -1) {
            Log.v("debugWebSocket", "WebSocket onClose() \n    Chamando reconectarWebSocket()");
            startReconnect(); // reconecta automaticamente se foi uma desconexão válida
        }
        if (activity instanceof MainActivity) {
            MainActivity main = (MainActivity) activity;
            main.runOnUiThread(() -> {
                // Aqui você pode esconder botões, mostrar loader, etc.
            });
        } else if (activity instanceof ConfigurationWiFi) {
            ConfigurationWiFi main = (ConfigurationWiFi) activity;
            main.runOnUiThread(() -> {
                // Dentro do onCreate() ou onde for necessário:
                MaterialButton btnConnect = main.findViewById(R.id.btnConnect);
                // Altera a cor de fundo (background) do botão Conectar
                btnConnect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(main, R.color.WHITE)));
                // Altera a cor do texto do botão Conectar
                btnConnect.setTextColor(ContextCompat.getColor(main, R.color.darkGray));
            });
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.e("debugWebSocket", "WebSocket onError() \n    Exception: " + ex);
        if (activity instanceof MainActivity) {
            MainActivity main = (MainActivity) activity;
            main.runOnUiThread(() -> {
                // Faça alguma coisa em caso de Erro
            });
        }
    }

    // Metodos das reconexão automatica
    // Método para iniciar reconexão automática
    public void startReconnect() {
        if (!isReconnecting) {
            isReconnecting = true;
            reconnectHandler.post(reconnectRunnable);
        }
    }

    // Método para parar reconexão automática (por exemplo, ao sair da Activity)
    public void stopReconnect() {
        isReconnecting = false;
        reconnectHandler.removeCallbacks(reconnectRunnable);
    }

    private final Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isReconnecting) return;

            if (isOpen()) {
                Log.d("debugWebSocket", "WebSocket já conectado.");
            } else {
                Log.d("debugWebSocket", "Tentando reconectar WebSocket...");
                try {
                    reconnect(); // método da própria WebSocketClient
                } catch (Exception e) {
                    Log.e("debugWebSocket", "Erro ao tentar reconectar", e);
                }
            }
            // Agenda nova tentativa
            reconnectHandler.postDelayed(this, 3000);
        }
    };

}