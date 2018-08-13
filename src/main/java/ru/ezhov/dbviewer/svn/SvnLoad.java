package ru.ezhov.dbviewer.svn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author ezhov_da
 */
@XStreamAlias(value = "root")
public class SvnLoad {
	private final static String NAME_FILE_SVN = "svns.xml";
	public static SvnLoad INSTANCE;
	@XStreamAlias(value = "svns")
	private List<Svn> listSvn;

	private SvnLoad() {
	}

	public synchronized static void loadList() throws IOException {
		FileWriter fileWriter = null; //для тестирования записи
		InputStreamReader fileReader = null;
		try {
			XStream xStream = new XStream(new DomDriver());
			Annotations.configureAliases(xStream, Command.class);
			Annotations.configureAliases(xStream, Svn.class);
			Annotations.configureAliases(xStream, SvnLoad.class);
//читаем объект---------------------------------------------------------------------------------------------------------------------
			fileReader = new InputStreamReader(new FileInputStream(new File(NAME_FILE_SVN)), "UTF8");
			INSTANCE = (SvnLoad) xStream.fromXML(fileReader);
//пишем объект----------------------------------------------------------------------------------------------------------------------
//            INSTANCE = new SvnLoad();
//            Command command = new Command();
//            command.setArgument("asdsad");;
//            command.setMethodFile("asdsad");
//            command.setName("asdsad");;
//            command.setPathToCommand("asdsad");;
//            Svn svn = new Svn();
//            List<Command> commands = new ArrayList<Command>();
//            commands.add(command);
//            svn.setCommands(commands);
//            svn.setName("test");
//            List<Svn> svns = new ArrayList<Svn>();
//            svns.add(svn);
//            INSTANCE.setListSvn(svns);
//            fileWriter = new FileWriter(new File(NAME_FILE_SVN));
//            xStream.toXML(INSTANCE, fileWriter);
//------------------------------------------------------------------------------------------------------------------------------------------
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			SvnLoad.loadList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Svn> getListSvn() {
		return listSvn;
	}

	public void setListSvn(List<Svn> listSvn) {
		this.listSvn = listSvn;
	}
}
