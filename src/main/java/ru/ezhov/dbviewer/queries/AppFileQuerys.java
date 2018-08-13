package ru.ezhov.dbviewer.queries;

/**
 * @author ezhov_da
 */
public class AppFileQuerys {
	private final String nameFile;
	private final AppQuerys appQuerys;

	public AppFileQuerys(String nameFile, AppQuerys appQuerys) {
		this.nameFile = nameFile;
		this.appQuerys = appQuerys;
	}

	public String getNameFile() {
		return nameFile;
	}

	public AppQuerys getAppQuerys() {
		return appQuerys;
	}
}
