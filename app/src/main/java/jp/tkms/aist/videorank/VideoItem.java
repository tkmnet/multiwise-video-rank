package jp.tkms.aist.videorank;

import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.effect.ColorAdjust;
import javafx.geometry.Pos;

public class VideoItem {
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Spinner<Integer> orderSpinner;
    private Button pauseResumeButton;
    private String baseName;
    private VBox container;
    private StackPane videoPane;

    public VideoItem(MediaPlayer mediaPlayer, MediaView mediaView, String baseName, int maxOrder) {
        this.mediaPlayer = mediaPlayer;
        this.mediaView = mediaView;
        this.baseName = baseName;
        orderSpinner = new Spinner<>();
        orderSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxOrder, 0));
        orderSpinner.setStyle("-fx-font-size: 3em;");
        pauseResumeButton = new Button("||");
        pauseResumeButton.setPrefWidth(80);
        pauseResumeButton.setStyle("-fx-font-size: 3em;");
        pauseResumeButton.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                pauseResumeButton.setText(">");
                ColorAdjust grayscale = new ColorAdjust();
                grayscale.setSaturation(-1);
                mediaView.setEffect(grayscale);
            } else {
                mediaPlayer.play();
                pauseResumeButton.setText("||");
                mediaView.setEffect(null);
            }
        });
        videoPane = new StackPane();
        videoPane.getChildren().add(mediaView);
        mediaView.fitWidthProperty().bind(videoPane.widthProperty());
        HBox controlsBox = new HBox(5, orderSpinner, pauseResumeButton);
        controlsBox.setAlignment(Pos.CENTER);
        container = new VBox(5, videoPane, controlsBox);
        HBox.setHgrow(container, Priority.ALWAYS);
    }

    public void simulateClick(int order) {
        if (orderSpinner.getValue() == 0) {
            orderSpinner.getValueFactory().setValue(order);
        }
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setSaturation(-1);
            mediaView.setEffect(grayscale);
            pauseResumeButton.setText(">");
        }
    }

    public Spinner<Integer> getOrderSpinner() { return orderSpinner; }
    public String getBaseName() { return baseName; }
    public VBox getContainer() { return container; }
    public StackPane getVideoPane() { return videoPane; }
}

