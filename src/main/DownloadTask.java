package main;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class DownloadTask {
    private double lastUpdated;
    private int downloaded;
    private final int filesize;
    private final String name, server, size;
    private final ProgressBar progressBar;
    private final Button abort;
    private final Label percentage, status;

    public DownloadTask(String filename, String serverName, int fileSize) {
        name = filename;
        server = serverName;

        abort = new Button();
        abort.setText("Cancel");

        progressBar = new ProgressBar();
        progressBar.setProgress(0.0);

        filesize = fileSize;

        String letters = "KMGTP";
        char letter = ' ';
        for(int i = 0; fileSize >= 1000.0; ++i, fileSize /= 1024.0) {
            letter = letters.charAt(i);
        }
        size = Float.toString(((float) ((int) (fileSize * 10))) / 10) + ' ' + letter + 'B';

        downloaded = 0;

        percentage = new Label("0.0%");
        status = new Label("Downloading");
    }

    public Button getAbort() {
        return abort;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public String getSize() {
        return size;
    }

    public void setDownloaded(double downloaded) {
        this.downloaded += downloaded;
        final double fraction = ((double) this.downloaded) / ((double) filesize);
        if (lastUpdated + 0.001 < fraction) {
            Platform.runLater(() -> {
                progressBar.setProgress(fraction);
                percentage.setText(Float.toString(((float) ((int) (fraction * 1000))) / 10) + '%');
            });
            lastUpdated = fraction;
        }
    }

    public Label getPercentage() {
        return percentage;
    }

    public boolean completed() {
        return filesize == downloaded;
    }

    public Label getStatus() {
        return status;
    }

    public void finish() {
        if(completed()) {
            Platform.runLater(() -> {
                status.setText("Completed");
                progressBar.setProgress(1.0);
                progressBar.lookup(".bar").setStyle("-fx-background-color: -fx-box-border, " + "rgb(43,191,43)");
                percentage.setText("100%");
            });
        } else {
            Platform.runLater(() -> {
                status.setText("Failed");
                progressBar.lookup(".bar").setStyle("-fx-background-color: -fx-box-border, " + "rgb(157,14,14)");
            });
        }
        abort.setDisable(true);
    }
}
