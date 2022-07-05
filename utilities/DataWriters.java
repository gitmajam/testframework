package com.tribu.qaselenium.testframework.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
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

public class DataWriters {
	protected static Logger log;

/**csv writers**/	
	
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

	
	/**JSON writers**/
	
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
				e.printStackTrace();
			}
		}

		try {
			FileWriter outputfile = new FileWriter(file);
			outputfile.write(classObj.toJSONString());
			outputfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void editTestJSON(Method method, ITestContext context, String key, String value) {

		String suiteName = context.getSuite().getName();
		String testName = context.getCurrentXmlTest().getName();
		String className = method.getDeclaringClass().getSimpleName();

		JSONObject suiteObj = new JSONObject();
		JSONObject testObj = new JSONObject();
		JSONObject classObj = new JSONObject();
		JSONObject jsonObj = new JSONObject();

		// find file
		String tempFilePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "jsonData.json";

		File file = new File(tempFilePath);

		if (!file.exists()) {
			log.info("creating json file");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// get JSON
			try (FileReader reader = new FileReader(file)) {
				// Read JSON file
				JSONParser jsonParser = new JSONParser();
				jsonObj = (JSONObject) jsonParser.parse(reader);

				if (((JSONObject) jsonObj.get(suiteName)) != null) {
					suiteObj = (JSONObject) jsonObj.get(suiteName);
					if (((JSONObject) suiteObj.get(testName)) != null) {
						testObj = (JSONObject) suiteObj.get(testName);
						if (((JSONObject) testObj.get(className)) != null) {
							classObj = (JSONObject) testObj.get(className);
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
		}

		classObj.put(key, value);
		testObj.put(className, classObj);
		suiteObj.put(testName, testObj);
		jsonObj.put(suiteName, suiteObj);

		try {
			FileWriter outputfile = new FileWriter(file);
			outputfile.write(jsonObj.toJSONString());
			outputfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
