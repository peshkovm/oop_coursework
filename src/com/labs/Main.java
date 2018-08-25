package com.labs;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        GUI gui = new GUI();
        gui.setVisible(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class GUI extends JFrame {
    static int b = 0;
    boolean isTableModelListener = true;
    DefaultTableModel calendarModel;
    JTable calendarTable;
    Vector<Integer> calendarTableIdArray = new Vector<Integer>();
    DefaultTableModel scoresModel;
    JTable scoresTable;
    Vector<Integer> scoresTableIdArray = new Vector<Integer>();
    DefaultTableModel playersModel;
    JTable playersTable;
    Vector<Integer> playersTableIdArray = new Vector<Integer>();
    centerPanel.tabbedPanel tabbedpanel;
    java.sql.Connection con;
    Statement st;
    centerPanel.teamsPanel teamspanel;
    centerPanel.autodromsPanel autodromspanel;
    DefaultComboBoxModel calendarComboBox1model;
    JComboBox calendarComboBox1;
    private int row;
    centerPanel centerpanel;
    JComboBox playersComboBox1;
    DefaultComboBoxModel playersComboBox1model;

    class teamPanel extends JPanel {
        private int id;
        String label1;

        void setId(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }
    }

    class autodromPanel extends JPanel {
        private int id;
        String label1;

        void setId(int id) {
            this.id = id;
        }

        int getId() {
            return id;
        }
    }

    GUI() {
        String url = "jdbc:mysql://localhost/coursework";
        String name = "root";
        String password = "root";
        try {
            con = DriverManager.getConnection(url, name, password);
            System.out.println("Соединение с MySQL установлено");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Соединение с MySQL разорвано");
            }
        });
        centerpanel = new centerPanel();
        toolBarPanel toolbarpanel = new toolBarPanel();
        this.getContentPane().add(BorderLayout.NORTH, toolbarpanel);
        this.getContentPane().add(BorderLayout.CENTER, centerpanel);
        this.setSize(900, 750);
        this.setTitle("F1 manager");
    }

    private class centerPanel extends JPanel {
        centerPanel() {
            F1panel panel2 = new F1panel();
            tabbedpanel = new tabbedPanel();
            this.setLayout(new BorderLayout());
            this.add(BorderLayout.NORTH, panel2);
            this.add(BorderLayout.CENTER, tabbedpanel);
        }

        private class tabbedPanel extends JTabbedPane {
            tabbedPanel() {
                calendarPanel panel1 = new calendarPanel();
                scoresPanel panel2 = new scoresPanel();
                teamspanel = new teamsPanel();
                playersPanel panel4 = new playersPanel();
                autodromspanel = new autodromsPanel();
                this.addTab("Календарь", null, panel1, "Календарь");
                this.addTab("Очки", null, panel2, "Очки");
                this.addTab("Команды", null, teamspanel, "Команды");
                this.addTab("Спортсмены", null, panel4, "Спортсмены");
                this.addTab("Арены", null, autodromspanel, "Арены");
            }
        }

        private class F1panel extends JPanel {
            F1panel() {
                ImageIcon image = new ImageIcon("C:\\edu_java_labs\\lab2\\images\\F1_logo.jpg");
                JLabel label1 = new JLabel(image);
                JLabel label2 = new JLabel("Формула-1 2017");
                Font font = new Font("Arial", Font.BOLD, 15);
                label2.setFont(font);
                String[] items = {"2013", "2014", "2015", "2016", "2017"};
                JComboBox comboBox = new JComboBox(items);
                comboBox.setEditable(false);
                comboBox.setSelectedIndex(items.length - 1);
                this.add(label1);
                this.add(label2);
            }
        }

        private class calendarPanel extends JPanel {
            calendarPanel() {
                Vector<String> columnNames = new Vector<String>(3);
                columnNames.add("Дата, время");
                columnNames.add("Этап");
                columnNames.add("");
                Vector<Vector<String>> data = new Vector<Vector<String>>();
                String query1 = "select * from calendar";
                try {
                    ResultSet rs = st.executeQuery(query1);
                    while (rs.next()) {
                        Vector<String> list = new Vector<String>(3);
                        list.add(rs.getString("race_date_time"));
                        list.add(rs.getString("stage"));
                        list.add(rs.getString("stage_extra"));
                        data.add(list);
                        calendarTableIdArray.add(rs.getInt("id"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                calendarModel = new DefaultTableModel(data, columnNames);
                calendarTable = new JTable(calendarModel);
                calendarTable.setAutoCreateRowSorter(true);
                JScrollPane scrollpane = new JScrollPane(calendarTable);
                comboBoxsPanel comboboxspanel = new comboBoxsPanel();
                scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                this.setLayout(new BorderLayout());
                calendarTable.setFillsViewportHeight(true);
                this.add(scrollpane, BorderLayout.CENTER);
                this.add(comboboxspanel, BorderLayout.NORTH);
            }

            class calendarTableCellListener implements CellEditorListener {
                @Override
                public void editingStopped(ChangeEvent changeEvent) {
                    int row = calendarTable.getSelectedRow();
                    String data1 = (String) calendarTable.getValueAt(row, 1);
                    if (calendarComboBox1model.getIndexOf(data1) == -1 && !data1.contains(" ")) {
                        calendarComboBox1model.addElement(data1);
                    }
                }

                @Override
                public void editingCanceled(ChangeEvent changeEvent) {
                }
            }

            private class comboBoxsPanel extends JPanel {
                comboBoxsPanel() {
                    calendarComboBox1model = new DefaultComboBoxModel(new String[]{"Все"});
                    String[] items1 = {"Все"};
                    String[] items2 = {"Все", "Завершённые", "Будущие"};
                    String[] items3 = {"Все", "Финал", "Предварительные"};
                    JLabel label1 = new JLabel("Этап:");
                    JLabel label2 = new JLabel("Игры:");
                    calendarComboBox1 = new JComboBox(calendarComboBox1model);
                    JComboBox comboBox2 = new JComboBox(items2);
                    JComboBox comboBox3 = new JComboBox(items3);
                    JButton button1 = new JButton("Ок");
                    this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    this.setLayout(new FlowLayout(FlowLayout.LEFT));
                    this.add(label1);
                    this.add(Box.createHorizontalGlue());
                    this.add(calendarComboBox1);
                    this.add(Box.createHorizontalStrut(20));
                    this.add(Box.createHorizontalGlue());
                    this.add(Box.createHorizontalStrut(20));
                    calendarComboBox1.addActionListener(new comboBoxsPanelListener());
                    changecalendarComboBox1List();
                }

                class comboBoxsPanelListener implements ActionListener {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (calendarComboBox1.getSelectedIndex() != 0) {
                            newFilter(calendarComboBox1);
                        } else calendarTable.setRowSorter(null);
                    }
                }

                private void newFilter(JComboBox combobox) {
                    RowFilter<DefaultTableModel, Object> rf = null;
                    try {
                        rf = RowFilter.regexFilter((String) combobox.getItemAt(combobox.getSelectedIndex()), 1);
                    } catch (java.util.regex.PatternSyntaxException e) {
                        return;
                    }
                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(calendarModel);
                    sorter.setRowFilter(rf);
                    calendarTable.setRowSorter(sorter);
                }
            }
        }

        private class scoresPanel extends JPanel {
            scoresPanel() {
                Vector<String> columnNames = new Vector<String>(3);
                columnNames.add("");
                columnNames.add("Гра-при 1");
                columnNames.add("Гра-при 2");
                columnNames.add("Гра-при 3");
                columnNames.add("Гра-при 4");
                columnNames.add("Очки");
                Vector<Vector<String>> data = new Vector<Vector<String>>();
                String query1 = "select *,sportsman from scores,players where scores.id_sportsman = players.id";
                try {
                    ResultSet rs = st.executeQuery(query1);
                    while (rs.next()) {
                        Vector<String> list = new Vector<String>(6);
                        list.add(rs.getString("sportsman"));
                        list.add(rs.getString("grand_prix_1"));
                        list.add(rs.getString("grand_prix_2"));
                        list.add(rs.getString("grand_prix_3"));
                        list.add(rs.getString("grand_prix_4"));
                        list.add(rs.getString("scores"));
                        data.add(list);
                        scoresTableIdArray.add(rs.getInt("scores.id"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                scoresModel = new DefaultTableModel(data, columnNames);
                scoresTable = new JTable(scoresModel);
                JScrollPane scrollpane = new JScrollPane(scoresTable);
                scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scoresTable.setFillsViewportHeight(true);
                Font bigfont = new Font("Arial", Font.BOLD, 15);
                JLabel label1 = new JLabel("Личный зачёт");
                label1.setFont(bigfont);
                label1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                this.setLayout(new BorderLayout());
                this.add(label1, BorderLayout.NORTH);
                this.add(scrollpane, BorderLayout.CENTER);
            }
        }

        private class teamsPanel extends JPanel {
            teamsPanel() {
                String query1 = "select id,picture_path,title,country from teams";
                try {
                    ResultSet rs = st.executeQuery(query1);
                    while (rs.next()) {
                        teamPanel teampanel = new teamPanel();
                        String picture_path = rs.getString("picture_path");
                        JLabel iconlabel = new JLabel();
                        JLabel label1 = new JLabel(rs.getString("title"));
                        JLabel label2 = new JLabel(rs.getString("country"));
                        try {
                            iconlabel.setIcon(new ImageIcon(ImageIO.read(new File(picture_path))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Font bigfont = new Font("Arial", Font.BOLD, 15);
                        teampanel.setLayout(new BoxLayout(teampanel, BoxLayout.Y_AXIS));
                        teampanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                        label1.setFont(bigfont);
                        label2.setForeground(Color.GRAY);
                        iconlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                        teampanel.setId(rs.getInt("id"));
                        teampanel.label1 = rs.getString("title");
                        teampanel.add(iconlabel);
                        teampanel.add(label1);
                        teampanel.add(label2);
                        this.add(teampanel);
                        teampanel.addMouseListener(new MouseListener() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
                                if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                                    int playestablerow = playersTable.getRowCount() - 1;
                                    while (playestablerow >= 0) {
                                        if (playersTable.getValueAt(playestablerow, 1).equals(label1.getText())) {
                                            JOptionPane.showMessageDialog(teampanel, "Cannot change a team: a foreign key constraint fails", "Change warning!", JOptionPane.WARNING_MESSAGE);
                                            return;
                                        }
                                        playestablerow--;
                                    }
                                    JFileChooser fileopen = new JFileChooser();
                                    fileopen.setCurrentDirectory(new File("D://lab2_Maksim_images"));
                                    int ret = fileopen.showDialog(null, "Открыть файл");
                                    if (ret == JFileChooser.APPROVE_OPTION) {
                                        File file = fileopen.getSelectedFile();
                                        try {
                                            String picture_path = null;
                                            String picture_name = file.getName();
                                            String[] fileNameMas = fileNameParser(picture_name);
                                            label1.setText(fileNameMas[0]);
                                            label2.setText(fileNameMas[1]);
                                            try {
                                                picture_path = file.getCanonicalPath();
                                                picture_path = picture_path.replace('\\', '/');
                                                iconlabel.setIcon(new ImageIcon(ImageIO.read(new File(picture_path))));
                                                teamspanel.updateUI();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            String query1 = "update teams set " + "picture_path='" + picture_path + "'," + "title='" + fileNameMas[0] + "'," + "country='" + fileNameMas[1] + "' where id=" + teampanel.getId();
                                            int rs1 = st.executeUpdate(query1);
                                            int comboboxrow = playersComboBox1.getItemCount() - 1;
                                            while (comboboxrow >= 0) {
                                                playersComboBox1model.removeElementAt(comboboxrow);
                                                comboboxrow--;
                                            }
                                            String query3 = "select title from teams";
                                            ResultSet rs2 = st.executeQuery(query3);
                                            while (rs2.next()) {
                                                String tablerowdata = rs2.getString("title");
                                                if (playersComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals("Текст"))
                                                    playersComboBox1model.addElement(tablerowdata);
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                                    int result = JOptionPane.showConfirmDialog(teampanel, "Are you really want to delete this team?", "Delete question", JOptionPane.YES_NO_OPTION);
                                    if (result == JOptionPane.YES_OPTION) {
                                        int playestablerow = playersTable.getRowCount() - 1;
                                        while (playestablerow >= 0) {
                                            if (playersTable.getValueAt(playestablerow, 1).equals(label1.getText())) {
                                                JOptionPane.showMessageDialog(teampanel, "Cannot delete a team: a foreign key constraint fails", "Delete warning!", JOptionPane.WARNING_MESSAGE);
                                                return;
                                            }
                                            playestablerow--;
                                        }
                                        String query1 = "delete from teams where id = " + teampanel.getId();
                                        try {
                                            int rs1 = st.executeUpdate(query1);
                                            teamspanel.remove(teampanel);
                                            teamspanel.updateUI();
                                            int comboboxrow = playersComboBox1.getItemCount() - 1;
                                            while (comboboxrow > 0) {
                                                playersComboBox1model.removeElementAt(comboboxrow);
                                                comboboxrow--;
                                            }
                                            String query3 = "select title from teams";
                                            ResultSet rs2 = st.executeQuery(query3);
                                            while (rs2.next()) {
                                                String tablerowdata = rs2.getString("title");
                                                if (playersComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals("Текст"))
                                                    playersComboBox1model.addElement(tablerowdata);
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            JOptionPane.showMessageDialog(teampanel, "Cannot delete a team: a foreign key constraint fails", "Delete warning!", JOptionPane.WARNING_MESSAGE);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                            }

                            @Override
                            public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
                            }

                            @Override
                            public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
                                teamspanel.updateUI();
                                label2.setForeground(Color.DARK_GRAY);
                                teampanel.setBackground(new Color(235, 54, 61, 100));
                            }

                            @Override
                            public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
                                label2.setForeground(Color.GRAY);
                                teampanel.setBackground(null);
                            }
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        private class playersPanel extends JPanel {
            playersPanel() {
                Vector<String> columnNames = new Vector<String>(5);
                columnNames.add("Спортсмен");
                columnNames.add("Команда");
                columnNames.add("ДР");
                columnNames.add("Рост");
                columnNames.add("Вес");
                Vector<Vector<String>> data = new Vector<Vector<String>>();
                String query1 = "select players.id,sportsman,teams.title,b_day,height,weight from players,teams where players.id_team = teams.id order by players.id";
                try {
                    ResultSet rs = st.executeQuery(query1);
                    while (rs.next()) {
                        Vector<String> list = new Vector<String>(5);
                        list.add(rs.getString("sportsman"));
                        list.add(rs.getString("title"));
                        list.add(rs.getString("b_day"));
                        list.add(rs.getString("height"));
                        list.add(rs.getString("weight"));
                        data.add(list);
                        playersTableIdArray.add(rs.getInt("players.id"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                playersModel = new DefaultTableModel(data, columnNames);
                playersTable = new JTable(playersModel);
                TableColumn teamColumn = playersTable.getColumnModel().getColumn(1);
                playersComboBox1model = new DefaultComboBoxModel();
                playersComboBox1 = new JComboBox(playersComboBox1model);
                String query2 = "select * from teams";
                try {
                    ResultSet rs1 = st.executeQuery(query2);
                    while (rs1.next()) {
                        String tablerowdata = rs1.getString("title");
                        if (playersComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals(""))
                            playersComboBox1model.addElement(tablerowdata);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                teamColumn.setCellEditor(new DefaultCellEditor(playersComboBox1));
                playersModel.addTableModelListener(new playersTableModelListener());
                JScrollPane scrollpane = new JScrollPane(playersTable);
                scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                this.setLayout(new BorderLayout());
                playersTable.setFillsViewportHeight(true);
                this.add(scrollpane, BorderLayout.CENTER);
            }

            class playersTableModelListener implements TableModelListener {
                @Override
                public void tableChanged(TableModelEvent tableModelEvent) {
                    int selectRow = playersTable.getSelectedRow();
                    if (selectRow != -1 && playersTable.getSelectedColumn() == 0 && isTableModelListener) {
                        String cellData = (String) playersTable.getValueAt(selectRow, 0);
                        scoresTable.setValueAt(cellData, selectRow, 0);
                    }
                }
            }

            class playersTableCellListener implements CellEditorListener {
                @Override
                public void editingStopped(ChangeEvent changeEvent) {
                    int selectRow = playersTable.getSelectedRow();
                    if (selectRow != -1 && playersTable.getSelectedColumn() == 0) {
                        String cellData = (String) playersTable.getValueAt(selectRow, 0);
                        scoresTable.setValueAt(cellData, selectRow, 0);
                    }
                }

                @Override
                public void editingCanceled(ChangeEvent changeEvent) {
                }
            }

            class comboBoxsPanelListener implements ActionListener {
                @Override
                public void actionPerformed(ActionEvent event) {
                }
            }

            class ItemChangeListener implements ItemListener {
                @Override
                public void itemStateChanged(ItemEvent event) {
                    if (playersTable.getSelectedRow() != -1) {
                        String query1 = "select id from teams where teams.title = '" + playersComboBox1.getSelectedItem() + "'";
                        try {
                            ResultSet rs = st.executeQuery(query1);
                            rs.next();
                            String query2 = "update players set id_team= " + rs.getString("id") + " where id= " + playersTableIdArray.get(playersTable.getSelectedRow());
                            int rs1 = st.executeUpdate(query2);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private class autodromsPanel extends JPanel {
            autodromsPanel() {
                String query1 = "select id,picture_path,title,country_city from autodroms";
                try {
                    ResultSet rs = st.executeQuery(query1);
                    while (rs.next()) {
                        autodromPanel autodrompanel = new autodromPanel();
                        String picture_path = rs.getString("picture_path");
                        JLabel iconlabel = new JLabel();
                        JLabel label1 = new JLabel(rs.getString("title"));
                        JLabel label2 = new JLabel(rs.getString("country_city"));
                        try {
                            iconlabel.setIcon(new ImageIcon(ImageIO.read(new File(picture_path))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Font bigfont = new Font("Arial", Font.BOLD, 15);
                        autodrompanel.setLayout(new BoxLayout(autodrompanel, BoxLayout.Y_AXIS));
                        autodrompanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                        label1.setFont(bigfont);
                        label2.setForeground(Color.GRAY);
                        iconlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                        autodrompanel.setId(rs.getInt("id"));
                        autodrompanel.label1 = rs.getString("title");
                        autodrompanel.add(iconlabel);
                        autodrompanel.add(label1);
                        autodrompanel.add(label2);
                        this.add(autodrompanel);
                        autodrompanel.addMouseListener(new MouseListener() {
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
                                if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                                    JFileChooser fileopen = new JFileChooser();
                                    fileopen.setCurrentDirectory(new File("D://lab2_Maksim_images"));
                                    int ret = fileopen.showDialog(null, "Открыть файл");
                                    if (ret == JFileChooser.APPROVE_OPTION) {
                                        File file = fileopen.getSelectedFile();
                                        try {
                                            String picture_path = null;
                                            String picture_name = file.getName();
                                            String[] fileNameMas = fileNameParser(picture_name);
                                            label1.setText(fileNameMas[0]);
                                            label2.setText(fileNameMas[1]);
                                            try {
                                                picture_path = file.getCanonicalPath();
                                                picture_path = picture_path.replace('\\', '/');
                                                iconlabel.setIcon(new ImageIcon(ImageIO.read(new File(picture_path))));
                                                autodromspanel.updateUI();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            String query1 = "update autodroms set " + "picture_path='" + picture_path + "'," + "title='" + fileNameMas[0] + "'," + "country_city='" + fileNameMas[1] + "' where id=" + autodrompanel.getId();
                                            int rs1 = st.executeUpdate(query1);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                                    int result = JOptionPane.showConfirmDialog(autodrompanel, "Are you really want to delete this autodrom?", "Delete question", JOptionPane.YES_NO_OPTION);
                                    if (result == JOptionPane.YES_OPTION) {
                                        String query1 = "delete from autodroms where id = " + autodrompanel.getId();
                                        try {
                                            int rs1 = st.executeUpdate(query1);
                                            autodromspanel.remove(autodrompanel);
                                            autodromspanel.updateUI();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            JOptionPane.showMessageDialog(autodrompanel, "Cannot delete a autodrom: " + e.getErrorCode() + " Error code", "Delete warning!", JOptionPane.WARNING_MESSAGE);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                            }

                            @Override
                            public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
                            }

                            @Override
                            public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
                                autodromspanel.updateUI();
                                label2.setForeground(Color.DARK_GRAY);
                                autodrompanel.setBackground(new Color(235, 54, 61, 100));
                            }

                            @Override
                            public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
                                label2.setForeground(Color.GRAY);
                                autodrompanel.setBackground(null);
                            }
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class toolBarPanel extends JToolBar {
        toolBarPanel() {
            JButton saveButton = new JButton(new ImageIcon("C:\\edu_java_labs\\lab2\\images\\saveButton.png"));
            JButton openButton = new JButton(new ImageIcon("C:\\edu_java_labs\\lab2\\images\\openButton.png"));
            JButton addButton = new JButton(new ImageIcon("C:\\edu_java_labs\\lab2\\images\\addButton.png"));
            JButton deleteButton = new JButton(new ImageIcon("C:\\edu_java_labs\\lab2\\images\\deleteButton.png"));
            JButton calendarButton = new JButton(new ImageIcon("images/calendar.png"));
            JButton eraserButton = new JButton(new ImageIcon("images/eraser.png"));
            this.add(saveButton);
            this.add(addButton);
            this.add(deleteButton);
            this.add(calendarButton);
            this.add(eraserButton);
            addButton.addActionListener(new addButtonListener());
            deleteButton.addActionListener(new deleteButtonListener());
            saveButton.addActionListener(new saveButtonListener());
            openButton.addActionListener(new openButtonListener());
            calendarButton.addActionListener(new calendarButtonListener());
            eraserButton.addActionListener(new eraserButtonListener());
        }

        class addButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tabbedpanel.getSelectedIndex() == 2) add_deleteTeamOrAutodrom();
                else if (tabbedpanel.getSelectedIndex() == 4) add_deleteTeamOrAutodrom();
                else addTableRow();
            }
        }

        class deleteButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTableRow();
            }
        }

        class saveButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveTableRow();
            }
        }

        class openButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    try {
                        String picture_path = file.getCanonicalPath();
                        String query1 = "insert into teams (picture_path) values ('" + picture_path + "')";
                        int rs1 = st.executeUpdate(query1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        class calendarButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SwingCalendar newcalendar = new SwingCalendar();
                newcalendar.setVisible(true);
            }
        }

        class eraserButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (tabbedpanel.getSelectedIndex() == 0) {
                    int a = calendarTable.getSelectedRow();
                    int b = 0;
                    while (a != -1 && b < calendarTable.getSelectedRowCount()) {
                        calendarTable.setValueAt("", a, calendarTable.getSelectedColumn());
                        a++;
                        b++;
                    }
                    if (calendarTable.getSelectedRow() == -1)
                        JOptionPane.showMessageDialog(calendarTable, "Choose cell to clear.", "Cell clear warning", JOptionPane.WARNING_MESSAGE);
                }
                if (tabbedpanel.getSelectedIndex() == 1) {
                    int a = scoresTable.getSelectedRow();
                    int b = 0;
                    while (a != -1 && b < scoresTable.getSelectedRowCount()) {
                        scoresTable.setValueAt("", a, scoresTable.getSelectedColumn());
                        a++;
                        b++;
                    }
                    if (scoresTable.getSelectedRow() == -1)
                        JOptionPane.showMessageDialog(scoresTable, "Choose cell to clear.", "Cell clear warning", JOptionPane.WARNING_MESSAGE);
                }
                if (tabbedpanel.getSelectedIndex() == 3) {
                    int a = playersTable.getSelectedRow();
                    int b = 0;
                    if (playersTable.getSelectedColumn() != 1)
                        while (a != -1 && b < playersTable.getSelectedRowCount()) {
                            playersTable.setValueAt("", a, playersTable.getSelectedColumn());
                            a++;
                            b++;
                        }
                    if (playersTable.getSelectedRow() == -1)
                        JOptionPane.showMessageDialog(playersTable, "Choose cell to clear.", "Cell clear warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    void addTableRow() {
        SwingCalendar todayCalendar = new SwingCalendar();
        todayCalendar.setVisible(false);
        if (tabbedpanel.getSelectedIndex() == 0) {
            Object[] data = {(String) todayCalendar.getToday(), "этап", "гонка"};
            int row = calendarTable.getSelectedRow();
            calendarModel.addRow(data);
            String query1 = "insert into calendar (race_date_time,stage, stage_extra) values ('" + (String) todayCalendar.getToday() + "', 'этап', 'гонка')";
            String query2 = "select last_insert_id()";
            try {
                int rs1 = st.executeUpdate(query1);
                ResultSet rs = st.executeQuery(query2);
                if (rs.next()) {
                    calendarTableIdArray.add(rs.getInt("last_insert_id()"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (tabbedpanel.getSelectedIndex() == 3) {
            String query10 = "select teams.title, teams.id from teams";
            ResultSet rs10;
            String title = null;
            int firstId = 0;
            try {
                rs10 = st.executeQuery(query10);
                rs10.next();
                title = rs10.getString("title");
                firstId = rs10.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Object[] data = {"Фамилия Имя", title, (String) todayCalendar.getToday(), "0", "0"};
            Object[] data2 = {"Фамилия Имя", "0", "0", "0", "0", "0"};
            int row = playersTable.getSelectedRow();
            playersModel.addRow(data);
            String query1 = "insert into players (sportsman,id_team,b_day,height,weight) values ('Фамилия Имя','" + firstId + "','" + (String) todayCalendar.getToday() + "','0','0')";
            String query2 = "select last_insert_id()";
            try {
                int rs1 = st.executeUpdate(query1);
                ResultSet rs = st.executeQuery(query2);
                if (rs.next()) {
                    playersTableIdArray.add(rs.getInt("last_insert_id()"));
                }
                scoresModel.addRow(data2);
                String query3 = "insert into scores (id_sportsman,grand_prix_1,grand_prix_2,grand_prix_3,grand_prix_4,scores)" + "values ('" + rs.getInt("last_insert_id()") + "', '0','0','0','0','0')";
                String query4 = "select last_insert_id()";
                int rs2 = st.executeUpdate(query3);
                ResultSet rs3 = st.executeQuery(query4);
                if (rs3.next()) scoresTableIdArray.add(rs3.getInt("last_insert_id()"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void deleteTableRow() {
        if (tabbedpanel.getSelectedIndex() == 0) {
            int row = calendarTable.getSelectedRow();
            if (row >= 0) {
                calendarModel.removeRow(row);
                String query1 = "delete from calendar where id = " + calendarTableIdArray.get(row);
                calendarTableIdArray.remove(row);
                try {
                    int rs1 = st.executeUpdate(query1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else
                JOptionPane.showMessageDialog(calendarTable, "Choose row to delete.", "Row delete warning", JOptionPane.WARNING_MESSAGE);
            changecalendarComboBox1List();
        }
        if (tabbedpanel.getSelectedIndex() == 1) {
        }
        if (tabbedpanel.getSelectedIndex() == 3) {
            int row = playersTable.getSelectedRow();
            int cell = playersTable.getSelectedColumn();
            if (row >= 0 && cell != 1) {
                isTableModelListener = false;
                playersModel.removeRow(row);
                scoresModel.removeRow(row);
                String query1 = "delete from players where id = " + playersTableIdArray.get(row);
                String query2 = "delete from scores where id = " + scoresTableIdArray.get(row);
                scoresTableIdArray.remove(row);
                playersTableIdArray.remove(row);
                try {
                    int rs2 = st.executeUpdate(query2);
                    int rs1 = st.executeUpdate(query1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else
                JOptionPane.showMessageDialog(calendarTable, "Choose row to delete.", "Row delete warning", JOptionPane.WARNING_MESSAGE);
            isTableModelListener = true;
        }
    }

    void saveTableRow() {
        if (tabbedpanel.getSelectedIndex() == 0) {
            if (calendarTable.getRowCount() != 0) {
                int row = 0;
                do {
                    int id = calendarTableIdArray.get(row);
                    String data0 = (String) calendarTable.getValueAt(row, 0);
                    String data1 = (String) calendarTable.getValueAt(row, 1);
                    String data2 = (String) calendarTable.getValueAt(row, 2);
                    String query1 = "update calendar set race_date_time='" + data0 + "',stage='" + data1 + "',stage_extra='" + data2 + "' where id = " + id;
                    try {
                        int rs1 = st.executeUpdate(query1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    row++;
                } while (row < calendarTable.getRowCount());
                int comboboxrow = calendarComboBox1.getItemCount() - 1;
                while (comboboxrow > 0) {
                    calendarComboBox1model.removeElementAt(comboboxrow);
                    comboboxrow--;
                }
                int tablerow = 0;
                while (tablerow < calendarTable.getRowCount()) {
                    String tablerowdata = (String) calendarModel.getValueAt(tablerow, 1);
                    if (calendarComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals(""))
                        calendarComboBox1model.addElement(tablerowdata);
                    tablerow++;
                }
            }
            changecalendarComboBox1List();
        }
        if (tabbedpanel.getSelectedIndex() == 1) {
            if (scoresTable.getRowCount() != 0) {
                int row = 0;
                do {
                    int id = scoresTableIdArray.get(row);
                    String data1 = (String) scoresTable.getValueAt(row, 1);
                    String data2 = (String) scoresTable.getValueAt(row, 2);
                    String data3 = (String) scoresTable.getValueAt(row, 3);
                    String data4 = (String) scoresTable.getValueAt(row, 4);
                    String data5 = (String) scoresTable.getValueAt(row, 5);
                    if (data1.equals("")) data1 = "0";
                    if (data2.equals("")) data2 = "0";
                    if (data3.equals("")) data3 = "0";
                    if (data4.equals("")) data4 = "0";
                    if (data5.equals("")) data5 = "0";
                    String query1 = "update scores set grand_prix_1=" + data1 + ",grand_prix_2=" + data2 + ",grand_prix_3=" + data3 + ",grand_prix_4=" + data4 + ",scores=" + data5 + " where id = " + id;
                    try {
                        int rs1 = st.executeUpdate(query1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    row++;
                } while (row < scoresTable.getRowCount());
            }
        }
        if (tabbedpanel.getSelectedIndex() == 3) {
            if (playersTable.getRowCount() != 0) {
                int row = 0;
                do {
                    int id = playersTableIdArray.get(row);
                    int id_team = 0;
                    String data0 = (String) playersTable.getValueAt(row, 0);
                    String data1 = (String) playersTable.getValueAt(row, 1);
                    String query1 = "select id from teams where teams.title = '" + data1 + "'";
                    try {
                        ResultSet rs = st.executeQuery(query1);
                        rs.next();
                        id_team = Integer.parseInt(rs.getString("id"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String data2 = (String) playersTable.getValueAt(row, 2);
                    String data3 = (String) playersTable.getValueAt(row, 3);
                    String data4 = (String) playersTable.getValueAt(row, 4);
                    if (data3.equals("")) data3 = "0";
                    if (data4.equals("")) data4 = "0";
                    String query2 = "update players set sportsman= '" + data0 + "',id_team= " + id_team + ",b_day= '" + data2 + "',height= " + data3 + ",weight= " + data4 + " where players.id = " + id;
                    try {
                        int rs1 = st.executeUpdate(query2);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    row++;
                } while (row < playersTable.getRowCount());
            }
        }
    }

    void add_deleteTeamOrAutodrom() {
        if (tabbedpanel.getSelectedIndex() == 2) {
            teamPanel teampanel = new teamPanel();
            ImageIcon teampanelicon = new ImageIcon("images/image.png");
            JLabel iconlabel = new JLabel();
            iconlabel.setIcon(teampanelicon);
            JLabel label1 = new JLabel("Текст");
            JLabel label2 = new JLabel("Текст");
            Font bigfont = new Font("Arial", Font.BOLD, 15);
            teampanel.setLayout(new BoxLayout(teampanel, BoxLayout.Y_AXIS));
            teampanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            label1.setFont(bigfont);
            label2.setForeground(Color.GRAY);
            iconlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            teampanel.add(iconlabel);
            teampanel.add(label1);
            teampanel.add(label2);
            teamspanel.add(teampanel);
            teamspanel.updateUI();
            String query1 = "insert into teams (picture_path,title,country) values ('images/image.png', 'Текст', 'Текст')";
            String query2 = "select last_insert_id()";
            ResultSet rs = null;
            try {
                int rs1 = st.executeUpdate(query1);
                rs = st.executeQuery(query2);
                if (rs.next()) {
                    teampanel.setId(rs.getInt("last_insert_id()"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            teampanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                        int playestablerow = playersTable.getRowCount() - 1;
                        while (playestablerow >= 0) {
                            if (playersTable.getValueAt(playestablerow, 1).equals(label1.getText())) {
                                JOptionPane.showMessageDialog(teampanel, "Cannot change a team: a foreign key constraint fails", "Change warning!", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            playestablerow--;
                        }
                        JFileChooser fileopen = new JFileChooser();
                        fileopen.setCurrentDirectory(new File("D:/lab2_Maksim_images"));
                        int ret = fileopen.showDialog(null, "Открыть файл");
                        if (ret == JFileChooser.APPROVE_OPTION) {
                            File file = fileopen.getSelectedFile();
                            try {
                                String picture_path = null;
                                String picture_name = file.getName();
                                String[] fileNameMas = fileNameParser(picture_name);
                                label1.setText(fileNameMas[0]);
                                label2.setText(fileNameMas[1]);
                                try {
                                    picture_path = file.getCanonicalPath();
                                    picture_path = picture_path.replace('\\', '/');
                                    iconlabel.setIcon(new ImageIcon(ImageIO.read(new File(picture_path))));
                                    teamspanel.updateUI();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String query1 = "update teams set " + "picture_path='" + picture_path + "'," + "title='" + fileNameMas[0] + "'," + "country='" + fileNameMas[1] + "' where id=" + teampanel.getId();
                                int rs1 = st.executeUpdate(query1);
                                int comboboxrow = playersComboBox1.getItemCount() - 1;
                                while (comboboxrow > 0) {
                                    playersComboBox1model.removeElementAt(comboboxrow);
                                    comboboxrow--;
                                }
                                String query3 = "select title from teams";
                                ResultSet rs2 = st.executeQuery(query3);
                                while (rs2.next()) {
                                    String tablerowdata = rs2.getString("title");
                                    if (playersComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals("Текст"))
                                        playersComboBox1model.addElement(tablerowdata);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        int result = JOptionPane.showConfirmDialog(teampanel, "Are you really want to delete this team?", "Delete question", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            int playestablerow = playersTable.getRowCount() - 1;
                            while (playestablerow >= 0) {
                                if (playersTable.getValueAt(playestablerow, 1).equals(label1.getText())) {
                                    JOptionPane.showMessageDialog(teampanel, "Cannot delete a team: a foreign key constraint fails", "Delete warning!", JOptionPane.WARNING_MESSAGE);
                                    return;
                                }
                                playestablerow--;
                            }
                            String query1 = "delete from teams where id = " + teampanel.getId();
                            try {
                                int rs1 = st.executeUpdate(query1);
                                teamspanel.remove(teampanel);
                                teamspanel.updateUI();
                                int comboboxrow = playersComboBox1.getItemCount() - 1;
                                while (comboboxrow > 0) {
                                    playersComboBox1model.removeElementAt(comboboxrow);
                                    comboboxrow--;
                                }
                                String query3 = "select title from teams";
                                ResultSet rs2 = st.executeQuery(query3);
                                while (rs2.next()) {
                                    String tablerowdata = rs2.getString("title");
                                    if (playersComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals("Текст"))
                                        playersComboBox1model.addElement(tablerowdata);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(teampanel, "Cannot delete a team: a foreign key constraint fails", "Delete warning!", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
                    teamspanel.updateUI();
                    label2.setForeground(Color.DARK_GRAY);
                    teampanel.setBackground(new Color(235, 54, 61, 100));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
                    label2.setForeground(Color.GRAY);
                    teampanel.setBackground(null);
                }
            });
        }
        if (tabbedpanel.getSelectedIndex() == 4) {
            autodromPanel autodrompanel = new autodromPanel();
            ImageIcon autodrompanelicon = new ImageIcon("images/image.png");
            JLabel iconlabel = new JLabel();
            iconlabel.setIcon(autodrompanelicon);
            JLabel label1 = new JLabel("Текст");
            JLabel label2 = new JLabel("Текст");
            Font bigfont = new Font("Arial", Font.BOLD, 15);
            autodrompanel.setLayout(new BoxLayout(autodrompanel, BoxLayout.Y_AXIS));
            autodrompanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            label1.setFont(bigfont);
            label2.setForeground(Color.GRAY);
            iconlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            autodrompanel.add(iconlabel);
            autodrompanel.add(label1);
            autodrompanel.add(label2);
            autodromspanel.add(autodrompanel);
            autodromspanel.updateUI();
            String query1 = "insert into autodroms (picture_path,title,country_city) values ('images/image.png', 'Текст', 'Текст')";
            String query2 = "select last_insert_id()";
            ResultSet rs = null;
            try {
                int rs1 = st.executeUpdate(query1);
                rs = st.executeQuery(query2);
                if (rs.next()) {
                    autodrompanel.setId(rs.getInt("last_insert_id()"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            autodrompanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
                    if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                        JFileChooser fileopen = new JFileChooser();
                        fileopen.setCurrentDirectory(new File("D:/lab2_Maksim_images"));
                        int ret = fileopen.showDialog(null, "Открыть файл");
                        if (ret == JFileChooser.APPROVE_OPTION) {
                            File file = fileopen.getSelectedFile();
                            try {
                                String picture_path = null;
                                String picture_name = file.getName();
                                String[] fileNameMas = fileNameParser(picture_name);
                                label1.setText(fileNameMas[0]);
                                label2.setText(fileNameMas[1]);
                                try {
                                    picture_path = file.getCanonicalPath();
                                    picture_path = picture_path.replace('\\', '/');
                                    iconlabel.setIcon(new ImageIcon(ImageIO.read(new File(picture_path))));
                                    autodromspanel.updateUI();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String query1 = "update autodroms set " + "picture_path='" + picture_path + "'," + "title='" + fileNameMas[0] + "'," + "country_city='" + fileNameMas[1] + "' where id=" + autodrompanel.getId();
                                int rs1 = st.executeUpdate(query1);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (mouseEvent.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        int result = JOptionPane.showConfirmDialog(autodrompanel, "Are you really want to delete this autodrom?", "Delete question", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            String query1 = "delete from autodroms where id = " + autodrompanel.getId();
                            try {
                                int rs1 = st.executeUpdate(query1);
                                autodromspanel.remove(autodrompanel);
                                autodromspanel.updateUI();
                            } catch (SQLException e) {
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(autodrompanel, "Cannot delete a autodrom: " + e.getErrorCode() + " Error code", "Delete warning!", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }

                @Override
                public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
                    autodromspanel.updateUI();
                    label2.setForeground(Color.DARK_GRAY);
                    autodrompanel.setBackground(new Color(235, 54, 61, 100));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
                    label2.setForeground(Color.GRAY);
                    autodrompanel.setBackground(null);
                }
            });
        }
    }

    void changecalendarComboBox1List() {
        int comboboxrow = calendarComboBox1.getItemCount() - 1;
        while (comboboxrow > 0) {
            calendarComboBox1model.removeElementAt(comboboxrow);
            comboboxrow--;
        }
        int tablerow = 0;
        while (tablerow < calendarTable.getRowCount()) {
            String tablerowdata = (String) calendarModel.getValueAt(tablerow, 1);
            if (calendarComboBox1model.getIndexOf(tablerowdata) == -1 && !tablerowdata.equals(""))
                calendarComboBox1model.addElement(tablerowdata);
            tablerow++;
        }
    }

    private String[] fileNameParser(String str) {
        int i = 0, j = 0;
        String[] strMas = new String[]{"Текст", "Текст"};
        if (str.indexOf('_') != -1 && str.indexOf('.') != -1) {
            while (str.charAt(i) != '_') {
                i++;
            }
            strMas[0] = str.substring(0, i);
            j = i++;
            while (str.charAt(j) != '.') {
                j++;
            }
            strMas[1] = str.substring(i, j);
        }
        return strMas;
    }

    public class SwingCalendar extends JFrame {
        private DefaultTableModel model;
        private Calendar cal = new GregorianCalendar();
        private JLabel label;
        private JTable table;
        private String cellValue = "ДД ММ ГГ";
        String month;
        int year;
        int a = 0;

        SwingCalendar() {
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setTitle("Календарь");
            this.setSize(300, 220);
            this.setLayout(new BorderLayout());
            Button OKButton = new Button("OK");
            b++;
            label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            JButton b1 = new JButton(new ImageIcon("images/cal_leftArrow.png"));
            b1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    cal.add(Calendar.MONTH, -1);
                    a--;
                    updateMonth();
                }
            });
            JButton b2 = new JButton(new ImageIcon("images/cal_rightArrow.png"));
            b2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    cal.add(Calendar.MONTH, +1);
                    a++;
                    updateMonth();
                }
            });
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(b1, BorderLayout.WEST);
            panel.add(label, BorderLayout.CENTER);
            panel.add(b2, BorderLayout.EAST);
            String[] columns = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
            model = new DefaultTableModel(null, columns);
            table = new JTable(model) {
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component component = super.prepareRenderer(renderer, row, column);
                    DateFormat dateFormat1 = new SimpleDateFormat("dd");
                    DateFormat dateFormat2 = new SimpleDateFormat("mm");
                    Date date = new Date();
                    int startDay = cal.get(Calendar.DAY_OF_WEEK);
                    int i = 0;
                    i = count_i(i);
                    i += Integer.parseInt(dateFormat1.format(date)) - 1;
                    int row1 = i / 7;
                    int col1 = i % 7;
                    if (a == 0) {
                        if (table.getSelectedRow() == -1) if (row == row1 && column == col1 && a == 0) {
                            component.setBackground(new Color(203, 232, 246, 255));
                        } else component.setBackground(new Color(0, 0, 0, 0));
                        else if (table.getSelectedRow() == row1) {
                            if (row == row1 && a == 0) {
                                component.setBackground(new Color(51, 153, 255, 255));
                            } else component.setBackground(new Color(0, 0, 0, 0));
                        } else if (table.getSelectedRow() != row1) {
                            if (row == row1 && column == col1 && a == 0) {
                                component.setBackground(new Color(203, 232, 246, 255));
                            } else if (row == table.getSelectedRow())
                                component.setBackground(new Color(51, 153, 255, 255));
                            else component.setBackground(new Color(0, 0, 0, 0));
                        }
                    } else {
                        if (table.getSelectedRow() == -1) component.setBackground(new Color(0, 0, 0, 0));
                        else {
                            if (row == table.getSelectedRow()) component.setBackground(new Color(51, 153, 255, 255));
                            else component.setBackground(new Color(0, 0, 0, 0));
                        }
                    }
                    return component;
                }
            };
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane pane = new JScrollPane(table);
            this.add(panel, BorderLayout.NORTH);
            this.add(pane, BorderLayout.CENTER);
            this.add(OKButton, BorderLayout.SOUTH);
            OKButton.addActionListener(new OKButtonListener());
            this.updateMonth();
            this.setLocationRelativeTo(centerpanel);
        }

        void updateMonth() {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"));
            year = cal.get(Calendar.YEAR);
            label.setText(monthParser(month) + " " + year);
            int startDay = cal.get(Calendar.DAY_OF_WEEK);
            int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int weeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
            model.setRowCount(0);
            model.setRowCount(weeks);
            int todayrow = 0, todaycolumn;
            int i = 0;
            i = count_i(i);
            for (int day = 1; day <= numberOfDays; day++) {
                model.setValueAt(day, i / 7, i % 7);
                i++;
            }
        }

        String monthParser(String month) {
            switch (month) {
                case "сентября":
                    month = month.replace("сентября", "Сентябрь");
                    break;
                case "октября":
                    month = month.replace("октября", "Октябрь");
                    break;
                case "ноября":
                    month = month.replace("ноября", "Ноябрь");
                    break;
                case "декабря":
                    month = month.replace("декабря", "Декабрь");
                    break;
                case "января":
                    month = month.replace("января", "Январь");
                    break;
                case "февраля":
                    month = month.replace("февраля", "Февраль");
                    break;
                case "марта":
                    month = month.replace("марта", "Март");
                    break;
                case "апреля":
                    month = month.replace("апреля", "Апрель");
                    break;
                case "мая":
                    month = month.replace("мая", "Май");
                    break;
                case "июня":
                    month = month.replace("июня", "Июнь");
                    break;
                case "июля":
                    month = month.replace("июля", "Июль");
                    break;
                case "августа":
                    month = month.replace("августа", "Август");
                    break;
            }
            return month;
        }

        int count_i(int i) {
            int startDay = cal.get(Calendar.DAY_OF_WEEK);
            if (startDay - 2 >= 0) i = startDay - 2;
            else i = 6;
            return i;
        }

        String getToday() {
            DateFormat dateFormat1 = new SimpleDateFormat("dd");
            Date date = new Date();
            int startDay = cal.get(Calendar.DAY_OF_WEEK);
            int i = 0;
            i = count_i(i);
            i += Integer.parseInt(dateFormat1.format(date)) - 1;
            int row1 = i / 7;
            int col1 = i % 7;
            if (row1 != -1 && col1 != -1) {
                cellValue = (table.getValueAt(row1, col1)).toString();
                cellValue += (" " + month + " " + year);
            }
            return cellValue;
        }

        class OKButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (tabbedpanel.getSelectedIndex() == 0) {
                    int row = calendarTable.getSelectedRow();
                    int column = calendarTable.getSelectedColumn();
                    if (row != -1 && column == 0) {
                        cellValue = (table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())).toString();
                        cellValue += (" " + month + " " + year);
                        calendarTable.setValueAt(cellValue, row, column);
                    }
                }
                if (tabbedpanel.getSelectedIndex() == 3) {
                    int row = playersTable.getSelectedRow();
                    int column = playersTable.getSelectedColumn();
                    if (row != -1 && column == 2) {
                        cellValue = (table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())).toString();
                        cellValue += (" " + month + " " + year);
                        playersTable.setValueAt(cellValue, row, column);
                    }
                }
            }
        }
    }

    boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }
}