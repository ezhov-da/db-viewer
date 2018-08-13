package ru.ezhov.dbviewer;

import ru.ezhov.dbviewer.connection.AppConnection;
import ru.ezhov.dbviewer.connection.AppConnections;
import ru.ezhov.dbviewer.connection.LoadDrivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ezhov_da
 */
public final class ConnectionReview {
	private static final Logger LOG = Logger.getLogger(ConnectionReview.class.getName());
	private static Connection connection;

	private ConnectionReview() {
	}

	public static Connection getConnection() {
		if (connection == null) {
			try {
				Class.forName("com.teradata.jdbc.TeraDriver");
				//connection = DriverManager.getConnection(connectionString);
				connection = DriverManager.getConnection(AppConnections.INSTANCE.getListConnection().get(0).getUrl(), AppConnections.INSTANCE.getListConnection().get(0).getProperties());
			} catch (Exception ex) {
				LOG.log(Level.SEVERE, "не удалось получить соединение", ex);
			}
		}
		return connection;
	}

	/**
	 * Загружаем выбранный драйвер и получаем подключение
	 * <p>
	 *
	 * @param appConnection <p>
	 * @throws Exception
	 */
	public static void setConnection(AppConnection appConnection) throws Exception {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException ex) {
				Logger.getLogger(ConnectionReview.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		LOG.log(Level.INFO, "начали загрузку драйвера: {0}", appConnection.getClassForName());
		LoadDrivers.load(appConnection);
		LOG.info("получаем подключение");
		connection = LoadDrivers.getConnection();
		LOG.info("подключение создано");
	}

	/**
	 * закрываем подключение
	 */
	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException ex) {
				Logger.getLogger(ConnectionReview.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
