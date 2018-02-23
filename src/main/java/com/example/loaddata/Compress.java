package com.example.loaddata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class Compress {
    public static byte[] compress(byte[] input) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater compressor = new Deflater(1);
        try {
            compressor.setInput(input);
            compressor.finish();
            final byte[] buf = new byte[2048];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                bos.write(buf, 0, count);
            }
        } finally {
            compressor.end();
        }
        byte[] output =  bos.toByteArray();
        bos.close();
        return output;
    }
}
