package com.example.mosaicifier;

import javafx.scene.image.Image;

public record MosaicPiece(Image img, Colour colour) {
    public Image getImg() {
        return img;
    }

    public Colour getColour() {
        return colour;
    }
}
