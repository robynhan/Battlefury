package battlefury.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private static final int PORT = 4000;
    private static final int BUFFER_SIZE = 1024;

    public static void main(final String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                Socket socket = server.accept();
                executor.submit(new Handler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable {
        private Socket socket;

        public Handler(final Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                int read;
                byte[] buf = new byte[BUFFER_SIZE];

                // echo client.
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
