/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import utility.ExceptionUI;

/**
 *
 * @author Adithya
 */
public class MainStageController implements Initializable {

    //FXML Properties Start
    @FXML
    private Button confgbt, connectbt, fileconfgbt;
    @FXML
    private AnchorPane root_anchr_pane, workspace;
    @FXML
    private Label datetime, feedFilename, refreshrate, feedstatus;
    @FXML
    private VBox mainvbox;
    //FXML Properties Stop

    //Timeline Variable Start
    private Timeline dateTimeDisplay; //Date & Time
    //Timeline Variable Stop 

    private CurrentStatusController csc;
    private final String propfilename = "fact_common.properties";
    private ObservableMap feedPropData;
    private boolean feedStatus;
    private PropertyFileWrapper pfw;
    private StatusProperties statusProperties;
    private Timeline statusFetch;

    public void getGlobalInstance() {
        feedPropData = FXCollections.observableHashMap();
        connectbt.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            getGlobalInstance();
            pfw = new PropertyFileWrapper("resources/" + propfilename);
            //Displaying Basic Info
            showBasicCommonInfo(pfw);
            //showing curretn status stage
            showCurrentStatusStage();

        } catch (IOException ex) {
            Logger.getLogger(MainStageController.class.getName()).log(Level.SEVERE, null, ex);
            new ExceptionUI(ex);
        }

    }

    @FXML
    public void exitApplication(ActionEvent event) {
        try {

        } catch (Exception e) {
        }
        Platform.exit();
    }

    public void sessionClose() {
        try {

        } catch (Exception e) {
        }
        System.exit(0);
    }

    /**
     * Current Stage View Method*
     */
    public void showCurrentStatusStage() throws IOException {
        System.out.println("Loading : showCurrentStatusStage");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("CurrentStatus.fxml"));
        SplitPane cmdPane = (SplitPane) fxmlLoader.load();
        csc = (CurrentStatusController) fxmlLoader.getController();

        cmdPane.prefWidthProperty().bind(workspace.widthProperty());
        cmdPane.prefHeightProperty().bind(workspace.heightProperty());
        workspace.getChildren().add(cmdPane);
    }

    /**
     * Display Basic Common Info
     */
    public void showBasicCommonInfo(PropertyFileWrapper propFile) throws FileNotFoundException, IOException {

        dateTimeDisplay = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                TimeZone tz = TimeZone.getTimeZone(propFile.getTimeZone());
                DateFormat dateFormat = new SimpleDateFormat(propFile.getTimeFormat());

                dateFormat.setTimeZone(tz);
                datetime.setText("Date & Time : " + dateFormat.format(new Date()));
            }
        }));
        dateTimeDisplay.setCycleCount(Timeline.INDEFINITE);
        dateTimeDisplay.play();

        refreshrate.setText(propFile.getFetchTime() + " sec");

        if (!propFile.getStatusPath().isEmpty()) {
            statusProperties = new StatusProperties(propFile.getStatusPath());
            feedFilename.setText(statusProperties.getFeedName());
            feedstatus.setText(statusProperties.getStatus());
            connectbt.setDisable(false);
        }
    }

    /*
    * Setting the Default UI Values
     */
    public void setDefaultUIValues() {
        feedFilename.setText("Test");

        refreshrate.setText("1 Sec");
        connectbt.setDisable(true);
    }

    /*
    Loading the Connection File to UI
     */
    @FXML
    public void loadStatusFiles(ActionEvent ae) throws IOException {
        GetConfigFiles configFiles = new GetConfigFiles(mainvbox, pfw.getStatusPath());

        System.out.println(configFiles.getStatusFile());
        String filePath = configFiles.getStatusFile();
        if (filePath != null) {
            if (statusProperties != null) {
                statusProperties = new StatusProperties(filePath);
            }
            feedFilename.setText(statusProperties.getFeedName());
            feedstatus.setText(statusProperties.getStatus());
            connectbt.setDisable(false);
        }
    }

    /*
    Configuration of Connection Files
     */
    @FXML
    public void confgPropFile(ActionEvent ae) throws IOException {
        ConfigFile cf = new ConfigFile(mainvbox, pfw);
        if (!pfw.getStatusPath().isEmpty()) {
            statusProperties = new StatusProperties(pfw.getStatusPath());
            feedFilename.setText(statusProperties.getFeedName());
            feedstatus.setText(statusProperties.getStatus());
            refreshrate.setText(pfw.getFetchTime());
        }

    }

    @FXML
    public void statusProcess(ActionEvent ae) {
        try {
            if (!feedStatus) {
                StatusProperties sp = new StatusProperties(pfw.getStatusPath());
                feedStatus = true;
                if (sp.getDiffFile() != null && !sp.getDiffFile().isEmpty() && Files.exists(Paths.get(sp.getDiffFile()), LinkOption.NOFOLLOW_LINKS)) {
                    csc.processData(sp);
                }
                buttonUIIndicator(connectbt, "/icon/connect.png", "/icon/disconnect50x50.png", "glow");
                confgbt.setDisable(true);
//                fileconfgbt.setDisable(true);
                statusFetch = new Timeline(new KeyFrame(Duration.seconds(Integer.valueOf(pfw.getFetchTime())), new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            StatusProperties sp1 = new StatusProperties(pfw.getStatusPath());
                            System.out.println("This is called every " + pfw.getFetchTime() + " seconds on UI thread");
                            feedstatus.setText(sp1.getStatus());

                            if (statusProperties.getDiffFile() != null && !statusProperties.getDiffFile().isEmpty() && Files.exists(Paths.get(sp.getDiffFile()), LinkOption.NOFOLLOW_LINKS)) {
                                csc.processData(sp1);
                            } else {
                                System.out.println("New Diff File not created");
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(MainStageController.class.getName()).log(Level.SEVERE, null, ex);
                            new ExceptionUI(ex);
                        }
                    }
                }));

                statusFetch.setCycleCount(Timeline.INDEFINITE);
                statusFetch.play();

            } else {
                feedStatus = false;
//                csc.stopFetch();
                statusFetch.stop();
                confgbt.setDisable(false);
//                fileconfgbt.setDisable(false);
                buttonUIIndicator(connectbt, "/icon/connect.png", "/icon/disconnect50x50.png", "off");
            }
        } catch (Exception ex) {
            Logger.getLogger(MainStageController.class.getName()).log(Level.SEVERE, null, ex);
            new ExceptionUI(ex);
        }
    }

    public void buttonUIIndicator(Button bt, String imagepath1, String imagepath2, String scpstatus) {

        switch (scpstatus) {

            case "off":
                System.out.println(scpstatus);

                bt.setStyle("-fx-background-color: \n"
                        + "        #707070,\n"
                        + "        linear-gradient(#fcfcfc, #f3f3f3),\n"
                        + "        linear-gradient(#f2f2f2 0%, #ebebeb 49%, #dddddd 50%, #cfcfcf 100%);"
                        + "-fx-background-image: url(\"" + imagepath2 + "\");");
                break;

            case "on":
                System.out.println(scpstatus);
                bt.setStyle("-fx-background-color: \n"
                        + "        linear-gradient(#ffd65b, #e68400),\n"
                        + "        linear-gradient(#ffef84, #f2ba44),\n"
                        + "        linear-gradient(#ffea6a, #efaa22),\n"
                        + "        linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),\n"
                        + "        linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));"
                        + "-fx-background-image: url(\"" + imagepath1 + "\");");
                break;
            case "glow":

                bt.setStyle("-fx-background-color: \n"
                        + "        linear-gradient(#ffd65b, #e68400),\n"
                        + "        linear-gradient(#ffef84, #f2ba44),\n"
                        + "        linear-gradient(#ffea6a, #efaa22),\n"
                        + "        linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),\n"
                        + "        linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));"
                        + "-fx-background-image: url(\"" + imagepath1 + "\");");
                break;
        }

    }

    public void loadStatus(String statusFile) {
        if (pfw.getStatusPath() == null || pfw.getStatusPath().isEmpty() || pfw.getStatusPath().equals("")) {

        }
    }
}
