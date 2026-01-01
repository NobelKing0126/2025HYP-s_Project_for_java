package util;

import java.util.regex.Pattern;

/**
 * 数据验证工具类
 */
public class ValidationUtil {
    
    // 学号格式：10位数字
    private static final Pattern STUDENT_NO_PATTERN = Pattern.compile("^\\d{10}$");
    
    // 工号格式：T+3位数字
    private static final Pattern TEACHER_NO_PATTERN = Pattern.compile("^T\\d{3}$");
    
    // 手机号格式
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 邮箱格式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.\\w+$");
    
    /**
     * 验证学号格式
     */
    public static boolean isValidStudentNo(String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return false;
        }
        return STUDENT_NO_PATTERN.matcher(studentNo.trim()).matches();
    }
    
    /**
     * 验证工号格式
     */
    public static boolean isValidTeacherNo(String teacherNo) {
        if (teacherNo == null || teacherNo.trim().isEmpty()) {
            return false;
        }
        return TEACHER_NO_PATTERN.matcher(teacherNo.trim()).matches();
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // 允许为空
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // 允许为空
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * 验证成绩范围（0-100）
     */
    public static boolean isValidScore(Double score) {
        if (score == null) {
            return true; // 允许为空
        }
        return score >= 0 && score <= 100;
    }
    
    /**
     * 验证成绩字符串并转换
     */
    public static Double parseScore(String scoreStr) throws IllegalArgumentException {
        if (scoreStr == null || scoreStr.trim().isEmpty()) {
            return null;
        }
        try {
            double score = Double.parseDouble(scoreStr.trim());
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("成绩必须在0-100之间");
            }
            return score;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("成绩格式不正确");
        }
    }
    
    /**
     * 验证字符串非空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * 验证学分范围（0-10）
     */
    public static boolean isValidCredit(Double credit) {
        if (credit == null) {
            return true;
        }
        return credit >= 0 && credit <= 10;
    }
    
    /**
     * 获取验证错误信息
     */
    public static String getValidationError(String field, String value) {
        switch (field) {
            case "studentNo":
                return "学号格式不正确，应为10位数字";
            case "teacherNo":
                return "工号格式不正确，应为T+3位数字";
            case "phone":
                return "手机号格式不正确";
            case "email":
                return "邮箱格式不正确";
            case "score":
                return "成绩必须在0-100之间";
            default:
                return field + "格式不正确";
        }
    }
}