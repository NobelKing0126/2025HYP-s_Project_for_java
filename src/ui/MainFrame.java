package ui;

import entity.User;
import service.UserService;
import ui.panel.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 主界面框架
 */
public class MainFrame extends JFrame {
    
    private User currentUser;
    private UserService userService = new UserService();
    
    private JTree navTree;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    
    // 各功能面板
    private StudentPanel studentPanel;
    private ClassPanel classPanel;
    private CoursePanel coursePanel;
    private ScorePanel scorePanel;
    private StatisticsPanel statisticsPanel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
        initMenuBar();
        initEvents();
    }
    
    private void initComponents() {
        setTitle("学生信息管理系统 - " + currentUser.getRoleName() + "：" + 
                (currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getUsername()));
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 主布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 左侧导航树
        JPanel navPanel = createNavPanel();
        mainPanel.add(navPanel, BorderLayout.WEST);
        
        // 右侧内容区域
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        // 初始化各功能面板
        initPanels();
        
        // 欢迎面板
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, "welcome");
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // 底部状态栏
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // 显示欢迎页
        cardLayout.show(contentPanel, "welcome");
    }
    
    private JPanel createNavPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        
        // 创建导航树
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("功能菜单");
        
        // 根据角色添加不同的菜单项
        if (currentUser.isAdmin()) {
            // 管理员可以看到所有菜单
            DefaultMutableTreeNode studentNode = new DefaultMutableTreeNode("学生管理");
            DefaultMutableTreeNode classNode = new DefaultMutableTreeNode("班级管理");
            DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode("课程管理");
            DefaultMutableTreeNode scoreNode = new DefaultMutableTreeNode("成绩管理");
            DefaultMutableTreeNode statsNode = new DefaultMutableTreeNode("统计分析");
            
            root.add(studentNode);
            root.add(classNode);
            root.add(courseNode);
            root.add(scoreNode);
            root.add(statsNode);
            
        } else if (currentUser.isTeacher()) {
            // 教师只能看到成绩管理和统计
            DefaultMutableTreeNode studentNode = new DefaultMutableTreeNode("学生查询");
            DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode("我的课程");
            DefaultMutableTreeNode scoreNode = new DefaultMutableTreeNode("成绩录入");
            DefaultMutableTreeNode statsNode = new DefaultMutableTreeNode("成绩统计");
            
            root.add(studentNode);
            root.add(courseNode);
            root.add(scoreNode);
            root.add(statsNode);
            
        } else if (currentUser.isStudent()) {
            // 学生只能查看自己的信息
            DefaultMutableTreeNode infoNode = new DefaultMutableTreeNode("个人信息");
            DefaultMutableTreeNode scoreNode = new DefaultMutableTreeNode("我的成绩");
            
            root.add(infoNode);
            root.add(scoreNode);
        }
        
        navTree = new JTree(root);
        navTree.setRootVisible(false);
        navTree.setShowsRootHandles(true);
        navTree.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        // 展开所有节点
        for (int i = 0; i < navTree.getRowCount(); i++) {
            navTree.expandRow(i);
        }
        
        JScrollPane scrollPane = new JScrollPane(navTree);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void initPanels() {
        studentPanel = new StudentPanel(currentUser);
        classPanel = new ClassPanel(currentUser);
        coursePanel = new CoursePanel(currentUser);
        scorePanel = new ScorePanel(currentUser);
        statisticsPanel = new StatisticsPanel(currentUser);
        
        contentPanel.add(studentPanel, "student");
        contentPanel.add(classPanel, "class");
        contentPanel.add(coursePanel, "course");
        contentPanel.add(scorePanel, "score");
        contentPanel.add(statisticsPanel, "statistics");
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // 欢迎标题
        JLabel titleLabel = new JLabel("欢迎使用学生信息管理系统");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 102, 153));
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        // 用户信息
        String userInfo = String.format("当前用户：%s（%s）", 
                currentUser.getRealName() != null ? currentUser.getRealName() : currentUser.getUsername(),
                currentUser.getRoleName());
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        gbc.gridy = 1;
        panel.add(userLabel, gbc);
        
        // 提示信息
        JLabel tipLabel = new JLabel("请从左侧菜单选择功能");
        tipLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        tipLabel.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panel.add(tipLabel, gbc);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        panel.add(statusLabel, BorderLayout.WEST);
        
        JLabel timeLabel = new JLabel();
        timeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        panel.add(timeLabel, BorderLayout.EAST);
        
        // 更新时间
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        timer.start();
        
        return panel;
    }
    
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 系统菜单
        JMenu systemMenu = new JMenu("系统");
        
        JMenuItem changePwdItem = new JMenuItem("修改密码");
        changePwdItem.addActionListener(e -> showChangePasswordDialog());
        systemMenu.add(changePwdItem);
        
        systemMenu.addSeparator();
        
        JMenuItem logoutItem = new JMenuItem("注销");
        logoutItem.addActionListener(e -> logout());
        systemMenu.add(logoutItem);
        
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        systemMenu.add(exitItem);
        
        menuBar.add(systemMenu);
        
        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void initEvents() {
        // 导航树选择事件
        navTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) navTree.getLastSelectedPathComponent();
            if (node == null || node.getChildCount() > 0) return;
            
            String nodeName = node.toString();
            switchPanel(nodeName);
        });
    }
    
    private void switchPanel(String nodeName) {
        switch (nodeName) {
            case "学生管理":
            case "学生查询":
            case "个人信息":
                studentPanel.refreshData();
                cardLayout.show(contentPanel, "student");
                statusLabel.setText("学生管理");
                break;
            case "班级管理":
                classPanel.refreshData();
                cardLayout.show(contentPanel, "class");
                statusLabel.setText("班级管理");
                break;
            case "课程管理":
            case "我的课程":
                coursePanel.refreshData();
                cardLayout.show(contentPanel, "course");
                statusLabel.setText("课程管理");
                break;
            case "成绩管理":
            case "成绩录入":
            case "我的成绩":
                scorePanel.refreshData();
                cardLayout.show(contentPanel, "score");
                statusLabel.setText("成绩管理");
                break;
            case "统计分析":
            case "成绩统计":
                statisticsPanel.refreshData();
                cardLayout.show(contentPanel, "statistics");
                statusLabel.setText("统计分析");
                break;
            default:
                cardLayout.show(contentPanel, "welcome");
        }
    }
    
    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JPasswordField oldPwdField = new JPasswordField();
        JPasswordField newPwdField = new JPasswordField();
        JPasswordField confirmPwdField = new JPasswordField();
        
        panel.add(new JLabel("原密码："));
        panel.add(oldPwdField);
        panel.add(new JLabel("新密码："));
        panel.add(newPwdField);
        panel.add(new JLabel("确认密码："));
        panel.add(confirmPwdField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "修改密码", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String oldPwd = new String(oldPwdField.getPassword());
            String newPwd = new String(newPwdField.getPassword());
            String confirmPwd = new String(confirmPwdField.getPassword());
            
            if (!newPwd.equals(confirmPwd)) {
                JOptionPane.showMessageDialog(this, "两次输入的新密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                userService.changePassword(currentUser.getId(), oldPwd, newPwd);
                JOptionPane.showMessageDialog(this, "密码修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, "确定要注销吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void showAboutDialog() {
        String message = "学生信息管理系统 v1.0\n\n" +
                        "基于Java Swing + MySQL开发\n" +
                        "支持管理员、教师、学生三种角色\n\n" +
                        "© 2024 All Rights Reserved";
        JOptionPane.showMessageDialog(this, message, "关于", JOptionPane.INFORMATION_MESSAGE);
    }
}
