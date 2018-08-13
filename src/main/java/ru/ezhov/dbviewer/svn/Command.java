package ru.ezhov.dbviewer.svn;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * класс, который содержит информацию о комманде для svn
 * <p>
 *
 * @author ezhov_da
 */
@XStreamAlias(value = "command")
public class Command {
	@XStreamAlias(value = "name")
	private String name;
	@XStreamAlias(value = "pathToCommand")
	private String pathToCommand;
	@XStreamAlias(value = "argument")
	private String argument;
	@XStreamAlias(value = "methodFile")
	private String methodFile;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathToCommand() {
		return pathToCommand;
	}

	public void setPathToCommand(String pathToCommand) {
		this.pathToCommand = pathToCommand;
	}

	public String getArgument() {
		return argument;
	}

	public void setArgument(String argument) {
		this.argument = argument;
	}

	public String getMethodFile() {
		return methodFile;
	}

	public void setMethodFile(String methodFile) {
		this.methodFile = methodFile;
	}

	@Override
	public String toString() {
		return name;
	}


}
