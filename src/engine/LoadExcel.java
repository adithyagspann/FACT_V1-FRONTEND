/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author Adithya
 */
public class LoadExcel {

    private Workbook excelFile;
    private Sheet workSheet;

    public LoadExcel(String statusFile) throws IOException, BiffException {
        excelFile = Workbook.getWorkbook(new File(statusFile));
    }

    public ObservableList getExcelData(String timeStamp) {
        System.out.println("getExcelData called");
        workSheet = excelFile.getSheet(getSheetName(timeStamp));

        
        ObservableList data = FXCollections.observableArrayList();
        for (int row = 1; row < workSheet.getRows(); row++) {
            ObservableList excelData = FXCollections.observableArrayList();
            for (int col = 0; col < workSheet.getColumns(); col++) {

                excelData.add(workSheet.getCell(col, row).getContents());
            }

            data.add(excelData);
        }

        return data;
    }

    public List getColName(String timeStamp) {
        System.out.println("getColName called");
        workSheet = excelFile.getSheet(getSheetName(timeStamp));
        List colNames = new ArrayList();
        for (int col = 0; col < workSheet.getColumns(); col++) {
            colNames.add(workSheet.getCell(col, 0).getContents());
        }
        return colNames;
    }

    public String getSheetName(String timeStamp) {
        System.out.println("getSheetName called");
        String[] sheetNames = excelFile.getSheetNames();
        String sheetName = "";
        for (String sName : sheetNames) {
            if (sName.contains(timeStamp)) {
                sheetName = sName;
                break;
            }
        }
        return sheetName;
    }

    public static void main(String args[]) {
        try {
            LoadExcel excel = new LoadExcel("C:\\Users\\Admin\\Desktop\\CSV_Data\\attr_promo_20170717001028_Results.xls");
            System.out.println("Columns: \n" + excel.getExcelData("20170717001028"));
        } catch (IOException ex) {
            Logger.getLogger(LoadExcel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BiffException ex) {
            Logger.getLogger(LoadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
