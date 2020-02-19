package persist;

import exceptions.CrudException;

/**
 * An interface to implement basic CRUD operations.
 *
 * @param <T> base type.
 */
public interface CrudPersister<T> {

  /**
   * Add <pre>t</pre> to database and store the
   * "generated key" by the database to the
   * <pre>id</pre> field of <pre>t</pre>.
   *
   * @param t must have an <pre>id</pre> field.
   * @throws CrudException if operation fails.
   */
  void create(T t) throws CrudException;

  /**
   * Search the database to find a record with the given <pre>id</pre>.
   *
   * @param id the primary key to search for a record.
   * @return an object that encapsulates the retrieved record.
   * @throws CrudException if operation fails.
   */
  T read(int id) throws CrudException;

  /**
   * Update the database record of <pre>t</pre> with its current state.
   *
   * @param t must have an <pre>id</pre> field.
   * @throws CrudException if operation fails.
   */
  void update(T t) throws CrudException;

  /**
   * Remove the record with <pre>id</pre> primary key.
   *
   * @param id corresponds to the primary key of a record in a table.
   * @throws CrudException if operation fails.
   */
  void delete(int id) throws CrudException;

}
