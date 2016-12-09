package battlefury.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ReactiveEchoServer implements Runnable {
    private static final int PORT = 9090;
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private static final int WORKER_POOL_SIZE = 10;
    private static ExecutorService workerPool;

    ReactiveEchoServer(final int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        // Register serverSocketChannel with selector listening on OP_ACCEPT events.
        // Callback: Acceptor, selected when a new connection incomes.
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    public void run() {
        try {
            // Event Loop
            while (true) {
                selector.select();
                Iterator it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    SelectionKey sk = (SelectionKey) it.next();
                    it.remove();
                    handleIncomingMessage(sk);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handleIncomingMessage(final SelectionKey sk) {
        Runnable r = (Runnable) sk.attachment(); // handler or acceptor callback/runnable
        if (r != null) {
            r.run();
        }
    }

    public static ExecutorService getWorkerPool() {
        return workerPool;
    }

    // Acceptor: if connection is established, assign a handler to it.
    private class Acceptor implements Runnable {
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new ReactiveHandler(selector, socketChannel);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(final String[] args) {
        workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);

        try {
            new Thread(new ReactiveEchoServer(PORT)).start(); // a single thread blocking on selector for events
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
