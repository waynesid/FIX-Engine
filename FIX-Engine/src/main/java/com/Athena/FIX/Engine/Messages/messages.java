package com.Athena.FIX.Engine.Messages;

import java.io.IOException;

/**
 * @author Wayne Sidney
 * Created on {08/07/2022}
 */
public interface messages {
     void sendQuoteRequest() throws IOException;
     void sendNewSingleOrder() throws IOException;
     void sendOrderCancelRequest() throws IOException;
     void getOrderStatusRequest() throws IOException;

}
