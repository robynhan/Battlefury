package battlefury.rpc.service;

public class DefaultEchoService implements EchoService {
    @Override
    public String echo(final String message) {
        return "echo: " + message;
    }
}
