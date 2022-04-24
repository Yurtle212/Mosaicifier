package com.example.mosaicifier;

import java.awt.Desktop;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static jdk.jfr.consumer.EventStream.openFile;

public class Mosaicifier extends Application {
    private Desktop desktop = Desktop.getDesktop();
    GridPane imageGridPane;
    final double imageX = 50;
    final double imageY = 50;
    final ArrayList<MosaicPiece> mosaicPieces = new ArrayList<>();

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Image Opener");

        final FileChooser fileChooser = new FileChooser();

        final Button openMultipleButton = new Button("Open Images");

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            try {
                                openFiles(list);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });


        final GridPane inputGridPane = new GridPane();
        imageGridPane = new GridPane();

        GridPane.setConstraints(openMultipleButton, 0, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openMultipleButton);

        imageGridPane.setHgap(0);
        imageGridPane.setVgap(0);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane, imageGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup, 600, 600));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void openFiles(List<File> files) throws FileNotFoundException {
        for (File file : files) {
            System.out.println();
            System.out.println(file.toURI().toString());

            Image tmpImg = new Image(file.toURI().toString());
            int width = (int)tmpImg.getWidth();
            int height = (int)tmpImg.getHeight();
            byte[] buffer = new byte[width * height * 4];

            tmpImg.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), buffer, 0, width*4);
            int j = 1;
            int avgR = 0, avgG = 0, avgB = 0;
            int r = 0, g = 0, b = 0;

            for (byte by : buffer) {
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
                            avgR += r;
                            avgG += g;
                            avgB += b;
                        }
                    default:
                        break;
                }
                j++;
            }
            avgR = avgR/(j/4);
            avgG = avgG/(j/4);
            avgB = avgB/(j/4);

            System.out.printf("Averages: r%d g%d b%d", avgR, avgG, avgB);
            Colour avgColour = new Colour((byte) avgR, (byte) avgG, (byte) avgB);
            MosaicPiece mosaicPiece = new MosaicPiece(tmpImg, avgColour);
            mosaicPieces.add(mosaicPiece);
        }
    }
}