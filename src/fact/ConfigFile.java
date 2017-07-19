package fact;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import utility.ExceptionUI;

/**
 *
 * @author Adithya
 */
public class ConfigFile {

// Create the custom dialog.
    private Dialog<Pair<String, String>> dialog;
    private ButtonType loginButtonType;
    private JFXTextField timeZone, timeFormat, statusPath, fetchTime;
    private Button statusFileLoad, validate;
    private Label validateStatus;
    private boolean timeZoneStatus = true;
    private boolean timeFormatStatus = true;
    private boolean statusPathStatus = true;
    private boolean fetchTimeStatus = true;
//file chooser
    DirectoryChooser directoryChooser = new DirectoryChooser();

    File file, fremote;
    private int colIdx = 0;
    private int rowIdx = 0;

    //New Feed
    public ConfigFile(VBox mainvbox, PropertyFileWrapper pfw) {

        dialog = new Dialog<>();

        dialog.setTitle("Application Configuration");
//        dialog.setHeaderText("Add the Feed File Connection");

        // Get the Stage.
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        // Add a custom icon.
        stage.getIcons().add(new Image(this.getClass().getResource("/icon/filesicon.png").toString()));

        // Set the icon (must be included in the project).
//        dialog.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/icon/filesicon.png"))));
        // Set the button types.
        loginButtonType = new ButtonType("UPDATE", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 10, 10, 10));

        timeZone = new JFXTextField();
        timeZone.setPromptText("Enter the Time Zone");
        if (!pfw.getTimeZone().isEmpty() || !pfw.getTimeZone().equals("") || pfw.getTimeZone() != null) {
            timeZone.setText(pfw.getTimeZone());
        }
//        feedName.setMaxWidth(250);
        timeFormat = new JFXTextField();
        timeFormat.setPromptText("Enter the time format");
        if (!pfw.getTimeFormat().isEmpty() || !pfw.getTimeFormat().equals("") || pfw.getTimeFormat() != null) {
            timeFormat.setText(pfw.getTimeFormat());
        }
        fetchTime = new JFXTextField();
        fetchTime.setPromptText("Enter Refresh Rate");
        if (!pfw.getFetchTime().isEmpty() || !pfw.getFetchTime().equals("") || pfw.getFetchTime() != null) {
            fetchTime.setText(pfw.getFetchTime());
        }

        statusPath = new JFXTextField();
        statusPath.setPromptText("Please select the Status folder");
        statusPath.setDisable(true);
        if (!pfw.getStatusPath().isEmpty() || !pfw.getStatusPath().equals("") || pfw.getStatusPath() != null) {
            statusPath.setText(pfw.getStatusPath());
        }
        statusFileLoad = new Button("Browse");
        validate = new Button("Validate");
//        validateStatus = new Label("Click Validate button to activate Update");

        statusFileLoad.setOnAction((ActionEvent event) -> {

         
            try {

                Stage mainstage = (Stage) mainvbox.getScene().getWindow();
                File dirName = directoryChooser.showDialog(mainstage);
                FileReader fr = null;
         
                statusPath.setText(dirName.getAbsolutePath());

          
            } catch (Exception ex) {
//                 new ExceptionUI(ex);
                ex.printStackTrace();
            }

        });

        grid.add(new Label("Time Zone"), colIdx, rowIdx);
        grid.add(timeZone, colIdx + 1, rowIdx, 2, 1);
        grid.add(new Label("Date Format"), colIdx, ++rowIdx);
        grid.add(timeFormat, colIdx + 1, rowIdx, 2, 1);
        grid.add(new Label("Refresh Rate"), colIdx, ++rowIdx);
        grid.add(fetchTime, colIdx + 1, rowIdx, 2, 1);
        grid.add(new Label("Status Directory"), colIdx, ++rowIdx);
        grid.add(statusPath, colIdx + 1, rowIdx, 2, 1);
        grid.add(statusFileLoad, colIdx + 3, rowIdx, 3, 1);
        grid.add(validate, colIdx + 1, ++rowIdx, 2, 1);

//        grid.add(msgValidate, colIdx, ++rowIdx, 2, 1);
//        grid.add(validateData, colIdx + 2, rowIdx);
        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);

        loginButton.setDisable(true);

//        test.setDisable(true);
        dialog.getDialogPane().setContent(grid);

        validate.setOnAction((ActionEvent event) -> {
            int i = 0;
            StringBuffer execptionData = new StringBuffer();
            if (timeZone.getText().isEmpty()) {
                execptionData.append((++i) + ". Please Enter the Time zone.\n");
                timeZoneStatus = false;
            } else {

                timeZoneStatus = true;

            }
            if (timeFormat.getText().isEmpty()) {
                execptionData.append((++i) + ". Please Enter the Date & Time Format.\n");
                timeFormatStatus = false;
            } else {
                timeFormatStatus = true;
            }
            if (fetchTime.getText().isEmpty()) {
                execptionData.append((++i) + ". Please Enter the Refresh Rate.\n");
                fetchTimeStatus = false;
            } else {
                fetchTimeStatus = true;
            }
            if (statusPath.getText().isEmpty()) {
                execptionData.append((++i) + ". Please select Status file directory Format.\n");
                statusPathStatus = false;
            } else {
                statusPathStatus = true;
            }

            if (timeZoneStatus == false || timeFormatStatus == false || fetchTimeStatus == false || statusPathStatus == false) {

                System.out.println("timeZoneStatus: " + timeZoneStatus);
                System.out.println("timeFormatStatus: " + timeFormatStatus);
                System.out.println("fetchTimeStatus: " + fetchTimeStatus);
                System.out.println("statusPathStatus: " + statusPathStatus);

                new ExceptionUI(new Exception(execptionData.toString()));
                execptionData.delete(0, execptionData.length());
            } else {
                validate.setDisable(true);

                loginButton.setDisable(false);
            }
        });

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton
                -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>("", "");
//                return new Pair<>("", "");
            }

            return null;
        }
        );

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(dbconstring
                -> {
            try {
                System.out.println("Clicked - Update Button");
                pfw.setFetchTime(fetchTime.getText());
                pfw.setStatusPath(statusPath.getText());
                pfw.setTimeFormat(timeFormat.getText());
                pfw.setTimeZone(timeZone.getText());

//                pfw.storeUpdate();
                System.out.println("Completed");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        );

    }

}
