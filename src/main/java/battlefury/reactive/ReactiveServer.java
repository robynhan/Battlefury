package battlefury.reactive;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class ReactiveServer {
    private static final int PORT = 9090;

    public static void main(final String[] args) throws Exception {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(PORT));
        server.configureBlocking(false);

        Reactor reactor = new Reactor();
        reactor.registerChannel(SelectionKey.OP_ACCEPT, server);
        reactor.registerEventHandler(
                SelectionKey.OP_ACCEPT, new AcceptEventHandler(reactor.getDemultiplexer()));

        reactor.registerEventHandler(
                SelectionKey.OP_READ, new ReadEventHandler(reactor.getDemultiplexer()));

        reactor.registerEventHandler(
                SelectionKey.OP_WRITE, new WriteEventHandler());

        reactor.run();
    }

}
