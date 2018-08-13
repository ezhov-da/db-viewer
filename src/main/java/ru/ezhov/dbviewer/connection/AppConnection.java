package ru.ezhov.dbviewer.connection;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Properties;

/**
 * класс, который хранит отдельное подключение
 * <p>
 *
 * @author ezhov_da
 */
@XStreamAlias("connection")
public class AppConnection {
	//для теста записи объекта------------------------------------------------------------------------------------------------------
//    @XStreamAlias("nameConnection")
//    private String name = "teradata";
//    @XStreamAlias("url")
//    private String url = "jdbc:teradata://teradata";
//    @XStreamAlias("classForName")
//    private String classForName = "com.teradata.jdbc.TeraDriver";
//    @XStreamAlias("properties")
//    private Properties properties;
//рабочие данные-------------------------------------------------------------------------------------------------------------------
	@XStreamAlias("nameConnection")
	private String name;
	@XStreamAlias("url")
	private String url;
	@XStreamAlias("nameJarFile")
	private String nameJarFile;
	@XStreamAlias("classForName")
	private String classForName;
	@XStreamAlias("properties")
	private Properties properties;

	//------------------------------------------------------------------------------------------------------------------------------------------
	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getClassForName() {
		return classForName;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getNameJarFile() {
		return nameJarFile;
	}
}
