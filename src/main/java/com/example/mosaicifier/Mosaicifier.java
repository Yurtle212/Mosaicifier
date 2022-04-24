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
    Mosaic mosaicGenerator;

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Image Opener");

        final FileChooser fileChooser = new FileChooser();

        final Button openMultipleButton = new Button("Open Images");
        final Button createMosaic = new Button("Genetate Mosaic");

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

        createMosaic.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        mosaicGenerator.createMosaic(50, mosaicPieces, new Image("bestpizza.png"), 5);
                    }
                });


        final GridPane inputGridPane = new GridPane();
        imageGridPane = new GridPane();
        mosaicGenerator = new Mosaic(imageGridPane);

        GridPane.setConstraints(openMultipleButton, 0, 0);
        GridPane.setConstraints(createMosaic, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openMultipleButton, createMosaic);

        GridPane.setConstraints(imageGridPane, 0, 1);

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

            Colour avgColour = Mosaic.getAverageColour(buffer);
            MosaicPiece mosaicPiece = new MosaicPiece(tmpImg, avgColour);
            mosaicPieces.add(mosaicPiece);
        }
    }
}