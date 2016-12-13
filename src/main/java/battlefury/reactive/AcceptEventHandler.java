package battlefury.reactive;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptEventHandler implements EventHandler {
    private final Selector demultiplexer;

    public AcceptEventHandler(final Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void handleEvent(final SelectionKey handle) throws Exception {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            socketChannel.configureBlocking(false);
            socketChannel.register(demultiplexer, SelectionKey.OP_READ);
        }

    }
}
