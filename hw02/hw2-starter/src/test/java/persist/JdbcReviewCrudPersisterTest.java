package persist;

import exceptions.CrudException;
import model.Course;
import model.Review;
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

public class JdbcReviewCrudPersisterTest {
  private Connection conn;
  private JdbcCourseCrudPersister courseCrud;
  private JdbcReviewCrudPersister reviewCrud;
  private Course course;

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

    sql = "DROP TABLE IF EXISTS Reviews;";
    st.execute(sql);

    sql = "CREATE TABLE IF NOT EXISTS Reviews(id INTEGER PRIMARY KEY, courseId VARCHAR(30), rating VARCHAR(30), comment VARCHAR(100)" +
            ", FOREIGN KEY (courseId)" +
            "REFERENCES Reviews (courseId)" +
            "ON UPDATE RESTRICT);";
    st.execute(sql);

    courseCrud = new JdbcCourseCrudPersister(conn);
    reviewCrud = new JdbcReviewCrudPersister(conn);

    course = new Course("oose", "jhu-oose.com");
    courseCrud.create(course);
  }

  @Test
  public void createReviewChangesId() throws SQLException {
    Review r1 = new Review(course.getId(), 5, "fun");
    assertEquals(0, r1.getId());
    reviewCrud.create(r1);
    assertNotEquals(0, r1.getId());
  }

  @Test
  public void readReviewWorks() {
    Review r1 = new Review(course.getId(), 5, "fun");
    reviewCrud.create(r1);
    Review r2 = reviewCrud.read(r1.getId());
    assertEquals(r1, r2);
  }

  @Test
  public void updateReviewWorks() {
    Review r1 = new Review(course.getId(), 5, "fun");
    reviewCrud.create(r1);

    r1.setComment("super fun");
    reviewCrud.update(r1);

    Review r2 = reviewCrud.read(r1.getId());
    assertEquals(r1, r2);
  }

  @Test
  public void deleteReviewWorks() {
    Review r1 = new Review(course.getId(), 5, "fun");
    reviewCrud.create(r1);

    reviewCrud.delete(r1.getId());
    Review r2 = reviewCrud.read(r1.getId());
    assertNull(r2);
  }

  @Test
  public void readReviewsWorks() {
    // TODO: Implement me!
    Review r1 = new Review(course.getId(), 5, "fun");
    Review r2 = new Review(course.getId(), 5, "cool");
    Review r3 = new Review(65, 5, "nicefun");

    reviewCrud.create(r1);
    reviewCrud.create(r2);
    reviewCrud.create(r3);

    List<Review> results = reviewCrud.readAll(course.getId());
    assertTrue(results.contains(r1));
    assertTrue(results.contains(r2));
    assertFalse(results.contains(r3));
  }

  @Test (expected = CrudException.class)
  public void addingReviewToNonExistingCourseFails() {
    // TODO: Implement me!
    //  By non-existing course, it means when adding a review, 
    //  use a course_id that does not exists in the Courses table.
    Review r1 = new Review(6521, 5, "fun");
    reviewCrud.create(r1);
  }

  @After
  public void tearDown() throws SQLException {
    conn.close();
  }
}