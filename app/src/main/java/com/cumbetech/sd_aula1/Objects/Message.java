package com.cumbetech.sd_aula1.Objects;

import java.net.InetAddress;

/**
 * Created by Fernando on 23/01/2016.
 */
public class Message {
    public static final int SENDED = 1;
    public static final int RECEIVED = 2;

    public int type;
    public String message;
    public String name;
    public InetAddress addr;

    public Message(int type, String name, String message, InetAddress addr) {
        this.type = type;
        this.name = name;
        this.message = message;
        this.addr = addr;
    }
}
