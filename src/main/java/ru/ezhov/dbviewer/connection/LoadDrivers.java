package ru.ezhov.dbviewer.connection;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * класс отвечает за загрузку драйверов в систему
 * <p>
 *
 * @author ezhov_da
 */
public class LoadDrivers {
	private static final Logger LOG = Logger.getLogger(LoadDrivers.class.getName());
	private static File[] jars;
	private static AppConnection appConnection;
	private static Class pluginClass;
	private static Connection connection;

	private LoadDrivers() {
	}

	public static final synchronized void load(AppConnection appConnection)
		throws InstantiationException, IllegalAccessException, SQLException {
		LoadDrivers.appConnection = appConnection;
		readFolderDriver();
		loadClassLoader();
		createClasses();
	}

	/**
	 * загружаем jar из папки
	 */
	private static void readFolderDriver() {
		File pluginDir = new File(AppConnections.INSTANCE.getFolderDriver());
		jars = pluginDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".jar");
			}
		});
	}

	/**
	 * загружаем класс лоадеры
	 */
	private static void loadClassLoader() {
		try {
			URL[] jarURL = new URL[jars.length];
			for (int i = 0; i < jars.length; i++) {
				jarURL[i] = jars[i].toURI().toURL();
			}
			URLClassLoader classLoader = new URLClassLoader(jarURL);
			pluginClass = classLoader.loadClass(appConnection.getClassForName());
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	private static void createClasses() throws InstantiationException, IllegalAccessException, SQLException {
		Driver driver = (Driver) pluginClass.newInstance();
		if (appConnection.getProperties() == null) {
			connection = driver.connect(appConnection.getUrl(), new Properties());
		} else {
			connection = driver.connect(appConnection.getUrl(), appConnection.getProperties());
		}
	}

	public static Connection getConnection() {
		return connection;
	}
}
