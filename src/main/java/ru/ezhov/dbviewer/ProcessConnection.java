package ru.ezhov.dbviewer;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ezhov_da
 */
public class ProcessConnection {
    private static final Logger LOG = Logger.getLogger(ProcessConnection.class.getName());

    private ProcessConnection() {
    }

    public static synchronized void fillComboBox(JComboBox comboBox, Date date, String query) {
        try {
            Connection connection = ConnectionReview.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> list = getListComboBox(resultSet);
            fillCombo(comboBox, list);
            resultSet.close();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Получаем список
     * <p>
     *
     * @param resultSet <p>
     * @return
     */
    private static List<String> getListComboBox(ResultSet resultSet) {
        List<String> list = new ArrayList<String>(100);
        try {
            while (resultSet.next()) {
                list.add(resultSet.getString("prc_name"));
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Наполняем непосредственно комбобокс
     */
    private static void fillCombo(final JComboBox comboBox, final List<String> list) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
                comboBoxModel.removeAllElements();
                comboBoxModel.removeAllElements();
                for (String string : list) {
                    comboBoxModel.addElement(string);
                }
            }
        });
    }
}
