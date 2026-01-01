package util;

import entity.Student;
import entity.Score;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入导出工具类
 */
public class ExcelUtil {
    
    /**
     * 导出学生信息到Excel
     */
    public static void exportStudents(List<Object[]> data, String[] headers) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出学生信息");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel文件 (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("学生信息.xlsx"));
        
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("学生信息");
                
                // 创建标题样式
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                // 写入表头
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // 写入数据
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setAlignment(HorizontalAlignment.CENTER);
                
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Object[] rowData = data.get(i);
                    for (int j = 0; j < rowData.length; j++) {
                        Cell cell = row.createCell(j);
                        if (rowData[j] != null) {
                            cell.setCellValue(rowData[j].toString());
                        }
                        cell.setCellStyle(dataStyle);
                    }
                }
                
                // 自动调整列宽
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // 写入文件
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                
                JOptionPane.showMessageDialog(null, 
                    "导出成功！\n文件保存位置：" + file.getAbsolutePath(),
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, 
                    "导出失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 导出成绩信息到Excel
     */
    public static void exportScores(List<Object[]> data, String[] headers) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出成绩信息");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel文件 (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File("成绩信息.xlsx"));
        
        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("成绩信息");
                
                // 创建标题样式
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                
                // 写入表头
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // 创建数据样式
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setAlignment(HorizontalAlignment.CENTER);
                
                // 成绩不及格样式
                CellStyle failStyle = workbook.createCellStyle();
                failStyle.setAlignment(HorizontalAlignment.CENTER);
                Font failFont = workbook.createFont();
                failFont.setColor(IndexedColors.RED.getIndex());
                failStyle.setFont(failFont);
                
                // 写入数据
                for (int i = 0; i < data.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    Object[] rowData = data.get(i);
                    for (int j = 0; j < rowData.length; j++) {
                        Cell cell = row.createCell(j);
                        if (rowData[j] != null) {
                            String value = rowData[j].toString();
                            cell.setCellValue(value);
                            
                            // 检查是否是成绩列且不及格
                            if (j == 5) { // 假设第6列是成绩
                                try {
                                    double score = Double.parseDouble(value);
                                    if (score < 60) {
                                        cell.setCellStyle(failStyle);
                                        continue;
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                            cell.setCellStyle(dataStyle);
                        }
                    }
                }
                
                // 自动调整列宽
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // 写入文件
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                
                JOptionPane.showMessageDialog(null, 
                    "导出成功！\n文件保存位置：" + file.getAbsolutePath(),
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, 
                    "导出失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 从Excel导入学生数据
     */
    public static List<String[]> importStudents() {
        List<String[]> dataList = new ArrayList<>();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导入学生信息");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Excel文件 (*.xlsx, *.xls)", "xlsx", "xls"));
        
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = file.getName().endsWith(".xlsx") 
                     ? new XSSFWorkbook(fis) : new HSSFWorkbook(fis)) {
                
                Sheet sheet = workbook.getSheetAt(0);
                int rowCount = sheet.getPhysicalNumberOfRows();
                
                // 跳过表头，从第二行开始读取
                for (int i = 1; i < rowCount; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    
                    int cellCount = row.getPhysicalNumberOfCells();
                    String[] rowData = new String[cellCount];
                    
                    for (int j = 0; j < cellCount; j++) {
                        Cell cell = row.getCell(j);
                        rowData[j] = getCellValueAsString(cell);
                    }
                    
                    dataList.add(rowData);
                }
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, 
                    "导入失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return dataList;
    }
    
    /**
     * 获取单元格值为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                // 处理数字格式，避免科学计数法
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    return String.valueOf((long) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}