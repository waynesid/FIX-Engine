package com.Athena.FIX.Engine.message;

import com.paritytrading.philadelphia.FIXConnection;
import com.paritytrading.philadelphia.FIXConnectionStatusListener;
import com.paritytrading.philadelphia.FIXMessage;
import com.paritytrading.philadelphia.FIXMessageListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wayne Sidney
 * Created on {09/07/2022}
 */
public class Messages implements FIXMessageListener, FIXConnectionStatusListener {

    private final List<Message> messages;

    public Messages() {
        messages = new ArrayList<>();
    }

    synchronized List<Message> collect(){
        return new ArrayList<>(messages);
    }



    @Override
    public void close(FIXConnection fixConnection, String s) throws IOException {

    }

    @Override
    public void sequenceReset(FIXConnection fixConnection) throws IOException {

    }

    @Override
    public void tooLowMsgSeqNum(FIXConnection fixConnection, long l, long l1) throws IOException {

    }

    @Override
    public void heartbeatTimeout(FIXConnection fixConnection) throws IOException {

    }

    @Override
    public void reject(FIXConnection fixConnection, FIXMessage fixMessage) throws IOException {

        add(fixMessage);
    }

    @Override
    public void logon(FIXConnection fixConnection, FIXMessage fixMessage) throws IOException {
        add(fixMessage);
    }

    @Override
    public void logout(FIXConnection fixConnection, FIXMessage fixMessage) throws IOException {
        add(fixMessage);
    }

    @Override
    public void message(FIXMessage fixMessage) throws IOException {
        add(fixMessage);

    }

    private void add(FIXMessage message){
        add(Message.get(message));
    }

    private synchronized void add(Message message){
        messages.add(message);
    }
}
