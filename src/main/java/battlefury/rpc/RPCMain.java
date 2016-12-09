package battlefury.rpc;

import battlefury.rpc.client.RpcImporter;
import battlefury.rpc.server.RpcExporter;
import battlefury.rpc.service.DefaultEchoService;
import battlefury.rpc.service.EchoService;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class RPCMain {

    private static final int EXPORTER_PORT = 8088;
    private static final int IMPOETER_PORT = 8088;

    private RPCMain() {
    }

    public static void main(final String[] args) throws Exception {
        new Thread(() -> {
            try {
                RpcExporter.exporter("localhost", EXPORTER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        RpcImporter<EchoService> importer = new RpcImporter<>();
        EchoService echoService = importer.importer(DefaultEchoService.class,
                new InetSocketAddress("localhost", IMPOETER_PORT));
        System.out.println(echoService.echo("Hello "));
    }
}
