package ru.hh.school.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class FileHandler {

    private static class TimedMappedByteBuffer{
        private long lastModified = 0;
        private MappedByteBuffer buffer;
    }

    private static final Map<String, TimedMappedByteBuffer> cacheMap = new HashMap<>();

    public static MappedByteBuffer getFile(String filePath) throws IOException {
        TimedMappedByteBuffer timedMappedByteBuffer = cacheMap.get(filePath);

        File fileToGet = new File(filePath);
        if(!fileToGet.exists()){
            throw new FileNotFoundException();
        }
        if(fileToGet.isDirectory()){
            throw new IOException();
        }
        if(!fileToGet.canRead()){
            throw new IOException();
        }
        if(timedMappedByteBuffer == null){
            timedMappedByteBuffer = new TimedMappedByteBuffer();
            if("true".equals(ConfigHandler.getProperty(ConfigHandler.CACHE).orElse("true"))){
                cacheMap.put(filePath, timedMappedByteBuffer);
            }
        }

        if(fileToGet.lastModified() > timedMappedByteBuffer.lastModified){
            try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(
                    fileToGet.toPath(), EnumSet.of(StandardOpenOption.READ))) {

                timedMappedByteBuffer.buffer = fileChannel
                        .map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

                timedMappedByteBuffer.lastModified = fileToGet.lastModified();
            }
        } else {
            timedMappedByteBuffer.buffer.rewind();
            System.out.print("FROM CACHE ");
        }
        return timedMappedByteBuffer.buffer;
    }

    private FileHandler(){

    }

}
