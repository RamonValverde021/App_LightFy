package com.lightfy;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lightfy.model.json_main.JsonMainReceive;

public class MessageWebSocket {
    private final Activity activity;

    public MessageWebSocket(Activity activity) {
        this.activity = activity;
    }

    public void reciveMessage(String mensagemJson) {
        if (activity instanceof MainActivity) {
            MainActivity main = (MainActivity) activity;
            main.runOnUiThread(() -> {
                try {
                    Gson gson = new Gson();
                    JsonMainReceive mensagem = gson.fromJson(mensagemJson, JsonMainReceive.class);

                    if (mensagem != null && mensagem.dados != null && mensagem.dados.sensor != null) {
                        String id = mensagem.
                                id;
                        String designacao = mensagem.designacao;
                        String sensor = mensagem.dados.sensor;
                        Log.d("DEBUG", "ID: " + id + ", Designação: " + designacao + ", Sensor: [" + sensor + "]");

                        ImageView lampada = activity.findViewById(R.id.btnLamp);
                        ConstraintLayout tela = activity.findViewById(R.id.main);
                        TextView titulo = activity.findViewById(R.id.lblTitle);
                        TextView copyright = activity.findViewById(R.id.lblCopyright);

                        if ("LightFy".equals(id) && "Status".equals(designacao)) {
                            if ("Ligada".equals(sensor.trim())) {
                                lampada.setImageResource(R.drawable.lampada_acesa);
                                tela.setBackgroundColor(ContextCompat.getColor(activity, R.color.WHITE));
                                titulo.setTextColor(ContextCompat.getColor(activity, R.color.blue_system));
                                copyright.setTextColor(ContextCompat.getColor(activity, R.color.darkGray));
                            } else {
                                lampada.setImageResource(R.drawable.lampada_apagada);
                                tela.setBackgroundColor(ContextCompat.getColor(activity, R.color.BLACK));
                                titulo.setTextColor(ContextCompat.getColor(activity, R.color.WHITE));
                                copyright.setTextColor(ContextCompat.getColor(activity, R.color.WHITE));
                            }
                        }

                        Log.d("GSON", "Sensor seguro: " + sensor);
                    } else {
                        Log.w("GSON", "Algum campo nulo no caminho até 'Sensor'");
                    }
                } catch (JsonSyntaxException e) {
                    Log.e("GSON", "Erro ao interpretar JSON: " + e.getMessage());
                }
            });
        }
    }
}
