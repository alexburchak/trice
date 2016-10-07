package org.alexburchak.trice.ws;

import java.io.IOException;

/**
 * @author alexburchak
 */
public interface IncomingMessageTypeVisitor {
    void visitTrice(String payload) throws IOException;

    void visitEcirt(String payload) throws IOException;

    void visitUnknown(String payload) throws IOException;
}
