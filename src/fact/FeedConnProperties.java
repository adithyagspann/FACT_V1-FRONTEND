package fact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Adithya
 */
public class FeedConnProperties {

    // private String connPath, feedname, hostname, username, password, fileType, fileSrcExtension, fileTrgExtension, srcSep, trgSep, remotePath, remoteModTime, filePath, trgFileheader, srcFileheader, startDate, cronTime, mailpassto, mailpasscc, passreptofail, mailfailto, mailfailcc, statusFilePath, mailConfiguration;
    private Properties loadConn;
    private FileInputStream propFile;
    //private String dateFormat;

    private File connectionFile;

    public FeedConnProperties(String connPath) throws FileNotFoundException, IOException, IOException {
        loadConn = new Properties();
        connectionFile = new File(connPath);
        propFile = new FileInputStream(connectionFile);
        loadConn.load(propFile);
        System.out.println("File: " + connectionFile.getAbsolutePath());

        if (!Files.exists(Paths.get(loadConn.getProperty("statusFilePath")), LinkOption.NOFOLLOW_LINKS)) {
            Files.createDirectory(Paths.get(loadConn.getProperty("statusFilePath")));

        }

    }

    public void setDateformat(String dateFormat) {
        loadConn.setProperty("dateFormat", dateFormat);
    }

    public String getDateformat() {
        return loadConn.getProperty("dateFormat");
    }

    public String getMailConfiguration() {
        return loadConn.getProperty("mailConfiguration");
    }

    public String getDateFormat() {
        return loadConn.getProperty("dateFormat");
    }

    public String getStatusFilePath() {
        return loadConn.getProperty("statusFilePath");
    }

    public String getFeedname() {
        return loadConn.getProperty("feedname");
    }

    public String getHostname() {
        return loadConn.getProperty("hostname");
    }

    public String getUsername() {
        return loadConn.getProperty("username");
    }

    public String getPassword() {
        return loadConn.getProperty("password");
    }

    public String getFileType() {
        return loadConn.getProperty("fileType");
    }

    public String getFileSrcExtension() {
        return loadConn.getProperty("fileSrcExtension");
    }

    public String getFileTrgExtension() {
        return loadConn.getProperty("fileTrgExtension");
    }

    public String getSrcSep() {
        return loadConn.getProperty("srcSep");
    }

    public String getTrgSep() {
        return loadConn.getProperty("trgSep");
    }

    public String getRemotePath() {
        return loadConn.getProperty("remotePath");
    }

    public String getRemoteModTime() {
        return loadConn.getProperty("remoteModTime");
    }

    public String getLocalFilePath() {

//        String storeFilePath = filePath + remotePath.substring(remotePath.lastIndexOf("/"), remotePath.lastIndexOf(".")) + "_" + remoteModTime + remotePath.substring(remotePath.lastIndexOf("."), remotePath.length());
        return loadConn.getProperty("filePath");
    }

    public String getSrcFileheader() {
        return loadConn.getProperty("srcFileheader");
    }

    public String getTrgFileheader() {
        return loadConn.getProperty("trgFileheader");
    }

    public String getStartDate() {
        return loadConn.getProperty("startDate");
    }

    public String getCronTime() {
        return loadConn.getProperty("cronTime");
    }

    public String getMailpassto() {
        return loadConn.getProperty("mailpassto");
    }

    public String getMailpasscc() {
        return loadConn.getProperty("mailpasscc");
    }

    public String getMailfailto() {
        return loadConn.getProperty("mailfailto");
    }

    public String getMailfailcc() {
        return loadConn.getProperty("mailfailcc");
    }

    public String getConnectionFile() {
        return connectionFile.getAbsolutePath();
    }

    public void setFeedname(String feedname) {
        loadConn.setProperty("feedname", feedname);
    }

    public void setHostname(String hostname) {
        loadConn.setProperty("hostname", hostname);
    }

    public void setUsername(String username) {
        loadConn.setProperty("username", username);
    }

    public void setPassword(String password) {
        loadConn.setProperty("password", password);
    }

    public void setFileType(String fileType) {
        loadConn.setProperty("fileType", fileType);
    }

    public void setFileSrcExtension(String fileSrcExtension) {
        loadConn.setProperty("fileSrcExtension", fileSrcExtension);
    }

    public void setFileTrgExtension(String fileTrgExtension) {
        loadConn.setProperty("fileTrgExtension", fileTrgExtension);
    }

    public void setSrcSep(String srcSep) {
        loadConn.setProperty("srcSep", srcSep);
    }

    public void setTrgSep(String trgSep) {
        loadConn.setProperty("trgSep", trgSep);
    }

    public void setRemotePath(String remotePath) {
        loadConn.setProperty("remotePath", remotePath);
    }

    public void setRemoteModTime(String remoteModTime) {
        loadConn.setProperty("remoteModTime", remoteModTime);
    }

    public void setFilePath(String filePath) {
        loadConn.setProperty("filePath", filePath);
    }

    public void setTrgFileheader(String trgFileheader) {
        loadConn.setProperty("trgFileheader", trgFileheader);
    }

    public void setSrcFileheader(String srcFileheader) {
        loadConn.setProperty("srcFileheader", srcFileheader);
    }

    public void setStartDate(String startDate) {
        loadConn.setProperty("startDate", startDate);
    }

    public void setCronTime(String cronTime) {
        loadConn.setProperty("cronTime", cronTime);
    }

    public void setMailpassto(String mailpassto) {
        loadConn.setProperty("mailpassto", mailpassto);
    }

    public void setMailpasscc(String mailpasscc) {
        loadConn.setProperty("mailpasscc", mailpasscc);
    }

    public void setMailfailto(String mailfailto) {
        loadConn.setProperty("mailfailto", mailfailto);
    }

    public void setMailfailcc(String mailfailcc) {
        loadConn.setProperty("mailfailcc", mailfailcc);
    }

    public void setStatusFilePath(String statusFilePath) {
        loadConn.setProperty("statusFilePath", statusFilePath);
    }

    public void setMailConfiguration(String mailConfiguration) {
        loadConn.setProperty("mailConfiguration", mailConfiguration);
    }

    public void saveProperties() throws FileNotFoundException, IOException {
        loadConn.store(new FileOutputStream(connectionFile), new Date().toString());
    }

}
