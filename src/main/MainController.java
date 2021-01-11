package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {
    @FXML
    public TextField addressInput;
    @FXML
    public TableView<DownloadTask> taskList;
    @FXML
    private void initialize() {
        TableColumn<DownloadTask, String> serverColumn = new TableColumn<>("Origin");
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("server"));
        taskList.getColumns().add(serverColumn);

        TableColumn<DownloadTask, String> nameColumn = new TableColumn<>("Filename");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        taskList.getColumns().add(nameColumn);

        TableColumn<DownloadTask, ProgressBar> progressColumn = new TableColumn<>("Progress");
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progressBar"));
        taskList.getColumns().add(progressColumn);

        TableColumn<DownloadTask, Label> percentageColumn = new TableColumn<>("Progress %");
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        taskList.getColumns().add(percentageColumn);

        TableColumn<DownloadTask, String> sizeColumn = new TableColumn<>("Filesize");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        taskList.getColumns().add(sizeColumn);

        TableColumn<DownloadTask, Label> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        taskList.getColumns().add(statusColumn);

        TableColumn<DownloadTask, Button> buttonColumn = new TableColumn<>("");
        buttonColumn.setCellValueFactory(new PropertyValueFactory<>("abort"));
        taskList.getColumns().add(buttonColumn);
    }
    @FXML
    public void addressSubmit(ActionEvent actionEvent) {
        new Thread(new DownloadRunnable(addressInput.getText(), taskList)).start();
    }
}
