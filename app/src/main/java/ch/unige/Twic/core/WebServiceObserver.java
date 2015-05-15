package ch.unige.Twic.core;

/**
 * Observer allowing tabs to be updated (their content) by the webservice
 */
public interface WebServiceObserver {
    public void updateResponse(String response);
}
