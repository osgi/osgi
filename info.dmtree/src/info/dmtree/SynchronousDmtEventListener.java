package info.dmtree;

/**
 * Like for {@link DmtEventListener}, registered implementations of this class
 * are notified via {@link DmtEvent} objects about important changes in the
 * tree. The main difference is that implementers of SynchronousDmtEventListener
 * are notified synchronously and therefore blocking.
 * 
 * @see DmtEventListener
 * @since 1.1
 */
public interface SynchronousDmtEventListener extends DmtEventListener {

    /**
     * {@code DmtAdmin} uses this method to notify the registered
     * listeners about the change. This method is called synchronously from the
     * actual event occurrence.
     * 
     * @param event the {@code DmtEvent} describing the change in detail
     */
    void changeOccurred(DmtEvent event);
}
