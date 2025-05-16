package com.odev.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private TextField taskInputField;

    @FXML
    private ListView<String> taskListView;

    @FXML
    private void handleAddTask() {
        String task = taskInputField.getText().trim();
        if (!task.isEmpty()) {
            taskListView.getItems().add(task);
            taskInputField.clear();
        }
    }
}

