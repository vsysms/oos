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

public class JdbcReviewCrudPersister implements CrudPersister<Review> {

  private Connection conn;

  public JdbcReviewCrudPersister(Connection conn) {
    this.conn = conn;
  }

  @Override
  public void create(Review review) throws CrudException {
    String sql = "INSERT INTO Reviews(courseId, rating, comment) VALUES (?, ?, ?)";
    PreparedStatement pst = null;
    try {
      pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      pst.setInt(1, review.getCourseId());
      pst.setInt(2, review.getRating());
      pst.setString(3, review.getComment());
      pst.executeUpdate();

      ResultSet rs = pst.getGeneratedKeys();
      rs.next();
      review.setId(rs.getInt(1));
    } catch (SQLException e) {
      throw new CrudException("Unable to create the review", e);
    }
  }

  @Override
  public Review read(int id) throws CrudException {
    String sql = "SELECT * FROM Reviews WHERE id = ?;";
    PreparedStatement pst = null;
    Review review = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setInt(1, id);
      ResultSet rs = pst.executeQuery();

      if (!rs.next()) return null;

      review = new Review(
              rs.getInt("courseId"),
              rs.getInt("rating"),
              rs.getString("comment")
      );
      review.setId(rs.getInt("id"));
    } catch (SQLException e) {
      throw new CrudException("Unable to read the review", e);
    }

    return review;
  }

  @Override
  public void update(Review review) throws CrudException {
    String sql = "UPDATE Reviews SET id = ?, courseID = ?, rating = ?, comment = ?;";
    PreparedStatement pst = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setInt(1, review.getId());
      pst.setInt(2, review.getCourseId());
      pst.setInt(3, review.getRating());
      pst.setString(4, review.getComment());
      pst.executeUpdate();
    } catch (SQLException e) {
      throw new CrudException("Unable to update the review", e);
    }
  }

  @Override
  public void delete(int id) throws CrudException {
    String sql = "DELETE FROM Reviews WHERE id = ?;";
    PreparedStatement pst = null;
    try {
      pst = conn.prepareStatement(sql);
      pst.setInt(1, id);
      pst.executeUpdate();
    } catch (SQLException e) {
      throw new CrudException("Unable to delete review", e);
    }
  }

  /**
   * Return a list of all the reviews for a course with the given id.
   *
   * @param courseId the id (primary key) of a course.
   * @return list of reviews.
   */
  public List<Review> readAll(int courseId) {
    List<Review> reviews = null;
    Review review = null;
    Statement st = null;
    try {
      reviews = new ArrayList<Review>();
      st = conn.createStatement();
      String sql = "SELECT * FROM Reviews WHERE courseId = courseId;";
      ResultSet rs = st.executeQuery(sql);
      while (rs.next()) {
        review = new Review(rs.getInt("courseId"), rs.getInt("rating"), rs.getString("comment"));
        review.setId(rs.getInt("id"));
        if(review.getCourseId() == courseId) {
          reviews.add(review);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return reviews;
  }
}
