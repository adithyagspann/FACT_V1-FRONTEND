/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Admin
 */
public class PropertyFileWrapper {

    private Properties commonProp;
    private String timeZone, timeFormat, statusPath, fetchTime;
    private String fileName;

    public PropertyFileWrapper(String fileName) throws FileNotFoundException, IOException {
        this.fileName = fileName;
        commonProp = new Properties();
        commonProp.load(new FileInputStream(fileName));
        timeZone = commonProp.getProperty("timeZone");
        timeFormat = commonProp.getProperty("timeFormat");
        statusPath = commonProp.getProperty("statusPath");
        fetchTime = commonProp.getProperty("fetchTime");

    }

    public String getFetchTime() {
        return commonProp.getProperty("fetchTime");
    }

    public String getTimeZone() {
        return commonProp.getProperty("timeZone");
    }

    public String getTimeFormat() {
        return commonProp.getProperty("timeFormat");
    }

    public String getStatusPath() {
        return commonProp.getProperty("statusPath");
    }

    public void setTimeZone(String timeZone) {
        commonProp.setProperty("timeZone", timeZone);
    }

    public void setTimeFormat(String timeFormat) {
        commonProp.setProperty("timeFormat", timeFormat);
    }

    public void setStatusPath(String timeFormat) {
        commonProp.setProperty("timeFormat", timeFormat);
    }

    public void setFetchTime(String timeFormat) {
        commonProp.setProperty("timeFormat", timeFormat);
    }

    public void storeUpdate() throws FileNotFoundException {
        commonProp.save(new FileOutputStream(fileName), new Date().toString());
    }

}
