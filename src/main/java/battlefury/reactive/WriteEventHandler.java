package battlefury.reactive;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteEventHandler implements EventHandler {
    public void handleEvent(final SelectionKey handle) throws Exception {
        System.out.println("===== Write Event Handler =====");

        SocketChannel socketChannel = (SocketChannel) handle.channel();

        ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
        socketChannel.write(inputBuffer);
        socketChannel.close();
    }
}
