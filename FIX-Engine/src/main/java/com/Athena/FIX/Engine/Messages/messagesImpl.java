package com.Athena.FIX.Engine.Messages;

import com.paritytrading.philadelphia.FIXConnection;
import com.paritytrading.philadelphia.FIXConnectionStatusListener;
import com.paritytrading.philadelphia.FIXMessage;
import com.paritytrading.philadelphia.FIXMessageListener;
import com.paritytrading.philadelphia.fix42.FIX42Enumerations;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

import java.io.IOException;
import java.util.Date;

import static com.paritytrading.philadelphia.fix42.FIX42MsgTypes.*;
import static com.paritytrading.philadelphia.fix42.FIX42Tags.*;
import static com.paritytrading.philadelphia.fix42.FIX42Tags.QuoteRequestType;

/**
 * @author Wayne Sidney
 * Created on {08/07/2022}
 */
public class messagesImpl implements messages, FIXMessageListener, FIXConnectionStatusListener {
    private final FIXConnection connection;

    private FIXMessage message;

    public messagesImpl(FIXConnection connection) {
        this.connection = connection;

    }

    @Override
    public void sendQuoteRequest() throws IOException {
        connection.prepare(message, QuoteRequest);

        message.addField(QuoteReqID).setString("1");
        message.addField(ClOrdID).setString("123");
        message.addField(NoRelatedSym).setInt(2);
        message.addField(Symbol).setString("EUR/USD");
        message.addField(SecurityType).setString(FIX42Enumerations.SecurityTypeValues.ForeignExchangeContract);
        message.addField(Symbol).setString("EUR/CHF");
        message.addField(SecurityType).setString(FIX42Enumerations.SecurityTypeValues.ForeignExchangeContract);
        message.addField(QuoteRequestType).setInt(FIX42Enumerations.QuoteRequestTypeValues.Automatic);

        connection.send(message);

    }

    @Override
    public void sendNewSingleOrder() throws IOException {
        connection.prepare(message, OrderSingle);

        message.addField(OrderSingle).setString("1");
        message.addField(ClOrdID).setString("123");
        message.addField(Symbol).setString("USD/EUR");
        message.addField(OrdType).setInt(100);
        message.addField(Text).setString("buy currency");
        message.addField(TransactTime).setString((CharSequence) new MutableDateTime(System.currentTimeMillis(), DateTimeZone.UTC));

        connection.send(message);

    }

    @Override
    public void sendOrderCancelRequest() throws IOException {
        connection.prepare(message, OrderCancelRequest);

        message.addField(OrderCancelRequest).setString("F");
        message.addField(OrigClOrdID).setString("123");
        message.addField(ClOrdID).setString("321");
        message.addField(Side).setString("BUY");
        message.addField(TransactTime).setString((CharSequence) new MutableDateTime(System.currentTimeMillis(), DateTimeZone.UTC));

        connection.send(message);

    }

    @Override
    public void getOrderStatusRequest() throws IOException {

        connection.prepare(message, OrderStatusRequest);

        message.addField(OrderStatusRequest).setString("H");
        message.addField(ClOrdID).setString("321");
        message.addField(Side).setString("BUY");
        message.addField(TransactTime).setString((CharSequence) new MutableDateTime(System.currentTimeMillis(), DateTimeZone.UTC));

        connection.send(message);



    }

    @Override
    public void message(FIXMessage fixMessage) throws IOException {

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

    }

    @Override
    public void logon(FIXConnection fixConnection, FIXMessage fixMessage) throws IOException {

    }

    @Override
    public void logout(FIXConnection fixConnection, FIXMessage fixMessage) throws IOException {

    }
}
