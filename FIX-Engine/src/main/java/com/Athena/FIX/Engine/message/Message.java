package com.Athena.FIX.Engine.message;

import com.paritytrading.philadelphia.FIXMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Wayne Sidney
 * Created on {09/07/2022}
 */
public
class Message {

    private static final Set<Integer> UNPRINTED_TAGS = new HashSet<>();

    static {
        UNPRINTED_TAGS.add(49); // SenderCompID
        UNPRINTED_TAGS.add(56); // TargetCompID
        UNPRINTED_TAGS.add(34); // MsgSeqNum
        UNPRINTED_TAGS.add(52); // SendingTime
    }

    private static final int MsgType = 35;

    private final String msgType;

    private final List<Field> fields;

    private Message(String msgType, List<Field> fields) {
        this.msgType = msgType;
        this.fields  = fields;
    }

    public static Message get(String input) {
        List<Field> fields = new ArrayList<>();

        String msgType = null;

        for (String part : input.split("\\|")) {
            Field field = Field.get(part);

            if (field.getTag() == MsgType)
                msgType = field.getValue();
            else
                fields.add(field);
        }

        if (msgType == null)
            throw new IllegalArgumentException("MsgType(35) missing");

        return new Message(msgType, fields);
    }

    public static Message get(FIXMessage message) {
        List<Field> fields = new ArrayList<>();

        String msgType = null;

        for (int i = 0; i < message.getFieldCount(); i++) {
            int    tag   = message.tagAt(i);
            String value = message.valueAt(i).toString();

            if (tag == MsgType)
                msgType = value;
            else
                fields.add(new Field(tag, value));
        }

        if (msgType == null)
            throw new IllegalArgumentException("MsgType(35) missing");

        return new Message(msgType, fields);
    }

    public String getMsgType() {
        return msgType;
    }

    public void put(FIXMessage message) {
        for (Field field : fields)
            message.addField(field.getTag()).setString(field.getValue());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(MsgType);
        builder.append('=');
        builder.append(msgType);

        for (Field field : fields) {
            if (UNPRINTED_TAGS.contains(field.getTag()))
                continue;

            builder.append('|');
            builder.append(field);
        }

        return builder.toString();
    }

}