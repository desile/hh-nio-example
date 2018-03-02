package ru.hh.school.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class HttpSession {

    private Charset charset = Charset.forName("UTF-8");
    private CharsetEncoder encoder = charset.newEncoder();
    private final SocketChannel channel;
    private final ByteBuffer buffer = ByteBuffer.allocate(2048);

    public HttpSession(SocketChannel channel) {
        this.channel = channel;
    }

    public String read() throws IOException {
        buffer.limit(buffer.capacity());
        int read = channel.read(buffer);
        if(read == -1){
            return "";
        }
        buffer.clear();
        return readLinesFromBuffer();
    }

    private String readLinesFromBuffer() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        return sb.toString();
    }

    private void writeLine(String line) throws IOException {
        channel.write(encoder.encode(CharBuffer.wrap(line + "\r\n")));
    }

    public void sendResponse(HttpResponse response) {
        try {
            writeLine(response.getVersion() + " " + response.getResponseCode());
            writeLine("Content-Type: " + response.getContentType());
            writeLine("");
            if(response.getResponseCode() == HttpResponseCode.OK){
                channel.write(response.getContent());
            } else {
                writeLine(response.getResponseCode().toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            channel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
