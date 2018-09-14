package com.example.hp.chatroom;

public class Message {

    private static  final  int TYPE_RECIVED = 0;
    private static  final  int TYPE_SENT = 1;
    private String message;
    private int type;

    public Message(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
