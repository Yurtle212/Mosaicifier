package com.example.mosaicifier;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mosaic {
    private GridPane mosaicPane;

    public Mosaic(GridPane mosaicPane) {
        this.mosaicPane = mosaicPane;
        mosaicPane.setHgap(0);
        mosaicPane.setVgap(0);
    }

    public void createMosaic(int width, ArrayList<MosaicPiece> pieces, Image toMosaic, double imgSize) {
        int height = (int) ((width / toMosaic.getWidth()) * toMosaic.getHeight());

        Platform.runLater(() -> {
            ArrayList<Colour> colours = getColourList(width, height, toMosaic);
            int i;

            for (i = 0; i < colours.size(); i++) {
                Image mImage = findClosestMosaicPiece(pieces, colours.get(i)).getImg();
                ImageView view = new ImageView(mImage);
                view.setPreserveRatio(false);
                view.setFitWidth(imgSize);
                view.setFitHeight(imgSize);
                GridPane.setConstraints(view, i % width, i / width);
                mosaicPane.getChildren().add(view);
            }
        });
    }

    public static MosaicPiece findClosestMosaicPiece(ArrayList<MosaicPiece> pieces, Colour lookingFor) {
        int closestDistance = -1;
        MosaicPiece closest = pieces.get(0);

        for (MosaicPiece piece : pieces) {
            int dist = lookingFor.getDistance(piece.getColour());
            if (closestDistance == -1 || dist < closestDistance) {
                closestDistance = dist;
                closest = piece;
            }
        }

        return closest;
    }

    public static ArrayList<Colour> getColourList(int gridWidth, int gridHeight, Image img) {
        ArrayList<Colour> colours = new ArrayList<>();
        int sectionHeight = (((int) img.getHeight()) / gridHeight);
        int sectionWidth = (((int) img.getWidth()) / gridWidth);

        for (int i = 0; i < sectionHeight * gridHeight; i += sectionHeight) {
            for (int j = 0; j < sectionWidth * gridWidth; j += sectionWidth) {
                byte[] pixelBuffer = new byte[sectionHeight * sectionWidth * 4];
                img.getPixelReader().getPixels(j, i, sectionWidth, sectionHeight,
                        PixelFormat.getByteBgraInstance(), pixelBuffer, 0, sectionWidth*4);
                colours.add(getAverageColour(pixelBuffer));
            }
        }
        return colours;
    }

    public static Colour getAverageColour(byte[] cBytes) {
        int j = 1;
        int avgR = 0, avgG = 0, avgB = 0;
        byte r = 0, g = 0, b = 0;

        for (byte by : cBytes) {
            switch (j%4){
                case 1:
                    b = by;
                    break;
                case 2:
                    g = by;
                    break;
                case 3:
                    r = by;
                    break;
                case 0:
                    if (by == -1) {
                        avgR += r & 0xff;
                        avgG += g & 0xff;
                        avgB += b & 0xff;
                    }
                default:
                    break;
            }
            j++;
        }
        avgR = avgR/(j/4);
        avgG = avgG/(j/4);
        avgB = avgB/(j/4);

        return new Colour(avgR, avgG, avgB);
    }
}
