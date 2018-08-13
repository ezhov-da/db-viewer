package ru.ezhov.dbviewer.svn;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * класс, который содержит список комманд для конкретного svn
 * <p>
 *
 * @author ezhov_da
 */
@XStreamAlias(value = "svn")
public class Svn {
	@XStreamAlias(value = "name")
	private String name;
	@XStreamAlias(value = "commands")
	private List<Command> commands;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	@Override
	public String toString() {
		return name;
	}
}
