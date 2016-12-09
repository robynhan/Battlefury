package battlefury.rpc.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcDynamicProxy implements InvocationHandler {

    private final InetSocketAddress address;
    private final Class<?> serviceClass;

    public RpcDynamicProxy(final InetSocketAddress address, final Class<?> serviceClass) {
        this.address = address;
        this.serviceClass = serviceClass;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            socket = new Socket();
            socket.connect(address);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeUTF(serviceClass.getName());
            output.writeUTF(method.getName());
            output.writeObject(method.getParameterTypes());
            output.writeObject(args);

            input = new ObjectInputStream(socket.getInputStream());
            return input.readObject();
        } finally {
            if (socket != null) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }
}
