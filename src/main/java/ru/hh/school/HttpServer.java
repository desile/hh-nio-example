package ru.hh.school;

import ru.hh.school.http.HttpRequest;
import ru.hh.school.http.HttpResponse;
import ru.hh.school.utils.ConfigHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

public class HttpServer implements Runnable {

    private Charset charset = Charset.forName("UTF-8");
    private CharsetEncoder encoder = charset.newEncoder();
    private Selector selector = Selector.open();
    private ServerSocketChannel server = ServerSocketChannel.open();

    public HttpServer(InetSocketAddress address) throws IOException {
        server.socket().bind(address);
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void run() {
        while(true) {
            try {
                selector.selectNow();
                Iterator<SelectionKey> i = selector.selectedKeys().iterator();
                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    try {
                        // get a new connection
                        if (key.isAcceptable()) {
                            // accept them
                            SocketChannel client = server.accept();
                            // non blocking please
                            client.configureBlocking(false);
                            // show out intentions
                            client.register(selector, SelectionKey.OP_READ);
                            // read from the connection
                        } else if (key.isReadable()) {
                            //  get the client
                            SocketChannel client = (SocketChannel) key.channel();
                            // get the session
                            HttpSession session = (HttpSession) key.attachment();
                            // create it if it doesnt exist
                            if (session == null) {
                                session = new HttpSession(client);
                                key.attach(session);
                            }
                            // get more data
                            String rawRequest = session.read();
                            if (!rawRequest.isEmpty()) {
                                HttpRequest request = new HttpRequest(rawRequest);
                                HttpResponse response = new HttpResponse(request);
                                session.sendResponse(response);
                            }
                            session.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        if (key.attachment() instanceof HttpSession) {
                            ((HttpSession) key.attachment()).close();
                            System.out.println("CLOSED");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ConfigHandler.load();
        HttpServer server = new HttpServer(new InetSocketAddress(5555));
        server.run();
    }

}
