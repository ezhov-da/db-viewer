package ru.ezhov.dbviewer;

import javax.swing.*;
import java.awt.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Это класс логирования для вывода ошибок в окно логов
 * <p>
 *
 * @author ezhov_da
 */
public class OwnHandlerLog extends ConsoleHandler {
	private final JTextPane textPane = BasicFrame.BASIC_FRAME.gettextPaneLog();

	@Override
	public void publish(LogRecord record) {
		super.publish(record);
		if (record.getLevel() != Level.SEVERE) {
			return;
		}
		textPane.setText(record.getThrown().getLocalizedMessage());
		textPane.setForeground(Color.red);
	}

	@Override
	public void flush() {
		super.flush();
	}

	@Override
	public void close() throws SecurityException {
		super.close();
	}
}
