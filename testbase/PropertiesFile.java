package com.tribu.qaselenium.testframework.testbase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesFile {

	// Create a object for class properties
	static Properties prop = new Properties();
	// defining the project path
	static String projectPath = System.getProperty("user.dir");

	public static String getProperties(String key) {
		try {
			// create a object for class InputStream
			InputStream input = new FileInputStream(projectPath + "/src/test/resources/configfile/config.properties");
			// Load properties file
			prop.load(input);
			// get values from properties file
			return prop.getProperty(key);
		} catch (Exception exp) {
			System.out.println(exp.getMessage());
			System.out.println(exp.getCause());
			exp.printStackTrace();
			return null;
		}
	}

	public static void setProperties(String propertieTitle, String key, String propertieValue) {
		try {
			// create a object for class OuputStream
			OutputStream output = new FileOutputStream(
					projectPath + "/src/test/resources/configfile/config.properties");
			// Load properties file
			prop.setProperty(key, propertieValue);
			// store values i properties file
			prop.store(output, propertieTitle);
		} catch (Exception exp) {
			System.out.println(exp.getMessage());
			System.out.println(exp.getCause());
			exp.printStackTrace();
		}
	}
}
