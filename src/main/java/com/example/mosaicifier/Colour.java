package com.example.mosaicifier;

public record Colour(int r, int g, int b) {
    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getDistance(Colour compareTo) {
        return Math.abs(compareTo.getR() - r)
                + Math.abs(compareTo.getG() - g)
                + Math.abs(compareTo.getB() - b);
    }

    @Override
    public String toString() {
        return "Colour{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}
