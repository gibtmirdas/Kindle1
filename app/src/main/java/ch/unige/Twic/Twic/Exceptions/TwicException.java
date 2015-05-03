package ch.unige.Twic.Twic.Exceptions;

/**
 * Created by Dwii on 12.04.15.
 */
public class TwicException extends Exception {
    private int messageId;

    public TwicException(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }
}
