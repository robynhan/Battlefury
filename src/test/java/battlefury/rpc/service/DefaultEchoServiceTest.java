package battlefury.rpc.service;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DefaultEchoServiceTest {
    @Test
    public void should_echo_message() {
        DefaultEchoService service = new DefaultEchoService();
        assertThat(service.echo("hello"), is("echo: hello"));
    }
}