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
public class StatusProperties {

    private String newDiffCount, originalNewFile, feedName, oldModTime, newModTime, status, oldDiffCount, originalOldFile, newCount, oldCount, diffFile, connFile, mailFile;

    public StatusProperties(String statusFile) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(statusFile));

        newDiffCount = properties.getProperty("newDiffCount");
        originalOldFile = properties.getProperty("originalOldFile");
        feedName = properties.getProperty("feedName");
        oldModTime = properties.getProperty("oldModTime");
        newModTime = properties.getProperty("newModTime");
        status = properties.getProperty("status");
        oldDiffCount = properties.getProperty("oldDiffCount");
        originalNewFile = properties.getProperty("originalNewFile");
        newCount = properties.getProperty("newCount");
        oldCount = properties.getProperty("oldCount");
        diffFile = properties.getProperty("diffFile");
        connFile = properties.getProperty("connFile");
        mailFile = properties.getProperty("mailConfig");
    }

    public String getMailFile() {
        return mailFile;
    }

    public String getConnFile() {
        return connFile;
    }

    public String getNewDiffCount() {
        return newDiffCount;
    }

    public String getOriginalOldFile() {
        return originalOldFile;
    }

    public String getFeedName() {
        return feedName;
    }

    public String getOldModTime() {
        return oldModTime;
    }

    public String getNewModTime() {
        return newModTime;
    }

    public String getStatus() {
        return status;
    }

    public String getOldDiffCount() {
        return oldDiffCount;
    }

    public String getOriginalNewFile() {
        return originalNewFile;
    }

    public String getOldCount() {
        return oldCount;
    }

    public String getNewCount() {
        return newCount;
    }

    public String getDiffFile() {
        return diffFile;
    }

}
