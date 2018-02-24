package com.example.sramdisplay;

import java.util.ArrayList;
import java.util.List;

public class DieDisplayData {
    private int dieX, dieY;
    private String failType;
    private List<Integer> data = new ArrayList<>();

    public int getDieX() {
        return dieX;
    }

    public void setDieX(int dieX) {
        this.dieX = dieX;
    }

    public int getDieY() {
        return dieY;
    }

    public void setDieY(int dieY) {
        this.dieY = dieY;
    }

    public String getFailType() {
        return failType;
    }

    public void setFailType(String failType) {
        this.failType = failType;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
