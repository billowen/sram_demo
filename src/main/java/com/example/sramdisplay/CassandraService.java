package com.example.sramdisplay;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
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

    public List<DieDisplayData> getDieDisplayData(int waferId, int dieX, int dieY, int resolution) {
        int rowStep = 16384 / resolution;
        int colStep = 16384 / resolution;


        ResultSet rs = session.execute("SELECT * FROM result WHERE wafer_id=? AND die_x=? AND die_y=?", waferId, dieX, dieY);
        List<DieDisplayData> list = new ArrayList<>();
        for (Row row : rs) {
            String failType = row.getString("fail_type");
            ByteBuffer data = row.getBytes("data");
            BitSet bits = BitSet.valueOf(data);
            int blockX = row.getInt("block_x");
            int blockY = row.getInt("block_y");

            DieDisplayData die = null;
            for (DieDisplayData i : list) {
                if (i.getFailType().equals(failType)) {
                    die = i;
                    break;
                }
            }
            if (die == null) {
                die = new DieDisplayData();
                die.setDieX(dieX);
                die.setDieY(dieY);
                die.setFailType(failType);
                list.add(die);
            }

            List<Bit> points = die.getData();

        }

        return list;
    }
}
