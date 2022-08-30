package com.Athena.FIX.Engine.initiator;

import com.paritytrading.philadelphia.*;
import com.paritytrading.philadelphia.coinbase.Coinbase;
import com.paritytrading.philadelphia.fix42.FIX42Enumerations;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

import static com.paritytrading.philadelphia.fix42.FIX42MsgTypes.QuoteRequest;
import static com.paritytrading.philadelphia.fix42.FIX42Tags.*;

/**
 * @author Wayne Sidney
 * Created on {06/07/2022}
 */
public class Initiator implements FIXMessageListener, Closeable {

    private static final FIXConfig CONFIG = new FIXConfig.Builder()
            .setVersion(FIXVersion.FIX_4_2)
            .setHeartBtInt(30)
            .setCheckSumEnabled(true)
            .setSenderCompID("initiator")
            .setTargetCompID("coinbase")
            .build();

    private final FIXConnection connection;


    private long intervalNanos;

    private int receiveCount;

    private FIXMessage message;

    private String quoteReqId;

    private int counter;

    public Initiator(SocketChannel channel) {
        connection = new FIXConnection(channel, CONFIG, this, new FIXConnectionStatusListener() {

            @Override
            public void close(FIXConnection connection, String message) throws IOException {
                connection.close();
            }

            @Override
            public void sequenceReset(FIXConnection connection) {
            }

            @Override
            public void tooLowMsgSeqNum(FIXConnection connection, long receivedMsgSeqNum, long expectedMsgSeqNum) {
            }

            @Override
            public void heartbeatTimeout(FIXConnection connection) throws IOException {
                connection.close();
            }

            @Override
            public void reject(FIXConnection connection, FIXMessage message) throws IOException {
            }

            @Override
            public void logon(FIXConnection connection, FIXMessage message) throws IOException {

                sendQuoteRequest();

            }

            @Override
            public void logout(FIXConnection connection, FIXMessage message) throws IOException {
                connection.sendLogout();
            }

        });

    }

    public static Initiator open(SocketAddress address) throws IOException {
        SocketChannel channel = SocketChannel.open();

        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.connect(address);
        channel.configureBlocking(false);

        return new Initiator(channel);
    }


    @Override
    public void message(FIXMessage message) {
        FIXValue clOrdId = message.valueOf(ClOrdID);

        receiveCount++;
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    public FIXConnection getTransport() {
        return connection;
    }

    public void reset() {
        receiveCount = 0;
    }

    public void setIntervalNanos(long i) {
        intervalNanos = i;
    }

    public void sendAndReceive(FIXMessage message, FIXValue clOrdId, int orders) throws IOException {
        for (long sentAtNanoTime = System.nanoTime(); receiveCount < orders; connection.receive()) {
            if (System.nanoTime() >= sentAtNanoTime) {
                clOrdId.setInt(sentAtNanoTime);

                connection.update(message);
                connection.send(message);

                sentAtNanoTime += intervalNanos;
            }
        }
    }

    private void sendQuoteRequest() throws IOException {

        connection.prepare(message, QuoteRequest);

        message.addField(QuoteReqID).setString(quoteReqId);
        message.addField(ClOrdID).setString(quoteReqId + counter);
        message.addField(NoRelatedSym).setInt(2);
        message.addField(Symbol).setString("EUR/USD");
        message.addField(SecurityType).setString(FIX42Enumerations.SecurityTypeValues.ForeignExchangeContract);
        message.addField(Symbol).setString("EUR/CHF");
        message.addField(SecurityType).setString(FIX42Enumerations.SecurityTypeValues.ForeignExchangeContract);
        message.addField(QuoteRequestType).setInt(FIX42Enumerations.QuoteRequestTypeValues.Automatic);


        connection.send(message);
    }

}



