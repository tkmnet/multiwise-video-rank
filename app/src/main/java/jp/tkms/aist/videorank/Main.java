package jp.tkms.aist.videorank;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.effect.ColorAdjust;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Pos;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;

public class Main extends Application {
    private int currentIndex = 0;
    private final String baseDir = "./target";
    private List<MediaPlayer> currentMediaPlayers = new ArrayList<>();
    private int clickOrderCounter = 1;
    private List<VideoItem> currentVideoItems = new ArrayList<>();
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        File resultsFile = new File("results.csv");
        if (resultsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(resultsFile))) {
                String line, lastLine = null;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) lastLine = line;
                }
                if (lastLine != null) {
                    String[] tokens = lastLine.split(",");
                    int lastFolder = Integer.parseInt(tokens[0].trim());
                    currentIndex = lastFolder + 1;
                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }

        BorderPane root = new BorderPane();
        HBox videoBox = new HBox(10);
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-font-size: 3em;");
        bottomBox.getChildren().add(nextButton);

        loadVideos(videoBox, currentIndex);

        nextButton.setOnAction(e -> {
            if (!validateSpinners()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Invalid ordering");
                alert.setContentText("Please ensure each video has a unique order between 1 and " + currentVideoItems.size() + ".");
                alert.showAndWait();
                return;
            }
            writeResultsToCSV();
            for (MediaPlayer mp : currentMediaPlayers) mp.stop();
            currentMediaPlayers.clear();
            currentIndex++;
            loadVideos(videoBox, currentIndex);
        });

        Scene scene = new Scene(root, 1800, 800);
        root.setCenter(videoBox);
        root.setBottom(bottomBox);

        scene.setOnKeyPressed(e -> {
            if (e.getCode().isDigitKey()) {
                try {
                    int digit = Integer.parseInt(e.getText());
                    if (digit >= 1 && digit <= currentVideoItems.size())
                        currentVideoItems.get(digit - 1).simulateClick(clickOrderCounter++);
                } catch (NumberFormatException ex) { }
            } else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.N) {
                nextButton.fire();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Video Rank - Folder: " + (currentIndex + 1) + " / " + getFolderCount());
        primaryStage.show();
    }

    private boolean validateSpinners() {
        int num = currentVideoItems.size();
        Set<Integer> orders = new HashSet<>();
        for (VideoItem item : currentVideoItems) {
            int val = item.getOrderSpinner().getValue();
            if (val < 1 || val > num || !orders.add(val)) return false;
        }
        for (int i = 1; i <= num; i++) if (!orders.contains(i)) return false;
        return true;
    }

    private void writeResultsToCSV() {
        List<VideoItem> sortedItems = new ArrayList<>(currentVideoItems);
        sortedItems.sort(Comparator.comparingInt(item -> item.getOrderSpinner().getValue()));
        StringBuilder sb = new StringBuilder();
        sb.append(currentIndex);
        for (VideoItem item : sortedItems) sb.append(",").append(item.getBaseName());
        sb.append("\n");
        try (FileWriter fw = new FileWriter("results.csv", true)) {
            fw.write(sb.toString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadVideos(HBox videoBox, int index) {
        videoBox.getChildren().clear();
        currentVideoItems.clear();
        clickOrderCounter = 1;
        int totalCount = getFolderCount();
        if (totalCount > 0 && index >= totalCount) {
            Alert finishedAlert = new Alert(AlertType.INFORMATION);
            finishedAlert.setTitle("Finished");
            finishedAlert.setContentText("All folders are finished. The application will now exit.");
            finishedAlert.showAndWait();
            Platform.exit();
            return;
        }
        primaryStage.setTitle("Video Rank - Folder: " + (index + 1) + " / " + totalCount);
        String folderPath = baseDir + "/" + index;
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) return;
        File[] videoFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) { return name.toLowerCase().endsWith(".mp4"); }
        });
        if (videoFiles == null || videoFiles.length == 0) return;
        Arrays.sort(videoFiles);
        int videosToShow = Math.min(videoFiles.length, 8);
        for (int i = 0; i < videosToShow; i++) {
            File videoFile = videoFiles[i];
            String mediaUrl = videoFile.toURI().toString();
            Media media = new Media(mediaUrl);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaPlayer.play();
            currentMediaPlayers.add(mediaPlayer);
            String fileName = videoFile.getName();
            String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
            VideoItem item = new VideoItem(mediaPlayer, mediaView, baseName, videosToShow);
            item.getVideoPane().setOnMouseClicked(e -> item.simulateClick(clickOrderCounter++));
            videoBox.getChildren().add(item.getContainer());
            currentVideoItems.add(item);
        }
    }

    private int getFolderCount() {
        File base = new File(baseDir);
        File[] directories = base.listFiles(File::isDirectory);
        return directories != null ? directories.length : 0;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

