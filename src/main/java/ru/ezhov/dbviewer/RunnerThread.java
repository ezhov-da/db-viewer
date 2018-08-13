package ru.ezhov.dbviewer;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который отвечает за обработку информации
 * <p>
 *
 * @author ezhov_da
 */
public class RunnerThread extends Thread {
	private static final Logger LOG = Logger.getLogger(RunnerThread.class.getName());
	private final int interval;
	private final String query;
	private final JTable table;
	private boolean flagStop;

	public RunnerThread(int interval, String query, JTable table) {
		this.interval = interval;
		this.query = query;
		this.table = table;
	}

	@Override
	public void run() {
		while (true) {
			if (flagStop) {
				LOG.info("остановили поток");
				return;
			}
			LOG.info("обновляем таблицу");
			fillTable();
			try {
				Thread.sleep(interval * 60000);
			} catch (InterruptedException ex) {
				LOG.log(Level.SEVERE, null, ex);
			}
		}
	}

	private void fillTable() {
		try {
			Connection connection = ConnectionReview.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			ResultSet resultSet = preparedStatement.executeQuery();
			ResultSetMetaData rsmd = resultSet.getMetaData();
			//get heading
			final Vector<String> headings = new Vector<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				headings.add(rsmd.getColumnName(i));
			}
			//get data
			final Vector<Vector<String>> data = new Vector<Vector<String>>();
			Vector<String> column;
			while (resultSet.next()) {
				column = new Vector<String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					column.add(resultSet.getString(i));
				}
				data.add(column);
			}
			resultSet.close();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ProcessTable.clearAndFillTable(table, data, headings);
					LOG.info("обновили");
				}
			});
		} catch (SQLException ex) {
			LOG.log(Level.SEVERE, null, ex);
		}
	}

	public void setFlagStop(boolean flagStop) {
		this.flagStop = flagStop;
	}
}
