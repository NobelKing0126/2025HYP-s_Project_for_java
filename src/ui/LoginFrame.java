package ui;

import entity.User;
import service.UserService;
import util.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton exitButton;
    
    private UserService userService = new UserService();
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color WHITE = Color.WHITE;
    
    public LoginFrame() {
        initComponents();
        initEvents();
    }
    
    private void initComponents() {
        setTitle("学生信息管理系统");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 主面板
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), PRIMARY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        
        // 标题
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("学生信息管理系统");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subLabel = new JLabel("Student Management System");
        subLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        subLabel.setForeground(new Color(255, 255, 255, 180));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // 表单卡片
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // 表单 - 使用GridLayout避免重叠
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 12));
        formPanel.setOpaque(false);
        
        Font labelFont = new Font("Dialog", Font.PLAIN, 14);
        Font fieldFont = new Font("Dialog", Font.PLAIN, 14);
        
        // 用户名
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(labelFont);
        formPanel.add(userLabel);
        
        usernameField = new JTextField(12);
        usernameField.setFont(fieldFont);
        formPanel.add(usernameField);
        
        // 密码
        JLabel pwdLabel = new JLabel("密  码:");
        pwdLabel.setFont(labelFont);
        formPanel.add(pwdLabel);
        
        passwordField = new JPasswordField(12);
        passwordField.setFont(fieldFont);
        formPanel.add(passwordField);
        
        // 角色
        JLabel roleLabel = new JLabel("角  色:");
        roleLabel.setFont(labelFont);
        formPanel.add(roleLabel);
        
        roleComboBox = new JComboBox<>(new String[]{"管理员", "教师", "学生"});
        roleComboBox.setFont(fieldFont);
        formPanel.add(roleComboBox);
        
        cardPanel.add(formPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setOpaque(false);
        
        loginButton = new JButton("登 录");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(90, 35));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        exitButton = new JButton("退 出");
        exitButton.setFont(new Font("Dialog", Font.BOLD, 14));
        exitButton.setPreferredSize(new Dimension(90, 35));
        exitButton.setBackground(new Color(149, 165, 166));
        exitButton.setForeground(WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);
        exitButton.setOpaque(true);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPanel.add(loginButton);
        btnPanel.add(exitButton);
        
        cardPanel.add(btnPanel, BorderLayout.SOUTH);
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                usernameField.requestFocus();
            }
        });
    }
    
    private void initEvents() {
        loginButton.addActionListener(e -> doLogin());
        exitButton.addActionListener(e -> System.exit(0));
        
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
    }
    
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        int roleIndex = roleComboBox.getSelectedIndex();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        
        if (!DBUtil.testConnection()) {
            JOptionPane.showMessageDialog(this, "数据库连接失败，请检查配置！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = userService.login(username, password);
            
            if (user == null) {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocus();
                return;
            }
            
            String[] roles = {"admin", "teacher", "student"};
            if (!roles[roleIndex].equals(user.getRole())) {
                JOptionPane.showMessageDialog(this, "角色选择不正确", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            dispose();
            new MainFrame(user).setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "登录失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
