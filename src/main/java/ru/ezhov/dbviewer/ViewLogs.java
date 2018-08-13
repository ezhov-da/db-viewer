package ru.ezhov.dbviewer;

import ru.ezhov.dbviewer.connection.AppConnections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author ezhov_da
 */
public class ViewLogs {
    private static final Logger LOG = Logger.getLogger(ViewLogs.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AppConnections.loadConnections();   //загружаем список подключений
            hideToTray();   //сворачиваем в трей
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    BasicFrame.BASIC_FRAME.setVisible(true);
                }
            });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Не удалось загрузить приложение", "Ошибка", JOptionPane.ERROR_MESSAGE);
            LOG.log(Level.OFF, "ошибка при открытии приложения", ex);
        }
    }

    private static void hideToTray() throws AWTException {
        SystemTray systemTray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(new ImageIcon(ViewLogs.class.getResource("/developer_16x16.png")).getImage(), "контроль логов");
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BasicFrame.BASIC_FRAME.setVisible(true);
            }
        });
        systemTray.add(trayIcon);
    }

    static {
        try {
            LogManager.getLogManager().readConfiguration(ViewLogs.class.getResourceAsStream("/logger.properties"));
        } catch (Exception ex) {
            Logger.getLogger(ViewLogs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
