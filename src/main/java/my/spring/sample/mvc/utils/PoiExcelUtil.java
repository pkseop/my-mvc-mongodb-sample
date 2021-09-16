package my.spring.sample.mvc.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;

public class PoiExcelUtil {

    public static String getCellValue(XSSFCell xssfCell) {
        if(xssfCell == null)
            return null;
        switch(xssfCell.getCellType()) {
            case NUMERIC:
                double v = xssfCell.getNumericCellValue();
                int i = (int)v;
                return String.valueOf(i);
            case BOOLEAN:
                boolean b = xssfCell.getBooleanCellValue();
                return String.valueOf(b);
            default:
                return xssfCell.getStringCellValue();
        }
    }
}
