package com.example.gruc;

public class User {
    String nome;
    String cpf;
    byte[] foto;
    String dataAvsec;
    String dataCnv;
    String dataCred;

    // Construtor
    public User(String nome, String cpf, byte[] foto, String dataAvsec, String dataCnv, String dataCred) {
        this.nome = nome;
        this.cpf = cpf;
        this.foto = foto;
        this.dataAvsec = dataAvsec;
        this.dataCnv = dataCnv;
        this.dataCred = dataCred;
    }

    // Getters (necessários para ler os dados)
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public byte[] getFoto() { return foto; }
    public String getDataAvsec() { return dataAvsec; }
    public String getDataCnv() { return dataCnv; }
    public String getDataCred() { return dataCred; }
}