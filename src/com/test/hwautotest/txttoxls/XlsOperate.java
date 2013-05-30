package com.test.hwautotest.txttoxls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import android.os.SystemProperties;
import android.util.Log;



public class XlsOperate {

	private FileInputStream fileIn;
	private FileOutputStream fileOut;
	private String mFilePath;
	private String value;
	private String st_sum;
	private String id;
	private String cellValue;
	private String hwversion;
	private String swversion;
	private String project;
	private HSSFSheet sheet;
	private HSSFCell cell;
	private HSSFRow row;
	private HSSFWorkbook wb;
	private boolean isSearch;

	public XlsOperate(String mFilePath) {
		this.mFilePath = mFilePath;
	}

	public ArrayList<String> ReadTxtFile(String strFilePath) {
		String path = strFilePath;
		ArrayList<String> fileContent = new ArrayList<String>();
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (file.isDirectory()) {
			Log.d("TestFile", "The File doesn't not exist.");
		} else {
			try {
				InputStream instream = new FileInputStream(file);
				if (instream != null) {
					InputStreamReader inputreader = new InputStreamReader(
							instream, "gbk");
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line = null;
					// 分行读取
					while ((line = buffreader.readLine()) != null) {
						if (line.indexOf("finish") == -1) {
							fileContent.add(line.replace("|", "/"));
						}
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContent;
	}

	public int getCharacterPosition(String string, int n) {
		// 这里是获取"/"符号的位置
		Matcher slashMatcher = Pattern.compile("/").matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// 当"/"符号第n次出现的位置
			if (mIdx == n) {
				break;
			}
		}
		return slashMatcher.start();
	}

	public HashSet<String> fillResult(ArrayList<String> tasksummary,
			String caseName) {

		HashSet<String> sheetNames = new HashSet<String>();

		try {
			fileIn = new FileInputStream(mFilePath + caseName + ".xls");
			wb = new HSSFWorkbook(fileIn);
			
			// 设置单元格通用样式
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
//			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//填充模式
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);

			for (int k = 0; k < tasksummary.size(); k++) {
				isSearch = false;
				id = tasksummary.get(k).substring(0,
						tasksummary.get(k).indexOf("/"));
				value = tasksummary.get(k).substring(
						getCharacterPosition(tasksummary.get(k), 1) + 1,
						getCharacterPosition(tasksummary.get(k), 2));
				st_sum = tasksummary.get(k).substring(tasksummary.get(k).lastIndexOf("/") + 1);
				
				for (int i = 2; i < wb.getNumberOfSheets(); i++) {// 从第3个sheet开始循环
					sheet = wb.getSheetAt(i);

					for (int j = 1; j <= sheet.getLastRowNum(); j++) {// 从第2行开始
						if(caseName.equals("ft_testCase")){
							cellValue = (int) sheet.getRow(j).getCell(1).getNumericCellValue() + "";
						}else if(caseName.equals("st_testCase") && sheet.getRow(j).getCell(1) != null){
							
							cellValue = sheet.getRow(j).getCell(1).getStringCellValue()
									.substring(sheet.getRow(j).getCell(1).getStringCellValue().lastIndexOf("_") + 1);
							Log.i("YANG", cellValue);
						}

						if (id.equals(cellValue) && caseName.equals("ft_testCase")) {
							//设置字体
							HSSFFont font = wb.createFont();
							font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
							font.setFontName("Arial");
							style.setFont(font);
							
							sheetNames.add(sheet.getSheetName());// 将所有处理过的名字加入set里，保证唯一性
							cell = sheet.getRow(j).createCell(6,HSSFCell.CELL_TYPE_STRING);
							isSearch = true;
							if (value.equals("0")) {
								cell.setCellValue("Pass");
								cell.setCellStyle(style);
							} else if (value.equals("1")) {
								cell.setCellValue("Fail");
								cell.setCellStyle(style);
							}
							break;
						}else if(id.equals(cellValue) && caseName.equals("st_testCase")){
							isSearch = true;
							sheetNames.add(sheet.getSheetName());
							cell = sheet.getRow(j).createCell(6,HSSFCell.CELL_TYPE_NUMERIC);//此格为循环总次数
							cell.setCellStyle(style);
							cell.setCellValue(Integer.parseInt(st_sum));
							cell = sheet.getRow(j).createCell(7,HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellStyle(style);
							cell.setCellValue(Integer.parseInt(value));
							refreshFormula(sheet, j, j, 8, 8);//注意刷新范围！！！只刷新有公式的第8列
							break;
						}
					}
					if (isSearch) {
						break;
					}
				}
			}
			
			sheet = wb.getSheet("Cover");
			hwversion = SystemProperties.get("ro.gn.extHWvernumber");
			swversion = SystemProperties.get("ro.gn.extvernumber");
			project = "Gionee_Android_"
					+ swversion.substring(0, swversion.indexOf("_"));
			sheet.getRow(2).getCell(2).setCellValue(hwversion);
			sheet.getRow(3).getCell(2).setCellValue(swversion);
			if (caseName.equals("ft_testCase")) {
				
				// 注意模板如果修改了，必须修改刷新范围！！！
				refreshFormula(sheet, 6, 16, 2, 6);// 重新刷新公式
				// 填写项目名称
				sheet.getRow(1).getCell(2).setCellValue(project + "_Auto Test Case");

			}else if(caseName.equals("st_testCase")){
				refreshFormula(sheet, 6, 8, 2, 7);//注意刷新范围！！！
				sheet.getRow(1).getCell(2).setCellValue(project + "_Stable Test Case");
			}
			fileOut = new FileOutputStream(mFilePath + caseName + ".xls");
			wb.write(fileOut);
			fileOut.close();
			fileIn.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sheetNames;
	}

	public void test() {
		Log.i("YANG", HSSFCell.CELL_TYPE_BLANK + "----------3");
		Log.i("YANG", HSSFCell.CELL_TYPE_BOOLEAN + "----------4");
		Log.i("YANG", HSSFCell.CELL_TYPE_ERROR + "----------5");
		Log.i("YANG", HSSFCell.CELL_TYPE_FORMULA + "---------2-");
		Log.i("YANG", HSSFCell.CELL_TYPE_NUMERIC + "----------0");
		Log.i("YANG", HSSFCell.CELL_TYPE_STRING + "----------1");
	}

	public void removeRow(HSSFSheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			// 将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			HSSFRow removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}

	public void refreshFormula(HSSFSheet sheet, int rowStart, int rowEnd,
			int columnStart, int columnEnd) {

		for (int i = rowStart; i <= rowEnd; i++) {// 控制行数
			row = sheet.getRow(i);

			for (int j = columnStart; j <= columnEnd; j++) {// 控制列数
				cell = row.getCell(j);
				if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					cell.setCellFormula(cell.getCellFormula());
				}
			}
		}
	}

	public void removeSheetRow(HashSet<String> sheetNames, String caseName) {
		
		// 处理set
		Iterator<String> iter = sheetNames.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			// 移除各模块下多余的case
			try {
				fileIn = new FileInputStream(mFilePath + caseName + ".xls");
				wb = new HSSFWorkbook(fileIn);
				sheet = wb.getSheet(name);
				if(caseName.equals("ft_testCase")){
					// 移除
					for (int i = 1; i <= sheet.getLastRowNum(); i++) {
						if (sheet.getRow(i) == null) {
							removeRow(sheet, i);
							i--;
						} else if (sheet.getRow(i).getCell(6) == null) {
							removeRow(sheet, i);
							i--;
						} else if (sheet.getRow(i).getCell(6).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
							removeRow(sheet, i);
							i--;// 删除掉行之后要再处理一遍当前行
						} else if (!sheet.getRow(i).getCell(6).getStringCellValue().equals("Pass")
								&& !sheet.getRow(i).getCell(6).getStringCellValue().equals("Fail")) {
							removeRow(sheet, i);
							i--;
						}
					}

					// 重新定义index
					for (int j = 1; j <= sheet.getLastRowNum(); j++) {
						cell = sheet.getRow(j).getCell(0);
						if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
							cell.setCellValue(j);
						}
					}
				}

				 //清除cover下多余的字样
				sheet = wb.getSheet("Cover");
				for (int j = 0; j < sheet.getLastRowNum(); j++) {
					if(sheet.getRow(j) != null && sheet.getRow(j).getCell(1) !=null){//判断第J行的第1列不为空
						cell = sheet.getRow(j).getCell(1);
						if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
							if(sheet.getRow(j).getCell(10) != null
									&& sheet.getRow(j).getCell(1).getStringCellValue().equals(name)){//如果模块一样，则清除未测试字串
								sheet.getRow(sheet.getRow(j).getCell(1).getRowIndex()).getCell(10).setCellValue("");
							}
							if(sheet.getRow(j).getCell(11) != null
									&& sheet.getRow(j).getCell(1).getStringCellValue().equals(name)){
								sheet.getRow(sheet.getRow(j).getCell(1).getRowIndex()).getCell(11).setCellValue("");
							}
						}
					}
				}

				fileOut = new FileOutputStream(mFilePath + caseName + ".xls");
				wb.write(fileOut);
				fileOut.close();
				fileIn.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
