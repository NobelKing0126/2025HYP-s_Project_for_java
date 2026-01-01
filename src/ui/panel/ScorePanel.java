package ui.panel;

import entity.User;
import entity.Score;
import entity.Student;
import entity.Course;
import entity.Clazz;
import service.ScoreService;
import dao.StudentDao;
import dao.CourseDao;
import dao.ClazzDao;
import util.ExcelUtil;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

/**
 * 成绩管理面板
 */
public class ScorePanel extends JPanel {
    
    private User currentUser;
    private ScoreService scoreService = new ScoreService();
    private StudentDao studentDao = new StudentDao();
    private CourseDao courseDao = new CourseDao();
    private ClazzDao clazzDao = new ClazzDao();
    
    // 搜索组件
    private JTextField searchStudentNoField;
    private JTextField searchStudentNameField;
    private JComboBox<Clazz> searchClassCombo;
    private JComboBox<Course> searchCourseCombo;
    private JComboBox<String> searchExamTypeCombo;
    private JButton searchButton;
    private JButton resetButton;
    
    // 功能按钮
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JButton batchInputButton;
    
    // 数据表格
    private JTable scoreTable;
    private DefaultTableModel tableModel;
    
    // 分页
    private JLabel pageInfoLabel;
    private JButton prevButton;
    private JButton nextButton;
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalRecords = 0;
    
    // 当前数据
    private List<Score> allScores = new ArrayList<>();
    
    public ScorePanel(User user) {
        this.currentUser = user;
        initComponents();
        initEvents();
        refreshData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 顶部搜索区域
        add(createSearchPanel(), BorderLayout.NORTH);
        
        // 中间表格区域
        add(createTablePanel(), BorderLayout.CENTER);
        
        // 底部按钮区域
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        // 根据角色设置按钮可见性
        updateButtonVisibility();
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("查询条件"));
        
        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // 学号
        searchFieldsPanel.add(new JLabel("学号："));
        searchStudentNoField = new JTextField(10);
        searchFieldsPanel.add(searchStudentNoField);
        
        // 姓名
        searchFieldsPanel.add(new JLabel("姓名："));
        searchStudentNameField = new JTextField(8);
        searchFieldsPanel.add(searchStudentNameField);
        
        // 班级
        searchFieldsPanel.add(new JLabel("班级："));
        searchClassCombo = new JComboBox<>();
        searchClassCombo.setPreferredSize(new Dimension(120, 25));
        loadClassCombo();
        searchFieldsPanel.add(searchClassCombo);
        
        // 课程
        searchFieldsPanel.add(new JLabel("课程："));
        searchCourseCombo = new JComboBox<>();
        searchCourseCombo.setPreferredSize(new Dimension(150, 25));
        loadCourseCombo();
        searchFieldsPanel.add(searchCourseCombo);
        
        // 考试类型
        searchFieldsPanel.add(new JLabel("类型："));
        searchExamTypeCombo = new JComboBox<>(new String[]{"全部", "平时", "期中", "期末"});
        searchFieldsPanel.add(searchExamTypeCombo);
        
        // 搜索按钮
        searchButton = new JButton("搜索");
        searchFieldsPanel.add(searchButton);
        
        // 重置按钮
        resetButton = new JButton("重置");
        searchFieldsPanel.add(resetButton);
        
        panel.add(searchFieldsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格模型
        String[] columns = {"ID", "学号", "姓名", "班级", "课程编号", "课程名称", "成绩", "等级", "考试类型", "考试日期"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scoreTable = new JTable(tableModel);
        scoreTable.setRowHeight(25);
        scoreTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        scoreTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        TableColumnModel columnModel = scoreTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(5).setPreferredWidth(120);
        columnModel.getColumn(6).setPreferredWidth(60);
        columnModel.getColumn(7).setPreferredWidth(60);
        columnModel.getColumn(8).setPreferredWidth(70);
        columnModel.getColumn(9).setPreferredWidth(100);
        
        // 隐藏ID列
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setPreferredWidth(0);
        
        // 设置成绩列的渲染器（不及格显示红色）
        columnModel.getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (value != null && !isSelected) {
                    try {
                        double score = Double.parseDouble(value.toString());
                        if (score < 60) {
                            setForeground(Color.RED);
                        } else if (score >= 90) {
                            setForeground(new Color(0, 128, 0));
                        } else {
                            setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException ignored) {
                        setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 左侧功能按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        addButton = new JButton("录入成绩");
        addButton.setBackground(new Color(92, 184, 92));
        buttonPanel.add(addButton);
        
        batchInputButton = new JButton("批量录入");
        batchInputButton.setBackground(new Color(91, 192, 222));
        buttonPanel.add(batchInputButton);
        
        editButton = new JButton("修改成绩");
        editButton.setBackground(new Color(240, 173, 78));
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("删除成绩");
        deleteButton.setBackground(new Color(217, 83, 79));
        buttonPanel.add(deleteButton);
        
        buttonPanel.add(new JLabel("    "));
        
        exportButton = new JButton("导出Excel");
        buttonPanel.add(exportButton);
        
        panel.add(buttonPanel, BorderLayout.WEST);
        
        // 右侧分页
        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        prevButton = new JButton("上一页");
        pagePanel.add(prevButton);
        
        pageInfoLabel = new JLabel("第 1 页 / 共 1 页（共 0 条）");
        pagePanel.add(pageInfoLabel);
        
        nextButton = new JButton("下一页");
        pagePanel.add(nextButton);
        
        panel.add(pagePanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadClassCombo() {
        try {
            searchClassCombo.removeAllItems();
            Clazz allClass = new Clazz();
            allClass.setId(null);
            allClass.setClassName("全部班级");
            searchClassCombo.addItem(allClass);
            
            List<Clazz> classes = clazzDao.findAll();
            for (Clazz clazz : classes) {
                searchClassCombo.addItem(clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadCourseCombo() {
        try {
            searchCourseCombo.removeAllItems();
            Course allCourse = new Course();
            allCourse.setId(null);
            allCourse.setCourseName("全部课程");
            searchCourseCombo.addItem(allCourse);
            
            List<Course> courses;
            if (currentUser.isTeacher()) {
                courses = courseDao.findByTeacherId(currentUser.getRelatedId());
            } else {
                courses = courseDao.findAll();
            }
            
            for (Course course : courses) {
                searchCourseCombo.addItem(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateButtonVisibility() {
        if (currentUser.isStudent()) {
            // 学生只能查看自己的成绩
            addButton.setVisible(false);
            batchInputButton.setVisible(false);
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            
            // 禁用大部分搜索条件
            searchStudentNoField.setEnabled(false);
            searchStudentNameField.setEnabled(false);
            searchClassCombo.setEnabled(false);
        } else if (currentUser.isTeacher()) {
            // 教师可以录入和修改自己课程的成绩
            deleteButton.setVisible(false);
        }
        // 管理员可以进行所有操作
    }
    
    private void initEvents() {
        searchButton.addActionListener(e -> doSearch());
        resetButton.addActionListener(e -> doReset());
        addButton.addActionListener(e -> showAddDialog());
        batchInputButton.addActionListener(e -> showBatchInputDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> doDelete());
        exportButton.addActionListener(e -> doExport());
        
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });
        
        nextButton.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            if (currentPage < totalPages) {
                currentPage++;
                updateTable();
            }
        });
        
        scoreTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !currentUser.isStudent()) {
                    showEditDialog();
                }
            }
        });
    }
    
    public void refreshData() {
        try {
            if (currentUser.isStudent()) {
                // 学生只能看自己的成绩
                allScores = scoreService.findByStudentId(currentUser.getRelatedId());
            } else if (currentUser.isTeacher()) {
                // 教师只能看自己教授课程的成绩
                allScores = scoreService.findByTeacherId(currentUser.getRelatedId());
            } else {
                allScores = scoreService.findAll();
            }
            totalRecords = allScores.size();
            currentPage = 1;
            updateTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void doSearch() {
        try {
            String studentNo = searchStudentNoField.getText().trim();
            String studentName = searchStudentNameField.getText().trim();
            
            Clazz selectedClass = (Clazz) searchClassCombo.getSelectedItem();
            Integer classId = (selectedClass != null && selectedClass.getId() != null) ? 
                              selectedClass.getId() : null;
            
            Course selectedCourse = (Course) searchCourseCombo.getSelectedItem();
            Integer courseId = (selectedCourse != null && selectedCourse.getId() != null) ? 
                               selectedCourse.getId() : null;
            
            String examType = (String) searchExamTypeCombo.getSelectedItem();
            if ("全部".equals(examType)) examType = null;
            
            // 如果是学生，强制只查询自己的成绩
            if (currentUser.isStudent()) {
                allScores = scoreService.findByStudentId(currentUser.getRelatedId());
                // 在内存中进一步过滤
                if (courseId != null) {
                    Integer finalCourseId = courseId;
                    allScores.removeIf(s -> !finalCourseId.equals(s.getCourseId()));
                }
                if (examType != null) {
                    String finalExamType = examType;
                    allScores.removeIf(s -> !finalExamType.equals(s.getExamType()));
                }
            } else {
                allScores = scoreService.search(
                    studentNo.isEmpty() ? null : studentNo,
                    studentName.isEmpty() ? null : studentName,
                    classId,
                    courseId,
                    examType,
                    null, null
                );
                
                // 如果是教师，过滤只显示自己课程的成绩
                if (currentUser.isTeacher()) {
                    List<Course> myCourses = courseDao.findByTeacherId(currentUser.getRelatedId());
                    List<Integer> myCourseIds = new ArrayList<>();
                    for (Course c : myCourses) {
                        myCourseIds.add(c.getId());
                    }
                    allScores.removeIf(s -> !myCourseIds.contains(s.getCourseId()));
                }
            }
            
            totalRecords = allScores.size();
            currentPage = 1;
            updateTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "搜索失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void doReset() {
        searchStudentNoField.setText("");
        searchStudentNameField.setText("");
        searchClassCombo.setSelectedIndex(0);
        searchCourseCombo.setSelectedIndex(0);
        searchExamTypeCombo.setSelectedIndex(0);
        refreshData();
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allScores.size());
        
        for (int i = start; i < end; i++) {
            Score s = allScores.get(i);
            Object[] row = {
                s.getId(),
                s.getStudentNo(),
                s.getStudentName(),
                s.getClassName(),
                s.getCourseNo(),
                s.getCourseName(),
                s.getScore() != null ? String.format("%.1f", s.getScore()) : "",
                s.getGrade(),
                s.getExamType(),
                s.getExamDate() != null ? sdf.format(s.getExamDate()) : ""
            };
            tableModel.addRow(row);
        }
        
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;
        pageInfoLabel.setText(String.format("第 %d 页 / 共 %d 页（共 %d 条）", 
                currentPage, totalPages, totalRecords));
        
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }
    
    private void showAddDialog() {
        ScoreDialog dialog = new ScoreDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "录入成绩", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void showBatchInputDialog() {
        BatchScoreDialog dialog = new BatchScoreDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        refreshData();
    }
    
    private void showEditDialog() {
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的成绩记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer scoreId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Score score = scoreService.findById(scoreId);
            if (score == null) {
                JOptionPane.showMessageDialog(this, "成绩记录不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 教师权限检查
            if (currentUser.isTeacher()) {
                if (!scoreService.canTeacherModifyScore(currentUser.getRelatedId(), scoreId)) {
                    JOptionPane.showMessageDialog(this, "您只能修改自己所教课程的成绩", 
                            "权限不足", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            ScoreDialog dialog = new ScoreDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                "修改成绩", score);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取成绩信息失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDelete() {
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的成绩记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer scoreId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String studentName = (String) tableModel.getValueAt(selectedRow, 2);
        String courseName = (String) tableModel.getValueAt(selectedRow, 5);
        
        int result = JOptionPane.showConfirmDialog(this, 
                "确定要删除 " + studentName + " 的 " + courseName + " 成绩吗？", 
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                scoreService.deleteScore(scoreId);
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void doExport() {
        if (allScores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有数据可导出", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] headers = {"学号", "姓名", "班级", "课程编号", "课程名称", "成绩", "等级", "考试类型", "考试日期"};
        List<Object[]> data = new ArrayList<>();
        
        for (Score s : allScores) {
            data.add(new Object[]{
                s.getStudentNo(),
                s.getStudentName(),
                s.getClassName(),
                s.getCourseNo(),
                s.getCourseName(),
                s.getScore() != null ? String.format("%.1f", s.getScore()) : "",
                s.getGrade(),
                s.getExamType(),
                s.getExamDate() != null ? sdf.format(s.getExamDate()) : ""
            });
        }
        
        ExcelUtil.exportScores(data, headers);
    }
    
    /**
     * 成绩录入/编辑对话框
     */
    private class ScoreDialog extends JDialog {
        private Score score;
        private boolean confirmed = false;
        
        private JComboBox<Student> studentCombo;
        private JComboBox<Course> courseCombo;
        private JTextField scoreField;
        private JComboBox<String> examTypeCombo;
        private JTextField examDateField;
        
        public ScoreDialog(JFrame parent, String title, Score score) {
            super(parent, title, true);
            this.score = score;
            initComponents();
            initData();
            pack();
            setLocationRelativeTo(parent);
        }
        
        private void initComponents() {
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // 学生
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("学生：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            studentCombo = new JComboBox<>();
            studentCombo.setPreferredSize(new Dimension(250, 28));
            loadStudentCombo();
            formPanel.add(studentCombo, gbc);
            
            row++;
            
            // 课程
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("课程：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            courseCombo = new JComboBox<>();
            courseCombo.setPreferredSize(new Dimension(250, 28));
            loadDialogCourseCombo();
            formPanel.add(courseCombo, gbc);
            
            row++;
            
            // 成绩
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("成绩：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            scoreField = new JTextField(10);
            scoreField.setToolTipText("0-100之间的数字");
            formPanel.add(scoreField, gbc);
            
            row++;
            
            // 考试类型
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("考试类型："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            examTypeCombo = new JComboBox<>(new String[]{"期末", "期中", "平时"});
            formPanel.add(examTypeCombo, gbc);
            
            row++;
            
            // 考试日期
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("考试日期："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            examDateField = new JTextField(15);
            examDateField.setToolTipText("格式：yyyy-MM-dd");
            formPanel.add(examDateField, gbc);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);
            
            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            
            JButton saveButton = new JButton("保存");
            saveButton.setPreferredSize(new Dimension(100, 35));
            saveButton.addActionListener(e -> doSave());
            
            JButton cancelButton = new JButton("取消");
            cancelButton.setPreferredSize(new Dimension(100, 35));
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private void loadStudentCombo() {
            try {
                studentCombo.removeAllItems();
                
                List<Student> students = studentDao.findAll();
                for (Student s : students) {
                    studentCombo.addItem(s);
                }
                
                // 自定义显示
                studentCombo.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Student) {
                            Student s = (Student) value;
                            setText(s.getStudentNo() + " - " + s.getName() + 
                                   (s.getClassName() != null ? " (" + s.getClassName() + ")" : ""));
                        }
                        return this;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadDialogCourseCombo() {
            try {
                courseCombo.removeAllItems();
                
                List<Course> courses;
                if (currentUser.isTeacher()) {
                    courses = courseDao.findByTeacherId(currentUser.getRelatedId());
                } else {
                    courses = courseDao.findAll();
                }
                
                for (Course c : courses) {
                    courseCombo.addItem(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void initData() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            if (score != null) {
                // 编辑模式
                // 选择学生
                for (int i = 0; i < studentCombo.getItemCount(); i++) {
                    Student s = studentCombo.getItemAt(i);
                    if (s.getId().equals(score.getStudentId())) {
                        studentCombo.setSelectedIndex(i);
                        break;
                    }
                }
                studentCombo.setEnabled(false); // 编辑时不能更改学生
                
                // 选择课程
                for (int i = 0; i < courseCombo.getItemCount(); i++) {
                    Course c = courseCombo.getItemAt(i);
                    if (c.getId().equals(score.getCourseId())) {
                        courseCombo.setSelectedIndex(i);
                        break;
                    }
                }
                courseCombo.setEnabled(false); // 编辑时不能更改课程
                
                scoreField.setText(score.getScore() != null ? 
                        String.format("%.1f", score.getScore()) : "");
                examTypeCombo.setSelectedItem(score.getExamType());
                examTypeCombo.setEnabled(false); // 编辑时不能更改考试类型
                
                if (score.getExamDate() != null) {
                    examDateField.setText(sdf.format(score.getExamDate()));
                }
            } else {
                // 新增模式，默认日期为今天
                examDateField.setText(sdf.format(new java.util.Date()));
            }
        }
        
        private void doSave() {
            try {
                Student selectedStudent = (Student) studentCombo.getSelectedItem();
                Course selectedCourse = (Course) courseCombo.getSelectedItem();
                
                if (selectedStudent == null) {
                    throw new IllegalArgumentException("请选择学生");
                }
                if (selectedCourse == null) {
                    throw new IllegalArgumentException("请选择课程");
                }
                
                String scoreStr = scoreField.getText().trim();
                if (scoreStr.isEmpty()) {
                    throw new IllegalArgumentException("请输入成绩");
                }
                
                double scoreValue = Double.parseDouble(scoreStr);
                if (scoreValue < 0 || scoreValue > 100) {
                    throw new IllegalArgumentException("成绩必须在0-100之间");
                }
                
                Score s = (score != null) ? score : new Score();
                s.setStudentId(selectedStudent.getId());
                s.setCourseId(selectedCourse.getId());
                s.setScore(scoreValue);
                s.setExamType((String) examTypeCombo.getSelectedItem());
                s.setRecorderId(currentUser.getRelatedId());
                
                String dateStr = examDateField.getText().trim();
                if (!dateStr.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    s.setExamDate(sdf.parse(dateStr));
                }
                
                if (score == null) {
                    scoreService.addScore(s);
                    JOptionPane.showMessageDialog(this, "录入成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    scoreService.updateScore(s);
                    JOptionPane.showMessageDialog(this, "修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
                
                confirmed = true;
                dispose();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "成绩格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
    
    /**
     * 批量录入成绩对话框
     */
    private class BatchScoreDialog extends JDialog {
        private JComboBox<Course> courseCombo;
        private JComboBox<Clazz> classCombo;
        private JComboBox<String> examTypeCombo;
        private JTable inputTable;
        private DefaultTableModel inputTableModel;
        
        public BatchScoreDialog(JFrame parent) {
            super(parent, "批量录入成绩", true);
            initComponents();
            setSize(700, 500);
            setLocationRelativeTo(parent);
        }
        
        private void initComponents() {
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // 顶部选择区域
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topPanel.setBorder(BorderFactory.createTitledBorder("选择条件"));
            
            topPanel.add(new JLabel("课程："));
            courseCombo = new JComboBox<>();
            courseCombo.setPreferredSize(new Dimension(200, 28));
            loadBatchCourseCombo();
            topPanel.add(courseCombo);
            
            topPanel.add(new JLabel("班级："));
            classCombo = new JComboBox<>();
            classCombo.setPreferredSize(new Dimension(150, 28));
            loadBatchClassCombo();
            topPanel.add(classCombo);
            
            topPanel.add(new JLabel("类型："));
            examTypeCombo = new JComboBox<>(new String[]{"期末", "期中", "平时"});
            topPanel.add(examTypeCombo);
            
            JButton loadButton = new JButton("加载学生");
            loadButton.setFont(new Font("Dialog", Font.PLAIN, 14));
            loadButton.setPreferredSize(new Dimension(100, 30));
            loadButton.addActionListener(e -> loadStudents());
            topPanel.add(loadButton);
            
            mainPanel.add(topPanel, BorderLayout.NORTH);
            
            // 中间表格
            String[] columns = {"学号", "姓名", "成绩"};
            inputTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // 只有成绩列可编辑
                }
            };
            
            inputTable = new JTable(inputTableModel);
            inputTable.setRowHeight(28);
            inputTable.setFont(new Font("Dialog", Font.PLAIN, 14));
            inputTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 14));
            
            // 设置成绩列编辑器
            inputTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean stopCellEditing() {
                    String value = ((JTextField) getComponent()).getText().trim();
                    if (!value.isEmpty()) {
                        try {
                            double score = Double.parseDouble(value);
                            if (score < 0 || score > 100) {
                                JOptionPane.showMessageDialog(BatchScoreDialog.this, 
                                    "成绩必须在0-100之间", "输入错误", JOptionPane.WARNING_MESSAGE);
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(BatchScoreDialog.this, 
                                "请输入有效的数字", "输入错误", JOptionPane.WARNING_MESSAGE);
                            return false;
                        }
                    }
                    return super.stopCellEditing();
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(inputTable);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            
            // 底部按钮
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            
            JButton saveButton = new JButton("保存全部");
            saveButton.setFont(new Font("Dialog", Font.BOLD, 14));
            saveButton.setPreferredSize(new Dimension(120, 35));
            saveButton.addActionListener(e -> saveAll());
            
            JButton cancelButton = new JButton("关闭");
            cancelButton.setFont(new Font("Dialog", Font.PLAIN, 14));
            cancelButton.setPreferredSize(new Dimension(80, 35));
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            add(mainPanel);
        }
        
        private void loadBatchCourseCombo() {
            try {
                courseCombo.removeAllItems();
                List<Course> courses;
                if (currentUser.isTeacher()) {
                    courses = courseDao.findByTeacherId(currentUser.getRelatedId());
                } else {
                    courses = courseDao.findAll();
                }
                for (Course c : courses) {
                    courseCombo.addItem(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadBatchClassCombo() {
            try {
                classCombo.removeAllItems();
                List<Clazz> classes = clazzDao.findAll();
                for (Clazz c : classes) {
                    classCombo.addItem(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void loadStudents() {
            inputTableModel.setRowCount(0);
            
            Clazz selectedClass = (Clazz) classCombo.getSelectedItem();
            if (selectedClass == null) {
                JOptionPane.showMessageDialog(this, "请选择班级", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                List<Student> students = studentDao.findByClassId(selectedClass.getId());
                for (Student s : students) {
                    inputTableModel.addRow(new Object[]{s.getStudentNo(), s.getName(), ""});
                }
                
                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "该班级没有学生", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "加载学生失败：" + e.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void saveAll() {
            Course selectedCourse = (Course) courseCombo.getSelectedItem();
            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "请选择课程", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 停止编辑
            if (inputTable.isEditing()) {
                inputTable.getCellEditor().stopCellEditing();
            }
            
            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMsg = new StringBuilder();
            
            for (int i = 0; i < inputTableModel.getRowCount(); i++) {
                String studentNo = (String) inputTableModel.getValueAt(i, 0);
                String scoreStr = inputTableModel.getValueAt(i, 2).toString().trim();
                
                if (scoreStr.isEmpty()) {
                    continue; // 跳过空成绩
                }
                
                try {
                    double scoreValue = Double.parseDouble(scoreStr);
                    
                    Student student = studentDao.findByStudentNo(studentNo);
                    if (student == null) {
                        throw new IllegalArgumentException("学生不存在");
                    }
                    
                    Score score = new Score();
                    score.setStudentId(student.getId());
                    score.setCourseId(selectedCourse.getId());
                    score.setScore(scoreValue);
                    score.setExamType((String) examTypeCombo.getSelectedItem());
                    score.setRecorderId(currentUser.getRelatedId());
                    score.setExamDate(new java.util.Date());
                    
                    scoreService.addScore(score);
                    successCount++;
                    
                } catch (Exception e) {
                    failCount++;
                    errorMsg.append("第").append(i + 1).append("行 ").append(studentNo)
                            .append("：").append(e.getMessage()).append("\n");
                }
            }
            
            String msg = "保存完成！\n成功：" + successCount + " 条";
            if (failCount > 0) {
                msg += "\n失败：" + failCount + " 条\n\n失败详情：\n" + errorMsg.toString();
            }
            
            JOptionPane.showMessageDialog(this, msg, "保存结果", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}