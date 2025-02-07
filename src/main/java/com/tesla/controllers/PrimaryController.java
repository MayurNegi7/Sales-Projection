package com.tesla.controllers;

import com.tesla.App;
import com.tesla.components.AlertBox;
import com.tesla.db.Controller;
import com.tesla.ds.MyArrayList;
import com.tesla.forecast.DeseasonalizedRegressionAnalysis;
import com.tesla.forecast.DoubleExponentialSmoothing;
import com.tesla.forecast.ExponentialSmoothing;
import com.tesla.forecast.RegressionAnalysis;
import com.tesla.models.Dataset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    @FXML
    private TableView<Dataset> tableView;
    @FXML
    private TableColumn<Dataset, String> datasets;
    @FXML
    private TableColumn<Dataset, String> productType;
    @FXML
    private TableColumn<Dataset, Integer> minSalesCount;
    @FXML
    private TableColumn<Dataset, Integer> maxSalesCount;
    @FXML
    private TableColumn<Dataset, Integer> numOfRecsForEachMonth;

    ObservableList<Dataset> observableList = FXCollections.observableArrayList();
    
     
    @FXML
    private void switchToAddDatasetScene() {
        try {
            App.setRoot("fxml/add-dataset");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteSelectedDataset() {
        ObservableList<Dataset> selected = tableView.getSelectionModel().getSelectedItems();
        if (selected.size() == 0) {
            AlertBox.displayAlert("Invalid Choice", "Please select a dataset.");
        } else if (selected.size() > 1) {
            AlertBox.displayAlert("Invalid Choice", "Please select only one dataset.");
        } else {
            // delete selected dataset from database and ui
            ViewUpdateController.dataset = selected.get(0);
            selected.forEach(dataset -> {
                Document document = new Document("Name", dataset.getName());
                Controller.deleteDataset(document);
            });
            reload();
        }
    }

  
    @FXML
private void initializeForecastProcess() {
    ObservableList<Dataset> selected = tableView.getSelectionModel().getSelectedItems();
    if (selected.size() == 0) {
        AlertBox.displayAlert("Invalid Choice", "Please select a dataset.");
    } else if (selected.size() > 1) {
        AlertBox.displayAlert("Invalid Choice", "Please select only one dataset.");
    } else {
        System.out.println("Selected records sent to forecast classes");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        Dataset selectedDataset = selected.get(0);
        System.out.println("Original Dataset:             " + selectedDataset.getListOfRecords().toString());

        // Print product type value
        System.out.println("Product Type: " + selectedDataset.getProductType()); // Added line

        // calculate forecast records of each forecast method
        ExponentialSmoothing.initializeForecast(selectedDataset.getNumOfRecordsForEachMonth(), selectedDataset);
        DoubleExponentialSmoothing.initializeForecast(selectedDataset.getNumOfRecordsForEachMonth(), selectedDataset);
        RegressionAnalysis.initializeForecast(selectedDataset.getNumOfRecordsForEachMonth(), selectedDataset);
        DeseasonalizedRegressionAnalysis.initializeForecast(selectedDataset.getNumOfRecordsForEachMonth(), selectedDataset);
        BirdsEyeForecastsController.setDataset(selectedDataset);

        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("MSE of Exponential Smoothing       : " + ExponentialSmoothing.getMSE());
        System.out.println("MSE of Double Exponential Smoothing: " + DoubleExponentialSmoothing.getMSE());
        System.out.println("MSE of Regression Analysis:          " + RegressionAnalysis.getMSE());
        System.out.println("MSE of Deseasonalized Reg. Analysis: " + DeseasonalizedRegressionAnalysis.getMSE());
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        ViewForecastedDataset.numOfRecs = selectedDataset.getNumOfRecordsForEachMonth();
        ViewForecastedDataset.datasetNameTxt = selectedDataset.getName();

        try {
            App.setRoot("fxml/birds-eye-forecast");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    // refresh the page
    private void reload() {
        try {
            App.setRoot("fxml/primary");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
public void initialize(URL url, ResourceBundle resourceBundle) {
    // display every dataset stored in database
    MyArrayList<Document> listOfDocuments = Controller.getAllDatasets();
    for (Document doc : listOfDocuments) {
        MyArrayList<String> _jan1 = new MyArrayList<>(), _feb1 = new MyArrayList<>(), _mar1 = new MyArrayList<>(), _apr1 = new MyArrayList<>(), _may1 = new MyArrayList<>(), _jun1 = new MyArrayList<>(), _jul1 = new MyArrayList<>(), _aug1 = new MyArrayList<>(), _sep1 = new MyArrayList<>(), _oct1 = new MyArrayList<>(), _nov1 = new MyArrayList<>(), _dec1 = new MyArrayList<>(), _jan2 = new MyArrayList<>(), _feb2 = new MyArrayList<>(), _mar2 = new MyArrayList<>(), _apr2 = new MyArrayList<>(), _may2 = new MyArrayList<>(), _jun2 = new MyArrayList<>(), _jul2 = new MyArrayList<>(), _aug2 = new MyArrayList<>(), _sep2 = new MyArrayList<>(), _oct2 = new MyArrayList<>(), _nov2 = new MyArrayList<>(), _dec2 = new MyArrayList<>();
        Document firstYear = (Document) doc.get("First Year");
        Document secondYear = (Document) doc.get("Second Year");

        manuelCastingToMyArrayList(firstYear, _jan1, _feb1, _mar1, _apr1, _may1, _jun1, _jul1, _aug1, _sep1, _oct1, _nov1, _dec1);
        manuelCastingToMyArrayList(secondYear, _jan2, _feb2, _mar2, _apr2, _may2, _jun2, _jul2, _aug2, _sep2, _oct2, _nov2, _dec2);

        // Fetch the product type from the document
        String productType = doc.getString("Product Type");

        // Check if productType is null or empty
        if (productType == null || productType.isEmpty()) {
            System.out.println("Product Type is null or empty for document: " + doc.toJson());
        } else {
            System.out.println("Product Type: " + productType);
        }

        observableList.add(new Dataset(
            doc.getString("Name"), 
            productType,  // Use the product type with the check in place
            doc.getInteger("Number Of Records For Each Month"), 
            _jan1, _feb1, _mar1, _apr1, _may1, _jun1, _jul1, _aug1, _sep1, _oct1, _nov1, _dec1, 
            _jan2, _feb2, _mar2, _apr2, _may2, _jun2, _jul2, _aug2, _sep2, _oct2, _nov2, _dec2
        ));
    }

    datasets.setCellValueFactory(new PropertyValueFactory<>("name"));
    productType.setCellValueFactory(new PropertyValueFactory<>("productType")); // Add this line
    minSalesCount.setCellValueFactory(new PropertyValueFactory<>("minSalesCount"));
    maxSalesCount.setCellValueFactory(new PropertyValueFactory<>("maxSalesCount"));
    numOfRecsForEachMonth.setCellValueFactory(new PropertyValueFactory<>("numOfRecordsForEachMonth"));
    tableView.setItems(observableList);
}

    @SuppressWarnings("unchecked")
    private void manuelCastingToMyArrayList(Document secondYear, MyArrayList<String> _jan1, MyArrayList<String> _feb1, MyArrayList<String> _mar1, MyArrayList<String> _apr1, MyArrayList<String> _may1, MyArrayList<String> _jun1, MyArrayList<String> _jul1, MyArrayList<String> _aug1, MyArrayList<String> _sep1, MyArrayList<String> _oct1, MyArrayList<String> _nov1, MyArrayList<String> _dec1) {
        List<String> jan = (List<String>) secondYear.get("January");
        jan.forEach(_jan1::add);
        List<String> feb = (List<String>) secondYear.get("February");
        feb.forEach(_feb1::add);
        List<String> mar = (List<String>) secondYear.get("March");
        mar.forEach(_mar1::add);
        List<String> apr = (List<String>) secondYear.get("April");
        apr.forEach(_apr1::add);
        List<String> may = (List<String>) secondYear.get("May");
        may.forEach(_may1::add);
        List<String> jun = (List<String>) secondYear.get("Jun");
        jun.forEach(_jun1::add);
        List<String> jul = (List<String>) secondYear.get("July");
        jul.forEach(_jul1::add);
        List<String> aug = (List<String>) secondYear.get("August");
        aug.forEach(_aug1::add);
        List<String> sep = (List<String>) secondYear.get("September");
        sep.forEach(_sep1::add);
        List<String> oct = (List<String>) secondYear.get("October");
        oct.forEach(_oct1::add);
        List<String> nov = (List<String>) secondYear.get("November");
        nov.forEach(_nov1::add);
        List<String> dec = (List<String>) secondYear.get("December");
        dec.forEach(_dec1::add);
    }

    @FXML
    public void switchToUpdate() {
        ObservableList<Dataset> selected = tableView.getSelectionModel().getSelectedItems();
        if (selected.size() == 0) {
            AlertBox.displayAlert("Invalid Choice", "Please select a dataset.");
        } else if (selected.size() > 1) {
            AlertBox.displayAlert("Invalid Choice", "Please select only one dataset.");
        } else {
            ViewUpdateController.dataset = selected.get(0);

            try {
                App.setRoot("fxml/view-update");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
