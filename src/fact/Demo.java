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
 * @author Admin
 */
public class Demo {

    public static void main(String args[]) throws FileNotFoundException, IOException {
     
        Properties d = new Properties();
        d.load(new FileInputStream("conn/mdc1vr1002_06_16_2017_00_16_24.properties"));
        System.out.println(d.get("fetchtime"));
    }
}
