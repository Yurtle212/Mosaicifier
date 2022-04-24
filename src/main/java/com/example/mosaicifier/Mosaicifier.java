package com.example.mosaicifier;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static jdk.jfr.consumer.EventStream.openFile;

public class Mosaicifier extends Application {
    private Desktop desktop = Desktop.getDesktop();
    GridPane imageGridPane;
    final ArrayList<MosaicPiece> mosaicPieces = new ArrayList<>();
    Mosaic mosaicGenerator;

    Image toMosaic = null;

    @Override
    public void start(final Stage stage) {
        stage.setTitle("Mosaic Generator");

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.png *.jpg)", "*.png", "*.jpg"));

        final Button openMultipleButton = new Button("Open Tiling Images");
        final Button openFileToMosaic = new Button("Open Image to Mosaic");
        final Button createMosaic = new Button("Genetate Mosaic");
        final Label widthLabel = new Label("Tiling Width:");
        final TextField widthInput = new TextField("10");
        widthInput.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        widthInput.setText("10");

        final Label sizeLabel = new Label("             Image Size Override:");
        final TextField sizeInput = new TextField("");

        final Button saveMosaic = new Button("Save Mosaic");

        saveMosaic.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        final FileChooser fileChooserSave = new FileChooser();
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Png File (*.png)", "*.png"));
                        File file =
                                fileChooserSave.showSaveDialog(stage);
                        if (file != null) {
                            try {
                                //Pad the capture area
                                WritableImage writableImage = new WritableImage((int)imageGridPane.getWidth(),
                                        (int)imageGridPane.getHeight());
                                imageGridPane.snapshot(null, writableImage);
                                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                                //Write the snapshot to the chosen file
                                ImageIO.write(renderedImage, "png", file);
                            } catch (IOException ex) { ex.printStackTrace(); }
                        }
                    }
                }
        );

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

        openFileToMosaic.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        File file =
                                fileChooser.showOpenDialog(stage);
                        if (file != null) {
                            toMosaic = new Image(file.toURI().toString());
                        }
                    }
                });

        createMosaic.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        int tilingWidth, size;
                        tilingWidth = !widthInput.getText().equals("") ? Integer.parseInt(widthInput.getText()) : 10;
                        size = sizeInput.getText().equals("") ? 0 : Integer.parseInt(sizeInput.getText());

                        mosaicGenerator.createMosaic(tilingWidth, mosaicPieces, toMosaic, stage, size);
                    }
                });


        final GridPane inputGridPane = new GridPane();
        imageGridPane = new GridPane();
        mosaicGenerator = new Mosaic(imageGridPane);

        GridPane.setConstraints(openMultipleButton, 0, 0);
        GridPane.setConstraints(openFileToMosaic, 1, 0);
        GridPane.setConstraints(createMosaic, 2, 0);
        GridPane.setConstraints(widthLabel, 3, 0);
        GridPane.setConstraints(widthInput, 4, 0);
        GridPane.setConstraints(sizeLabel, 5, 0);
        GridPane.setConstraints(sizeInput, 6, 0);
        GridPane.setConstraints(saveMosaic, 7, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openMultipleButton, openFileToMosaic, createMosaic, widthLabel, widthInput,
                sizeLabel, sizeInput, saveMosaic);

        GridPane.setConstraints(imageGridPane, 0, 1);

        imageGridPane.setHgap(0);
        imageGridPane.setVgap(0);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane, imageGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup, 1000, 600));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void openFiles(List<File> files) throws FileNotFoundException {
        for (File file : files) {
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