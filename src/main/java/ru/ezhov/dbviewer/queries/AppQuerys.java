package ru.ezhov.dbviewer.queries;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, который содержит запросы для работы приложения
 * <p>
 *
 * @author ezhov_da
 */
@XStreamAlias("root")
public final class AppQuerys {
    private static final Logger LOG = Logger.getLogger(AppQuerys.class.getName());
    @XStreamAlias("list")
    private List<Query> queries;

    private AppQuerys() {
    }

    /**
     * Загружаем объект с запросами
     * <p>
     *
     * @param file <p>
     * @return <p>
     * @throws FileNotFoundException
     */
    public static AppQuerys loadQuerys(File file) throws FileNotFoundException, IOException {
        InputStreamReader fileReader = null;
        try {
            XStream xStream = new XStream(new DomDriver());
            Annotations.configureAliases(xStream, AppQuerys.class);
//			Annotations.configureAliases(xStream, Query.class);
            fileReader = new InputStreamReader(new FileInputStream(file), "UTF8");
            AppQuerys appQuerys = (AppQuerys) xStream.fromXML(fileReader);
//пишем объект----------------------------------------------------------------------------------------------------------------------
//			FileWriter fileWriter = new FileWriter(file);
//			AppQuerys appQuerys = new AppQueries();
//			Query query = new Query("asadasd", "afafsaf");
//			List<Query> queries = new ArrayList<Query>();
//			queries.add(query);
//			appQuerys.setQueries(queries);
//			xStream.toXML(appQuerys, fileWriter);
//			return null;
//-------------------------------------------------------------------------------------------------------------------------------------------
            return appQuerys;
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            AppQuerys.loadQuerys(new File("queries.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }
}
