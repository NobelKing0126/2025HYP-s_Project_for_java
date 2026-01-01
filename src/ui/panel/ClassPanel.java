package ui.panel;

import entity.User;
import entity.Clazz;
import dao.ClazzDao;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 班级管理面板
 */
public class ClassPanel extends JPanel {
    
    private User currentUser;
    private ClazzDao clazzDao = new ClazzDao();
    
    // 搜索组件
    private JTextField searchClassNameField;
    private JComboBox<String> searchGradeCombo;
    private JTextField searchMajorField;
    private JButton searchButton;
    private JButton resetButton;
    
    // 功能按钮
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    // 数据表格
    private JTable classTable;
    private DefaultTableModel tableModel;
    
    // 当前数据
    private List<Clazz> allClasses = new ArrayList<>();
    
    public ClassPanel(User user) {
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
        
        // 班级名称
        searchFieldsPanel.add(new JLabel("班级名称："));
        searchClassNameField = new JTextField(12);
        searchFieldsPanel.add(searchClassNameField);
        
        // 年级
        searchFieldsPanel.add(new JLabel("年级："));
        searchGradeCombo = new JComboBox<>();
        searchGradeCombo.setPreferredSize(new Dimension(100, 25));
        loadGradeCombo();
        searchFieldsPanel.add(searchGradeCombo);
        
        // 专业
        searchFieldsPanel.add(new JLabel("专业："));
        searchMajorField = new JTextField(12);
        searchFieldsPanel.add(searchMajorField);
        
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
        String[] columns = {"ID", "班级名称", "年级", "专业", "院系", "学生人数"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        classTable = new JTable(tableModel);
        classTable.setRowHeight(25);
        classTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        classTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 设置列宽
        TableColumnModel columnModel = classTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(120);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(150);
        columnModel.getColumn(5).setPreferredWidth(80);
        
        // 隐藏ID列
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setPreferredWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(classTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        addButton = new JButton("添加班级");
        addButton.setBackground(new Color(92, 184, 92));
        panel.add(addButton);
        
        editButton = new JButton("编辑班级");
        editButton.setBackground(new Color(91, 192, 222));
        panel.add(editButton);
        
        deleteButton = new JButton("删除班级");
        deleteButton.setBackground(new Color(217, 83, 79));
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadGradeCombo() {
        try {
            searchGradeCombo.removeAllItems();
            searchGradeCombo.addItem("全部");
            
            List<String> grades = clazzDao.findAllGrades();
            for (String grade : grades) {
                searchGradeCombo.addItem(grade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateButtonVisibility() {
        // 只有管理员可以增删改
        boolean isAdmin = currentUser.isAdmin();
        addButton.setVisible(isAdmin);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);
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
        
        // 双击编辑
        classTable.addMouseListener(new MouseAdapter() {
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
            allClasses = clazzDao.findAll();
            updateTable();
            loadGradeCombo();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void doSearch() {
        try {
            String className = searchClassNameField.getText().trim();
            String grade = (String) searchGradeCombo.getSelectedItem();
            String major = searchMajorField.getText().trim();
            
            // 先获取所有班级，然后在内存中过滤
            List<Clazz> allList = clazzDao.findAll();
            allClasses = new ArrayList<>();
            
            for (Clazz c : allList) {
                boolean match = true;
                
                if (!className.isEmpty() && !c.getClassName().contains(className)) {
                    match = false;
                }
                if (!"全部".equals(grade) && !grade.equals(c.getGrade())) {
                    match = false;
                }
                if (!major.isEmpty() && (c.getMajor() == null || !c.getMajor().contains(major))) {
                    match = false;
                }
                
                if (match) {
                    allClasses.add(c);
                }
            }
            
            updateTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "搜索失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void doReset() {
        searchClassNameField.setText("");
        searchGradeCombo.setSelectedIndex(0);
        searchMajorField.setText("");
        refreshData();
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Clazz c : allClasses) {
            Object[] row = {
                c.getId(),
                c.getClassName(),
                c.getGrade(),
                c.getMajor(),
                c.getDepartment(),
                c.getStudentCount() != null ? c.getStudentCount() : 0
            };
            tableModel.addRow(row);
        }
    }
    
    private void showAddDialog() {
        ClassDialog dialog = new ClassDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "添加班级", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的班级", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer classId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Clazz clazz = clazzDao.findById(classId);
            if (clazz == null) {
                JOptionPane.showMessageDialog(this, "班级不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ClassDialog dialog = new ClassDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                "编辑班级", clazz);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                refreshData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "获取班级信息失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDelete() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的班级", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer classId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String className = (String) tableModel.getValueAt(selectedRow, 1);
        Integer studentCount = (Integer) tableModel.getValueAt(selectedRow, 5);
        
        if (studentCount != null && studentCount > 0) {
            JOptionPane.showMessageDialog(this, 
                    "班级 " + className + " 下还有 " + studentCount + " 名学生，不能删除！\n" +
                    "请先将学生转移到其他班级或删除学生。", 
                    "无法删除", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
                "确定要删除班级 " + className + " 吗？", 
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                clazzDao.delete(classId);
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage(), 
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 班级编辑对话框
     */
    private class ClassDialog extends JDialog {
        private Clazz clazz;
        private boolean confirmed = false;
        
        private JTextField classNameField;
        private JTextField gradeField;
        private JTextField majorField;
        private JTextField departmentField;
        
        public ClassDialog(JFrame parent, String title, Clazz clazz) {
            super(parent, title, true);
            this.clazz = clazz;
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
            
            // 班级名称
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("班级名称：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            classNameField = new JTextField(20);
            formPanel.add(classNameField, gbc);
            
            row++;
            
            // 年级
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("年级：*"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            gradeField = new JTextField(20);
            gradeField.setToolTipText("例如：2021、2022");
            formPanel.add(gradeField, gbc);
            
            row++;
            
            // 专业
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("专业："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            majorField = new JTextField(20);
            formPanel.add(majorField, gbc);
            
            row++;
            
            // 院系
            gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            formPanel.add(new JLabel("院系："), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
            departmentField = new JTextField(20);
            formPanel.add(departmentField, gbc);
            
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
        
        private void initData() {
            if (clazz != null) {
                classNameField.setText(clazz.getClassName());
                gradeField.setText(clazz.getGrade());
                majorField.setText(clazz.getMajor());
                departmentField.setText(clazz.getDepartment());
            }
        }
        
        private void doSave() {
            try {
                String className = classNameField.getText().trim();
                String grade = gradeField.getText().trim();
                
                if (className.isEmpty()) {
                    throw new IllegalArgumentException("班级名称不能为空");
                }
                if (grade.isEmpty()) {
                    throw new IllegalArgumentException("年级不能为空");
                }
                
                // 检查班级名称是否重复
                if (clazz == null) {
                    if (clazzDao.existsByClassName(className)) {
                        throw new IllegalArgumentException("班级名称已存在");
                    }
                } else {
                    if (clazzDao.existsByClassName(className, clazz.getId())) {
                        throw new IllegalArgumentException("班级名称已存在");
                    }
                }
                
                Clazz c = (clazz != null) ? clazz : new Clazz();
                c.setClassName(className);
                c.setGrade(grade);
                c.setMajor(majorField.getText().trim());
                c.setDepartment(departmentField.getText().trim());
                
                if (clazz == null) {
                    clazzDao.insert(c);
                    JOptionPane.showMessageDialog(this, "添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    clazzDao.update(c);
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