/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import com.jfoenix.controls.JFXComboBox;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import utility.ExceptionUI;

/**
 *
 * @author Adithya
 */
public class GetConfigFiles {

    private Dialog<Pair<String, String>> dialog;
    private ButtonType loginButtonType;
    private JFXComboBox connFiles;
    ObservableMap<String, File> connLoad = FXCollections.observableHashMap();
    ObservableMap<String, String> dataToUI = FXCollections.observableHashMap();
    String statusFiletoUi;
    int colIdx = 0, rowIdx = 0;
    private VBox mainvbox;
    private Map<String, File> statusList;

    public GetConfigFiles(VBox mainvbox, String statusPath) {
        try {
            this.mainvbox = mainvbox;
            if (checkStatusPath(statusPath)) {

                dialog = new Dialog<>();

                dialog.setTitle("Feed Status File Connection");
                dialog.setHeaderText("Feed Status File Connection");

                // Get the Stage.
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

                // Add a custom icon.
                stage.getIcons().add(new Image(this.getClass().getResource("/icon/filesicon.png").toString()));

                // Set the icon (must be included in the project).
                dialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icon/filesicon.png"))));

                // Set the button types.
                loginButtonType = new ButtonType("Load", ButtonBar.ButtonData.OK_DONE);

                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
                // Create the username and password labels and fields.
                GridPane grid = new GridPane();
                grid.setHgap(5);
                grid.setVgap(5);
                grid.setPadding(new Insets(20, 10, 10, 10));

                connFiles = new JFXComboBox();
                connFiles.setPromptText("Select a Feed Status");

                connFiles.setItems(getStatusFiles(statusPath));

                dialog.getDialogPane().setContent(grid);

                grid.add(new Label("Select Feed"), colIdx, ++rowIdx);
                grid.add(connFiles, colIdx + 1, rowIdx, 2, 1);
                Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                Node cancelButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

                loginButton.setDisable(true);

                dialog.setResultConverter(dialogButton
                        -> {
                    if (dialogButton == loginButtonType) {
                        return new Pair<>("", "");
//                return new Pair<>("", "");
                    }

                    return null;
                }
                );

                connFiles.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (connFiles.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("Select a Feed")) {
                        loginButton.setDisable(true);
                    } else {
                        loginButton.setDisable(false);
                        cancelButton.setDisable(false);
                    }
                });

                Optional<Pair<String, String>> result = dialog.showAndWait();

                result.ifPresent(dbconstring
                        -> {

                    System.out.println("Clicked - Load Button");

                    for (Map.Entry<String, File> entry : statusList.entrySet()) {
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        dataToUI.put(key.toString(), value.toString());

                        if (entry.getKey().equalsIgnoreCase(connFiles.getSelectionModel().getSelectedItem().toString())) {
                            statusFiletoUi = entry.getValue().toString();
                        }
                    }

                }
                );
            } else {
                System.out.println("Status File Path Not exists");
            }
        } catch (Exception ex) {
            Logger.getLogger(GetConfigFiles.class.getName()).log(Level.SEVERE, null, ex);
            new ExceptionUI(ex);
        }

    }

    public ObservableList getStatusFiles(String statusPath) throws IOException {

        File statusFiles = new File(statusPath);
        File[] statusFilesList = statusFiles.listFiles();
        ObservableList statusFilesName = FXCollections.observableArrayList();
        statusList = new HashMap<String, File>();
        FileInputStream fis = null;
        for (File file : statusFilesList) {
            String fileName = file.getName();
            System.out.println("File Path: " + file);
            Properties properties = new Properties();
            fis = new FileInputStream(file);
            properties.load(fis);

            statusFilesName.add(properties.getProperty("feedName"));
            statusList.put(properties.getProperty("feedName"), file);

        }
        System.out.println("List: " + statusFilesName);
        System.out.println("Map: " + statusList);
        fis.close();
        return statusFilesName;
    }

    public String getStatusFile() {

        return statusFiletoUi;
    }

    private boolean checkStatusPath(String statusPath) {
        return Files.exists(Paths.get(statusPath), LinkOption.NOFOLLOW_LINKS);
    }
}
