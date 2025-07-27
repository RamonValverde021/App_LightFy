package com.lightfy.model.json_configuration;

import com.google.gson.annotations.SerializedName;

public class JsonConfigSend {
    @SerializedName("Id")
    private String id = "LightFy";
    @SerializedName("Designação")
    private String designacao = "Gravação";
    @SerializedName("Dados")
    private Dados dados;

    public JsonConfigSend(String id, String designacao, String categoria, String ssid, String password, String ip, String subnet, String gateway, String dns, String port) {
        this.id = id;
        this.designacao = designacao;
        this.dados = new Dados(categoria, ssid, password, ip, subnet, gateway, dns, port);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignacao() {
        return designacao;
    }

    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public Dados getDados() {
        return dados;
    }

    public void setDados(Dados dados) {
        this.dados = dados;
    }

    public static class Dados {
        @SerializedName("Categoria")
        private String categoria = "Wi-Fi";
        @SerializedName("Comando")
        private Comando comando;

        public Dados(String categoria, String ssid, String password, String ip, String subnet, String gateway, String dns, String port) {
            this.categoria = categoria;
            this.comando = new Comando(ssid, password, ip, subnet, gateway, dns, port);
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public Comando getComando() {
            return comando;
        }

        public void setComando(Comando comando) {
            this.comando = comando;
        }

        public static class Comando {
            private String ssid = "admin";
            private String password = "admin";
            private String ip = "192.168.0.120";
            private String subnet = "255.255.255.0";
            private String gateway = "192.168.0.1";
            private String dns = "8.8.8.8";
            private String port = "8266";

            public Comando(String ssid, String password, String ip, String subnet, String gateway, String dns, String port) {
                this.ssid = ssid;
                this.password = password;
                this.ip = ip;
                this.subnet = subnet;
                this.gateway = gateway;
                this.dns = dns;
                this.port = port;
            }

            public String getSsid() {
                return ssid;
            }

            public void setSsid(String ssid) {
                this.ssid = ssid;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public String getSubnet() {
                return subnet;
            }

            public void setSubnet(String subnet) {
                this.subnet = subnet;
            }

            public String getGateway() {
                return gateway;
            }

            public void setGateway(String gateway) {
                this.gateway = gateway;
            }

            public String getDns() {
                return dns;
            }

            public void setDns(String dns) {
                this.dns = dns;
            }

            public String getPort() {
                return port;
            }

            public void setPort(String port) {
                this.port = port;
            }
        }
    }
}
