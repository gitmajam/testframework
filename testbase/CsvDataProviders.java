package com.tribu.seleniumframework.testbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvDataProviders {
	protected static Logger log;
	/**
	 * This method return an iterator of an array list containing all data sets
	 * found in the CSV file, the array contains HashMap objects and each HashMap
	 * contains pairs made up of a key and its related value.
	 * 
	 * for each register the data provider runs an individual test which applies the
	 * related data to that test.
	 * 
	 * if the parallel argument is true then the dataprovider runs test in parallel
	 * according to the number of register in the csv file.
	 * 
	 * if the parallel argument is false then the tests are run sequentially
	 */

	@DataProvider(name = "csvReader", parallel = true)
	public static Iterator<Object[]> csvReader(Method method) {
		log = LogManager.getLogger("logger csvReader");
		log.info("Se ejecuta csvReader()");
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "dataproviders" + File.separator + method.getDeclaringClass().getSimpleName() + File.separator
				+ method.getName() + ".csv";

		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					Map<String, String> testData = new HashMap<String, String>();
					for (int i = 0; i < keys.length; i++) {
						testData.put(keys[i], dataParts[i]);
					}
					list.add(new Object[] { testData });
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
}
