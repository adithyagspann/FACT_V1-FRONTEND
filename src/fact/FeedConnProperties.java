/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Adithya
 */
public class FeedConnProperties {

    private String mailpassto, mailfailto, fileheaderCheck, fetchType, filePath, remotePath, attachlog, fetchstartdate, username, fetchtime, mailpasscc, mailfailcc, passreptofail, feedname, hostname, password, fileType, remoteModTime;
    private Properties loadConn;
    private FileInputStream propFile;
    private String fileHeader;

    public FeedConnProperties(String connFile) throws FileNotFoundException, IOException {
        loadConn = new Properties();
        propFile = new FileInputStream(connFile);
        loadConn.load(propFile);

        mailpassto = loadConn.getProperty("mailpassto");
        mailfailto = loadConn.getProperty("mailfailto");
        fileheaderCheck = loadConn.getProperty("fileheaderCheck");
        fetchType = loadConn.getProperty("fetchType");

        attachlog = loadConn.getProperty("attachlog");
        fetchstartdate = loadConn.getProperty("fetchstartdate");
        username = loadConn.getProperty("username");
        fetchtime = loadConn.getProperty("fetchtime");
        mailpasscc = loadConn.getProperty("mailpasscc");
        mailfailcc = loadConn.getProperty("mailfailcc");
        passreptofail = loadConn.getProperty("passreptofail");
        feedname = loadConn.getProperty("feedname");
        hostname = loadConn.getProperty("hostname");
        password = loadConn.getProperty("password");
        fileType = loadConn.getProperty("fileType");
        remoteModTime = loadConn.getProperty("remoteModTime");
        remotePath = loadConn.getProperty("remotePath");

        propFile.close();

    }

    public String getRemoteModTime() {
        return remoteModTime;
    }

    public String getMailpassto() {
        return mailpassto;
    }

    public String getMailfailto() {
        return mailfailto;
    }

    public String getFileheaderCheck() {
        return fileheaderCheck;
    }

    public String getFetchType() {
        return fetchType;
    }

    public String getFilePath() {
        filePath = loadConn.getProperty("filePath");
        return filePath;
    }

    public String getRemotePath() {

        return remotePath;
    }

    public String getAttachlog() {
        return attachlog;
    }

    public String getFetchstartdate() {
        return fetchstartdate;
    }

    public String getUsername() {
        return username;
    }

    public String getFetchtime() {
        return fetchtime;
    }

    public String getMailpasscc() {
        return mailpasscc;
    }

    public String getMailfailcc() {
        return mailfailcc;
    }

    public String getPassreptofail() {
        return passreptofail;
    }

    public String getFeedname() {
        return feedname;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPassword() {
        return password;
    }

    public String getFileType() {
        return fileType;
    }

    public Properties getLoadConn() {
        return loadConn;
    }

    public FileInputStream getPropFile() {
        return propFile;
    }
    
    public String getHeader()
    {
        fileHeader = loadConn.getProperty("fileheader");
        return fileHeader;
    }

}
