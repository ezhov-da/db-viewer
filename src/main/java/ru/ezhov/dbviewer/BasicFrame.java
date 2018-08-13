package ru.ezhov.dbviewer;

import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.universalchardet.UniversalDetector;
import ru.ezhov.dbviewer.connection.AppConnection;
import ru.ezhov.dbviewer.connection.AppConnections;
import ru.ezhov.dbviewer.queries.Query;
import ru.ezhov.dbviewer.svn.Command;
import ru.ezhov.dbviewer.svn.Svn;
import ru.ezhov.dbviewer.svn.SvnLoad;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * перечисления, которые используются для обозначения действия
 * <p>
 *
 * @author ezhov_da
 */
enum ConRecon {
    CONNECT(1), RECONNECT(0);
    private final int action;

    ConRecon(int action) {
        this.action = action;
    }

    public int getIdButton() {
        return action;
    }
}

/**
 * Класс, который содержит базовую форму
 * <p>
 *
 * @author ezhov_da
 */
public class BasicFrame extends JFrame {
    public static final BasicFrame BASIC_FRAME = new BasicFrame();
    private static final Logger LOG = Logger.getLogger(BasicFrame.class.getName());
    private final static String VERSION = "1.1";
    private final static Color COLOR_BACKGROUND_TEXT_PANE = new Color(211, 211, 211);
    private final static int minusSizeScreen = 200;
    private final OwnJPanel ownJPanel = new OwnJPanel();

    private BasicFrame() {
        init();
    }

    protected void init() {
        add(ownJPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(
                Toolkit.getDefaultToolkit().getScreenSize().width - minusSizeScreen,
                Toolkit.getDefaultToolkit().getScreenSize().height - minusSizeScreen
        );
        setTitle("db viewer " + VERSION);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(ViewLogs.class.getResource("/developer.png")).getImage());
        addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if (Frame.ICONIFIED == e.getNewState()) {
                    Window window = e.getWindow();
                    window.setVisible(false);
                }
            }
        });
    }

    public JTextPane gettextPaneLog() {
        return ownJPanel.textPaneLogError;
    }

    private final class OwnJPanel extends JPanel {
        /**
         * горизонтальная раздетелитеьная панель
         */
        private final JPanel panelTabbedContent = new JPanel(new BorderLayout());
        //------------------------------------------------------------------------------------------------------------------------------------/
        /**
         * разделительная панель для вывода таблицы с файлами
         */
        private final JSplitPane splitPanelTreeViewFile = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        /**
         * панель для дерева
         */
        private final JPanel panelTree = new JPanel(new BorderLayout());
        /**
         * toolbar, который отвечает за работу с деревом
         */
        private final JToolBar toolBarTree = new JToolBar(JToolBar.HORIZONTAL);
        /**
         * кнопка обновления дерева
         */
        private final JButton buttonRefreshTree = new JButton(new ImageIcon(ViewLogs.class.getResource("/reload.png")));
        /**
         * дерево с файлами запросов
         */
        private final JTree tree = new JTree();
        /**
         * панель для просмотра текста запроса
         */
        private final JPanel panelTextQuerysView = new JPanel(new BorderLayout());
        /**
         * текстовое поле для вывода текста запроса
         */
        private final JTextPane textPaneView = new JTextPane();
        //------------------------------------------------------------------------------------------------------------------------------------
        /**
         * панель для отображения таблицы с результирующими данными
         */
        private final JPanel panelTableViewResultQuery = new JPanel(new BorderLayout(5, 5));
        //------------------------------------------------------------------------------------------------------------------------------------
        /**
         * это главная разделительная панель
         */
        private final JSplitPane splitPaneBasic = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        //------------------------------------------------------------------------------------------------------------------------------------
        /**
         * разделительная панель, которая содержит таблицу и окно с запросами
         */
        private final JSplitPane splitPaneTable = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        /**
         * панель, которая будет содержать вкладки с запросами
         */
        private final JPanel panelTabbed = new JPanel(new BorderLayout(5, 5));
        private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        //------------------------------------------------------------------------------------------------------------------------------------
        private final JPanel panelLogError = new JPanel(new BorderLayout(5, 5));
        private final JTextPane textPaneLogError = new JTextPane();
        //------------------------------------------------------------------------------------------------------------------------------------
        private final View viewTreeQuerys = new View("дерево запросов", null, null);
        private final View viewTreeFiles = new View("дерево файлов", null, null);
        private final ViewMap viewMap = new ViewMap();
        private final TabWindow tabWindow = new TabWindow();
        //------------------------------------------------------------------------------------------------------------------------------------
        private final TreeFilePanel treeFilePanel = new TreeFilePanel();
        private RootWindow rootWindow;

        public OwnJPanel() {
            init();
        }

        private void init() {
            setViews(); //инициализируем вьюшки с деревьями
            setLayout(new BorderLayout());
            //добавляем панели на сплит панель------------------------------------------------------------------------------
            toolBarTree.add(Box.createHorizontalGlue());
            toolBarTree.setFloatable(false);
            buttonRefreshTree.setToolTipText("обновить дерево");
            //ставим слушателя на обновление дерева
            buttonRefreshTree.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        tree.setModel(OwnTreeModel.getModel());
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            });
            toolBarTree.add(buttonRefreshTree);
            tree.addKeyListener(new OwnKeyListener(tree));
            tree.addMouseListener(new OwnMouseListener(tree));
            panelTree.add(toolBarTree, BorderLayout.NORTH);
            panelTree.add(new JScrollPane(tree), BorderLayout.CENTER);
            panelTextQuerysView.add(new JScrollPane(textPaneView), BorderLayout.CENTER);
            textPaneView.setEditable(false);
            textPaneView.setBackground(BasicFrame.COLOR_BACKGROUND_TEXT_PANE);
            textPaneView.addMouseListener(new ListenerCopy());
            //---------------------------------------------------------------------------------------------------------------------------------
            panelTabbed.add(tabbedPane, BorderLayout.CENTER);
            BasicFrame.this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl N"), "newTab");
            Action aNewTab = new AbstractAction("newTab") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    OwnTabbedPanel ownTabbedPanel = new OwnTabbedPanel();
                    View view = new View("new tab", null, ownTabbedPanel);
                    ownTabbedPanel.setView(view);
                    viewMap.addView(viewMap.getViewCount(), view);
                    tabWindow.addTab(view);
                }
            };
            BasicFrame.this.getRootPane().getActionMap().put("newTab", aNewTab);
            splitPaneTable.setBottomComponent(panelTabbed);
            splitPaneTable.setOneTouchExpandable(true);
            panelTableViewResultQuery.add(splitPaneTable, BorderLayout.CENTER);
            panelTabbedContent.add(panelTableViewResultQuery, BorderLayout.CENTER);
            //---------------------------------------------------------------------------------------------------------------------------------
            panelLogError.add(new JScrollPane(textPaneLogError), BorderLayout.CENTER);
            textPaneLogError.setBackground(BasicFrame.COLOR_BACKGROUND_TEXT_PANE);
            textPaneLogError.setEditable(false);
            //---------------------------------------------------------------------------------------------------------------------------------
            splitPaneBasic.setTopComponent(rootWindow);
            splitPaneBasic.setBottomComponent(panelLogError);
            splitPaneBasic.setDividerLocation(570);
            splitPaneBasic.setOneTouchExpandable(true);
            add(splitPaneBasic, BorderLayout.CENTER);
            try {
                tree.setModel(OwnTreeModel.getModel());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            tree.addTreeSelectionListener(new ListenerTree(textPaneView)); //ставим слушателя на дерево
        }

        /**
         * метод открывает файл выьбранный в дереве или текст запроса из xml
         */
        public void openFileText(JTree jt) {
            if (jt.getSelectionPath() == null) {
                return;
            }
            DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) jt.getSelectionPath().getLastPathComponent();
            Object o = defaultMutableTreeNode.getUserObject();
            if (o instanceof TreeFilePanel.OwnChildTree) {
                TreeFilePanel.OwnChildTree childTree = (TreeFilePanel.OwnChildTree) defaultMutableTreeNode.getUserObject();
                if (!childTree.getFile().isDirectory()) {
                    addView(childTree.getFile().getName(), childTree.getFile(), null);
                }
            } else if (o instanceof Query) {
                Query childTree = (Query) defaultMutableTreeNode.getUserObject();
                addView(childTree.getName(), null, childTree.getSelect());
            }
        }

        /**
         * добавляем новую вкладку
         * <p>
         *
         * @param nameView
         * @param f
         */
        private void addView(String nameView, File f, String text) {
            OwnTabbedPanel ownTabbedPanel = new OwnTabbedPanel();
            View view = new View(nameView, null, ownTabbedPanel);
            ownTabbedPanel.setView(view);
            if (f != null) {
                ownTabbedPanel.setOpenFile(f);
            } else {
                ownTabbedPanel.setText(text);
            }
            viewMap.addView(viewMap.getViewCount(), view);
            tabWindow.addTab(view);
        }

        /**
         * метод инициализирует и размещает деревья
         */
        private void setViews() {
            splitPanelTreeViewFile.setTopComponent(panelTree);
            splitPanelTreeViewFile.setBottomComponent(panelTextQuerysView);
            splitPanelTreeViewFile.setDividerLocation(500);
            viewTreeQuerys.setComponent(splitPanelTreeViewFile);
            viewTreeFiles.setComponent(treeFilePanel);
            viewMap.addView(0, viewTreeFiles);
            viewMap.addView(1, viewTreeQuerys);
            rootWindow = DockingUtil.createRootWindow(viewMap, true);
            OwnTabbedPanel ownTabbedPanel = new OwnTabbedPanel();
            View view = new View("new tab", null, ownTabbedPanel);
            ownTabbedPanel.setView(view);
            viewMap.addView(viewMap.getViewCount(), view);
            tabWindow.addTab(view);
            rootWindow.setWindow(tabWindow);
            rootWindow.getWindowBar(Direction.LEFT).setEnabled(true);
            rootWindow.getWindowBar(Direction.LEFT).addTab(viewTreeQuerys);
            rootWindow.getWindowBar(Direction.LEFT).addTab(viewTreeFiles);
            rootWindow.getWindowBar(Direction.LEFT).setContentPanelSize(400);
        }

        /**
         * перетягиваем запрос в окно редактирования
         */
        private class ListenerCopy extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                JTextPane pane = (JTextPane) e.getSource();
                String string = pane.getText();
                if ("".equals(string)) {
                    return;
                }
            }
        }

        /**
         * панель, которая добавляется для запросов
         */
        private class OwnTabbedPanel extends JPanel {
            private final static int CHARS_LENGTH = 25;
            private final JFileChooser fileChooser = new JFileChooser();
            /**
             * панель состояния
             */
            private final JToolBar toolBarStatus = new JToolBar();
            /**
             * label, который показывает кол-во выгруженных строк
             */
            private final JLabel labelCountRow = new JLabel();
            private final JPanel panelBasic = new JPanel(new BorderLayout());
            private final RSyntaxTextArea editorPane = new RSyntaxTextArea();
            private final JComboBox comboBoxSyntax = new JComboBox(new Object[]
                    {
                            "sql", "java", "javascript", "properties", "python", "xml"
                    });
            private final JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
            private final JLabel label = new JLabel("синтаксис:");
            /**
             * кнопка для отключения
             */
            private final JButton buttonReconnect = new JButton(new ImageIcon(ViewLogs.class.getResource("/reconnect.png")));
            /**
             * комбобокс, который отображает выбранное подключение
             */
            private final JComboBox comboBoxConnect = new JComboBox(AppConnections.INSTANCE.getListConnection().toArray());
            /**
             * кнопка подключения
             */
            private final JButton buttonConnect = new JButton(new ImageIcon(ViewLogs.class.getResource("/connect.png")));
            private final JLabel labelNameInterval = new JLabel("Интервал обновления (мин):");
            private final JSpinner spinnerInterval = new JSpinner();
            private final JToggleButton toggleButtonStartStop = new JToggleButton("старт/стоп");
            private final JTable table = new JTable();
            private final JScrollPane scrollPaneTable = new JScrollPane(table);
            private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            private File openFile;
            private String charsetFile;
            private View view;

            public OwnTabbedPanel() {
                super(new BorderLayout(5, 5));
                init();
            }

            protected void init() {
                editorPane.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
                editorPane.setCodeFoldingEnabled(true);
                editorPane.getInputMap().put(KeyStroke.getKeyStroke("ctrl S"), "saveFile");
                editorPane.getActionMap().put("saveFile", new OwnListenerSaveFile());
                RTextScrollPane sp = new RTextScrollPane(editorPane);
                comboBoxSyntax.setPreferredSize(new Dimension(100, 20));
                comboBoxSyntax.setMaximumSize(new Dimension(100, 20));
                comboBoxSyntax.addItemListener(new OwnItemListener());
                setToolbar(); // настраиваем toolbar
                setSettingsTable(); //настраиваем таблицу
                panelBasic.add(toolBar, BorderLayout.NORTH);
                splitPane.setOneTouchExpandable(true);
                splitPane.setDividerLocation(0.9);
                splitPane.setDividerLocation(300);
                splitPane.setTopComponent(sp);
                splitPane.setBottomComponent(scrollPaneTable);
                panelBasic.add(splitPane, BorderLayout.CENTER);
                setListeners(); //ставим слушателей
                setSettingsFileChooser();   //настраиваем диалог выбора файла
                //add(new JScrollPane(panelBasic), BorderLayout.CENTER);
                add(panelBasic, BorderLayout.CENTER);
                add(toolBarStatus, BorderLayout.SOUTH);
            }

            /**
             * настраиваем сохранение файла
             */
            private void setSettingsFileChooser() {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }

            /**
             * настройки таблицы
             */
            private void setSettingsTable() {
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.getModel().addTableModelListener(new OwnTableModelListener());
            }

            private void setToolbar() {
                toolBarStatus.add(labelCountRow);
                toolBarStatus.setFloatable(false);
                //---------------------------------------------------------------------------------------------------------------------------------
                toggleButtonStartStop.setEnabled(false);
                buttonReconnect.setToolTipText("отключить");
                buttonReconnect.setEnabled(false);
                comboBoxConnect.setToolTipText("подключения");
                buttonConnect.setToolTipText("подключить");
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(buttonReconnect);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(comboBoxConnect);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(buttonConnect);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.addSeparator();
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(labelNameInterval);
                spinnerInterval.setValue(1);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(spinnerInterval);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(toggleButtonStartStop);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(label);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(comboBoxSyntax);
                toolBar.setFloatable(false);
                setSizeToolBar();   //настраиваем размеры компонентов для toolbara
            }

            /**
             * метод устанавливает файл для открытия во вкладке
             * <p>
             *
             * @param openFile
             */
            public void setOpenFile(File openFile) {
                this.openFile = openFile;
                try {
                    readOpenFile();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            /**
             * метод читает файл для отображения
             */
            private void readOpenFile() throws FileNotFoundException, UnsupportedEncodingException, IOException {
                charsetFile = guessEncoding();    //получаем кодировку файла
                charsetFile = ("".equals(charsetFile)) ? "UTF-8" : charsetFile;
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(openFile);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charsetFile);
                    Scanner scanner = new Scanner(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder(10000);
                    while (scanner.hasNext()) {
                        stringBuilder.append(scanner.nextLine());
                        stringBuilder.append("\n");
                    }
                    editorPane.setText(stringBuilder.toString());
                    scanner.close();
                } catch (Exception ex) {
                    Logger.getLogger(BasicFrame.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException ex) {
                        Logger.getLogger(BasicFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            /**
             * метод определения кодировки файла
             * <p>
             *
             * @return кодировка
             * <p>
             * @throws FileNotFoundException
             * @throws IOException
             */
            public String guessEncoding() throws FileNotFoundException, IOException {
                byte[] buf = new byte[4096];
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(openFile);
                    // (1)
                    UniversalDetector detector = new UniversalDetector(null);
                    // (2)
                    int nread;
                    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                        detector.handleData(buf, 0, nread);
                    }
                    // (3)
                    detector.dataEnd();
                    // (4)
                    String encoding = detector.getDetectedCharset();
                    // (5)
                    detector.reset();
                    if (encoding != null) {
                        return encoding;
                    } else {
                        return "";
                    }
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }

            public void setView(View view) {
                this.view = view;
            }

            /**
             * ставим слушателей
             */
            private void setListeners() {
                buttonReconnect.addActionListener(new ListenerConnectReconnectButton(ConRecon.RECONNECT, buttonConnect, comboBoxConnect, toggleButtonStartStop));
                buttonConnect.addActionListener(new ListenerConnectReconnectButton(ConRecon.CONNECT, buttonReconnect, comboBoxConnect, toggleButtonStartStop));
                toggleButtonStartStop.addActionListener(new OwnListener(spinnerInterval, tree, table));
                editorPane.addCaretListener(new ListenerChange());
            }

            /**
             * ставим размеры на компоненты в toolbar-е
             */
            private void setSizeToolBar() {
                //выпадающий список подключения
                comboBoxConnect.setPreferredSize(new Dimension(250, toggleButtonStartStop.getPreferredSize().height));
                comboBoxConnect.setMaximumSize(new Dimension(250, toggleButtonStartStop.getPreferredSize().height));
                comboBoxConnect.setMinimumSize(new Dimension(250, toggleButtonStartStop.getPreferredSize().height));
                //название интервала
                labelNameInterval.setPreferredSize(new Dimension(labelNameInterval.getPreferredSize().width, toggleButtonStartStop.getPreferredSize().height));
                labelNameInterval.setMaximumSize(new Dimension(labelNameInterval.getPreferredSize().width, toggleButtonStartStop.getPreferredSize().height));
                labelNameInterval.setMinimumSize(new Dimension(labelNameInterval.getPreferredSize().width, toggleButtonStartStop.getPreferredSize().height));
                //интервал
                spinnerInterval.setPreferredSize(new Dimension(100, toggleButtonStartStop.getPreferredSize().height));
                spinnerInterval.setMaximumSize(new Dimension(100, toggleButtonStartStop.getPreferredSize().height));
                spinnerInterval.setMinimumSize(new Dimension(100, toggleButtonStartStop.getPreferredSize().height));
            }

            public String getText() {
                return editorPane.getText();
            }

            public void setText(String text) {
                editorPane.setText(text);
            }

            /**
             * это слушатель, который показывает сколько строк в таблице
             */
            private class OwnTableModelListener implements TableModelListener {
                @Override
                public void tableChanged(TableModelEvent e) {
                    labelCountRow.setText("всего " + (e.getLastRow() + 1) + " строк(и)");
                }
            }

            /**
             * класс отвечает за toogle откуда брать запрос
             */
            private class ListenerFrom implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton button = (JToggleButton) e.getSource();
                    if (button.isSelected()) {
                        button.setText("выполняется запрос из выбранной вкладки");
                    } else {
                        button.setText("выполняется запрос из выбранного файла");
                    }
                }
            }

            /**
             * слушатель для старта и остановки обновления
             */
            private final class OwnListener implements ActionListener {
                private final JSpinner spinner;
                private final JTree tree;
                private final JTable table;
                private RunnerThread runnerThread;

                public OwnListener(JSpinner spinner, JTree tree, JTable table) {
                    this.spinner = spinner;
                    this.tree = tree;
                    this.table = table;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton toggleButton = (JToggleButton) e.getSource();
                    if (toggleButton.isSelected()) {
                        String str = getQuery();
                        if ("".equals(str)) {
                            toggleButton.setSelected(false);
                            return;
                        }
                        OwnJPanel.this.textPaneLogError.setText("");
                        runnerThread = new RunnerThread(
                                Integer.parseInt(spinner.getValue().toString()),
                                str,
                                table
                        );
                        runnerThread.setFlagStop(false);
                        runnerThread.start();
                    } else {
                        if (runnerThread != null) {
                            tree.setEnabled(true);
                            runnerThread.setFlagStop(true);
                        }
                    }
                }

                private String getQuery() {
                    if (OwnJPanel.this.tree.getSelectionModel().getSelectionPath() == null) {
                        return "";
                    }
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) OwnJPanel.this.tree.getSelectionModel().getSelectionPath().getLastPathComponent();
                    Object o = defaultMutableTreeNode.getUserObject();
                    if (o instanceof Query) {
                        return ((Query) o).getSelect();
                    } else {
                        return "";
                    }
                }
            }

            /**
             * класс, который отвечает за подключение и разрыв соединения с сервером
             */
            private class ListenerConnectReconnectButton implements ActionListener {
                /**
                 * кнопка на которой будет слушатель
                 */
                private final ConRecon conRecon;
                /**
                 * другая кнопка для контроля
                 */
                private final JButton buttonOther;
                /**
                 * комбобокс, который мы должны гасить в случае подключения
                 */
                private final JComboBox comboBox;
                /**
                 * кнопка, которая не активна при отстутствии подключения
                 */
                private final JToggleButton toggleButton;
                /**
                 * основная кнопка для нажатия
                 */
                private JButton button;

                public ListenerConnectReconnectButton(ConRecon conRecon, JButton buttonOther, JComboBox comboBox, JToggleButton toggleButton) {
                    this.conRecon = conRecon;
                    this.buttonOther = buttonOther;
                    this.comboBox = comboBox;
                    this.toggleButton = toggleButton;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    button = (JButton) e.getSource();
                    switch (conRecon) {
                        case CONNECT:
                            connect();
                            comboBox.setEnabled(false);
                            toggleButton.setEnabled(true);
                            break;
                        case RECONNECT:
                            reconnect();
                            comboBox.setEnabled(true);
                            toggleButton.setEnabled(false);
                            break;
                    }
                    button.setEnabled(false);
                    buttonOther.setEnabled(true);
                }

                /**
                 * разрываем соединение
                 */
                private void reconnect() {
                    ConnectionReview.closeConnection();
                }

                /**
                 * создаем подключение из выбранного в списке
                 */
                private void connect() {
                    AppConnection appConnection = (AppConnection) comboBox.getSelectedItem();
                    try {
                        ConnectionReview.setConnection(appConnection);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Не удалось подключиться к выбранному подключению.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }

            private class OwnItemListener implements ItemListener {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String str = editorPane.getText();
                    editorPane.setSyntaxEditingStyle("text/" + comboBoxSyntax.getSelectedItem().toString());
                    editorPane.setText(str);
                }
            }

            private class ListenerChange implements CaretListener {
                @Override
                public void caretUpdate(CaretEvent e) {
                    if ("".equals(editorPane.getText()) & OwnTabbedPanel.this.openFile == null) {
                        view.getViewProperties().setTitle("");
                        return;
                    }
                    if (OwnTabbedPanel.this.openFile == null) {
                        String string = editorPane.getText().trim();
                        if (string.length() != 0) {
                            if (string.length() < CHARS_LENGTH) {
                                view.getViewProperties().setTitle(string);
                            } else {
                                view.getViewProperties().setTitle(string.substring(0, CHARS_LENGTH));
                            }
                        }
                    }
                }
            }

            /**
             * класс реализует сохранение файла
             */
            private class OwnListenerSaveFile extends AbstractAction {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int op;
                    if (openFile == null) {
                        op = fileChooser.showDialog(null, "Сохранить");
                    } else {
                        fileChooser.setCurrentDirectory(openFile);
                        op = fileChooser.showSaveDialog(null);
                    }
                    if (op == JFileChooser.APPROVE_OPTION) {
                        saveFile(fileChooser.getSelectedFile());
                    }
                }

                /**
                 * сохраняем файл
                 * <p>
                 *
                 * @param file файл для сохранения
                 */
                private void saveFile(File file) {
                    PrintWriter printWriter = null;
                    try {
                        charsetFile = (charsetFile == null) ? "UTF-8" : charsetFile;
                        printWriter = new PrintWriter(file, charsetFile);
                        printWriter.print(editorPane.getText());
                        printWriter.flush();
                        printWriter.close();
                        setFileSave(file);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    } finally {
                        printWriter.close();
                    }
                }

                /**
                 * метод ставит данные если файл сохранен
                 */
                private void setFileSave(File file) {
                    view.getViewProperties().setTitle(file.getName());
                    OwnTabbedPanel.this.openFile = file;
                }
            }

        }

        /**
         * класс реализует панель с деревом файлов
         */
        private class TreeFilePanel extends JPanel {
            private final JLabel labelTextSvn = new JLabel("SVN:");
            private final JLabel labelCommanSvn = new JLabel("команды:");
            /**
             * комбобокс, который отвечает за комманды svn
             */
            private final JComboBox comboBoxCommandSvn = new JComboBox();
            /**
             * кнопка применить комманду
             */
            private final JButton buttonCommandComboBox = new JButton(new ActionExecuteSvnCommand());
            private final JFileChooser fileChooser = new JFileChooser();
            private final JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
            private final JButton button = new JButton(new ActionOpenFile());
            private final JTree treeFile = new JTree();
            private JComboBox comboBoxSvn;
            private DefaultTreeModel defaultTreeModelFile;

            public TreeFilePanel() {
                super(new BorderLayout());
                loadComboBoxSvn();  //загружаем комбобокс с svn
                setToolBar();
                setModelTree(); // ставим модель
                add(new JScrollPane(toolBar), BorderLayout.NORTH);
                add(new JScrollPane(treeFile), BorderLayout.CENTER);
            }

            /**
             * загружаем список доступных svn
             */
            private void loadComboBoxSvn() {
                try {
                    SvnLoad.loadList();
                    comboBoxSvn = new JComboBox(SvnLoad.INSTANCE.getListSvn().toArray());
                    comboBoxSvn.addItemListener(new OwnListenerChangeSvn());
                    fillCommandComboBox();
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            /**
             * енаполняем командный комбобокс командами выбранного svn-а
             */
            private void fillCommandComboBox() {
                Svn svn = (Svn) comboBoxSvn.getSelectedItem();
                svn.getCommands();
                comboBoxCommandSvn.setModel(new DefaultComboBoxModel(svn.getCommands().toArray()));
            }

            /**
             * настраиваем toolbar
             */
            private void setToolBar() {
                //-----------------------------------------------------------------
                Dimension dimensionLabel = new Dimension(30, 20);
                labelTextSvn.setPreferredSize(dimensionLabel);
                labelTextSvn.setMinimumSize(dimensionLabel);
                labelTextSvn.setMaximumSize(dimensionLabel);
                Dimension dimensionLabelCommanSvn = new Dimension(65, 20);
                labelCommanSvn.setPreferredSize(dimensionLabelCommanSvn);
                labelCommanSvn.setMinimumSize(dimensionLabelCommanSvn);
                labelCommanSvn.setMaximumSize(dimensionLabelCommanSvn);
                Dimension dimensionComboBox = new Dimension(100, 20);
                comboBoxSvn.setPreferredSize(dimensionComboBox);
                comboBoxSvn.setMinimumSize(dimensionComboBox);
                comboBoxSvn.setMaximumSize(dimensionComboBox);
                comboBoxCommandSvn.setPreferredSize(dimensionComboBox);
                comboBoxCommandSvn.setMinimumSize(dimensionComboBox);
                comboBoxCommandSvn.setMaximumSize(dimensionComboBox);
                //-----------------------------------------------------------------
                comboBoxSvn.setAlignmentX(JComponent.LEFT_ALIGNMENT);
                toolBar.setFloatable(false);
                toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(button);
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(new JSeparator(SwingConstants.VERTICAL));
                toolBar.add(Box.createHorizontalStrut(5));
                toolBar.add(labelTextSvn);
                toolBar.add(comboBoxSvn);
                toolBar.add(labelCommanSvn);
                toolBar.add(comboBoxCommandSvn);
                toolBar.add(buttonCommandComboBox);
                toolBar.add(Box.createHorizontalGlue());
            }

            /**
             * ставим модель для дерева и настраиваем его
             */
            private void setModelTree() {
                defaultTreeModelFile = new DefaultTreeModel(new DefaultMutableTreeNode("папки"));
                treeFile.setModel(defaultTreeModelFile);
                treeFile.addKeyListener(new OwnKeyListener(treeFile));
                treeFile.addMouseListener(new OwnMouseListener(treeFile));
            }

            /**
             * добавляем дерево
             * <p>
             *
             * @param file
             * @param defaultMutableTreeNode
             */
            private void setNodes(File file, DefaultMutableTreeNode defaultMutableTreeNode) {
                if (defaultMutableTreeNode == null) {
                    DefaultMutableTreeNode defaultMutableTreeNodeRoot = (DefaultMutableTreeNode) defaultTreeModelFile.getRoot();
                    defaultMutableTreeNodeRoot.add(getRecursionFileTree(file));
                    defaultTreeModelFile.reload(defaultMutableTreeNodeRoot);
                }
            }

            /**
             * выполняем команду SVN на выбранном файле для дерева
             * <p>
             *
             * @param command выбранная команда
             * @param file    - выбранный файл для работы
             */
            private void executeSvn(Command command, File file) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Method method = file.getClass().getMethod(command.getMethodFile());
                Object o = method.invoke(file);
                String[] commandsForCmd = new String[]
                        {
                                command.getPathToCommand(),
                                command.getArgument(),
                                o.toString()
                        };
                String resultStr = String.format("%s%s\"%s\"", commandsForCmd);
                try {
                    Runtime.getRuntime().exec(resultStr);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            /**
             * получаем рекурчивно дерево из выьранной папки
             * <p>
             *
             * @param file - выбранная папка
             *             <p>
             * @return дерево папок
             */
            private DefaultMutableTreeNode getRecursionFileTree(File file) {
                DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(new OwnChildTree(file));
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isDirectory()) {
                        defaultMutableTreeNode.add(getRecursionFileTree(f));
                    } else {
                        defaultMutableTreeNode.add(new DefaultMutableTreeNode(new OwnChildTree(f)));
                    }
                }
                return defaultMutableTreeNode;
            }

            /**
             * это action для кнопки открытия файлов
             */
            private class ActionOpenFile extends AbstractAction {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int open = fileChooser.showOpenDialog(null);
                    if (open != JFileChooser.OPEN_DIALOG) {
                        return;
                    }
                    File file = fileChooser.getSelectedFile();
                    setNodes(file, null);
                }

                {
                    putValue(Action.SMALL_ICON, new ImageIcon(BasicFrame.class.getResource("/folder_add.png")));
                    putValue(Action.SHORT_DESCRIPTION, "открыть папку");
                }
            }

            /**
             * это action для кнопки открытия файлов
             */
            private class ActionExecuteSvnCommand extends AbstractAction {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) treeFile.getSelectionPath().getLastPathComponent();
                        Object o = defaultMutableTreeNode.getUserObject();
                        if (o instanceof OwnChildTree) {
                            OwnChildTree childTree = (OwnChildTree) defaultMutableTreeNode.getUserObject();
                            executeSvn(((Command) comboBoxCommandSvn.getSelectedItem()), childTree.getFile());
                        }
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }

                {
                    putValue(Action.SMALL_ICON, new ImageIcon(BasicFrame.class.getResource("/execute.png")));
                    putValue(Action.SHORT_DESCRIPTION, "выполнить команду для выбранного файла или папки");
                }
            }

            /**
             * класс реализует слушателя на изменение элемента svn
             */
            private class OwnListenerChangeSvn implements ItemListener {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            fillCommandComboBox();
                        }
                    });
                }
            }

            /**
             * данный класс хранит информацию об узле дерева
             */
            class OwnChildTree {
                private final File file;

                public OwnChildTree(File file) {
                    this.file = file;
                }

                @Override
                public String toString() {
                    return file.getName();
                }

                public File getFile() {
                    return file;
                }
            }

        }

        /**
         * класс для открытия файла по нажатию enter
         */
        class OwnKeyListener extends KeyAdapter {
            private final JTree tr;

            public OwnKeyListener(JTree tr) {
                this.tr = tr;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                openFileText(tr);
            }
        }

        /**
         * класс для открытия файла по двойному клику
         */
        class OwnMouseListener extends MouseAdapter {
            private final JTree tr;

            public OwnMouseListener(JTree tr) {
                this.tr = tr;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openFileText(tr);
                }
            }
        }

    }

}
