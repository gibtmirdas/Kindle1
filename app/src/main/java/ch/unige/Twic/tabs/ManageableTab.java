package ch.unige.Twic.tabs;

import ch.unige.Twic.exceptions.TwicException;

/**
 * This interface allow classes to update a tab independently of their type.
 */
public interface ManageableTab {

    void update() throws TwicException;
}
