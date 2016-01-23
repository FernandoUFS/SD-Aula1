package com.cumbetech.sd_aula1.Objects;

import android.util.Log;

import java.net.InetAddress;

/**
 * Created by Fernando on 23/01/2016.
 */
public class Mensagem {
    public static final int SENDED = 1;
    public static final int RECEIVED = 2;

    public int tipo;
    public String msg;
    public String nome;
    InetAddress addr;

    public Mensagem(int tipo, String nome, String msg, InetAddress addr) {
        this.tipo = tipo;
        this.nome = nome;
        this.msg = msg;
        this.addr = addr;
    }
}
