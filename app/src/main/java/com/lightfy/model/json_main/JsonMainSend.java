package com.lightfy.model.json_main;

import com.google.gson.annotations.SerializedName;

public class JsonMainSend {
    @SerializedName("Id")
    private String id = "LightFy";
    @SerializedName("Designação")
    private String designacao = "Acionamento";

    public JsonMainSend(String id, String designacao) {
        this.id = id;
        this.designacao = designacao;
    }
}
