package ch.unige.Twic.Twic.Exceptions;

/**
 * {@link TwicException} represent a TWiC exception raised by issues like connectivity error to the TWiC web service or bad url leading to a bad response from the TWiC web service
 */
public class TwicException extends Exception {
    /**
     * Id of the message in the {@link ch.unige.Twic.R.string} file
     */
    private int messageId;

    public TwicException(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }
}
