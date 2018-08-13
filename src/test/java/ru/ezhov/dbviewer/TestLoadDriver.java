//package ru.ezhov.dbviewer;
//import java.io.IOException;
//import java.sql.SQLException;
//import ru.ezhov.dbviewer.connection.AppConnections;
//import ru.ezhov.dbviewer.connection.LoadDrivers;
///**
// * тестируем загрузку драйверов
// * <p>
// * @author ezhov_da
// */
//public class TestLoadDriver
//{
//    public static void main(String[] arg) throws IOException, InstantiationException, IllegalAccessException, SQLException
//    {
//        AppConnections.loadConnections();   //загружаем список подключений
//        LoadDrivers.load(AppConnections.INSTANCE.getListConnection().get(0));
//    }
//}
