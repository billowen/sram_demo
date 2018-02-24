package com.example.sramdisplay;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Scanner;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Compress {
    public static void main(String[] args) throws IOException {
        File[] files = new File("patternFile").listFiles();
        for (File file : files) {
            Path p = file.toPath();
            String fileName = p.getFileName().toString();
            try (BufferedReader reader = Files.newBufferedReader(p);
                 OutputStream out3 = new BufferedOutputStream(new FileOutputStream("original/" + fileName));
                 OutputStream out1 = new BufferedOutputStream(new FileOutputStream("compress1/" + fileName));
                 OutputStream out2 = new BufferedOutputStream(new FileOutputStream("compress2/" + fileName))) {
                BitSet bits = new BitSet();
                String l;
                while ((l = reader.readLine()) != null) {
                    Scanner s = new Scanner(l.trim()).useDelimiter(",");
                    int x = s.nextInt();
                    int y = s.nextInt();
                    for (int row = 0; row < 16; row++) {
                        for (int col = 0; col < 16; col++) {
                            bits.set((row * 1024 + y) * 16384 + col * 1024 + x );
                        }
                    }
                    //bits.set(x * 1024 + y);
                }
                out3.write(bits.toByteArray());
                //out2.write(Compress.compress(bits.toByteArray()));
                out1.write(compress(bits.toByteArray()));
               //out2.write(compress2(bits.toByteArray()));
            }
        }

    }

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

    public static byte[] decompress(byte[] input) throws DataFormatException {
        Inflater infl = new Inflater();

        byte[] outByte = new byte[32 * 1024 * 1024];

        try {
            infl.setInput(input);
            infl.inflate(outByte);
        } finally {
            infl.end();
        }

        return outByte;
    }
}
