package ru.ezhov.dbviewer.queries;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author ezhov_da
 */
@XStreamAlias("query")
public class Query {
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("select")
	private String select;

	public Query() {
	}

	public Query(String name, String select) {
		this.name = name;
		this.select = select;
	}

	public String getName() {
		return name;
	}

	public String getSelect() {
		return select;
	}

	@Override
	public String toString() {
		return name;
	}
}
