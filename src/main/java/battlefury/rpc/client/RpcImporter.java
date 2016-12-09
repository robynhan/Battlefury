package battlefury.rpc.client;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

public class RpcImporter<S> {

    @SuppressWarnings("unchecked")
    public S importer(final Class<?> serviceClass, final InetSocketAddress address) {
        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass.getInterfaces()[0]},
                new RpcDynamicProxy(address, serviceClass)
        );
    }
}
