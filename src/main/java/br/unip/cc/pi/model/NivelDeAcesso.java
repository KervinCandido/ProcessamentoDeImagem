package br.unip.cc.pi.model;

import com.google.gson.annotations.SerializedName;

public enum NivelDeAcesso {

    @SerializedName("1")
    PUBLICO(1, "Público"),
    @SerializedName("2")
    DIRETOR_DE_DIVISAO(2, "Diretor de Divisão"),
    @SerializedName("3")
    MINISTRO_MEIO_AMBIENTE(3, "Ministro do Meio Ambiente");

    private final int id;
    private final String descricao;

    NivelDeAcesso(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return id + " - " + descricao;
    }
}
