/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import engine.LoadExcel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javafx.util.Duration;
import jxl.read.biff.BiffException;
import utility.ExceptionUI;

/**
 * FXML Controller class
 *
 * @author Adithya
 */
public class CurrentStatusController implements Initializable {

    @FXML
    private Label src_cnt_lbl, trg_cnt_lbl, src_unm_cnt_lbl, trg_unm_cnt_lbl;

    @FXML
    private TableView src_tol_tbl_view, trg_tol_tbl_view, src_unm_tbl_view, trg_unm_tbl_view;

    private FeedConnProperties connProperties;

    private TableColumn tabPane_tbl_columns;
    public Timeline statusFetch;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    public void processData(StatusProperties statusProperties) {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws SQLException, IOException, FileNotFoundException, ClassNotFoundException, BiffException {
//                StatusProperties statusProperties = new StatusProperties(statusPropertiesFile);
                System.out.println("Pro: " + statusProperties);
                System.out.println("Excel File: " + statusProperties.getDiffFile());
                LoadExcel loadExcel = new LoadExcel(statusProperties.getDiffFile());

                //non ui code
                ObservableList srcData = loadExcel.getExcelData(statusProperties.getNewModTime());
                ObservableList trgData = loadExcel.getExcelData(statusProperties.getOldModTime());

                Platform.runLater(new Runnable() {
                    public void run() {
                        //ui code
                        src_cnt_lbl.setText(statusProperties.getNewCount());
                        trg_cnt_lbl.setText(statusProperties.getOldCount());
                        src_unm_cnt_lbl.setText(statusProperties.getNewDiffCount());
                        trg_unm_cnt_lbl.setText(statusProperties.getOldDiffCount());
                        clearTableData();
                        setTableCol(src_unm_tbl_view, loadExcel.getColName(statusProperties.getNewModTime()));
                        setTableCol(trg_unm_tbl_view, loadExcel.getColName(statusProperties.getOldModTime()));
                        src_unm_tbl_view.getItems().addAll(srcData);
                        trg_unm_tbl_view.getItems().addAll(trgData);
                    }
                });

                return null;
            }

        };

        task.setOnRunning(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

            }
        });

        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

            }
        });

        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent arg0) {

                Throwable throwable = task.getException();
                Logger.getLogger(MainStageController.class.getName()).log(Level.SEVERE, null, throwable);
                new ExceptionUI(new Exception(throwable.getMessage()));
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }


    /*
    Setting the Column Names for the Table
     */
    private void setTableCol(TableView tableView, List tableColumns) {
        System.out.println("setTableView method Called");
        if (!tableColumns.isEmpty()) {

            for (Object colname : tableColumns) {
                tabPane_tbl_columns = new TableColumn(colname.toString());
                final int j = tableColumns.indexOf(colname);
                tabPane_tbl_columns.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {

                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableView.getColumns().add(tabPane_tbl_columns);
            }

        } else {
            System.out.println("(Error) No Columns Data found");
        }
    }

    private void clearTableData() {
//        src_tol_tbl_view.getItems().clear();
//        trg_tol_tbl_view.getItems().clear();
        src_unm_tbl_view.getColumns().clear();
        trg_unm_tbl_view.getColumns().clear();
    }

    public void clearAllData() {
        src_cnt_lbl.setText("0");
        trg_cnt_lbl.setText("0");
        src_unm_cnt_lbl.setText("0");
        trg_unm_cnt_lbl.setText("0");
        if (src_tol_tbl_view != null) {
            src_tol_tbl_view.getItems().clear();
            src_tol_tbl_view.getColumns().clear();
        }
        if (trg_tol_tbl_view != null) {
            trg_tol_tbl_view.getItems().clear();
            trg_tol_tbl_view.getColumns().clear();
        }
        if (src_unm_tbl_view != null) {
            src_unm_tbl_view.getItems().clear();
            src_unm_tbl_view.getColumns().clear();
        }
        if (trg_unm_tbl_view != null) {
            trg_unm_tbl_view.getItems().clear();
            trg_unm_tbl_view.getColumns().clear();
        }
    }

    private void setTableData(List columnName) throws IOException {

//        setTableCol(src_tol_tbl_view, columnName);
//        setTableCol(trg_tol_tbl_view, columnName);
        setTableCol(src_unm_tbl_view, columnName);
        setTableCol(trg_unm_tbl_view, columnName);

    }

//    public void stopFetch() {
//        statusFetch.stop();
//    }
}
