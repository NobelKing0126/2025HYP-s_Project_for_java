package ui.panel;

import entity.User;
import entity.Course;
import entity.Clazz;
import entity.Score;
import service.ScoreService;
import dao.CourseDao;
import dao.ClazzDao;
import dao.ScoreDao;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 统计分析面板
 */
public class StatisticsPanel extends JPanel {
    
    private User currentUser;
    private ScoreService scoreService = new ScoreService();
    private CourseDao courseDao = new CourseDao();
    private ClazzDao clazzDao = new ClazzDao();
    private ScoreDao scoreDao = new ScoreDao();
    
    // 选择组件
    private JComboBox<String> statsTypeCombo;
    private JComboBox<Course> courseCombo;
    private JComboBox<Clazz> classCombo;
    private JButton analyzeButton;
    
    // 统计结果区域
    private JPanel resultPanel;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    
    // 统计信息标签
    private JLabel countLabel;
    private JLabel avgLabel;
    private JLabel maxLabel;
    private JLabel minLabel;
    private JLabel passRateLabel;
    
    public StatisticsPanel(User user) {
        this.currentUser = user;
        initComponents();
        initEvents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 顶部选择区域
        add(createTopPanel(), BorderLayout.NORTH);
        
        // 中间结果区域
        add(createResultPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("统计设置"));
        
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        // 统计类型
        selectPanel.add(new JLabel("统计类型："));
        statsTypeCombo = new JComboBox<>(new String[]{
            "课程成绩分析", "班级成绩对比", "成绩分布统计", "学生成绩排名"
        });
        statsTypeCombo.setPreferredSize(new Dimension(150, 28));
        selectPanel.add(statsTypeCombo);
        
        // 课程选择
        selectPanel.add(new JLabel("选择课程："));
        courseCombo = new JComboBox<>();
        courseCombo.setPreferredSize(new Dimension(180, 28));
        loadCourseCombo();
        selectPanel.add(courseCombo);
        
        // 班级选择
        selectPanel.add(new JLabel("选择班级："));
        classCombo = new JComboBox<>();
        classCombo.setPreferredSize(new Dimension(130, 28));
        loadClassCombo();
        selectPanel.add(classCombo);
        
        // 分析按钮
        analyzeButton = new JButton("开始分析");
        analyzeButton.setFont(new Font("Dialog", Font.BOLD, 14));
        analyzeButton.setPreferredSize(new Dimension(110, 32));
        analyzeButton.setBackground(new Color(51, 122, 183));
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setFocusPainted(false);
        analyzeButton.setOpaque(true);
        analyzeButton.setBorderPainted(false);
        
        panel.add(selectPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        resultPanel = new JPanel(new BorderLayout(10, 10));
        
        // 左侧统计信息
        JPanel statsInfoPanel = new JPanel();
        statsInfoPanel.setLayout(new BoxLayout(statsInfoPanel, BoxLayout.Y_AXIS));
        statsInfoPanel.setBorder(BorderFactory.createTitledBorder("统计概要"));
        statsInfoPanel.setPreferredSize(new Dimension(200, 0));
        
        countLabel = new JLabel("总人数：--");
        avgLabel = new JLabel("平均分：--");
        maxLabel = new JLabel("最高分：--");
        minLabel = new JLabel("最低分：--");
        passRateLabel = new JLabel("及格率：--");
        
        Font labelFont = new Font("Dialog", Font.PLAIN, 14);
        countLabel.setFont(labelFont);
        avgLabel.setFont(labelFont);
        maxLabel.setFont(labelFont);
        minLabel.setFont(labelFont);
        passRateLabel.setFont(labelFont);
        
        statsInfoPanel.add(Box.createVerticalStrut(20));
        statsInfoPanel.add(countLabel);
        statsInfoPanel.add(Box.createVerticalStrut(15));
        statsInfoPanel.add(avgLabel);
        statsInfoPanel.add(Box.createVerticalStrut(15));
        statsInfoPanel.add(maxLabel);
        statsInfoPanel.add(Box.createVerticalStrut(15));
        statsInfoPanel.add(minLabel);
        statsInfoPanel.add(Box.createVerticalStrut(15));
        statsInfoPanel.add(passRateLabel);
        statsInfoPanel.add(Box.createVerticalGlue());
        
        resultPanel.add(statsInfoPanel, BorderLayout.WEST);
        
        // 中间分为上下两部分：表格和图表
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // 上半部分：数据表格
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("详细数据"));
        
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(25);
        resultTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        resultTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        centerPanel.add(tablePanel);
        
        // 下半部分：图表区域
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("成绩分布图"));
        chartPanel.setBackground(Color.WHITE);
        
        // 初始提示
        JLabel tipLabel = new JLabel("请选择统计条件后点击\"开始分析\"", JLabel.CENTER);
        tipLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        tipLabel.setForeground(Color.GRAY);
        chartPanel.add(tipLabel, BorderLayout.CENTER);
        
        centerPanel.add(chartPanel);
        
        resultPanel.add(centerPanel, BorderLayout.CENTER);
        
        return resultPanel;
    }
    
    private void loadCourseCombo() {
        try {
            courseCombo.removeAllItems();
            
            Course allCourse = new Course();
            allCourse.setId(null);
            allCourse.setCourseName("-- 请选择课程 --");
            courseCombo.addItem(allCourse);
            
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
    
    private void loadClassCombo() {
        try {
            classCombo.removeAllItems();
            
            Clazz allClass = new Clazz();
            allClass.setId(null);
            allClass.setClassName("-- 全部班级 --");
            classCombo.addItem(allClass);
            
            List<Clazz> classes = clazzDao.findAll();
            for (Clazz c : classes) {
                classCombo.addItem(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initEvents() {
        analyzeButton.addActionListener(e -> doAnalyze());
        
        // 统计类型变更时更新UI
        statsTypeCombo.addActionListener(e -> {
            int index = statsTypeCombo.getSelectedIndex();
            courseCombo.setEnabled(index == 0 || index == 2 || index == 3);
            classCombo.setEnabled(index == 1 || index == 3);
        });
    }
    
    public void refreshData() {
        loadCourseCombo();
        loadClassCombo();
    }
    
    private void doAnalyze() {
        int statsType = statsTypeCombo.getSelectedIndex();
        
        try {
            switch (statsType) {
                case 0:
                    analyzeCourseScore();
                    break;
                case 1:
                    analyzeClassComparison();
                    break;
                case 2:
                    analyzeScoreDistribution();
                    break;
                case 3:
                    analyzeStudentRanking();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "分析失败：" + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * 课程成绩分析
     */
    private void analyzeCourseScore() throws Exception {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        if (selectedCourse == null || selectedCourse.getId() == null) {
            JOptionPane.showMessageDialog(this, "请选择课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer courseId = selectedCourse.getId();
        
        // 获取统计数据
        List<Score> scores = scoreService.findByCourseId(courseId);
        Double avg = scoreService.getCourseAverageScore(courseId);
        Double max = scoreService.getCourseMaxScore(courseId);
        Double min = scoreService.getCourseMinScore(courseId);
        Map<String, Integer> distribution = scoreService.getCourseScoreDistribution(courseId);
        
        // 计算及格率
        int passCount = 0;
        for (Score s : scores) {
            if (s.getScore() != null && s.getScore() >= 60) {
                passCount++;
            }
        }
        double passRate = scores.isEmpty() ? 0 : (passCount * 100.0 / scores.size());
        
        // 更新统计信息
        countLabel.setText("总人数：" + scores.size());
        avgLabel.setText("平均分：" + (avg != null ? String.format("%.2f", avg) : "--"));
        maxLabel.setText("最高分：" + (max != null ? String.format("%.1f", max) : "--"));
        minLabel.setText("最低分：" + (min != null ? String.format("%.1f", min) : "--"));
        passRateLabel.setText("及格率：" + String.format("%.1f%%", passRate));
        
        // 更新表格
        String[] columns = {"学号", "姓名", "班级", "成绩", "等级"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        for (Score s : scores) {
            tableModel.addRow(new Object[]{
                s.getStudentNo(),
                s.getStudentName(),
                s.getClassName(),
                s.getScore() != null ? String.format("%.1f", s.getScore()) : "",
                s.getGrade()
            });
        }
        
        // 绘制分布图
        drawDistributionChart(distribution, selectedCourse.getCourseName());
    }
    
    /**
     * 班级成绩对比
     */
    private void analyzeClassComparison() throws Exception {
        Clazz selectedClass = (Clazz) classCombo.getSelectedItem();
        if (selectedClass == null || selectedClass.getId() == null) {
            JOptionPane.showMessageDialog(this, "请选择班级", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer classId = selectedClass.getId();
        
        // 获取班级各科平均分
        List<Object[]> courseAverages = scoreService.getClassCourseAverages(classId);
        
        // 更新统计信息
        double totalAvg = 0;
        int count = 0;
        for (Object[] row : courseAverages) {
            totalAvg += (Double) row[1];
            count++;
        }
        
        countLabel.setText("课程数：" + count);
        avgLabel.setText("总平均：" + (count > 0 ? String.format("%.2f", totalAvg / count) : "--"));
        maxLabel.setText("最高科：--");
        minLabel.setText("最低科：--");
        passRateLabel.setText("班级：" + selectedClass.getClassName());
        
        // 更新表格
        String[] columns = {"课程名称", "平均分"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        double maxAvg = 0, minAvg = 100;
        String maxCourse = "", minCourse = "";
        
        for (Object[] row : courseAverages) {
            String courseName = (String) row[0];
            Double avgScore = (Double) row[1];
            
            tableModel.addRow(new Object[]{courseName, String.format("%.2f", avgScore)});
            
            if (avgScore > maxAvg) {
                maxAvg = avgScore;
                maxCourse = courseName;
            }
            if (avgScore < minAvg) {
                minAvg = avgScore;
                minCourse = courseName;
            }
        }
        
        maxLabel.setText("最高科：" + maxCourse + " (" + String.format("%.1f", maxAvg) + ")");
        minLabel.setText("最低科：" + minCourse + " (" + String.format("%.1f", minAvg) + ")");
        
        // 绘制柱状图
        drawBarChart(courseAverages, selectedClass.getClassName() + " 各科成绩");
    }
    
    /**
     * 成绩分布统计
     */
    private void analyzeScoreDistribution() throws Exception {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        if (selectedCourse == null || selectedCourse.getId() == null) {
            JOptionPane.showMessageDialog(this, "请选择课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer courseId = selectedCourse.getId();
        Map<String, Integer> distribution = scoreService.getCourseScoreDistribution(courseId);
        
        // 计算总数
        int total = 0;
        for (Integer count : distribution.values()) {
            total += count;
        }
        
        // 更新统计信息
        countLabel.setText("总人数：" + total);
        avgLabel.setText("优秀率：" + (total > 0 ? 
                String.format("%.1f%%", distribution.getOrDefault("优秀(90-100)", 0) * 100.0 / total) : "--"));
        maxLabel.setText("良好率：" + (total > 0 ? 
                String.format("%.1f%%", distribution.getOrDefault("良好(80-89)", 0) * 100.0 / total) : "--"));
        minLabel.setText("及格率：" + (total > 0 ? 
                String.format("%.1f%%", (total - distribution.getOrDefault("不及格(<60)", 0)) * 100.0 / total) : "--"));
        passRateLabel.setText("不及格：" + distribution.getOrDefault("不及格(<60)", 0) + " 人");
        
        // 更新表格
        String[] columns = {"成绩等级", "人数", "占比"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        String[] levels = {"优秀(90-100)", "良好(80-89)", "中等(70-79)", "及格(60-69)", "不及格(<60)"};
        for (String level : levels) {
            int count = distribution.getOrDefault(level, 0);
            double percent = total > 0 ? count * 100.0 / total : 0;
            tableModel.addRow(new Object[]{level, count, String.format("%.1f%%", percent)});
        }
        
        // 绘制分布图
        drawDistributionChart(distribution, selectedCourse.getCourseName());
    }
    
    /**
     * 学生成绩排名
     */
    private void analyzeStudentRanking() throws Exception {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        Clazz selectedClass = (Clazz) classCombo.getSelectedItem();
        
        if (selectedCourse == null || selectedCourse.getId() == null) {
            JOptionPane.showMessageDialog(this, "请选择课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer courseId = selectedCourse.getId();
        Integer classId = (selectedClass != null && selectedClass.getId() != null) ? 
                          selectedClass.getId() : null;
        
        // 获取成绩并按分数排序
        List<Score> scores = scoreService.search(null, null, classId, courseId, null, null, null);
        scores.sort((a, b) -> {
            if (a.getScore() == null) return 1;
            if (b.getScore() == null) return -1;
            return Double.compare(b.getScore(), a.getScore());
        });
        
        // 更新统计信息
        countLabel.setText("总人数：" + scores.size());
        avgLabel.setText("课程：" + selectedCourse.getCourseName());
        if (!scores.isEmpty() && scores.get(0).getScore() != null) {
            maxLabel.setText("第一名：" + scores.get(0).getStudentName() + 
                    " (" + String.format("%.1f", scores.get(0).getScore()) + ")");
        }
        if (scores.size() > 1 && scores.get(1).getScore() != null) {
            minLabel.setText("第二名：" + scores.get(1).getStudentName() + 
                    " (" + String.format("%.1f", scores.get(1).getScore()) + ")");
        }
        if (scores.size() > 2 && scores.get(2).getScore() != null) {
            passRateLabel.setText("第三名：" + scores.get(2).getStudentName() + 
                    " (" + String.format("%.1f", scores.get(2).getScore()) + ")");
        }
        
        // 更新表格
        String[] columns = {"排名", "学号", "姓名", "班级", "成绩"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);
        
        int rank = 1;
        for (Score s : scores) {
            tableModel.addRow(new Object[]{
                rank++,
                s.getStudentNo(),
                s.getStudentName(),
                s.getClassName(),
                s.getScore() != null ? String.format("%.1f", s.getScore()) : ""
            });
        }
        
        // 清空图表区域
        chartPanel.removeAll();
        JLabel tipLabel = new JLabel("排名数据请查看上方表格", JLabel.CENTER);
        tipLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        tipLabel.setForeground(Color.GRAY);
        chartPanel.add(tipLabel, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    /**
     * 绘制成绩分布图（简易柱状图）
     */
    private void drawDistributionChart(Map<String, Integer> distribution, String title) {
        chartPanel.removeAll();
        
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int margin = 60;
                int barWidth = 60;
                int gap = 30;
                
                // 绘制标题
                g2d.setFont(new Font("Dialog", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title + " - 成绩分布");
                g2d.drawString(title + " - 成绩分布", (width - titleWidth) / 2, 30);
                
                // 找出最大值
                int maxCount = 1;
                for (Integer count : distribution.values()) {
                    if (count > maxCount) maxCount = count;
                }
                
                // 颜色数组
                Color[] colors = {
                    new Color(92, 184, 92),   // 优秀 - 绿色
                    new Color(91, 192, 222),  // 良好 - 蓝色
                    new Color(240, 173, 78),  // 中等 - 橙色
                    new Color(153, 153, 153), // 及格 - 灰色
                    new Color(217, 83, 79)    // 不及格 - 红色
                };
                
                String[] levels = {"优秀(90-100)", "良好(80-89)", "中等(70-79)", "及格(60-69)", "不及格(<60)"};
                
                int startX = (width - (barWidth * 5 + gap * 4)) / 2;
                int chartHeight = height - margin * 2 - 50;
                
                for (int i = 0; i < levels.length; i++) {
                    int count = distribution.getOrDefault(levels[i], 0);
                    int barHeight = (int) ((double) count / maxCount * chartHeight);
                    
                    int x = startX + i * (barWidth + gap);
                    int y = height - margin - barHeight;
                    
                    // 绘制柱子
                    g2d.setColor(colors[i]);
                    g2d.fillRect(x, y, barWidth, barHeight);
                    
                    // 绘制边框
                    g2d.setColor(colors[i].darker());
                    g2d.drawRect(x, y, barWidth, barHeight);
                    
                    // 绘制数量
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Dialog", Font.BOLD, 14));
                    String countStr = String.valueOf(count);
                    fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(countStr);
                    g2d.drawString(countStr, x + (barWidth - textWidth) / 2, y - 5);
                    
                    // 绘制标签
                    g2d.setFont(new Font("Dialog", Font.PLAIN, 11));
                    String shortLabel = levels[i].split("\\(")[0];
                    fm = g2d.getFontMetrics();
                    textWidth = fm.stringWidth(shortLabel);
                    g2d.drawString(shortLabel, x + (barWidth - textWidth) / 2, height - margin + 20);
                }
            }
        };
        
        chart.setBackground(Color.WHITE);
        chartPanel.add(chart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    /**
     * 绘制柱状图（班级各科成绩）
     */
    private void drawBarChart(List<Object[]> data, String title) {
        chartPanel.removeAll();
        
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int margin = 60;
                
                // 绘制标题
                g2d.setFont(new Font("Dialog", Font.BOLD, 16));
                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                int titleWidth = fm.stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 30);
                
                if (data.isEmpty()) {
                    g2d.setFont(new Font("Dialog", Font.PLAIN, 14));
                    g2d.setColor(Color.GRAY);
                    g2d.drawString("暂无数据", width / 2 - 30, height / 2);
                    return;
                }
                
                int barWidth = Math.min(50, (width - margin * 2) / data.size() - 10);
                int gap = 10;
                int chartHeight = height - margin * 2 - 50;
                
                int startX = (width - (barWidth * data.size() + gap * (data.size() - 1))) / 2;
                
                // 获取颜色
                Color[] colors = {
                    new Color(54, 162, 235),
                    new Color(255, 99, 132),
                    new Color(255, 206, 86),
                    new Color(75, 192, 192),
                    new Color(153, 102, 255),
                    new Color(255, 159, 64)
                };
                
                for (int i = 0; i < data.size(); i++) {
                    String courseName = (String) data.get(i)[0];
                    Double avgScore = (Double) data.get(i)[1];
                    
                    int barHeight = (int) (avgScore / 100 * chartHeight);
                    int x = startX + i * (barWidth + gap);
                    int y = height - margin - barHeight;
                    
                    // 绘制柱子
                    g2d.setColor(colors[i % colors.length]);
                    g2d.fillRect(x, y, barWidth, barHeight);
                    
                    // 绘制边框
                    g2d.setColor(colors[i % colors.length].darker());
                    g2d.drawRect(x, y, barWidth, barHeight);
                    
                    // 绘制分数
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Dialog", Font.BOLD, 12));
                    String scoreStr = String.format("%.1f", avgScore);
                    fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(scoreStr);
                    g2d.drawString(scoreStr, x + (barWidth - textWidth) / 2, y - 5);
                    
                    // 绘制课程名（截取前4个字符）
                    g2d.setFont(new Font("Dialog", Font.PLAIN, 10));
                    String shortName = courseName.length() > 4 ? 
                            courseName.substring(0, 4) + ".." : courseName;
                    fm = g2d.getFontMetrics();
                    textWidth = fm.stringWidth(shortName);
                    g2d.drawString(shortName, x + (barWidth - textWidth) / 2, height - margin + 15);
                }
            }
        };
        
        chart.setBackground(Color.WHITE);
        chartPanel.add(chart, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}