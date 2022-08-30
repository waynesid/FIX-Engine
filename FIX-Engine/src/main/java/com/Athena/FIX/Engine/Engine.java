package com.Athena.FIX.Engine;

import com.paritytrading.philadelphia.*;
import com.paritytrading.philadelphia.coinbase.Coinbase;
import com.paritytrading.philadelphia.fix42.FIX42Enumerations;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Locale;

import static com.paritytrading.philadelphia.coinbase.CoinbaseTags.Password;
import static com.paritytrading.philadelphia.fix42.FIX42MsgTypes.Logon;
import static com.paritytrading.philadelphia.fix42.FIX42Tags.EncryptMethod;
import static com.paritytrading.philadelphia.fix42.FIX42Tags.HeartBtInt;

/**
 * @author Wayne Sidney
 * Created on {09/07/2022}
 */
public class Engine {


    private static boolean receive;

    public static void main(String[] args) {

        try {
            main();
        } catch (ConfigException | FileNotFoundException e) {
            error(e);
        } catch (IOException e) {
            fatal(e);
        }
    }

    public static void getMarketData() throws IOException {
        Config config = ConfigFactory.parseResources("initiator.conf");
        String address = config.getString("coinbase.fix.address");
        int port = config.getInt("coinbase.fix.port");

        String passphrase = config.getString("coinbase.api.passphrase");
        String key        = config.getString("coinbase.api.key");
        String secret     = config.getString("coinbase.api.secret");

        SocketChannel channel = SocketChannel.open();

        channel.connect(new InetSocketAddress(address, port));

        var builder = new FIXConfig.Builder()
                .setVersion(FIXVersion.FIX_4_2)
                .setSenderCompID(key)
                .setTargetCompID("Coinbase")
                .setHeartBtInt(30);

        var listener = new FIXMessageListener() {

            @Override
            public void message(FIXMessage message) {
                printf("Message: %s\n", message.getMsgType().asString());
            }

        };

        var statusListener = new FIXConnectionStatusListener() {

            @Override
            public void close(FIXConnection connection, String message) {
                printf("Close: %s\n", message);
            }

            @Override
            public void sequenceReset(FIXConnection connection) {
                printf("Received Sequence Reset\n");
            }

            @Override
            public void tooLowMsgSeqNum(FIXConnection connection, long receivedMsgSeqNum,
                                        long expectedMsgSeqNum) {
                printf("Received too low MsgSeqNum: received %s, expected %s\n",
                        receivedMsgSeqNum, expectedMsgSeqNum);
            }

            @Override
            public void heartbeatTimeout(FIXConnection connection) {
                printf("Heartbeat timeout\n");
            }

            @Override
            public void reject(FIXConnection connection, FIXMessage message) {
                printf("Received Reject\n");
            }

            @Override
            public void logon(FIXConnection connection, FIXMessage message) throws IOException {
                printf("Received Logon\n");

                connection.sendLogout();

                printf("Sent Logout\n");
            }

            @Override
            public void logout(FIXConnection connection, FIXMessage message) {
                printf("Received Logout\n");

                receive = false;
            }

        };

        FIXConnection connection = new FIXConnection(channel, builder.build(), listener, statusListener);

        FIXMessage message = connection.create();

        connection.prepare(message, Logon);

        message.addField(EncryptMethod).setInt(FIX42Enumerations.EncryptMethodValues.None);
        message.addField(HeartBtInt).setInt(30);
        message.addField(Password).setString(passphrase);

        Coinbase.sign(message, secret);

        connection.send(message);

        printf("Sent Logon\n");

        receive = true;

        while (receive) {
            if (connection.receive() < 0)
                break;
        }

        connection.close();

    }


    public static void main() throws IOException {
        Config config = ConfigFactory.parseResources("initiator.conf");
        String address = config.getString("coinbase.fix.address");
        int port    = config.getInt("coinbase.fix.port");

        String passphrase = config.getString("coinbase.api.passphrase");
        String key        = config.getString("coinbase.api.key");
        String secret     = config.getString("coinbase.api.secret");

        SocketChannel channel = SocketChannel.open();

        channel.connect(new InetSocketAddress(address, port));

        var builder = new FIXConfig.Builder()
                .setVersion(FIXVersion.FIX_4_2)
                .setSenderCompID(key)
                .setTargetCompID("Coinbase")
                .setHeartBtInt(30);

        var listener = new FIXMessageListener() {

            @Override
            public void message(FIXMessage message) {
                printf("Message: %s\n", message.getMsgType().asString());
            }

        };

        var statusListener = new FIXConnectionStatusListener() {

            @Override
            public void close(FIXConnection connection, String message) {
                printf("Close: %s\n", message);
            }

            @Override
            public void sequenceReset(FIXConnection connection) {
                printf("Received Sequence Reset\n");
            }

            @Override
            public void tooLowMsgSeqNum(FIXConnection connection, long receivedMsgSeqNum,
                                        long expectedMsgSeqNum) {
                printf("Received too low MsgSeqNum: received %s, expected %s\n",
                        receivedMsgSeqNum, expectedMsgSeqNum);
            }

            @Override
            public void heartbeatTimeout(FIXConnection connection) {
                printf("Heartbeat timeout\n");
            }

            @Override
            public void reject(FIXConnection connection, FIXMessage message) {
                printf("Received Reject\n");
            }

            @Override
            public void logon(FIXConnection connection, FIXMessage message) throws IOException {
                printf("Received Logon\n");

                connection.sendLogon(true);

                printf("Sent Logout\n");
            }

            @Override
            public void logout(FIXConnection connection, FIXMessage message) {
                printf("Received Logout\n");

                receive = false;
            }

        };

        FIXConnection connection = new FIXConnection(channel, builder.build(), listener, statusListener);

        FIXMessage message = connection.create();

        connection.prepare(message, Logon);

        message.addField(EncryptMethod).setInt(FIX42Enumerations.EncryptMethodValues.None);
        message.addField(HeartBtInt).setInt(30);
        message.addField(Password).setString(passphrase);

        Coinbase.sign(message, secret);

        connection.send(message);

        printf("Sent Logon\n");

        receive = true;

        while (receive) {
            if (connection.receive() < 0)
                break;
        }

        connection.close();
    }

    private static void error(Throwable throwable) {
        System.err.println("error: " + throwable.getMessage());
        System.exit(1);
    }

    private static void fatal(Throwable throwable) {
        System.err.println("fatal: " + throwable.getMessage());
        System.err.println();
        throwable.printStackTrace(System.err);
        System.err.println();
        System.exit(1);
    }

    private static void printf(String format, Object... args) {
        System.out.printf(Locale.US, format, args);
    }
}
