package com.example.sramdisplay;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.zip.DataFormatException;

public class CompressTest {
    @Test
    public void compressTest() throws IOException, DataFormatException {
        BitSet bits = new BitSet();
        for(int row = 0; row < 16384; row++) {
            for (int i = 0 ; i < 163; i++) {
                int col = (int) (Math.random() * 16384);
                int index = row * 16384 + col;
                bits.set(index);
            }
        }
        byte[] orignal = bits.toByteArray();
        byte[] compressed  = Compress.compress(orignal);
        byte[] actual = Compress.decompress(compressed);
        assertTrue(Arrays.equals(orignal, actual));

    }
}
