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

    private String srcDiffCount, originalTrgFile, feedName, oldModTime, newModTime, status, trgDiffCount, originalSrcFile, srcCount, trgCount, diffFile;

    public StatusProperties(String statusFile) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(statusFile));

        srcDiffCount = properties.getProperty("srcDiffCount");
        originalTrgFile = properties.getProperty("originalTrgFile");
        feedName = properties.getProperty("feedName");
        oldModTime = properties.getProperty("oldModTime");
        newModTime = properties.getProperty("newModTime");
        status = properties.getProperty("status");
        trgDiffCount = properties.getProperty("trgDiffCount");
        originalSrcFile = properties.getProperty("originalSrcFile");
        srcCount = properties.getProperty("srcCount");
        trgCount = properties.getProperty("trgCount");
        diffFile = properties.getProperty("diffFile");

    }

    public String getSrcDiffCount() {
        return srcDiffCount;
    }

    public String getOriginalTrgFile() {
        return originalTrgFile;
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

    public String getTrgDiffCount() {
        return trgDiffCount;
    }

    public String getOriginalSrcFile() {
        return originalSrcFile;
    }

    public String getSrcCount() {
        return srcCount;
    }

    public String getTrgCount() {
        return trgCount;
    }

    public String getDiffFile() {
        return diffFile;
    }

}
