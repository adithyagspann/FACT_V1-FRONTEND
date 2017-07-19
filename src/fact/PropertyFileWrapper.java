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

    public void setTimeZone(String timeZone) throws FileNotFoundException {
        commonProp.setProperty("timeZone", timeZone);

        commonProp.save(new FileOutputStream(fileName), new Date().toString());
        System.out.println("timeZone: " + commonProp.getProperty("timeZone"));
    }

    public void setTimeFormat(String timeFormat) throws FileNotFoundException {
        commonProp.setProperty("timeFormat", timeFormat);
        commonProp.save(new FileOutputStream(fileName), new Date().toString());
        System.out.println("timeFormat: " + commonProp.getProperty("timeFormat"));
    }

    public void setStatusPath(String statusPath) throws FileNotFoundException {
        commonProp.setProperty("statusPath", statusPath);
        commonProp.save(new FileOutputStream(fileName), new Date().toString());
        System.out.println("statusPath: " + commonProp.getProperty("statusPath"));
    }

    public void setFetchTime(String fetchTime) throws FileNotFoundException {
        commonProp.setProperty("fetchTime", fetchTime);
        commonProp.save(new FileOutputStream(fileName), new Date().toString());
        System.out.println("fetchTime: " + commonProp.getProperty("fetchTime"));
    }

    public void storeUpdate() throws FileNotFoundException {
        commonProp.save(new FileOutputStream(fileName), new Date().toString());
    }

}
