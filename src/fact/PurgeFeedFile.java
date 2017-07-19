/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import utility.NotifyUI;

/**
 *
 * @author Admin
 */
public class PurgeFeedFile {

    private VBox mainVBox;
    private final Dialog<Pair<String, String>> dialog;
    private final ButtonType loginButtonType;
    private JFXCheckBox connFilesCheck;
    private int colIdx = 0;
    private int rowIdx = 0;
    private Map<JFXCheckBox, File> loadDeleteConn = new HashMap<>();

    public PurgeFeedFile(VBox mainVBox, Map<String, File> connFiles) {
        dialog = new Dialog<>();

        dialog.setTitle("Purge Feed Connection");
        dialog.setHeaderText("Purge the Feed  Connection");

        // Get the Stage.
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(this.getClass().getResource("/icon/filesicon.png").toString()));

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icon/filesicon.png"))));

        // Set the button types.
        loginButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 10, 10, 10));
        dialog.getDialogPane().setContent(grid);
        for (Map.Entry<String, File> entry : connFiles.entrySet()) {
            String key = entry.getKey();
            File value = entry.getValue();

            connFilesCheck = new JFXCheckBox(key);
            grid.add(connFilesCheck, colIdx, rowIdx, 2, 1);
            ++rowIdx;
            loadDeleteConn.put(connFilesCheck, value);
        }

        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);

//        loginButton.setDisable(true);
        dialog.setResultConverter(dialogButton
                -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>("", "");

            }

            return null;
        }
        );

//        connFilesCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (connFilesCheck.isSelected()) {
//                loginButton.setDisable(false);
//            } else {
//                loginButton.setDisable(true);
//            }
//        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(dbconstring
                -> {
            System.out.println("Clicked - Add Button");
            StringBuffer deleteNotify = new StringBuffer();
            int i = 1;
            for (Map.Entry<JFXCheckBox, File> entry : loadDeleteConn.entrySet()) {
                if (entry.getKey().isSelected()) {
                    try {
                        Properties loadProperties = new Properties();
                        FileInputStream fis = new FileInputStream(entry.getValue());
                        loadProperties.load(fis);
                        Files.deleteIfExists(Paths.get(loadProperties.getProperty("filePath")));
                        fis.close();
                        Files.deleteIfExists(Paths.get(entry.getValue().toString()));
                        deleteNotify.append((i++) + ". " + entry.getValue().getName() + " Feed is purged\n");
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(PurgeFeedFile.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PurgeFeedFile.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

            new NotifyUI(deleteNotify.toString());

        }
        );

    }

}
