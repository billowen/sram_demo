package com.example.sramdisplay;

import com.datastax.driver.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


@Service
public class CassandraService {
    private Session session;

    @Autowired
    public void setSession(Session session) {
        this.session = session;
    }

    public static List<Integer> shrinkBitmap(BitSet bits, int sizeX, int sizeY, int xResolution, int yResolution) {
        List<Integer> points = new ArrayList<>();
        int rowStep = sizeY > yResolution ? (int) Math.ceil(sizeY / (double) yResolution) : 1;
        int colStep = sizeX > xResolution ? (int) Math.ceil(sizeX / (double) yResolution) : 1;
        xResolution = sizeX < xResolution ? sizeX : xResolution;
        yResolution = sizeY < yResolution ? sizeY : yResolution;

        for (int iRow = 0; iRow < yResolution; iRow++) {
            int rowStart = rowStep * iRow;
            for (int iCol = 0; iCol < xResolution; iCol++) {
                int colStart = colStep * iCol;
                boolean flag = false;
                for (int iiRow = rowStart; iiRow < rowStart + rowStep && iiRow < sizeY; iiRow++) {
                    for (int iiCol = colStart; iiCol < colStart + colStep && iiCol < sizeX; iiCol++) {
                        if (bits.get(iiRow * sizeX + iiCol)) {
                            points.add(iiCol);
                            points.add(iiRow);
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            }
        }
        return points;
    }

    public List<DieDisplayData> getDieDisplayData(int waferId, int dieX, int dieY, int resolution) {
        List<DieDisplayData> list = new ArrayList<>();
        long count = 0;
        long start2 = System.currentTimeMillis();

            SimpleStatement stmt = new SimpleStatement("SELECT fail_type FROM result_by_die WHERE wafer_id=? AND die_x=? AND die_y=?", waferId, dieX, dieY);
            ResultSet rs = session.execute(stmt);
            List<String> failTypes = new ArrayList<>();
            for (Row row : rs) {
                failTypes.add(row.getString("fail_type"));
            }

            PreparedStatement preparedStatement = session.prepare("SELECT data FROM result_by_die WHERE wafer_id=? AND die_x=? AND die_y=? AND fail_type=?");
            for (String failType: failTypes) {
                BoundStatement boundStatement = preparedStatement.bind(waferId, dieX, dieY, failType);
                ResultSet rs2 = session.execute(boundStatement);

                Row data = rs2.one();

                if (data != null) {
                    DieDisplayData die = new DieDisplayData();
                    die.setDieX(dieX);
                    die.setDieY(dieY);
                    die.setFailType(failType);
                    list.add(die);
                    BitSet bits = BitSet.valueOf(data.getBytes("data"));
                    long t1 = System.currentTimeMillis();
                    List<Integer> points = shrinkBitmap(bits, 16384, 16384, 100, 100);
                    long t2 = System.currentTimeMillis();
                    count += (t2 - t1);
                    System.out.println(points.size());
                    die.setData(points);
                }

            }

//            for (Row row : rs) {
//                String failType = row.getString("fail_type");
//                ByteBuffer data = row.getBytes("data");
////                long start = System.currentTimeMillis();
////                BitSet bits = BitSet.valueOf(Compress.decompress(data.array()));
////                long end = System.currentTimeMillis();
////                count += (end - start);
//                BitSet bits = BitSet.valueOf(data);
//
//
//                DieDisplayData die = null;
//                for (DieDisplayData i : list) {
//                    if (i.getFailType().equals(failType)) {
//                        die = i;
//                        break;
//                    }
//                }
//                if (die == null) {
//                    die = new DieDisplayData();
//                    die.setDieX(dieX);
//                    die.setDieY(dieY);
//                    die.setFailType(failType);
//                    list.add(die);
//                }
//
//
//                List<Integer> points = shrinkBitmap(bits, 16384, 16384, 100, 100);
//
//
//                die.setData(points);
//            }

//        } catch (DataFormatException e) {
//            System.out.println("Uncompress the data failed");
//        }

        long end2 = System.currentTimeMillis();

        System.out.println(count);
        System.out.println(end2-start2);
        return list;
    }
}
