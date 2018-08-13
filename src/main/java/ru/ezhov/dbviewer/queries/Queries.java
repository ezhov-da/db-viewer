package ru.ezhov.dbviewer.queries;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ezhov_da
 */
public class Queries {
    /**
     * папка откуда грузятся запросы
     */
    public static String FOLDER_LOAD = "queries";
    /**
     * список подгруженных файлов с запросами
     */
    private static List<AppFileQuerys> list;

    public static void loadList() throws IOException {
        File file = new File(FOLDER_LOAD);
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        list = new ArrayList<AppFileQuerys>(files.length);
        for (File f : files) {
            list.add(new AppFileQuerys(f.getName(), AppQuerys.loadQuerys(f)));
        }
    }

    public static List<AppFileQuerys> getListFile() {
        return list;
    }
}
