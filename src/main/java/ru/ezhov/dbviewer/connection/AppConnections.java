package ru.ezhov.dbviewer.connection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ezhov_da
 */
@XStreamAlias("root")
public class AppConnections {
	private static final String NAME_FILE_CONNECTION = "connections.xml";
	public transient static AppConnections INSTANCE;
	/**
	 * папка в которой лежат драйвера
	 */
	@XStreamAlias("folderDriver")
	private String folderDriver = "drivers";
	/**
	 * список с подключениями
	 */
	@XStreamAlias("listConnection")
	private List<AppConnection> listConnection = new ArrayList<AppConnection>(100);

	/**
	 * загружаем все подключения из файла с настройками
	 * <p>
	 *
	 * @throws IOException
	 */
	public static void loadConnections() throws IOException {
		FileWriter fileWriter = null; //для тестирования записи
		InputStreamReader fileReader = null;
		try {
			XStream xStream = new XStream(new DomDriver());
//читаем объект---------------------------------------------------------------------------------------------------------------------
			fileReader = new InputStreamReader(new FileInputStream(new File(NAME_FILE_CONNECTION)), "UTF8");
			Annotations.configureAliases(xStream, AppConnections.class);
			Annotations.configureAliases(xStream, AppConnection.class);
			INSTANCE = (AppConnections) xStream.fromXML(fileReader);
//пишем объект----------------------------------------------------------------------------------------------------------------------
//			INSTANCE = new AppConnections();
//			AppConnection appConnection = new AppConnection();
//			Properties properties = new Properties();
//			properties.put("USER", "SAZ_ADM");
//			properties.put("PASSWORD", "HEAD_ADM");
//			properties.put("DATABASE", "AZ");
//			properties.put("CHARSET", "UTF-8");
//			appConnection.setProperties(properties);
//			INSTANCE.listConnection.add(appConnection);
//			fileWriter = new FileWriter(new File(NAME_FILE_CONNECTION));
//			Annotations.configureAliases(xStream, AppConnections.class);
//			Annotations.configureAliases(xStream, AppConnection.class);
//			xStream.toXML(INSTANCE, fileWriter);
//------------------------------------------------------------------------------------------------------------------------------------------
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			AppConnections.loadConnections();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * получить список с подключениями
	 * <p>
	 *
	 * @return
	 */
	public List<AppConnection> getListConnection() {
		return listConnection;
	}

	public String getFolderDriver() {
		return folderDriver;
	}
}
