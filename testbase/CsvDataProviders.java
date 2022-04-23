package com.tribu.qaselenium.testframework.testbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvDataProviders {

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
		String path = null;

		// accesing to classfield from caller class by reflection
		try {
			Field field = method.getDeclaringClass().getDeclaredField("dataProviderFilePath");
			Object testObj = method.getDeclaringClass().getDeclaredConstructor().newInstance();
			path = (String) field.get(testObj);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		List<Object[]> list = new ArrayList<Object[]>();
		File file = new File(path);
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
						list.add(new Object[] { testData });
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + path + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + path + " file.\n" + e.getStackTrace().toString());
		} catch (CsvValidationException e) {
			throw new RuntimeException(
					"Could not read next line in csv file" + path + "\n" + e.getStackTrace().toString());
		}

		return list.iterator();
	}

	// dataprovider filter by environment
	@DataProvider(name = "csvReaderEnvironment", parallel = false)
	public static Iterator<Object[]> csvReaderEnvironment(Method method) {
		String environment = PropertiesFile.getProperties("env");
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ method.getDeclaringClass().getSimpleName() + File.separator + "dataproviders" + File.separator
				+ method.getName() + ".csv";

		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					String todo = dataParts[0];
					String env = dataParts[1];
					if (todo.contentEquals("TRUE")) {
						if (env.contentEquals(environment)) {
							Map<String, String> testData = new HashMap<String, String>();
							for (int i = 0; i < keys.length; i++) {
								testData.put(keys[i], dataParts[i]);
							}
							list.add(new Object[] { testData });
						}
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

	// dataprovider Credentials by environment
	@DataProvider(name = "csvReaderCredentials", parallel = false)
	public static Iterator<Object[]> csvReaderCredentials(Method method) {
		String environment = PropertiesFile.getProperties("env");
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "credentials" + File.separator + "credentials.csv";

		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					String todo = dataParts[0];
					String env = dataParts[1];
					if (todo.contentEquals("TRUE")) {
						if (env.contentEquals(environment)) {
							Map<String, String> testData = new HashMap<String, String>();
							for (int i = 0; i < keys.length; i++) {
								testData.put(keys[i], dataParts[i]);
							}
							list.add(new Object[] { testData });
						}
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

	// this dataprovider search for method, name with a diferent file per method
	@DataProvider(name = "csvReaderMethod", parallel = false)
	public static Iterator<Object[]> csvReaderMethod(Method method, String browser) {
		List<Object[]> list = new ArrayList<Object[]>();
		String pathname = "src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ method.getDeclaringClass().getSimpleName() + File.separator + "dataproviders" + File.separator
				+ method.getName() + ".csv";

		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					if (method.getName().equals(dataParts[1]) || method.getName().contains(dataParts[1])) {
						Map<String, String> testData = new HashMap<String, String>();
						for (int i = 0; i < keys.length; i++) {
							testData.put(keys[i], dataParts[i]);
						}
						list.add(new Object[] { testData });
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

	// this dataprovider search for method in the same file
	@DataProvider(name = "csvReaderMethodFile", parallel = false)
	public static Iterator<Object[]> csvReaderMethodFile(Method method) {
		String path = null;

		// accesing to classfield from caller class by reflection
		try {
			Field field = method.getDeclaringClass().getDeclaredField("dataProviderFilePath");
			Object testObj = method.getDeclaringClass().getDeclaredConstructor().newInstance();
			path = (String) field.get(testObj);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// provider
		List<Object[]> list = new ArrayList<Object[]>();
		File file = new File(path);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					String todo = dataParts[0];
					if (todo.contentEquals("TRUE")) {
						if (method.getName().equals(dataParts[1]) || method.getName().contains(dataParts[1])
								|| method.getName().equals("")) {
							Map<String, String> testData = new HashMap<String, String>();
							for (int i = 0; i < keys.length; i++) {
								testData.put(keys[i], dataParts[i]);
							}
							list.add(new Object[] { testData });
						}
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + path + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + path + " file.\n" + e.getStackTrace().toString());
		} catch (CsvValidationException e) {
			throw new RuntimeException(
					"Could not read next line in csv file" + path + "\n" + e.getStackTrace().toString());
		}

		return list.iterator();
	}

	// this dataprovider delivers a list (list of Maps) of lists
	@DataProvider(name = "csvReaderMatrix", parallel = false)
	public static Iterator<Object[]> csvReaderMatrix(Method method, ITestContext testContext) {
		String path = null;

		// accesing to classfield from caller class by reflection
		try {
			Field field = method.getDeclaringClass().getDeclaredField("dataProviderFilePath");
			Object testObj = method.getDeclaringClass().getDeclaredConstructor().newInstance();
			path = (String) field.get(testObj);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		List<Object[]> list = new ArrayList<Object[]>();
		List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
		File file = new File(path);
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
						dataList.add(testData);
					}
				}
				list.add(new Object[] { dataList });
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + path + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + path + " file.\n" + e.getStackTrace().toString());
		} catch (CsvValidationException e) {
			throw new RuntimeException(
					"Could not read next line in csv file" + path + "\n" + e.getStackTrace().toString());
		}

		return list.iterator();
	}

}
