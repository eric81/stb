package com.eudemon.taurus.app.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.eudemon.taurus.app.utils.SpringUtil;

@Repository
public class Config {
	private static Configuration ppConfig = null;
	private static Configuration dbConfig = null;
	private static CompositeConfiguration config = null;
	private static Logger logger =  LoggerFactory.getLogger(Config.class);
	
	/**
	 * Configuration about system enviroment
	 */
	public static String osName = System.getProperty("os.name");
	public static String javaVersion = SystemUtils.JAVA_RUNTIME_VERSION;
	public static String userHome = SystemUtils.USER_HOME;

	public static void initFromFile() throws Exception {
		ppConfig = new PropertiesConfiguration("config/config.properties");
		config = new CompositeConfiguration();
		config.addConfiguration(ppConfig);
	}
	
	public static void initFromDatabase() throws Exception {
		DataSource dataSource = (DataSource) SpringUtil.getBean("dataSource");
		dbConfig = new DatabaseConfiguration(dataSource, "aconfig", "ckey", "cvalue");
		config.addConfiguration(dbConfig);
	}
	
	public static String getString(String key){
		return config.getString(key);
	}
	
	public static int getInt(String key){
		return config.getInt(key);
	}
	
	public static boolean getBoolean(String key){
		return config.getBoolean(key);
	}

	public static List<String> info() {
		List<String> ls = new ArrayList<String>();
		int headSize = 120;
		int itemSize = 30;
		String spliter = " : ";
		
		ls.add(StringUtils.center("Configuration Information Start", headSize, "-"));
		ls.add(StringUtils.rightPad(" OS Name", itemSize, "") + spliter + osName);
		ls.add(StringUtils.rightPad(" Java Runtime Version", itemSize, "") + spliter + javaVersion);
		ls.add(StringUtils.rightPad(" User Home", itemSize, "") + spliter + userHome);
		ls.add(" ");
		Iterator<String> keyIt = config.getKeys();
		while(keyIt.hasNext()){
			String key = keyIt.next();
			String value = config.getString(key);
			ls.add(StringUtils.rightPad(" " + key, itemSize, "") + spliter + value);
		}
		ls.add(StringUtils.center("Configuration Information End", headSize, "-"));
		return ls;
	}

	public static void logInfo() {
		for (String msg : info()) {
			logger.info(msg);
		}
	}
}
