package org.alexburchak.trice.ws;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alexburchak
 */
public enum IncomingMessageType {
    /**
     * Handshake like message type
     */
    TRICE("^TRICE\\s+'(\\w+)'$") {
        protected void visit(IncomingMessageTypeVisitor visitor, String payload) throws IOException {
            visitor.visitTrice(payload);
        }
    },
    /**
     * Say bye
     */
    ECIRT("^ECIRT$") {
        protected void visit(IncomingMessageTypeVisitor visitor, String payload) throws IOException {
            visitor.visitEcirt(payload);
        }
    },
    /**
     * Something else
     */
    UNKNOWN("^.*$") {
        protected void visit(IncomingMessageTypeVisitor visitor, String payload) throws IOException {
            visitor.visitUnknown(payload);
        }
    };

    protected Pattern pattern;

    IncomingMessageType(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    private static IncomingMessageType getMessageType(String message) {
        for (IncomingMessageType messageType : values()) {
            if (messageType.pattern.matcher(message).matches()) {
                return messageType;
            }
        }
        return UNKNOWN;
    }

    private String getPayload(String message) {
        Matcher matcher = pattern.matcher(message);
        return matcher.matches() && matcher.groupCount() > 0
                ? matcher.group(1)
                : null;
    }

    protected abstract void visit(IncomingMessageTypeVisitor visitor, String payload) throws IOException;

    @SuppressWarnings("unchecked")
    public static void handle(String message, IncomingMessageTypeVisitor visitor) throws IOException {
        IncomingMessageType messageType = getMessageType(message);

        messageType.visit(visitor, messageType.getPayload(message));
    }
}
