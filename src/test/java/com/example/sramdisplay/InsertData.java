package com.example.sramdisplay;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Scanner;

public class InsertData {
    public static void main(String[] args) throws IOException {
        try (Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build()) {
            Session session = cluster.connect("sram_test");
            File[] files = new File("data/Yield_5%").listFiles();
            PreparedStatement preparedStatement1 = session.prepare("INSERT INTO origin (wafer_id, die_x, die_y, block_x, block_y, data) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement preparedStatement2 = session.prepare("INSERT INTO result (wafer_id, die_x, die_y, block_x, block_y, fail_type, data) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            for (File file : files) {
                Path p = file.toPath();
                String failType = p.getFileName().toString();
                try (BufferedReader reader = Files.newBufferedReader(p)) {
                    BitSet bits = new BitSet();
                    String l;
                    while ((l = reader.readLine()) != null) {
                        Scanner s = new Scanner(l.trim()).useDelimiter(",");
                        int x = s.nextInt();
                        int y = s.nextInt();
                        bits.set(x * 1024 + y);
                    }
                    byte[] original = bits.toByteArray();
                    //byte[] compressed = Compress.compress(bits.toByteArray());
                    System.out.println(failType);
                    if (failType.equals("All")) {

                        for (int blockY = 0; blockY < 16; blockY ++) {
                            BatchStatement batchStatement = new BatchStatement();
                            for (int blockX = 0; blockX < 16; blockX ++) {
                                batchStatement.add(preparedStatement1.bind( 1, 1, 1, blockX, blockY, ByteBuffer.wrap(original)));
                            }
                            session.execute(batchStatement);
                        }
                    } else {

                        for (int blockY = 0; blockY < 16; blockY ++) {
                            BatchStatement batchStatement = new BatchStatement();
                            for (int blockX = 0; blockX < 16; blockX ++) {
                                batchStatement.add(preparedStatement2.bind( 1, 1, 1, blockX, blockY, failType, ByteBuffer.wrap(original)));
                            }
                            session.execute(batchStatement);
                        }
                    }
                }
            }
        }
    }
}
