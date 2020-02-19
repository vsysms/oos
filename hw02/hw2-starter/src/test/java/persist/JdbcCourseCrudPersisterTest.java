package persist;

import exceptions.CrudException;
import model.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JdbcCourseCrudPersisterTest {

  private Connection conn;
  private JdbcCourseCrudPersister courseCrud;

  private String getResourcesPath() {
    Path resourceDirectory = Paths.get("src", "test", "resources");
    return resourceDirectory.toFile().getAbsolutePath();
  }

  @Before
  public void setUp() throws SQLException {
    final String URI = "jdbc:sqlite:" + getResourcesPath() + "/db/Test.db";
    conn = DriverManager.getConnection(URI);

    String sql;
    Statement st = conn.createStatement();

    sql = "DROP TABLE IF EXISTS Courses;";
    st.execute(sql);

    sql = "CREATE TABLE IF NOT EXISTS Courses(id INTEGER PRIMARY KEY, name VARCHAR(30), url VARCHAR(100));";
    st.execute(sql);

    courseCrud = new JdbcCourseCrudPersister(conn);
  }

  @Test
  public void createCourseChangesId() {
    Course course = new Course("oose", "jhu-oose.com");
    assertEquals(0, course.getId());
    courseCrud.create(course);
    assertNotEquals(0, course.getId());
  }

  @Test
  public void readCourseWorks() {
    Course c1 = new Course("oose", "jhu-oose.com");
    courseCrud.create(c1);
    Course c2 = courseCrud.read(c1.getId());
    assertEquals(c1, c2);
  }

  @Test
  public void updateCourseWorks() {
    Course c1 = new Course("oose", "jhu-oose.com");
    courseCrud.create(c1);

    c1.setUrl("www.jhu-oose.com");
    courseCrud.update(c1);

    Course c2 = courseCrud.read(c1.getId());
    assertEquals(c1, c2);
  }

  @Test
  public void deleteCourseWorks() {
    Course c1 = new Course("oose", "jhu-oose.com");
    courseCrud.create(c1);

    courseCrud.delete(c1.getId());
    Course c2 = courseCrud.read(c1.getId());
    assertNull(c2);
  }

  @Test (expected = CrudException.class)
  public void addingCourseWithNullNameFails() {
    Course course = new Course(null, null);
    courseCrud.create(course);
    // Note: you are not suppose to make changes to 
    //   this test. 
  }

  @Test
  public void readCoursesWorks() {
    Course c1 = new Course("oose", "jhu-oose.com");
    Course c2 = new Course("Intro os", "jhu-os.com");
    Course c3 = new Course("Data Structures", "jhu-ds.com");

    courseCrud.create(c1);
    courseCrud.create(c2);
    courseCrud.create(c3);

    List<Course> results = courseCrud.readAll("os");
    assertTrue(results.contains(c1));
    assertTrue(results.contains(c2));
    assertFalse(results.contains(c3));
  }

  @After
  public void tearDown() throws SQLException {
    conn.close();
  }
}