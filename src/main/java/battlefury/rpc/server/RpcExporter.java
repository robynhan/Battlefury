package battlefury.rpc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class RpcExporter {
    private RpcExporter() {
    }

    private static Executor executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void exporter(final String hostName, final int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                executor.execute(new ExporterTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }
}
