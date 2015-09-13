package com.m3rc.crf;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Antonello on 17/08/15.
 */
public class Runner extends Application implements EventHandler<ActionEvent> {

    final static int NUMBER_OF_ITERATIONS = 200;
    static int NUMBER_OF_CROSS_VALIDATIONS = 5;

    private Stage stage;
    private Scene progressScene;
    private Button startButton;
    private TextField crossTextField;
    private GridPane progressGrid;
    private ProgressIndicator tokenizationInd;
    private HBox tokenizationBox;
    private ProgressIndicator featuresGenerationInd;
    private HBox featuresGenerationBox;
    private ProgressIndicator posTaggingInd;
    private HBox posTaggingBox;
    private ProgressIndicator crfTrainingInd;
    private HBox crfTrainingBox;
    private ProgressIndicator accuracyInd;
    private HBox accuracyBox;


    public static void main(String[] args) throws IOException, InterruptedException {

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;

        BorderPane border = new BorderPane();
        Scene startScene = new Scene(border);
        border.setPadding(new Insets(10, 10, 10, 10));
        Label titleLabel = new Label("Dialogue Act Classifier");
        titleLabel.setFont(Font.font("Verdana", 25));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        border.setTop(titleLabel);
        Label bottomLabel = new Label("developed by Antonello Fodde");
        bottomLabel.setPadding(new Insets(20, 0, 0, 0));
        bottomLabel.setFont(Font.font("Verdana", FontPosture.ITALIC, 10));
        border.setBottom(bottomLabel);
        GridPane startGrid = new GridPane();
        border.setCenter(startGrid);
        startGrid.setVgap(5);
        startGrid.setHgap(5);
        crossTextField = new TextField();
        crossTextField.setPrefColumnCount(2);
        crossTextField.setText("5");
        Label crossLabel = new Label("Number k for the k-fold cross-validation:");
        HBox crossBox = new HBox();
        crossBox.getChildren().addAll(crossLabel, crossTextField);
        crossBox.setSpacing(10);
        startButton = new Button("START");
        startButton.setOnAction(this);
        startGrid.getChildren().addAll(crossBox, startButton);
        GridPane.setConstraints(crossBox, 0, 0);
        GridPane.setConstraints(startButton, 1, 0);

        initProcessScene();
        progressGrid.getChildren().add(tokenizationBox);

        stage.setTitle("Dialogue Act Classifier v.01");
        stage.setScene(startScene);
        stage.setResizable(false);
        stage.show();
    }

    private void initProcessScene() {
        progressGrid = new GridPane();
        progressScene = new Scene(progressGrid);
        progressGrid.setPadding(new Insets(50, 50, 50, 50));
        progressGrid.setVgap(5);
        progressGrid.setHgap(5);
        tokenizationBox = new HBox();
        Label tokenizationLabel = new Label("Tokenizing...");
        tokenizationInd = new ProgressIndicator(-1.0f);
        tokenizationInd.setMaxSize(15f, 15f);
        tokenizationBox.setSpacing(10);
        tokenizationBox.getChildren().addAll(tokenizationLabel, tokenizationInd);
        featuresGenerationBox = new HBox();
        Label featuresGenerationLabel = new Label("Generating features...");
        featuresGenerationInd = new ProgressIndicator(-1.0f);
        featuresGenerationInd.setMaxSize(15f, 15f);
        featuresGenerationBox.setSpacing(10);
        featuresGenerationBox.getChildren().addAll(featuresGenerationLabel, featuresGenerationInd);
        posTaggingBox = new HBox();
        Label posTaggingLabel = new Label("Generating postag...");
        posTaggingInd = new ProgressIndicator(-1.0f);
        posTaggingInd.setMaxSize(15f, 15f);
        posTaggingBox.setSpacing(10);
        posTaggingBox.getChildren().addAll(posTaggingLabel, posTaggingInd);
        crfTrainingBox = new HBox();
        Label crfTrainingLabel = new Label("Training crf and performing " + NUMBER_OF_CROSS_VALIDATIONS +
                "-fold cross-validation...");
        crfTrainingInd = new ProgressIndicator(-1.0f);
        crfTrainingInd.setMaxSize(15f, 15f);
        crfTrainingBox.setSpacing(10);
        crfTrainingBox.getChildren().addAll(crfTrainingLabel, crfTrainingInd);
        accuracyBox = new HBox();
        Label accuracyLabel = new Label("Calculating accuracy...");
        accuracyInd = new ProgressIndicator(-1.0f);
        accuracyInd.setMaxSize(15f, 15f);
        accuracyBox.setSpacing(10);
        accuracyBox.getChildren().addAll(accuracyLabel, accuracyInd);
        GridPane.setConstraints(tokenizationBox, 0, 1);
        GridPane.setConstraints(featuresGenerationBox, 0, 2);
        GridPane.setConstraints(posTaggingBox, 0, 3);
        GridPane.setConstraints(crfTrainingBox, 0, 4);
        GridPane.setConstraints(accuracyBox, 0, 5);
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource().equals(startButton)) {
            String input = crossTextField.getText();

            if (input.matches("[1-9]|10")) {
                NUMBER_OF_CROSS_VALIDATIONS = Integer.parseInt(input);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Input not valid");
                alert.setContentText("Please insert a number between 1-10");
                alert.showAndWait();
                return;
            }
            try {
                stage.setScene(progressScene);
                Process tokenization = new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                        "Dataset/tokenizer.praat").inheritIO().start();
                System.out.println("Tokenizing...");
                tokenization.waitFor();
                initProcessScene();
                tokenizationInd.setProgress(1.0f);
                progressGrid.getChildren().addAll(tokenizationBox, featuresGenerationBox);
                stage.setScene(progressScene);

                Process featuresGeneration = new ProcessBuilder("/Applications/Praat.app/Contents/MacOS/Praat",
                        "Dataset/features_generator.praat").inheritIO().start();
                System.out.println("Generating features...");
                featuresGeneration.waitFor();
                initProcessScene();
                tokenizationInd.setProgress(1.0f);
                featuresGenerationInd.setProgress(1.0f);
                progressGrid.getChildren().addAll(tokenizationBox, featuresGenerationBox, posTaggingBox);
                stage.setScene(progressScene);

                Process posTagging = new ProcessBuilder("java", "-mx300m", "-classpath",
                        "stanford-postagger.jar", "edu.stanford.nlp.tagger.maxent.MaxentTagger",
                        "-model", "models/english-left3words-distsim.tagger", "-textFile", "../tokens.txt",
                        "-sentenceDelimiter", "newline")
                        .directory(new File("Dataset/stanfordpostagger"))
                        .inheritIO().redirectOutput(new File("Dataset/tokens-tagged.txt")).start();
                System.out.println("Generating postag...");
                posTagging.waitFor();

                PosMerger.mergePosTag();
                System.out.println("Postag merged");
                initProcessScene();
                tokenizationInd.setProgress(1.0f);
                featuresGenerationInd.setProgress(1.0f);
                posTaggingInd.setProgress(1.0f);
                progressGrid.getChildren().addAll(tokenizationBox, featuresGenerationBox, posTaggingBox,
                        crfTrainingBox);
                stage.setScene(progressScene);

                System.out.println("Training crf and performing " + NUMBER_OF_CROSS_VALIDATIONS + "-fold cross-validation...");
                Process crfTraining = new ProcessBuilder("crfsuite", "learn", "-g" + NUMBER_OF_CROSS_VALIDATIONS,
                        "-x", "-p", "max_iterations=" + NUMBER_OF_ITERATIONS, "features-pos.txt")
                        .directory(new File("Dataset"))
                        .inheritIO().redirectOutput(new File("Dataset/crf-out.txt")).start();
                crfTraining.waitFor();
                initProcessScene();
                tokenizationInd.setProgress(1.0f);
                featuresGenerationInd.setProgress(1.0f);
                posTaggingInd.setProgress(1.0f);
                crfTrainingInd.setProgress(1.0f);
                progressGrid.getChildren().addAll(tokenizationBox, featuresGenerationBox, posTaggingBox,
                        crfTrainingBox, accuracyBox);
                stage.setScene(progressScene);

                System.out.println("Computing accuracy...");
                String accuracy = AccuracyCalculator.writeAccuracyToFile(NUMBER_OF_CROSS_VALIDATIONS,
                        NUMBER_OF_ITERATIONS);
                System.out.println("Done! results in accuracy.txt");
                initProcessScene();
                tokenizationInd.setProgress(1.0f);
                featuresGenerationInd.setProgress(1.0f);
                posTaggingInd.setProgress(1.0f);
                crfTrainingInd.setProgress(1.0f);
                accuracyInd.setProgress(1.0f);
                progressGrid.getChildren().addAll(tokenizationBox, featuresGenerationBox, posTaggingBox,
                        crfTrainingBox, accuracyBox);
                stage.setScene(progressScene);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Results");
                alert.setHeaderText("Computed accuracy:");
                alert.setContentText(accuracy);
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

