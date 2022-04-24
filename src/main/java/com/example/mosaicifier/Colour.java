package com.example.mosaicifier;

public record Colour(byte r, byte g, byte b) {
    public byte getR() {
        return r;
    }

    public byte getG() {
        return g;
    }

    public byte getB() {
        return b;
    }

    public int getDistance(Colour compareTo) {
        return Math.abs(compareTo.getR() - r)
                + Math.abs(compareTo.getG() - g)
                + Math.abs(compareTo.getB() - b);
    }
}
