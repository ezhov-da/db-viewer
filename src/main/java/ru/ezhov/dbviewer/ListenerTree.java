package ru.ezhov.dbviewer;

import ru.ezhov.dbviewer.queries.Query;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * класс реализует слушателя мыши для дерева
 * <p>
 *
 * @author ezhov_da
 */
public class ListenerTree implements TreeSelectionListener {
	private final JTextPane textPane;

	public ListenerTree(JTextPane textPane) {
		this.textPane = textPane;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() == null) {
			return;
		}
		TreePath path = e.getNewLeadSelectionPath();
		DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		if (defaultMutableTreeNode.isLeaf()) {
			Query query = (Query) defaultMutableTreeNode.getUserObject();
			textPane.setText(query.getSelect());
		}
	}
}
