package ru.hh.school;

import ru.hh.school.http.HttpResponse;
import ru.hh.school.http.HttpResponseCode;

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
    private int mark = 0;

    public HttpSession(SocketChannel channel) {
        this.channel = channel;
    }

    public String read() throws IOException {
        fillBuffer();
        return readLinesFromBuffer();
    }

    private String readLinesFromBuffer() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        return sb.toString();
    }

    private void fillBuffer() throws IOException {
        buffer.limit(buffer.capacity());
        int read = channel.read(buffer);
        if (read == -1) {
            throw new IOException("End of stream");
        }
        buffer.flip();
        buffer.position(mark);
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