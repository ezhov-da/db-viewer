package ru.ezhov.dbviewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * this class clear table and fill table (only data table without header)
 * <p>
 *
 * @author ezhov_da
 */
public class ProcessTable {
	/**
	 * clear table
	 * <p>
	 *
	 * @param table - what table clear
	 */
	public synchronized static void clearTable(final JTable table) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (table.getRowCount() > 0) {
					table.removeRowSelectionInterval(0, table.getRowCount() - 1);
					DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
					defaultTableModel.setRowCount(0);
				}
			}
		});
	}

	/**
	 * fill table data
	 * <p>
	 *
	 * @param table
	 * @param data
	 */
	public synchronized static void fillTable(final JTable table, final Vector<Vector<String>> data) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
				for (Vector<String> vector : data) {
					defaultTableModel.addRow(vector);
				}
				table.repaint();
			}
		});
	}

	public synchronized static void clearAndFillTable(JTable table, Vector<Vector<String>> data, Vector<String> head) {
		/* ������ ������� */
        /* ������� ������� */
		if (table.getColumnCount() > 0) {
			table.removeColumnSelectionInterval(0, table.getColumnCount() - 1);
			DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
			defaultTableModel.setColumnCount(0);
		}
        /* ������� ������ */
		if (table.getRowCount() > 0) {
			table.removeRowSelectionInterval(0, table.getRowCount() - 1);
			DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
			defaultTableModel.setRowCount(0);
		}
        /* ��������� ������� */
		DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
        /* ������ ����� */
		for (String h : head) {
			defaultTableModel.addColumn(h);
		}
        /* ������ ���������� */
		for (Vector<String> vector : data) {
			defaultTableModel.addRow(vector);
		}
	}

	/**
	 * ���� ����� ��������� ������� ���������, ������� ��� ����, ���� ����� �����������
	 * ������������ ������������
	 * <p>
	 *
	 * @param table
	 * @param data
	 * @param head
	 */
	public synchronized static void clearAndFillTableObjects(JTable table, Vector<Vector<Object>> data, Vector<String> head) {
        /* ������ ������� */
        /* ������� ������� */
		if (table.getColumnCount() > 0) {
			table.removeColumnSelectionInterval(0, table.getColumnCount() - 1);
			DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
			defaultTableModel.setColumnCount(0);
		}
        /* ������� ������ */
		if (table.getRowCount() > 0) {
			table.removeRowSelectionInterval(0, table.getRowCount() - 1);
			DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
			defaultTableModel.setRowCount(0);
		}
        /* ��������� ������� */
		DefaultTableModel defaultTableModel = (DefaultTableModel) table.getModel();
        /* ������ ����� */
		for (Object h : head) {
			defaultTableModel.addColumn(h);
		}
        /* ������ ���������� */
		for (Vector<Object> vector : data) {
			defaultTableModel.addRow(vector);
		}
	}
}
