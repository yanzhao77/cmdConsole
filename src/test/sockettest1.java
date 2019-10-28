package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class sockettest1 {
    public static void main(String[] args) throws IOException {
        SocketChannel channel=SocketChannel.open(new InetSocketAddress("192.168.1.122",40561));
        while(!channel .finishConnect());
        System.out.println(channel.isConnected());
        while (true);
    }

}
