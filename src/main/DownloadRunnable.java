package main;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class DownloadRunnable implements Runnable {
    private final String address;
    private final TableView<DownloadTask> tableView;
    private boolean terminate;

    public DownloadRunnable(String address, TableView<DownloadTask> table) {
        this.address = address;
        tableView = table;
        terminate = false;
    }

    @Override
    public void run() {
        byte [] buffer = new byte[1024];
        int dataSize;
        InputStream inputStream;
        FileOutputStream fileOutputStream = null;
        Socket socket;
        String filename;

        try {
            socket = new Socket(address, 10101);
            inputStream = socket.getInputStream();
            dataSize = inputStream.read(buffer);
        } catch (IOException e) {
            errorAlert(e, "Connection error", "Critical error while connecting to server");
            return;
        }
        String[] data2 = new String(buffer, 0, dataSize).split(" ");
        filename = data2[0];
        AtomicReference<File> file = new AtomicReference<>();
        DownloadTask downloadTask = new DownloadTask(filename, address, Integer.parseInt(data2[1]));
        downloadTask.getAbort().setOnAction((event) -> {
            this.terminate = true;
        });
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(filename);
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            file.set(fileChooser.showSaveDialog(null));
            tableView.getItems().add(downloadTask);
            synchronized (file) {
                file.notify();
            }
        });
        synchronized (file) {
            try {
                file.wait();
            } catch (InterruptedException e) {
                errorAlert(e, "Interruption error", "Something interrupted getting output file location");
                Platform.runLater(() -> {
                    tableView.getItems().remove(downloadTask);
                });
                return;
            }
        }
        if(file.get() == null) {
            Platform.runLater(() -> {
                tableView.getItems().remove(downloadTask);
            });
            return;
        }
        try {
            fileOutputStream = new FileOutputStream(file.get());
            dataSize = inputStream.read(buffer);
            while(dataSize > 0) {
                fileOutputStream.write(buffer, 0, dataSize);
                downloadTask.setDownloaded(dataSize);
                if(terminate) {
                    Platform.runLater(() -> {
                        tableView.getItems().remove(downloadTask);
                    });
                    break;
                }
                dataSize = inputStream.read(buffer);
            }
        } catch (IOException e) {
            errorAlert(e, "Downloading error", "Error while downloading file");
        }
        try {
            fileOutputStream.close();
            socket.close();
        } catch (IOException | NullPointerException e) {
            errorAlert(e, "Closing error", "Error while closing streams");
        }
        downloadTask.finish();
        if(!downloadTask.completed()) {
            if(!file.get().delete()) {
                errorAlert(new Exception("File cannot be deleted, sir."), "File not deleted", "Cannot delete partially downloaded file: " + file.get().getName());
            }
        }
    }

    private void errorAlert(Exception e, String title, String header) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }
}
