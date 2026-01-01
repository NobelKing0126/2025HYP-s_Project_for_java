package ui.panel;

import entity.User;
import entity.Student;
import entity.Clazz;
import service.StudentService;
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
 * 学生管理面板
 */
public class StudentPanel extends JPanel {
    
    private User currentUser;
    private StudentService studentService = new StudentService();
    private ClazzDao clazzDao = new ClazzDao();
    
    // 搜索组件
    private JTextField searchStudentNoField;
    private JTextField searchNameField;
    private JComboBox<Clazz> searchClassCombo;
    private JComboBox<String> searchStatusCombo;
    private JButton searchButton;
    private JButton resetButton;
    
    // 功能按钮
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton exportButton;
    private JButton importButton;
    
    // 数据表格
    private JTable studentTable;
    private DefaultTableModel tableModel;
    
    // 分页组件
    private JLabel pageInfoLabel;
    private JButton prevButton;
    private JButton nextButton;
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalRecords = 0;
    
    // 当前数据
    private List<Student> allStudents = new ArrayList<>();
    
    public StudentPanel(User user) {
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
        
        // 底部分页和按钮区域
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        // 根据角色设置按钮可见性
        updateButtonVisibility();
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("查询条件"));
        
        JPanel searchFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        // 学号
        searchFieldsPanel.add(new JLabel("学号："));
        searchStudentNoField = new JTextField(10);
        searchFieldsPanel.add(searchStudentNoField);
        
        // 姓名
        searchFieldsPanel.add(new JLabel("姓名："));
        searchNameField = new JTextField(10);
        searchFieldsPanel.add(searchNameField);
        
        // 班级
        searchFieldsPanel.add(new JLabel("班级："));
        searchClassCombo = new JComboBox<>();
        searchClassCombo.setPreferredSize(new Dimension(150, 25));
        loadClassCombo();
        searchFieldsPanel.add(searchClassCombo);
        
        // 状态
        searchFieldsPanel.add(new JLabel("状态："));
        searchStatusCombo = new JComboBox<>(new String[]{"全部", "在读", "休学", "毕业", "退学"});
        searchFieldsPanel.add(searchStatusCombo);
        
        // 搜索按钮
        searchButton = new JButton("搜索");
        searchButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
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
        String[] columns = {"选择", "ID", "学号", "姓名", "性别", "班级", "电话", "邮箱", "状态", "入学日期"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // 只有第一列（复选框）可编辑
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        studentTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        studentTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        TableColumnModel columnModel = studentTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);  // 选择
        columnModel.getColumn(1).setPreferredWidth(40);  // ID
        columnModel.getColumn(2).setPreferredWidth(100); // 学号
        columnModel.getColumn(3).setPreferredWidth(80);  // 姓名
        columnModel.getColumn(4).setPreferredWidth(50);  // 性别
        columnModel.getColumn(5).setPreferredWidth(100); // 班级
        columnModel.getColumn(6).setPreferredWidth(110); // 电话
        columnModel.getColumn(7).setPreferredWidth(150); // 邮箱
        columnModel.getColumn(8).setPreferredWidth(60);  // 状态
        columnModel.getColumn(9).setPreferredWidth(100); // 入学日期
        
        // 隐藏ID列
        columnModel.getColumn(1).setMinWidth(0);
        columnModel.getColumn(1).setMaxWidth(0);
        columnModel.getColumn(1).setPreferredWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 左侧功能按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        addButton = new JButton("添加");
        addButton.setBackground(new Color(92, 184, 92));
        buttonPanel.add(addButton);
        
        editButton = new JButton("编辑");
        editButton.setBackground(new Color(91, 192, 222));
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("删除");
        deleteButton.setBackground(new Color(217, 83, 79));
        buttonPanel.add(deleteButton);
        
        buttonPanel.add(new JLabel("    "));
        
        exportButton = new JButton("导出Excel");
        buttonPanel.add(exportButton);
        
        importButton = new JButton("导入Excel");
        buttonPanel.add(importButton);
        
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
    
    private void updateButtonVisibility() {
        // 学生只能查看自己的信息，不能添加、编辑、删除
        if (currentUser.isStudent()) {
            addButton.setVisible(false);
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            importButton.setVisible(false);
            
            // 隐藏搜索条件，学生只能看自己
            searchStudentNoField.setEnabled(false);
            searchNameField.setEnabled(false);
            searchClassCombo.setEnabled(false);
            searchStatusCombo.setEnabled(false);
            searchButton.setEnabled(false);
            resetButton.setEnabled(false);
        }
        
        // 教师只能查看，不能添加、删除
        if (currentUser.isTeacher()) {
            addButton.setVisible(false);
            deleteButton.setVisible(false);
            importButton.setVisible(false);
        }
    }
    
    private void initEvents() {
        // 搜索按钮
        searchButton.addActionListener(e -> doSearch());
        
        // 重置按钮
        resetButton.addActionListener(e -> doReset());
        
        // 添加按钮
        addButton.addActionListener(e -> showAddDialog());
        
        // 编辑按钮
        editButton.addActionListener(e -> showEditDialog());
        
        // 删除按钮
        deleteButton.addActionListener(e -> doDelete());
        
        // 导出按钮
        exportButton.addActionListener(e -> doExport());
        
        // 导入按钮
        importButton.addActionListener(e -> doImport());
        
        // 分页按钮
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
        
        // 双击编辑
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !currentUser.isStudent()) {
                    showEditDialog();
                }
            }
        });
        
        // 回车搜索
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doSearch();
                }
            }
        };
        searchStudentNoField.addKeyListener(enterKey);
        searchNameField.addKeyListener(enterKey);
    }
    
    public void refreshData() {
        try {
            if (currentUser.isStudent()) {
                // 学生只能看自己的信息
                Student student = studentService.findById(currentUser.getRelatedId());
                allStudents = new ArrayList<>();
                if (student != null) {
                    allStudents.add(student);
                }
            } else {
                allStudents = studentService.findAll();
            }
            totalRecords = allStudents.size();
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
            String name = searchNameField.getText().trim();
            
            Clazz selectedClass = (Clazz) searchClassCombo.getSelectedItem();
            Integer classId = (selectedClass != null && selectedClass.getId() != null) ? 
                              selectedClass.getId() : null;
            
            String status = (String) searchStatusCombo.getSelectedItem();
            if ("全部".equals(status)) status = null;
            
            allStudents = studentService.search(
                studentNo.isEmpty() ? null : studentNo,
                name.isEmpty() ? null : name,
                classId,
                status
            );
            
            totalRecords = allStudents.size();
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
        searchNameField.setText("");
        searchClassCombo.setSelectedIndex(0);
        searchStatusCombo.setSelectedIndex(0);
        refreshData();
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allStudents.size());
        
        for (int i = start; i < end; i++) {
            Student s = allStudents.get(i);
            Object[] row = {
                false,
                s.getId(),
                s.getStudentNo(),
                s.getName(),
                s.getGender(),
                s.getClassName(),
                s.getPhone(),
                s.getEmail(),
                s.getStatus(),
                s.getEnrollmentDate() != null ? sdf.format(s.getEnrollmentDate()) : ""
            };
            tableModel.addRow(row);
        }
        
        // 更新分页信息
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;
        pageInfoLabel.setText(String.format("第 %d 页 / 共 %d 页（共 %d 条）", 
                currentPage, totalPages, totalRecords));
        
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }
    
    private void showAddDialog() {
        StudentDialog dialog = new StudentDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "添加学生", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer studentId = (Integer) tableModel.getValueAt(selectedRow, 1);
        
        try {
            Student student = studentService.findById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "学生不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StudentDialog dialog = new StudentDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                "编辑学生", student);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取学生信息失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDelete() {
        // 收集选中的行
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) tableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                selectedIds.add((Integer) tableModel.getValueAt(i, 1));
            }
        }
        
        // 如果没有勾选，检查是否选中了某一行
        if (selectedIds.isEmpty()) {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedIds.add((Integer) tableModel.getValueAt(selectedRow, 1));
            }
        }
        
        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的学生", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
                "确定要删除选中的 " + selectedIds.size() + " 名学生吗？\n删除后相关成绩记录也会被删除！", 
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int count = studentService.deleteBatch(selectedIds);
                JOptionPane.showMessageDialog(this, "成功删除 " + count + " 名学生", 
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void doExport() {
        if (allStudents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有数据可导出", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] headers = {"学号", "姓名", "性别", "班级", "电话", "邮箱", "状态", "入学日期"};
        List<Object[]> data = new ArrayList<>();
        
        for (Student s : allStudents) {
            data.add(new Object[]{
                s.getStudentNo(),
                s.getName(),
                s.getGender(),
                s.getClassName(),
                s.getPhone(),
                s.getEmail(),
                s.getStatus(),
                s.getEnrollmentDate() != null ? sdf.format(s.getEnrollmentDate()) : ""
            });
        }
        
        ExcelUtil.exportStudents(data, headers);
    }
    
    private void doImport() {
        List<String[]> dataList = ExcelUtil.importStudents();
        if (dataList.isEmpty()) {
            return;
        }
        
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMsg = new StringBuilder();
        
        for (int i = 0; i < dataList.size(); i++) {
            String[] row = dataList.get(i);
            try {
                if (row.length < 4) {
                    throw new IllegalArgumentException("数据列数不足");
                }
                
                Student student = new Student();
                student.setStudentNo(row[0]);
                student.setName(row[1]);
                student.setGender(row.length > 2 ? row[2] : "男");
                
                // 根据班级名称查找班级ID
                if (row.length > 3 && !row[3].isEmpty()) {
                    Clazz clazz = clazzDao.findByClassName(row[3]);
                    if (clazz != null) {
                        student.setClassId(clazz.getId());
                    }
                }
                
                if (row.length > 4) student.setPhone(row[4]);
                if (row.length > 5) student.setEmail(row[5]);
                
                studentService.addStudent(student, false);
                successCount++;
                
            } catch (Exception e) {
                failCount++;
                errorMsg.append("第").append(i + 2).append("行：").append(e.getMessage()).append("\n");
            }
        }
        
        String msg = "导入完成！\n成功：" + successCount + " 条\n失败：" + failCount + " 条";
        if (failCount > 0) {
            msg += "\n\n失败详情：\n" + errorMsg.toString();
        }
        JOptionPane.showMessageDialog(this, msg, "导入结果", JOptionPane.INFORMATION_MESSAGE);
        
        refreshData();
    }
    
    /**
     * 学生编辑对话框
     */
    private class StudentDialog extends JDialog {
        private Student student;
        private boolean confirmed = false;
        
        private JTextField studentNoField;
        private JTextField nameField;
        private JComboBox<String> genderCombo;
        private JComboBox<Clazz> classCombo;
        private JTextField phoneField;
        private JTextField emailField;
        private JTextField addressField;
        private JComboBox<String> statusCombo;
        private JTextField enrollmentDateField;
        private JCheckBox createAccountCheck;
        
        public StudentDialog(JFrame parent, String title, Student student) {
            super(parent, title, true);
            this.student = student;
            initComponents();
            initData();
            pack();
            setLocationRelativeTo(parent);
        }
        
        private void initComponents() {
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // 表单面板
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // 学号
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("学号：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            studentNoField = new JTextField(20);
            formPanel.add(studentNoField, gbc);
            
            // 姓名
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("姓名：*"), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            nameField = new JTextField(15);
            formPanel.add(nameField, gbc);
            
            row++;
            
            // 性别
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("性别："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            genderCombo = new JComboBox<>(new String[]{"男", "女"});
            formPanel.add(genderCombo, gbc);
            
            // 班级
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("班级："), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            classCombo = new JComboBox<>();
            loadDialogClassCombo();
            formPanel.add(classCombo, gbc);
            
            row++;
            
            // 电话
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("电话："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            phoneField = new JTextField(15);
            formPanel.add(phoneField, gbc);
            
            // 邮箱
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("邮箱："), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            emailField = new JTextField(20);
            formPanel.add(emailField, gbc);
            
            row++;
            
            // 状态
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("状态："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            statusCombo = new JComboBox<>(new String[]{"在读", "休学", "毕业", "退学"});
            formPanel.add(statusCombo, gbc);
            
            // 入学日期
            gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("入学日期："), gbc);
            gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            enrollmentDateField = new JTextField(15);
            enrollmentDateField.setToolTipText("格式：yyyy-MM-dd");
            formPanel.add(enrollmentDateField, gbc);
            
            row++;
            
            // 地址
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE;
            formPanel.add(new JLabel("地址："), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            addressField = new JTextField(40);
            formPanel.add(addressField, gbc);
            
            row++;
            gbc.gridwidth = 1;
            
            // 创建账户选项（仅添加时显示）
            if (student == null) {
                gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
                createAccountCheck = new JCheckBox("同时创建登录账户（默认密码：123456）", true);
                formPanel.add(createAccountCheck, gbc);
            }
            
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
        
        private void loadDialogClassCombo() {
            try {
                classCombo.removeAllItems();
                Clazz emptyClass = new Clazz();
                emptyClass.setId(null);
                emptyClass.setClassName("-- 请选择 --");
                classCombo.addItem(emptyClass);
                
                List<Clazz> classes = clazzDao.findAll();
                for (Clazz clazz : classes) {
                    classCombo.addItem(clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void initData() {
            if (student != null) {
                studentNoField.setText(student.getStudentNo());
                nameField.setText(student.getName());
                genderCombo.setSelectedItem(student.getGender());
                
                // 选择班级
                for (int i = 0; i < classCombo.getItemCount(); i++) {
                    Clazz c = classCombo.getItemAt(i);
                    if (c.getId() != null && c.getId().equals(student.getClassId())) {
                        classCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                phoneField.setText(student.getPhone());
                emailField.setText(student.getEmail());
                addressField.setText(student.getAddress());
                statusCombo.setSelectedItem(student.getStatus());
                
                if (student.getEnrollmentDate() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    enrollmentDateField.setText(sdf.format(student.getEnrollmentDate()));
                }
            } else {
                // 默认入学日期为当天
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                enrollmentDateField.setText(sdf.format(new java.util.Date()));
            }
        }
        
        private void doSave() {
            try {
                // 收集表单数据
                Student s = (student != null) ? student : new Student();
                s.setStudentNo(studentNoField.getText().trim());
                s.setName(nameField.getText().trim());
                s.setGender((String) genderCombo.getSelectedItem());
                
                Clazz selectedClass = (Clazz) classCombo.getSelectedItem();
                s.setClassId(selectedClass != null ? selectedClass.getId() : null);
                
                s.setPhone(phoneField.getText().trim());
                s.setEmail(emailField.getText().trim());
                s.setAddress(addressField.getText().trim());
                s.setStatus((String) statusCombo.getSelectedItem());
                
                // 解析日期
                String dateStr = enrollmentDateField.getText().trim();
                if (!dateStr.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    s.setEnrollmentDate(sdf.parse(dateStr));
                }
                
                // 保存
                if (student == null) {
                    boolean createAccount = createAccountCheck != null && createAccountCheck.isSelected();
                    studentService.addStudent(s, createAccount);
                    JOptionPane.showMessageDialog(this, "添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    studentService.updateStudent(s);
                    JOptionPane.showMessageDialog(this, "修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
                
                confirmed = true;
                dispose();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
}