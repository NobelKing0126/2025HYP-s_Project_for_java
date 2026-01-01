package ui.panel;

import entity.User;
import entity.Course;
import entity.Teacher;
import dao.CourseDao;
import dao.TeacherDao;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 课程管理面板
 */
public class CoursePanel extends JPanel {
    
    private User currentUser;
    private CourseDao courseDao = new CourseDao();
    private TeacherDao teacherDao = new TeacherDao();
    
    // 搜索组件
    private JTextField searchCourseNoField;
    private JTextField searchCourseNameField;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton;
    private JButton resetButton;
    
    // 功能按钮
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    // 数据表格
    private JTable courseTable;
    private DefaultTableModel tableModel;
    
    // 当前数据
    private List<Course> allCourses = new ArrayList<>();
    
    public CoursePanel(User user) {
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
        
        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        // 课程编号
        searchFieldsPanel.add(new JLabel("课程编号："));
        searchCourseNoField = new JTextField(10);
        searchFieldsPanel.add(searchCourseNoField);
        
        // 课程名称
        searchFieldsPanel.add(new JLabel("课程名称："));
        searchCourseNameField = new JTextField(12);
        searchFieldsPanel.add(searchCourseNameField);
        
        // 课程类型
        searchFieldsPanel.add(new JLabel("类型："));
        searchTypeCombo = new JComboBox<>(new String[]{"全部", "必修", "选修"});
        searchFieldsPanel.add(searchTypeCombo);
        
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
        String[] columns = {"ID", "课程编号", "课程名称", "学分", "学时", "授课教师", "学期", "类型"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(25);
        courseTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        courseTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        TableColumnModel columnModel = courseTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(60);
        columnModel.getColumn(4).setPreferredWidth(60);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(120);
        columnModel.getColumn(7).setPreferredWidth(60);
        
        // 隐藏ID列
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setPreferredWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(courseTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        addButton = new JButton("添加课程");
        addButton.setBackground(new Color(92, 184, 92));
        panel.add(addButton);
        
        editButton = new JButton("编辑课程");
        editButton.setBackground(new Color(91, 192, 222));
        panel.add(editButton);
        
        deleteButton = new JButton("删除课程");
        deleteButton.setBackground(new Color(217, 83, 79));
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void updateButtonVisibility() {
        // 只有管理员可以增删改
        boolean isAdmin = currentUser.isAdmin();
        addButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
    }
    
    private void initEvents() {
        searchButton.addActionListener(e -> doSearch());
        resetButton.addActionListener(e -> doReset());
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> doDelete());
        
        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && currentUser.isAdmin()) {
                    showEditDialog();
                }
            }
        });
    }
    
    public void refreshData() {
        try {
            if (currentUser.isTeacher()) {
                // 教师只能看自己的课程
                allCourses = courseDao.findByTeacherId(currentUser.getRelatedId());
            } else {
                allCourses = courseDao.findAll();
            }
            updateTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void doSearch() {
        try {
            String courseNo = searchCourseNoField.getText().trim();
            String courseName = searchCourseNameField.getText().trim();
            String type = (String) searchTypeCombo.getSelectedItem();
            
            Integer teacherId = currentUser.isTeacher() ? currentUser.getRelatedId() : null;
            
            allCourses = courseDao.search(
                courseNo.isEmpty() ? null : courseNo,
                courseName.isEmpty() ? null : courseName,
                teacherId,
                null,
                "全部".equals(type) ? null : type
            );
            
            updateTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "搜索失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doReset() {
        searchCourseNoField.setText("");
        searchCourseNameField.setText("");
        searchTypeCombo.setSelectedIndex(0);
        refreshData();
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Course c : allCourses) {
            Object[] row = {
                c.getId(),
                c.getCourseNo(),
                c.getCourseName(),
                c.getCredit(),
                c.getHours(),
                c.getTeacherName(),
                c.getSemester(),
                c.getCourseType()
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddDialog() {
        CourseDialog dialog = new CourseDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "添加课程", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Course course = courseDao.findById(courseId);
            if (course == null) {
                JOptionPane.showMessageDialog(this, "课程不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            CourseDialog dialog = new CourseDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                "编辑课程", course);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取课程信息失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDelete() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer courseId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 2);
        
        try {
            if (courseDao.hasScores(courseId)) {
                JOptionPane.showMessageDialog(this, 
                        "课程 " + courseName + " 已有成绩记录，不能删除！\n请先删除相关成绩记录。", 
                        "无法删除", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int result = JOptionPane.showConfirmDialog(this, 
                    "确定要删除课程 " + courseName + " 吗？", 
                    "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                courseDao.delete(courseId);
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 课程编辑对话框
     */
    private class CourseDialog extends JDialog {
        private Course course;
        private boolean confirmed = false;
        
        private JTextField courseNoField;
        private JTextField courseNameField;
        private JTextField creditField;
        private JTextField hoursField;
        private JComboBox<Teacher> teacherCombo;
        private JTextField semesterField;
        private JComboBox<String> typeCombo;
        
        public CourseDialog(JFrame parent, String title, Course course) {
            super(parent, title, true);
            this.course = course;
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
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // 课程编号
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("课程编号：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            courseNoField = new JTextField(15);
            formPanel.add(courseNoField, gbc);
            
            // 课程名称
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("课程名称：*"), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            courseNameField = new JTextField(15);
            formPanel.add(courseNameField, gbc);
            
            row++;
            
            // 学分
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("学分："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            creditField = new JTextField(10);
            formPanel.add(creditField, gbc);
            
            // 学时
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("学时："), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            hoursField = new JTextField(10);
            formPanel.add(hoursField, gbc);
            
            row++;
            
            // 授课教师
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("授课教师："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            teacherCombo = new JComboBox<>();
            loadTeacherCombo();
            formPanel.add(teacherCombo, gbc);
            
            // 课程类型
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("类型："), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            typeCombo = new JComboBox<>(new String[]{"必修", "选修"});
            formPanel.add(typeCombo, gbc);
            
            row++;
            
            // 学期
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("学期："), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            semesterField = new JTextField(20);
            semesterField.setToolTipText("例如：2023-2024-1");
            formPanel.add(semesterField, gbc);
            
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
        
        private void loadTeacherCombo() {
            try {
                teacherCombo.removeAllItems();
                Teacher emptyTeacher = new Teacher();
                emptyTeacher.setId(null);
                emptyTeacher.setName("-- 请选择 --");
                teacherCombo.addItem(emptyTeacher);
                
                List<Teacher> teachers = teacherDao.findAll();
                for (Teacher t : teachers) {
                    teacherCombo.addItem(t);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void initData() {
            if (course != null) {
                courseNoField.setText(course.getCourseNo());
                courseNameField.setText(course.getCourseName());
                creditField.setText(course.getCredit() != null ? course.getCredit().toString() : "");
                hoursField.setText(course.getHours() != null ? course.getHours().toString() : "");
                semesterField.setText(course.getSemester());
                typeCombo.setSelectedItem(course.getCourseType());
                
                // 选择教师
                for (int i = 0; i < teacherCombo.getItemCount(); i++) {
                    Teacher t = teacherCombo.getItemAt(i);
                    if (t.getId() != null && t.getId().equals(course.getTeacherId())) {
                        teacherCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        
        private void doSave() {
            try {
                String courseNo = courseNoField.getText().trim();
                String courseName = courseNameField.getText().trim();
                
                if (courseNo.isEmpty()) {
                    throw new IllegalArgumentException("课程编号不能为空");
                }
                if (courseName.isEmpty()) {
                    throw new IllegalArgumentException("课程名称不能为空");
                }
                
                // 检查课程编号是否重复
                if (course == null) {
                    if (courseDao.existsByCourseNo(courseNo)) {
                        throw new IllegalArgumentException("课程编号已存在");
                    }
                } else {
                    if (courseDao.existsByCourseNo(courseNo, course.getId())) {
                        throw new IllegalArgumentException("课程编号已存在");
                    }
                }
                
                Course c = (course != null) ? course : new Course();
                c.setCourseNo(courseNo);
                c.setCourseName(courseName);
                
                String creditStr = creditField.getText().trim();
                if (!creditStr.isEmpty()) {
                    c.setCredit(Double.parseDouble(creditStr));
                }
                
                String hoursStr = hoursField.getText().trim();
                if (!hoursStr.isEmpty()) {
                    c.setHours(Integer.parseInt(hoursStr));
                }
                
                Teacher selectedTeacher = (Teacher) teacherCombo.getSelectedItem();
                c.setTeacherId(selectedTeacher != null ? selectedTeacher.getId() : null);
                
                c.setSemester(semesterField.getText().trim());
                c.setCourseType((String) typeCombo.getSelectedItem());
                
                if (course == null) {
                    courseDao.insert(c);
                    JOptionPane.showMessageDialog(this, "添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    courseDao.update(c);
                    JOptionPane.showMessageDialog(this, "修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
                
                confirmed = true;
                dispose();
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "学分或学时格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
}