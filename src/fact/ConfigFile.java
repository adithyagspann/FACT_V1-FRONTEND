package fact;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.mail.MessagingException;
import mail.Mail;
import org.apache.commons.net.ftp.FTPClient;
import utility.ExceptionUI;

/**
 *
 * @author Adithya
 */
public class ConfigFile {

// Create the custom dialog.
    private Dialog<Pair<String, String>> dialog;
    private ButtonType loginButtonType;
    private JFXPasswordField password, smtpMailPwd;
    private JFXComboBox fileType;
    private JFXTextField timeZone,
            timeFormat,
            statusPath,
            fetchTime,
            feedname,
            hostname,
            username,
            srcSep,
            trgSep,
            remotePath,
            filePath,
            trgFileheader,
            srcFileheader,
            cronTime,
            mailpassto,
            mailpasscc,
            mailfailto,
            mailfailcc,
            statusFilePath,
            fromMail, hostSmtp, socketProperty, port;
    private Button statusFileLoad, validate, loadLocalPath, loadStatusFilestore, ftpConnectionCheck;
    private JFXCheckBox passfailedRep;
    private boolean timeZoneStatus = true;
    private boolean timeFormatStatus = true;
    private boolean statusPathStatus = true;
    private boolean fetchTimeStatus = true;
    private boolean ftpCheck = false, schedularCheck = false, mailAlertsCheck = false, smtpConfCheck = false, fileCheck = false, uiStatusCheck = false;
    private JFXDatePicker startDate;
    private TitledPane mailAlerts, ftptp, uiStatus, smtpConf, schedularConf, fileConf;
    private StringBuffer execptionData;
    private int exceptionCount;
    private FTPClient ftp = new FTPClient();
    /*
    Email Address Pattern Validation
     */
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /*
    Directory and File chooser
     */
    private FileChooser fileChooser = new FileChooser();
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    File file, fremote;
    private int colIdx = 0;
    private int rowIdx = 0;
    private StatusProperties statusProperties;
    private VBox mainvbox;

    //New Feed
    public ConfigFile(VBox mainvbox, PropertyFileWrapper pfw) {

        dialog = new Dialog<>();
        dialog.setTitle("Application Configuration");
//        execptionData = new StringBuffer();
        this.mainvbox = mainvbox;
// Get the Stage.
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
// Add a custom icon.
        stage.getIcons().add(new Image(this.getClass().getResource("/icon/filesicon.png").toString()));
// Set the button types.
        loginButtonType = new ButtonType("UPDATE", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.setMaxSize(400, 400);
        createTitledPane();
        setUIsettingPane(pfw);
        setFtpConnectionPane();
        setFileConfig();
        setSchedularConfig();
        setMailAlerts();
        setMailSmtpConfig();

        /*
        Setting the Connection data
         */
        if (!statusPath.getText().isEmpty()) {
            schedularConf.setDisable(false);
            fileConf.setDisable(false);
            mailAlerts.setDisable(false);
            smtpConf.setDisable(false);
            ftptp.setDisable(false);
            try {
                statusProperties = new StatusProperties(statusPath.getText());
                setConnProperties(statusProperties.getConnFile());
                setSmtpProperties(statusProperties.getMailFile());

            } catch (IOException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        validate = new Button("Validate");

        grid.add(uiStatus, colIdx, rowIdx);
        grid.add(ftptp, colIdx + 1, rowIdx);

        grid.add(schedularConf, colIdx + 2, rowIdx);
        grid.add(fileConf, colIdx, ++rowIdx);
        grid.add(mailAlerts, colIdx + 1, rowIdx);
        grid.add(smtpConf, colIdx + 2, rowIdx);
        grid.add(validate, colIdx + 1, ++rowIdx, 3, 2);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        dialog.getDialogPane().setContent(grid);
        validate.setOnAction((ActionEvent event) -> {
            exceptionCount = 0;
            execptionData = new StringBuffer();
            dataValidation();

            if (ftpCheck == true && schedularCheck == true && mailAlertsCheck == true && smtpConfCheck == true && fileCheck == true && uiStatusCheck == true) {
                System.out.println("Validation Success");
                loginButton.setDisable(false);
            } else {
                if (execptionData.length() != 0) {
                    new ExceptionUI(new Exception(execptionData.toString()));
                }
                execptionData.delete(0, execptionData.length());
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
                writeFeedConnection(statusProperties.getConnFile());
                writeSmtpConnection(statusProperties.getMailFile());
                writeStatusFile(pfw);
//                pfw.storeUpdate();
                System.out.println("Completed");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionUI(ex);
                dialog.showAndWait();
            } catch (IOException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionUI(ex);
                dialog.showAndWait();
            }
        }
        );

    }

    private ConfigFile() {

    }

    public ObservableList getFileTypes() throws FileNotFoundException, IOException {
        ObservableList fileTypes = FXCollections.observableArrayList();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("resources/fileTypes.txt"));
        String line = bufferedReader.readLine();
        while (line != null) {
            fileTypes.add(line);
            line = bufferedReader.readLine();
        }

        System.out.println("Check: " + fileTypes);
        return fileTypes;
    }

    public static void main(String args[]) {
        try {
            ConfigFile d = new ConfigFile();
            d.getFileTypes();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTitledPane() {
        ftptp = new TitledPane();
        ftptp.setText("FTP Settings");
        uiStatus = new TitledPane();
        uiStatus.setText("UI and Status file settings");
        mailAlerts = new TitledPane();
        mailAlerts.setText("Mail Alerts Settings");
        smtpConf = new TitledPane();
        smtpConf.setText("SMTP Settings");
        fileConf = new TitledPane();
        fileConf.setText("File Configuration");
        schedularConf = new TitledPane();
        schedularConf.setText("Schedular Configuration");
    }

    /*
    UI Code to set FTP to Dialog
     */
    public void setFtpConnectionPane() {
        int col = 0;
        int row = 0;
        feedname = new JFXTextField();
        feedname.setPromptText("Enter the Feed Name");
        hostname = new JFXTextField();
        hostname.setPromptText("Enter the host name");
        username = new JFXTextField();
        username.setPromptText("Enter the Username");
        password = new JFXPasswordField();
        password.setPromptText("Enter the password");
        ftpConnectionCheck = new Button();
        GridPane ftpgrid = new GridPane();
        ftpgrid.setHgap(10);
        ftpgrid.setVgap(5);
        ftpgrid.setPadding(new Insets(20, 10, 10, 10));
        ftpConnectionCheck = new Button("Test Connection");
        ftpgrid.add(new Label("Feed Name"), col, row);
        ftpgrid.add(feedname, col + 1, row, 2, 1);
        ftpgrid.add(new Label("Host Name"), col, ++row);
        ftpgrid.add(hostname, col + 1, row, 2, 1);
        ftpgrid.add(new Label("User Name"), col, ++row);
        ftpgrid.add(username, col + 1, row, 2, 1);
        ftpgrid.add(new Label("Password"), col, ++row);
        ftpgrid.add(password, col + 1, row, 4, 1);
//        ftpgrid.add(new Label(""), col, ++row);

        ftptp.setContent(ftpgrid);

    }

    /*
    UI Code to set Application Config to Dialog
     */
    public void setUIsettingPane(PropertyFileWrapper pfw) {

        int col = 0, row = 0;

        GridPane uistatusgrid = new GridPane();
        uistatusgrid.setHgap(20);
        uistatusgrid.setVgap(5);
        uistatusgrid.setPadding(new Insets(20, 10, 10, 10));
        timeZone = new JFXTextField();
        timeZone.setPromptText("Enter the Time Zone");
        if (!pfw.getTimeZone().isEmpty() || !pfw.getTimeZone().equals("") || pfw.getTimeZone() != null) {
            timeZone.setText(pfw.getTimeZone());
        }

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

        statusFileLoad.setOnAction((ActionEvent event) -> {

            try {

                Stage mainstage = (Stage) mainvbox.getScene().getWindow();
                File fileName = fileChooser.showOpenDialog(mainstage);

                if (fileName != null) {

                    if (fileName.getAbsoluteFile().exists()) {
                        statusPath.setText(fileName.getAbsolutePath());

                        if (!statusPath.getText().isEmpty()) {
                            schedularConf.setDisable(false);
                            fileConf.setDisable(false);
                            mailAlerts.setDisable(false);
                            smtpConf.setDisable(false);
                            ftptp.setDisable(false);
                            try {
                                statusProperties = new StatusProperties(statusPath.getText());
                                setConnProperties(statusProperties.getConnFile());
                                setSmtpProperties(statusProperties.getMailFile());

                            } catch (IOException ex) {
                                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    } else {
                        throw new FileNotFoundException(fileName.getAbsolutePath() + "  does not exists.");
                    }
                }
            } catch (Exception ex) {

                ex.printStackTrace();
                new ExceptionUI(ex);
            }

        });

        if (statusPath.getText().isEmpty()) {
            schedularConf.setDisable(true);
            fileConf.setDisable(true);
            mailAlerts.setDisable(true);
            smtpConf.setDisable(true);
            ftptp.setDisable(true);
        } else {
            schedularConf.setDisable(false);
            fileConf.setDisable(false);
            mailAlerts.setDisable(false);
            smtpConf.setDisable(false);
            ftptp.setDisable(false);
        }

        uistatusgrid.add(new Label("Time Zone"), col, row);
        uistatusgrid.add(timeZone, col + 1, row, 2, 1);
        uistatusgrid.add(new Label("Date & Time Format"), col, ++row);
        uistatusgrid.add(timeFormat, col + 1, row, 2, 1);
        uistatusgrid.add(new Label("Refresh Rate"), col, ++row);
        uistatusgrid.add(fetchTime, col + 1, row, 2, 1);
        uistatusgrid.add(new Label("Status File"), col, ++row);
        uistatusgrid.add(statusPath, col + 1, row, 2, 1);
        uistatusgrid.add(statusFileLoad, col + 3, row, 3, 1);
//        uistatusgrid.add(validate, col + 1, ++row, 2, 1);
        uiStatus.setContent(uistatusgrid);
    }

    /*
    UI Code to Set Mail Alerts
     */
    public void setMailAlerts() {
        int col = 0;
        int row = 0;
        /*
            Email Configuration for Feed
         */
        mailpassto = new JFXTextField();
        mailpassto.setPromptText("Enter the Email address seperated by ','");
        mailpasscc = new JFXTextField();
        mailpasscc.setPromptText("Enter the Email address seperated by ','");
        mailfailto = new JFXTextField();
        mailfailto.setPromptText("Enter the Email address seperated by ','");
        mailfailcc = new JFXTextField();
        mailfailcc.setPromptText("Enter the Email address seperated by ','");
        passfailedRep = new JFXCheckBox("Use Passed Recipients");

        GridPane mailAgrid = new GridPane();
        mailAgrid.setHgap(20);
        mailAgrid.setVgap(5);
        mailAgrid.setPadding(new Insets(20, 10, 10, 10));
        mailAgrid.add(new Label("Comparision Passed"), col, ++row);
        mailAgrid.add(new Label("Primary Recipients (To)"), col, ++row);
        mailAgrid.add(mailpassto, col + 1, row, 2, 1);
        mailAgrid.add(new Label("Secondary Recipients (cc)"), col, ++row);
        mailAgrid.add(mailpasscc, col + 1, row, 2, 1);
        mailAgrid.add(new Label("Comparision Failed"), col, ++row);
        mailAgrid.add(passfailedRep, col, ++row);
        mailAgrid.add(new Label("Primary Recipients (To)"), col, ++row);
        mailAgrid.add(mailfailto, col + 1, row, 2, 1);
        mailAgrid.add(new Label("Secondary Recipients (cc)"), col, ++row);

        mailAgrid.add(mailfailcc, col + 1, row, 2, 1);
        mailAgrid.add(new Label(""), col, ++row);
        mailAgrid.add(new Label(""), col, ++row);
        mailAlerts.setContent(mailAgrid);

        /*
        Pass recipients to fail 
         */
        passfailedRep.setOnAction((ActionEvent event) -> {
            if (passfailedRep.isSelected()) {
                mailfailto.setDisable(true);
                mailfailcc.setDisable(true);
                mailfailcc.setText(mailpasscc.getText());
                mailfailto.setText(mailfailto.getText());
            } else {
                mailfailto.setDisable(false);
                mailfailcc.setDisable(false);
                mailfailcc.clear();
                mailfailto.clear();
            }
        });

    }

    /*
    UI Code to Set Mail SMTP Configuration
     */
    public void setMailSmtpConfig() {
        int col = 0;
        int row = 0;
        /*
            Email SMTP Configuration
         */
        hostSmtp = new JFXTextField();
        hostSmtp.setPromptText("Enter the SMTP address");
        port = new JFXTextField();
        port.setPromptText("Enter the SMTP Port Number");
        fromMail = new JFXTextField();
        fromMail.setPromptText("Enter the Email address");
        smtpMailPwd = new JFXPasswordField();
        smtpMailPwd.setPromptText("Enter the Email password");
        socketProperty = new JFXTextField();
        socketProperty.setPromptText("Enter the Socket Port");
//        smtpConfCheck = new Button("Test Mail");

        GridPane smtpGrid = new GridPane();
        smtpGrid.setHgap(20);
        smtpGrid.setVgap(10);
        smtpGrid.setPadding(new Insets(20, 10, 10, 10));
        smtpGrid.add(new Label("Host SMTP Address"), col, ++row);
        smtpGrid.add(hostSmtp, col + 1, row, 2, 1);
        smtpGrid.add(new Label("SMTP Port"), col, ++row);

        smtpGrid.add(port, col + 1, row, 2, 1);
        smtpGrid.add(new Label("Socket Port"), col, ++row);
        smtpGrid.add(socketProperty, col + 1, row);
        smtpGrid.add(new Label("SMTP Email"), col, ++row);
        smtpGrid.add(fromMail, col + 1, row, 2, 1);
        smtpGrid.add(new Label("SMTP Email Password"), col, ++row);
        smtpGrid.add(smtpMailPwd, col + 1, row);

        smtpGrid.add(new Label(""), col, ++row);

        smtpGrid.add(new Label(""), col, ++row);
//        smtpGrid.add(new Label(""), col, ++row);
//        smtpGrid.add(new Label(""), col, ++row);

        smtpConf.setContent(smtpGrid);
    }

    public void setSchedularConfig() {
        int col = 0, row = 0;
        /*
            Schedular Properties
         */
        startDate = new JFXDatePicker();
        cronTime = new JFXTextField();
        cronTime.setPromptText("Enter the cron time");
//        dateFormat = new JFXTextField();
//        dateFormat.setPromptText("Enter Date format");

        GridPane schGrid = new GridPane();
        schGrid.setHgap(20);
        schGrid.setVgap(5);
        schGrid.add(new Label("Start Date"), col, row);
        schGrid.add(startDate, col + 1, row, 2, 1);
        schGrid.add(new Label("Cron Time"), col, ++row);
        schGrid.add(cronTime, col + 1, row, 2, 1);
        schGrid.add(new Label(""), col, ++row);
//        schGrid.add(dateFormat, col + 1, row, 2, 1);
        schGrid.add(new Label(""), col, ++row);
        schGrid.add(new Label(""), col, ++row);

        schedularConf.setContent(schGrid);
    }

    /*
    Set File Configuration
     */
    public void setFileConfig() {
        try {
            int col = 0, row = 0;
            fileType = new JFXComboBox();
            fileType.setPromptText("Select File Type");
            fileType.setItems(getFileTypes());
            /*
            File Properties
             */
            srcSep = new JFXTextField();
            srcSep.setPromptText("Enter the new file seperator");
            trgSep = new JFXTextField();
            trgSep.setPromptText("Enter the Old file seperator");
            remotePath = new JFXTextField();
            remotePath.setPromptText("Enter the Remote path");
            filePath = new JFXTextField();
            filePath.setPromptText("Select Local Path directory");
            filePath.setDisable(true);
            loadLocalPath = new Button("Browse");
            trgFileheader = new JFXTextField();
            trgFileheader.setPromptText("Enter the Fields Name seperated by ','");
            srcFileheader = new JFXTextField();
            srcFileheader.setPromptText("Enter the Fields Name seperated by ','");
            statusFilePath = new JFXTextField();
            statusFilePath.setText("Enter the Status Path");
            statusFilePath.setDisable(true);
            statusFileLoad = new Button("Browse");
            srcSep = new JFXTextField();
            srcSep.setPromptText("Enter the New File Separator");
            trgSep = new JFXTextField();
            trgSep.setPromptText("Enter the Old File Separator");

            GridPane fileGrid = new GridPane();
            fileGrid.setHgap(20);
            fileGrid.setVgap(5);

            fileGrid.add(new Label("File Type"), col, ++row);
            fileGrid.add(fileType, col + 1, row, 2, 1);
            fileGrid.add(new Label("Remote File name with path"), col, ++row);
            fileGrid.add(remotePath, col + 1, row, 2, 1);
            fileGrid.add(new Label("Local File Path"), col, ++row);
            fileGrid.add(filePath, col + 1, row, 2, 1);
            fileGrid.add(loadLocalPath, col + 3, row, 2, 1);
            fileGrid.add(new Label("New File Header"), col, ++row);
            fileGrid.add(srcFileheader, col + 1, row, 2, 1);
            fileGrid.add(new Label("Old File header"), col, ++row);
            fileGrid.add(trgFileheader, col + 1, row, 2, 1);
            fileGrid.add(new Label("New File Separator"), col, ++row);
            fileGrid.add(srcSep, col + 1, row, 2, 1);
            fileGrid.add(new Label("Old File Separator"), col, ++row);
            fileGrid.add(trgSep, col + 1, row, 2, 1);
            fileGrid.add(new Label("Status File Path"), col, ++row);
            fileGrid.add(statusFilePath, col + 1, row, 2, 1);
            fileGrid.add(statusFileLoad, col + 3, row, 2, 1);
            fileConf.setContent(fileGrid);

            loadLocalPath.setOnAction((ActionEvent event) -> {
                Stage mainstage = (Stage) mainvbox.getScene().getWindow();
                File fileName = directoryChooser.showDialog(mainstage);
                if (fileName != null) {
                    filePath.setText(fileName.getAbsolutePath());
                    if (!fileName.exists()) {
                        fileName.mkdir();
                    }
                }
            });
            statusFileLoad.setOnAction((ActionEvent event) -> {
                Stage mainstage = (Stage) mainvbox.getScene().getWindow();
                File fileName = directoryChooser.showDialog(mainstage);
                if (fileName != null) {
                    statusFilePath.setText(fileName.getAbsolutePath());

                    if (!fileName.exists()) {
                        fileName.mkdir();
                    }
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            new ExceptionUI(ex);
        }
    }

    /*
    Data Validation
     */
 /*
   Data Validation for FTP
     */
    public void validateFTP() {
        boolean hostCheck = false, userCheck = false, pwdCheck = false;
        try {
            if (!hostname.getText().isEmpty()) {
                System.out.println("Host Name");
                hostCheck = true;
            } else {
                System.out.println("Host Name invalid");
                execptionData.append((++exceptionCount) + ". FTP Host Name should not be empty.\n");
            }

            if (!username.getText().isEmpty()) {
                userCheck = true;
            } else {
                execptionData.append((++exceptionCount) + ". FTP UserName should not be empty.\n");
            }

            if (!password.getText().isEmpty()) {
                pwdCheck = true;
            } else {
                execptionData.append((++exceptionCount) + ". FTP password should not be empty.\n");
            }

            if (hostCheck == true && userCheck == true && pwdCheck == true) {
                System.out.println("Checking the Connection");
                ftp.connect(hostname.getText(), 21);
                ftp.login(username.getText(), password.getText());
                if (ftp.isConnected()) {

                    ftpCheck = true;
                } else {
                    ftpCheck = false;
                }

            } else {
                ftpCheck = false;
                hostCheck = false;
                userCheck = false;
                pwdCheck = false;
            }
        } catch (IOException ex) {
            hostCheck = false;
            userCheck = false;
            pwdCheck = false;
            Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
            new ExceptionUI(ex);
        }
    }

    /*
  Data Validation for UI and status file
     */
    public void validateUIStatus() {

        boolean timeZoneCheck = false, timeFormatCheck = false, fetchTimeCheck = false, statusPathCheck = false;

        if (!timeZone.getText().isEmpty()) {
            timeZoneCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Time zone should not be Empty.\n");
        }
        if (!statusPath.getText().isEmpty()) {
            statusPathCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Status File should not be empty.\n");
        }
        if (!fetchTime.getText().isEmpty()) {
            fetchTimeCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". UI Refresh Time should not be empty.\n");
        }
        if (!timeFormat.getText().isEmpty()) {
            timeFormatCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". UI Date & Time format should not be Empty.\n");
        }

        if (timeFormatCheck == true && timeZoneCheck == true && fetchTimeCheck == true && statusPathCheck == true) {
            uiStatusCheck = true;
        } else {

            uiStatusCheck = false;
            timeZoneCheck = false;
            timeFormatCheck = false;
            fetchTimeCheck = false;
            statusPathCheck = false;
        }
    }

    /*
   Data validation for Mail Alerts
     */
    public void validateMailAlerts() {
        System.out.println("Validating Mail Alerts");
        boolean mailPasstoCheck = false, mailPassccCheck = false, mailFailtoCheck = false, mailFailccCheck = false;

        if (!mailpassto.getText().isEmpty()) {

            String[] emailAdd = mailpassto.getText().split(",");

            for (String email : emailAdd) {
                if (VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                    mailPasstoCheck = true;
                } else {
                    execptionData.append((++exceptionCount) + ". Invalid Email address:  " + email + ".\n");
                }
            }

        } else {
            execptionData.append((++exceptionCount) + ". To Email address for passed test cases should not be empty.\n");

        }
        if (!mailpasscc.getText().isEmpty()) {
            String[] emailAdd = mailpasscc.getText().split(",");

            for (String email : emailAdd) {
                if (VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                    mailPassccCheck = true;
                } else {
                    execptionData.append((++exceptionCount) + ". Invalid Email address:  " + email + ".\n");
                }
            }

        } else {
            execptionData.append((++exceptionCount) + ". CC Email address for passed test cases should not be empty.\n");

        }
        if (!mailfailto.getText().isEmpty()) {
            String[] emailAdd = mailfailto.getText().split(",");

            for (String email : emailAdd) {
                if (VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                    mailFailtoCheck = true;
                } else {
                    execptionData.append((++exceptionCount) + ". Invalid Email address:  " + email + ".\n");
                }
            }

        } else {
            execptionData.append((++exceptionCount) + ". To Email address for failed test cases should not be empty.\n");

        }
        if (!mailfailcc.getText().isEmpty()) {
            String[] emailAdd = mailfailcc.getText().split(",");

            for (String email : emailAdd) {
                if (VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                    mailFailccCheck = true;
                } else {
                    execptionData.append((++exceptionCount) + ". Invalid Email address:  " + email + ".\n");
                }
            }
        } else {
            execptionData.append((++exceptionCount) + ".CC Email address for failed test cases should not be empty.\n");

        }

        if (mailPasstoCheck == true && mailPassccCheck == true && mailFailccCheck == true && mailFailtoCheck == true) {
            /*try {
             Properties connectionProperties = new Properties();
                String socketFactory_class = "javax.net.ssl.SSLSocketFactory"; //SSL Factory Class
                String mail_smtp_auth = "true";
                connectionProperties.put("mail.smtp.port", port);
                connectionProperties.put("mail.smtp.socketFactory.port", socketProperty);
                connectionProperties.put("mail.smtp.host", hostSmtp.getText());
                connectionProperties.put("mail.smtp.socketFactory.class", socketFactory_class);
                connectionProperties.put("mail.smtp.auth", mail_smtp_auth);
                connectionProperties.put("mail.smtp.socketFactory.fallback", "false");
                connectionProperties.put("mail.smtp.starttls.enable", "true");
                Mail mail = new Mail("");
                mail.createSession(fromMail.getText(), password.getText(), connectionProperties);
                mail.sendEmail(fromMail.getText(), mailpassto.getText(), mailpasscc.getText(), "Test Mail from FACT", "Hi, This is the Test Mail for the FACT Tool", "");
                mail.sendEmail(fromMail.getText(), mailfailto.getText(), mailfailcc.getText(), "Test Mail from FACT", "Hi, This is the Test Mail for the FACT Tool", "");*/
            mailAlertsCheck = true;
            /* } catch (IOException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                mailAlertsCheck = false;
                new ExceptionUI(ex);
            } catch (MessagingException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                mailAlertsCheck = false;
                new ExceptionUI(ex);
            }*/
        } else {
            mailAlertsCheck = false;
            mailPasstoCheck = false;
            mailPassccCheck = false;
            mailFailtoCheck = false;
            mailFailccCheck = false;
        }

    }

    /*
    Data Validatation for SMTP 
     */
    public void validateSMTP() {
        System.out.println("Validating SMTP");
        boolean hostSmtpCheck = false, portCheck = false, fromMailCheck = false, smtpMailpwdCheck = false, socketPortCheck = false;

//        smtpConfCheck = new Button("Test Mail");
        if (!hostSmtp.getText().isEmpty()) {
            hostSmtpCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Host SMTP address should not be empty.\n");
        }

        if (!port.getText().isEmpty()) {
            portCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Port should not be empty.\n");
        }

        if (!fromMail.getText().isEmpty()) {
            fromMailCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". From Mail should not be empty.\n");
        }

        if (!smtpMailPwd.getText().isEmpty()) {
            smtpMailpwdCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Mail Password  should not be empty.\n");
        }
        if (!socketProperty.getText().isEmpty()) {
            socketPortCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Socket port  should not be empty.\n");
        }

        if (smtpMailpwdCheck == true && fromMailCheck == true && portCheck == true && hostSmtpCheck == true && socketPortCheck == true) {
            smtpConfCheck = true;
        } else {
            smtpConfCheck = false;
            hostSmtpCheck = false;
            portCheck = false;
            fromMailCheck = false;
            smtpMailpwdCheck = false;
            socketPortCheck = false;
        }
    }

    /*
    Date Validation for Schedular
     */
    public void validateSchedular() {
        boolean startDateCheck = false, cronTimeCheck = false, dateFormatCheck = false;
        /*
            Schedular Properties
         */

        System.out.println("Date: " + startDate.getValue());
        if (startDate.getValue() != null) {
            System.out.println("Date: " + startDate.getValue());
            startDateCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Start Date should not be empty.\n");
        }

        if (!cronTime.getText().isEmpty()) {
            cronTimeCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Cron Time should not be empty.\n");
        }
//        if (!dateFormat.getText().isEmpty()) {
//            dateFormatCheck = true;
//        } else {
//            execptionData.append((++exceptionCount) + ". Date Format should not be empty.\n");
//        }

        if (startDateCheck == true && cronTimeCheck == true) {
            schedularCheck = true;
        } else {
            schedularCheck = false;
            startDateCheck = false;
            cronTimeCheck = false;
            dateFormatCheck = false;
        }

    }

    /*
  Validate File
     */
    public void validateFile() {
        System.out.println("Validating the File");
        boolean fileTypeCheck = false, srcSepcheck = false, trgSepCheck = false, remotePathcheck = false, filePathcheck = false, trgFileHeadercheck = false, srcFileheadercheck = false, statusFilePathCheck = false;

        if (!fileType.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("Select File Type")) {
            fileTypeCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Please Select the File Type.\n");
        }

        if (!srcSep.getText().isEmpty()) {
            srcSepcheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Source Seperator should not be empty.\n");
        }
        if (!trgSep.getText().isEmpty()) {
            trgSepCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Target Seperator should not be empty.\n");
        }
        if (!srcFileheader.getText().isEmpty()) {
            srcFileheadercheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Source Header should not be empty.\n");
        }
        if (!trgFileheader.getText().isEmpty()) {
            trgFileHeadercheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Target Header should not be empty.\n");
        }
        if (!remotePath.getText().isEmpty()) {
            InputStream ftpIn = null;
            try {
                //Check the File is Avaiable
                ftpIn = ftp.retrieveFileStream(remotePath.getText());
                if (ftpIn == null || ftp.getReplyCode() == 550) {
                    execptionData.append((++exceptionCount) + ". Remote File does not be exists.\n");
                } else {
                    remotePathcheck = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                new ExceptionUI(ex);
            } finally {
                try {
                    ftpIn.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConfigFile.class.getName()).log(Level.SEVERE, null, ex);
                    new ExceptionUI(ex);
                }
            }
        } else {
            execptionData.append((++exceptionCount) + ". Remote File Path  should not be empty.\n");
        }
        if (!filePath.getText().isEmpty()) {
            filePathcheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Local Path should not be empty.\n");
        }
        if (!statusPath.getText().isEmpty()) {
            statusFilePathCheck = true;
        } else {
            execptionData.append((++exceptionCount) + ". Status Path should not be empty.\n");
        }

        if (statusFilePathCheck == true && filePathcheck == true && remotePathcheck == true && trgFileHeadercheck == true && srcFileheadercheck == true && srcSepcheck == true && trgSepCheck == true && fileTypeCheck == true) {
            fileCheck = true;
        } else {
            fileCheck = false;
            fileTypeCheck = false;
            srcSepcheck = false;
            trgSepCheck = false;
            remotePathcheck = false;
            filePathcheck = false;
            trgFileHeadercheck = false;
            srcFileheadercheck = false;
            statusFilePathCheck = false;
        }

    }
    FeedConnProperties feedConnProperties;

    public void setConnProperties(String connPath) throws IOException {
        System.out.println("Set Conn Properties: " + connPath);
        feedConnProperties = new FeedConnProperties(connPath);

        if (!feedConnProperties.getFeedname().isEmpty()) {
            feedname.setText(feedConnProperties.getFeedname());
        }
        if (!feedConnProperties.getHostname().isEmpty()) {
            hostname.setText(feedConnProperties.getHostname());
        }
        if (!feedConnProperties.getUsername().isEmpty()) {
            username.setText(feedConnProperties.getUsername());
        }
        if (!feedConnProperties.getPassword().isEmpty()) {
            password.setText(feedConnProperties.getPassword());
        }

        if (!feedConnProperties.getStartDate().isEmpty()) {
            startDate.setValue(LocalDate.parse(feedConnProperties.getStartDate()));
        }
        if (!feedConnProperties.getCronTime().isEmpty()) {
            cronTime.setText(feedConnProperties.getCronTime());
        }
//        if (!feedConnProperties.getDateFormat().isEmpty()) {
//            dateFormat.setText(feedConnProperties.getDateFormat());
//        }

        if (!feedConnProperties.getFileType().isEmpty()) {
            fileType.setValue(feedConnProperties.getFileType());
        }

        if (!feedConnProperties.getSrcSep().isEmpty()) {
            srcSep.setText(feedConnProperties.getSrcSep());
        }

        if (!feedConnProperties.getTrgSep().isEmpty()) {
            trgSep.setText(feedConnProperties.getTrgSep());
        }

        if (!feedConnProperties.getRemotePath().isEmpty()) {
            remotePath.setText(feedConnProperties.getRemotePath());
        }

        if (!feedConnProperties.getLocalFilePath().isEmpty()) {
            filePath.setText(feedConnProperties.getLocalFilePath());
        }

        if (!feedConnProperties.getTrgFileheader().isEmpty()) {
            trgFileheader.setText(feedConnProperties.getTrgFileheader());
        }

        if (!feedConnProperties.getSrcFileheader().isEmpty()) {
            srcFileheader.setText(feedConnProperties.getSrcFileheader());
        }

        if (!feedConnProperties.getStatusFilePath().isEmpty()) {
            statusFilePath.setText(feedConnProperties.getStatusFilePath());
        }

        if (!feedConnProperties.getMailpassto().isEmpty()) {
            mailpassto.setText(feedConnProperties.getMailpassto());
        }

        if (!feedConnProperties.getMailpasscc().isEmpty()) {
            mailpasscc.setText(feedConnProperties.getMailpasscc());
        }

        if (!feedConnProperties.getMailfailto().isEmpty()) {

            mailfailto.setText(feedConnProperties.getMailfailto());
        }
        if (!feedConnProperties.getMailfailcc().isEmpty()) {

            mailfailcc.setText(feedConnProperties.getMailfailcc());
        }

        if (feedConnProperties.getMailfailcc().equalsIgnoreCase(feedConnProperties.getMailpasscc()) && feedConnProperties.getMailfailto().equalsIgnoreCase(feedConnProperties.getMailpassto())) {

            passfailedRep.setSelected(true);
            mailfailcc.setDisable(true);
            mailfailto.setDisable(true);
        } else {
            passfailedRep.setSelected(false);
        }

    }

    public void setSmtpProperties(String connPath) throws IOException {
        System.out.println("Set SMTP Properties: " + connPath);
        MailProperty mp = new MailProperty(connPath);

        hostSmtp.setText(mp.getHost());

        port.setText(mp.getPort());
        fromMail.setText(mp.getFromMail());

        smtpMailPwd.setText(mp.getPassword());
        socketProperty.setText(mp.getSocketProperty());

    }

    public void dataValidation() {
        validateUIStatus();
        validateSchedular();
        validateFTP();
        if (ftpCheck == true) {
            System.out.println("FTP Passed");
            validateFile();
            validateSMTP();
            validateMailAlerts();
        } else {
            System.out.println("FTP Failed");
        }
    }

    public void writeFeedConnection(String connPath) throws IOException {
        System.out.println("Set Conn Properties: " + connPath);
        FeedConnProperties feedConnProperties = new FeedConnProperties(connPath);
        feedConnProperties.setFeedname(feedname.getText());
        feedConnProperties.setHostname(hostname.getText());
        feedConnProperties.setUsername(username.getText());
        feedConnProperties.setPassword(password.getText());
        feedConnProperties.setStartDate(startDate.getValue().toString());
        feedConnProperties.setCronTime(cronTime.getText());
        feedConnProperties.setDateformat("YYYY-MM-DD");
        feedConnProperties.setFileType(fileType.getSelectionModel().getSelectedItem().toString());
        feedConnProperties.setSrcSep(srcSep.getText());
        feedConnProperties.setTrgSep(trgSep.getText());
        feedConnProperties.setRemotePath(remotePath.getText());
        feedConnProperties.setFilePath(filePath.getText());
        feedConnProperties.setTrgFileheader(trgFileheader.getText());
        feedConnProperties.setSrcFileheader(srcFileheader.getText());
        feedConnProperties.setStatusFilePath(statusFilePath.getText());
        feedConnProperties.setMailpassto(mailpassto.getText());
        feedConnProperties.setMailpasscc(mailpasscc.getText());
        feedConnProperties.setMailfailto(mailfailto.getText());
        feedConnProperties.setMailfailcc(mailfailcc.getText());
        feedConnProperties.setFileSrcExtension("." + fileType.getSelectionModel().getSelectedItem().toString());
        feedConnProperties.setFileTrgExtension("." + fileType.getSelectionModel().getSelectedItem().toString());
        feedConnProperties.setMailConfiguration(feedConnProperties.getMailConfiguration());

        feedConnProperties.saveProperties();
        File statusFileUi = new File(statusPath.getText());
        File statusFilePathBk = new File(statusFilePath.getText());
        if (!statusFileUi.getPath().equalsIgnoreCase(statusFilePathBk.getPath())) {
            if (!statusFilePathBk.exists()) {
                statusFilePathBk.mkdir();
            }

            Files.copy(Paths.get(statusFileUi.getAbsolutePath()), Paths.get(statusFilePathBk.getAbsolutePath() + "/" + statusFileUi.getName()), StandardCopyOption.REPLACE_EXISTING);
        }

    }

    public void writeSmtpConnection(String mailPath) throws IOException {
        MailProperty mailProperty = new MailProperty(mailPath);
        mailProperty.setFromMail(fromMail.getText());
        mailProperty.setHost(hostSmtp.getText());
        mailProperty.setPassword(smtpMailPwd.getText());
        mailProperty.setPort(port.getText());
        mailProperty.setSocketPort(socketProperty.getText());
        mailProperty.storeSMTp();
    }

    public void writeStatusFile(PropertyFileWrapper pfw) throws FileNotFoundException {
        pfw.setFetchTime(fetchTime.getText());
        File statusFileUi = new File(statusPath.getText());
        File statusFilePathBk = new File(statusFilePath.getText());
        System.out.println("Status Path : " + statusFilePathBk.getPath() + "/" + statusFileUi.getName());
        pfw.setStatusPath(statusFilePathBk.getPath() + "/" + statusFileUi.getName());
        pfw.setTimeFormat(timeFormat.getText());
        pfw.setTimeZone(timeZone.getText());
        pfw.storeUpdate();
    }

}
