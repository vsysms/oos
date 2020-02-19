package persist;

import exceptions.CrudException;
import model.Course;
import model.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcCourseCrudPersister implements CrudPersister<Course> {
  private Connection conn;

  public JdbcCourseCrudPersister(Connection conn) {
    this.conn = conn;
  }

  @Override
  public void create(Course course) throws CrudException {
    String sql = "INSERT INTO Courses(name, url) VALUES (?, ?)";
    PreparedStatement pst = null;
    try {
      if (course.getName() == null) {
        throw new SQLException();
      }
      pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pst.setString(1, course.getName());
      pst.setString(2, course.getUrl());
      pst.executeUpdate();

      ResultSet rs = pst.getGeneratedKeys();
      rs.next();
      course.setId(rs.getInt(1));

    } catch (SQLException e) {
      throw new CrudException("Unable to create the course", e);
    }
  }

  @Override
  public Course read(int courseId) throws CrudException {
    String sql = "SELECT * FROM Courses WHERE id = ?;";
    PreparedStatement pst = null;
    Course course = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setInt(1, courseId);
      ResultSet rs = pst.executeQuery();

      if (!rs.next()) return null;

      course = new Course(
          rs.getString("name"),
          rs.getString("url")
      );
      course.setId(rs.getInt("id"));
    } catch (SQLException e) {
      throw new CrudException("Unable to read the course", e);
    }

    return course;
  }

  @Override
  public void update(Course course) {
    String sql = "UPDATE Courses SET id = ?, name = ?, url = ?;";
    PreparedStatement pst = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setInt(1, course.getId());
      pst.setString(2, course.getName());
      pst.setString(3, course.getUrl());
      pst.executeUpdate();
    } catch (SQLException e) {
      throw new CrudException("Unable to update the course", e);
    }
  }

  @Override
  public void delete(int courseId) throws CrudException {
    String sql = "DELETE FROM Courses WHERE id = ?;";
    PreparedStatement pst = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setInt(1, courseId);
      pst.executeUpdate();
    } catch (SQLException e) {
      throw new CrudException("Unable to delete course", e);
    }
  }

  /**
   * Return a list of all courses where their name is or contains the given name.
   *
   * @param name must be non null.
   * @return a list of courses.
   */
  public List<Course> readAll(String name) {
    List<Course> courses = null;
    Course course = null;
    Statement st = null;
    try {
      courses = new ArrayList<Course>();
      st = conn.createStatement();
      String sql = "SELECT * FROM Courses WHERE name LIKE name;";
      ResultSet rs = st.executeQuery(sql);
      while (rs.next()) {
        course = new Course(rs.getString("name"), rs.getString("url"));
        course.setId(rs.getInt("id"));
        if(course.getName().contains(name)) {
          courses.add(course);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return courses;
  }
}
