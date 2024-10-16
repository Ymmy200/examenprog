import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;


public interface GradeDAO {
    List<Grade> getGradesByCourseId(int courseId);
}


class GradeDAOImpl implements GradeDAO {

    private Connection connection;

    public GradeDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Grade> getGradesByCourseId(int courseId) {
        List<Grade> grades = new ArrayList<>();
        String query = "SELECT g.id AS grade_id, e.student_id, gt.name AS grade_type, g.grade, gt.weight " +
                       "FROM grades g " +
                       "JOIN enrollment e ON g.enrollment_id = e.id " + 
                       "JOIN grade_type gt ON g.grade_type_id = gt.id " +
                       "WHERE e.course_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Grade grade = new Grade(
                    rs.getInt("grade_id"),
                    rs.getInt("student_id"),
                    rs.getString("grade_type"),
                    rs.getBigDecimal("grade"),
                    rs.getBigDecimal("weight")
                );
                grades.add(grade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }
}

class Grade {
    private int id;
    private int studentId;
    private String gradeType;
    private BigDecimal grade;
    private BigDecimal weight;

    public Grade(int id, int studentId, String gradeType, BigDecimal grade, BigDecimal weight) {
        this.id = id;
        this.studentId = studentId;
        this.gradeType = gradeType;
        this.grade = grade;
        this.weight = weight;
    }

   
    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getGradeType() {
        return gradeType;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}


public class GradeController {

    private GradeDAO gradeDAO;

    public GradeController(GradeDAO gradeDAO) {
        this.gradeDAO = gradeDAO;
    }

    public void printGradesByCourse(int courseId) {
        List<Grade> grades = gradeDAO.getGradesByCourseId(courseId);
        for (Grade grade : grades) {
            System.out.println("Student ID: " + grade.getStudentId() +
                               ", Grade Type: " + grade.getGradeType() +
                               ", Grade: " + grade.getGrade() +
                               ", Weight: " + grade.getWeight());
        }
    }

    public static void main(String[] args) {
        
        String jdbcUrl = "jdbc:mysql://localhost:3306/programacion";
        String user = "prueba";
        String password = "jimmya?";
        
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password)) {
            GradeDAO gradeDAO = new GradeDAOImpl(connection);
            GradeController controller = new GradeController(gradeDAO);
            controller.printGradesByCourse(123);  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
