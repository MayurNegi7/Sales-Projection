package com.tesla.controllers;

import com.tesla.App;
import com.tesla.db.Controller;
import com.tesla.ds.MyMap;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddDatasetController extends HandleFields {

    @FXML
    private TextField productType;  // New TextField for product type

    @FXML
    private TextField numOfRecs;  // Assuming this was also added

    @FXML
    private void switchToPrimary() {
        try {
            App.setRoot("fxml/primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add dataset to the database if it is not in the database
    public void submitDataset() {
        MyMap<String, String[]> records1 = getFirstYear();
        MyMap<String, String[]> records2 = getSecondYear();

        String name = nameDataset();
        String productTypeValue = productType.getText();  // Get the product type

        boolean valid = isDatasetValid(records1, records2, numOfRecs);

        if (valid) {
            Controller.addDataset(name, productTypeValue, Integer.parseInt(numOfRecs.getText()), records1, records2);  // Add productType to method
            switchToPrimary();
        }
    }

    // Name dataset according to the inserted date
    private String nameDataset() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "Dataset " + dtf.format(now);
    }
}
