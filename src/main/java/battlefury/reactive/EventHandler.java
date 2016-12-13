package battlefury.reactive;

import java.nio.channels.SelectionKey;

public interface EventHandler {
    void handleEvent(final SelectionKey selectionKey) throws Exception;
}
