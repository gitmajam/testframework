package com.tribu.qaselenium.testframework.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.ITestContext;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

<<<<<<< HEAD
import jdk.internal.org.jline.utils.Log;

=======
>>>>>>> 754e8cfd5eb81aeb615d6a1373f34c7278658734
public class DataReaders {

	/** csv readers **/

	public static List<Map<String, String>> csvReaderList(String pathname) {
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
		return list;
	}
	
	public static Iterator<Map<String, String>> csvReader(String pathname) {
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

	public static Map<String, String> csvDictionaryReader(String language) {
		Map<String, String> dictionary = new HashMap<String, String>();
		String translationFile = language.contentEquals("es") ? "dictionaryEsEs.csv" : "dictionaryEsEn.csv";
		String translationsPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "providerFiles" + File.separator + translationFile;

		File file = new File(translationsPath);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] dataParts;
			while ((dataParts = reader.readNext()) != null) {
				dictionary.put(dataParts[0], dataParts[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + translationsPath + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not read " + translationsPath + " file.\n" + e.getStackTrace().toString());
		} catch (CsvValidationException e) {
			throw new RuntimeException(
					"Could not read next line in csv file" + translationsPath + "\n" + e.getStackTrace().toString());
		}
		return dictionary;
	}

	/** xlsx readers **/

	public static Iterator<Map<String, String>> xlsxReader(String pathname) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// Read the XLSX file into FileInputStream object
		try {
			FileInputStream input_document;
			input_document = new FileInputStream(new File(pathname));

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
						switch (cell.getCellType()) {
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
			// close xlsx file
			input_document.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list.iterator();
	}

	public static void convertXlsxToCsv(String xlsxPathname, String csvPathname) throws Exception {
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

	/** JSON readers **/

	public static String jsonFileReader(ITestContext context, String test, String className, String key) {

		String tempFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "jsonData.json";

		File file = new File(tempFilePath);
		Object mainObj;

		String suiteName = context.getSuite().getName();
		String testName = test;

		JSONObject jsonObj = new JSONObject();
		JSONObject suiteObj = new JSONObject();
		JSONObject testObj = new JSONObject();
		JSONObject classObj = new JSONObject();

		// get JSON
		try (FileReader reader = new FileReader(file)) {
			// Read JSON file
			JSONParser jsonParser = new JSONParser();
			mainObj = jsonParser.parse(reader);
			jsonObj = (JSONObject) mainObj;

			if (((JSONObject) jsonObj.get(suiteName)) != null) {
				suiteObj = (JSONObject) jsonObj.get(suiteName);
				if (((JSONObject) suiteObj.get(testName)) != null) {
					testObj = (JSONObject) suiteObj.get(testName);
					if (((JSONObject) testObj.get(className)) != null) {
						classObj = (JSONObject) testObj.get(className);
						if (((String) classObj.get(key)) != null) {
							return (String) classObj.get(key);
						}
					}
				}
			}
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
