package com.tribu.qaselenium.testframework.testbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Supplier;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.tribu.qaselenium.testframework.pagebase.BasePO;
import com.tribu.qaselenium.testframework.pagebase.GUtils;

public class TestUtilities {

	protected String testSuiteName;
	protected String testName;
	protected String testMethodName;
	protected Logger log;

	protected Supplier<WebDriver> driver = () -> DriverFactory.getInstance().getDriver();

	// Static Sleep
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public <T extends BasePO<T>> Supplier<T> openUrl(Supplier<T> pageSupplier) {
		driver.get().get(pageSupplier.get().getPageUrl());
		GUtils.waitForPageToLoad(driver.get());
		return pageSupplier;
	}

	// open an url with a delay of 2 seconds
	public <T extends BasePO<T>> Supplier<T> openUrl(Supplier<T> pageSupplier, long delay) {
		driver.get().get(pageSupplier.get().getPageUrl());
		GUtils.waitForPageToLoad(driver.get());
		sleep(delay);
		return pageSupplier;
	}

	/** Take screenshot file png. return path */
	protected String takeScreenshot(String fileName) {
		File scrFile = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.FILE);
		String path = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "screenshots"
				+ File.separator + getTodaysDate() + File.separator + testSuiteName + File.separator + testName
				+ File.separator + testMethodName + File.separator + getSystemTime() + " " + fileName + ".png";
		try {
			FileUtils.copyFile(scrFile, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	/** Take screenshot base64, return file encoded */
	protected String takeScreenshot() {
		String scrFileEncoded = "";
		try {
			scrFileEncoded = ((TakesScreenshot) driver.get()).getScreenshotAs(OutputType.BASE64);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "data:image/png;base64," + scrFileEncoded;
	}

	/** Todays date in yyyyMMdd format */
	protected String getTodaysDate() {
		return "-" + new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	/** Current time in HHmmssSSS */
	protected String getSystemTime() {
		return "-" + new SimpleDateFormat("HHmmssSSS").format(new Date());
	}

	// provide credentials from the credentials csv file at the default path
	public Map<String, String> readCredentials() {
		String credentialsPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "credentials" + File.separator + "credentials.csv";
		Iterator<Map<String, String>> dataSet;
		dataSet = csvReader(credentialsPath);
		Map<String, String> dataMap = null;
		while (dataSet.hasNext()) {
			dataMap = dataSet.next();
			if (dataMap.get("environment").equals(PropertiesFile.getProperties("env"))) {
				return dataMap;
			}
		}
		return null;
	}

	public Iterator<Map<String, String>> csvReader(String pathname) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					String todo = dataParts[0];
					if (todo.contentEquals("TRUE")) {
						Map<String, String> testData = new HashMap<String, String>();
						for (int i = 0; i < keys.length; i++) {
							testData.put(keys[i], dataParts[i]);
						}
						list.add(testData);
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + pathname + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + pathname + " file.\n" + e.getStackTrace().toString());
		} catch (CsvValidationException e) {
			throw new RuntimeException(
					"Could not read next line in csv file" + pathname + "\n" + e.getStackTrace().toString());
		}
		return list.iterator();
	}

	public Iterator<Map<String, String>> xlsxReader(String pathname) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// Read the XLSX file into FileInputStream object
		FileInputStream input_document = new FileInputStream(new File(pathname));
		// Access input workbook in XSSFWorkbook object
		XSSFWorkbook xls_workbook = new XSSFWorkbook(input_document);
		// Access input worksheet
		XSSFSheet worksheet = xls_workbook.getSheetAt(0);
		// To iterate over the rows
		Iterator<Row> rowIterator = worksheet.iterator();
		Row keys = rowIterator.next();
		if (keys != null) {
			Row dataParts;
			while (rowIterator.hasNext()) {
				dataParts = rowIterator.next();
				Map<String, String> testData = new HashMap<String, String>();
				Iterator<Cell> cellIterator = dataParts.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) { // Identify CELL type
					// you need to add more code here based on
					// your requirement / transformations
					case STRING:
						testData.put(keys.getCell(cell.getColumnIndex()).getStringCellValue(),
								cell.getStringCellValue());
						break;
					case NUMERIC:
						testData.put(keys.getCell(cell.getColumnIndex()).getStringCellValue(),
								String.valueOf((int) cell.getNumericCellValue()));
						break;
					default:
						break;
					}
				}
				list.add(testData);
			}
		}
		input_document.close(); // close xlsx file
		return list.iterator();
	}

	public void convertXlsxToCsv(String xlsxPathname, String csvPathname) throws Exception {
		// Read the XLSX file into FileInputStream object
		FileInputStream input_document = new FileInputStream(new File(xlsxPathname));
		// Access input workbook in XSSFWorkbook object
		XSSFWorkbook my_xls_workbook = new XSSFWorkbook(input_document);
		// Access input worksheet
		XSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
		// To iterate over the rows
		Iterator<Row> rowIterator = my_worksheet.iterator();
		FileWriter my_csv = new FileWriter(csvPathname);
		CSVWriter my_csv_output = new CSVWriter(my_csv);
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			int i = 0;// String array
			// the example input xlsx has only two columns.
			String[] csvdata = new String[row.getLastCellNum()];
			Iterator<Cell> cellIterator = row.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next(); // Fetch CELL
				switch (cell.getCellType()) { // Identify CELL type
				// you need to add more code here based on
				// your requirement / transformations
				case STRING:
					csvdata[i] = cell.getStringCellValue();
					break;
				case NUMERIC:
					csvdata[i] = String.valueOf((int) cell.getNumericCellValue());
					break;
				default:
					break;
				}
				i = i + 1;
			}
			my_csv_output.writeNext(csvdata);
		}
		my_csv_output.close(); // close the CSV file
		// we created CSV from XLSX!
		input_document.close(); // close xlsx file
	}

	public void csvWriter(String[] keys, String[] value) {

		String tempFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "temp.csv";

		File file = new File(tempFilePath);
		if (file.exists()) {
			log.info("removing temp.scv file");
			file.delete();
		} else {
			log.info("creating temp.scv file");
			file.mkdirs();
		}

		try {
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);

			// create a List which contains String array
			List<String[]> data = new ArrayList<String[]>();
			data.add(keys);
			data.add(value);
			writer.writeAll(data);

			// closing writer connection
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void jsonFileWriter(Method method, String key, String value) {

		String tempFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "jsonData.json";

		String className = method.getDeclaringClass().getSimpleName();
		JSONObject testObj = new JSONObject();
		JSONObject classObj = new JSONObject();
		testObj.put(key, value);
		classObj.put(className, testObj);

		File file = new File(tempFilePath);

		if (file.exists()) {
			log.info("removing temp.scv file");
			file.delete();
		} else {
			log.info("creating temp.scv file");
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			FileWriter outputfile = new FileWriter(file);
			outputfile.write(classObj.toJSONString());
			outputfile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String jsonFileReader(String className, String key) {

		String tempFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "jsonData.json";
		
		File file = new File(tempFilePath);
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(file)) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);
			JSONObject classObj = (JSONObject) obj;
			JSONObject classNameObj = (JSONObject) classObj.get(className);
			log.info(classNameObj);
			String value = (String) classNameObj.get(key);
			log.info(value);
		return value;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
