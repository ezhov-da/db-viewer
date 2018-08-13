package ru.ezhov.dbviewer;

import ru.ezhov.dbviewer.queries.AppFileQuerys;
import ru.ezhov.dbviewer.queries.AppQuerys;
import ru.ezhov.dbviewer.queries.Query;
import ru.ezhov.dbviewer.queries.Queries;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.util.List;

/**
 * @author ezhov_da
 */
public class OwnTreeModel {
	public static DefaultTreeModel getModel() throws IOException {
		Queries.loadList();
		List<AppFileQuerys> appFileQueryses = Queries.getListFile();
		if (appFileQueryses.size() > 0) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("список файлов");
			DefaultMutableTreeNode file;
			DefaultMutableTreeNode queryNode;
			for (AppFileQuerys afq : appFileQueryses) {
				file = new DefaultMutableTreeNode(afq.getNameFile());
				AppQuerys querys = afq.getAppQuerys();
				for (Query query : querys.getQueries()) {
					queryNode = new DefaultMutableTreeNode(query);
					file.add(queryNode);
				}
				root.add(file);
			}
			DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
			return defaultTreeModel;
		}
		return null;
	}
}
