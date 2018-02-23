package com.example.loaddata;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Scanner;
import java.util.UUID;

public class InsertData {
    public static void main(String[] args) throws IOException {
        UUID waferId = UUID.randomUUID();
        try (Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build()) {
            Session session = cluster.connect("sram_test");
            File[] files = new File("patternFile").listFiles();
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
                    byte[] compressed = Compress.compress(bits.toByteArray());
                    PreparedStatement preparedStatement = session.prepare("INSERT INTO result (wafer_id, die_x, die_y, block_x, block_y, fail_type, data) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
                    for (int dieY = 0; dieY < 10; dieY ++) {
                        for (int dieX = 0; dieX < 10; dieX ++) {
                            for (int blockY = 0; blockY < 16; blockY ++) {
                                BatchStatement batchStatement = new BatchStatement();
                                for (int blockX = 0; blockX < 16; blockX ++) {
                                    batchStatement.add(preparedStatement.bind( waferId, dieX, dieY, blockX, blockY, failType, ByteBuffer.wrap(compressed)));
                                }
                                session.execute(batchStatement);
                            }
                        }
                    }
                }
            }


        }
    }
}

