package battlefury.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

class ReactiveHandler implements Runnable {
    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    private static final int READ_BUF_SIZE = 1024;
    private static final int WRITE_BUF_SIZE = 1024;
    private ByteBuffer readBuf = ByteBuffer.allocate(READ_BUF_SIZE);
    private ByteBuffer writeBuf = ByteBuffer.allocate(WRITE_BUF_SIZE);

    public ReactiveHandler(final Selector selector, final SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        // Register socketChannel with _selector listening on OP_READ events.
        // Callback: ReactiveHandler, selected when the connection is established and ready for READ
        selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        selector.wakeup(); // let blocking select() return
    }

    public void run() {
        try {
            if (selectionKey.isReadable()) {
                read();
            } else if (selectionKey.isWritable()) {
                write();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Process data by echoing input to output
    private synchronized void process() {
        readBuf.flip();
        byte[] bytes = new byte[readBuf.remaining()];
        readBuf.get(bytes, 0, bytes.length);

        writeBuf = ByteBuffer.wrap(bytes);

        // Set the key's interest to WRITE operation
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selectionKey.selector().wakeup();
    }

    synchronized void read() throws IOException {
        try {
            int numBytes = socketChannel.read(readBuf);
            System.out.println("read(): #bytes read into 'readBuf' buffer = " + numBytes);

            if (numBytes == -1) {
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("read(): client connection might have been dropped!");
            } else {
                ReactiveEchoServer.getWorkerPool().execute(this::process);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void write() throws IOException {
        int numBytes;

        try {
            numBytes = socketChannel.write(writeBuf);
            System.out.println("write(): #bytes read from 'writeBuf' buffer = " + numBytes);

            if (numBytes > 0) {
                readBuf.clear();
                writeBuf.clear();

                // Set the key's interest-set back to READ operation
                selectionKey.interestOps(SelectionKey.OP_READ);
                selectionKey.selector().wakeup();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
