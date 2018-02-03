package ru.hh.school;

import ru.hh.school.http.HttpRequest;
import ru.hh.school.http.HttpResponse;
import ru.hh.school.http.HttpSession;
import ru.hh.school.utils.ConfigHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class HttpServer implements Runnable {

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
                        if (key.isAcceptable()) {
                            SocketChannel client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) { //
                            SocketChannel client = (SocketChannel) key.channel();
                            HttpSession session = (HttpSession) key.attachment();
                            // create session if it doesnt exist
                            if (session == null) {
                                session = new HttpSession(client);
                                key.attach(session);
                            }
                            // read request
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
