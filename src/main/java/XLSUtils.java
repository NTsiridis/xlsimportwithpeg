/*
  Copyright (c) 2020 Tsiridis Nikolaos

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for manipulating xls files
 */
public class XLSUtils {


    /**
     * Read data from an xlsx file and convert it to simple
     * string
     *
     * @param xlsxStream InputStream with the data
     * @param sheetNo The number of the excel sheet to process (starting from 0 for the first sheet)
     * @param cellSep String to use as a separator for columns
     * @param rowSep  String to use as a separator for lines
     * @return The converted stream
     * @throws IOException if an error occurs during parsing
     */
    public static char[] stringify(InputStream xlsxStream, int sheetNo, String cellSep, String rowSep)
            throws
            IOException {

        StringBuffer buffer = new StringBuffer();
        Workbook wb = new XSSFWorkbook(xlsxStream);
        DataFormatter formatter = new DataFormatter();

        if (0 > sheetNo || sheetNo >= wb.getNumberOfSheets() ) {
            throw new IllegalArgumentException("Invalid sheet number:" + sheetNo + ". Allowed 0-" + (wb.getNumberOfSheets() - 1));
        }
        Sheet sheet = wb.getSheetAt(sheetNo);
        for (int r = 0, rn = sheet.getLastRowNum() ; r <= rn ; r++) {
            Row row = sheet.getRow(r);
            if ( row == null ) { buffer.append(rowSep); continue; }
            for (int c = 0, cn = row.getLastCellNum() ; c < cn ; c++) {

                Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if ( cell == null ) {
                    buffer.append(cellSep);
                    continue;
                }
                //cell is not empty
                String value = formatter.formatCellValue(cell);
                buffer.append(value.trim());
                buffer.append(cellSep);
            }
            buffer.append(rowSep);
        }
        return buffer.toString().toCharArray();
    }




}
