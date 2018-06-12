package com.mobius;

public final class IndexPoint {

    private Double index;

    private static class SingletonHolder {
        private static final IndexPoint INSTANCE = new IndexPoint();
    }


    private IndexPoint() {
    }

    public static final IndexPoint getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Double getIndex() {
        return index;
    }

    public void setIndex(Double index) {
        this.index = index;
    }
}
