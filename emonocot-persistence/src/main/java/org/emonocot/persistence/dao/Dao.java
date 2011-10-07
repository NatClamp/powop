package org.emonocot.persistence.dao;

import java.util.Map;

import org.emonocot.api.FacetName;
import org.emonocot.api.Sorting;
import org.emonocot.model.common.Base;
import org.emonocot.model.pager.Page;

/**
 *
 * @author ben
 *
 * @param <T>
 */
public interface Dao<T extends Base> {

    /**
     *
     * @param identifier
     *            Set the identifier of the object you would like to retrieve
     * @return the object or throw and exception if that object does not exist
     */
    T load(String identifier);
    
   /**
    *
    * @param identifier
    *            Set the identifier of the object you would like to delete
    */
   void delete(String identifier);

    /**
    *
    * @param identifier
    *            Set the identifier of the object you would like to retrieve
    * @return the object or null if that object does not exist
    */
    T find(String identifier);

   /**
    *
    * @param identifier
    *            Set the identifier of the object you would like to retrieve
    * @param fetch Set the fetch profile to use
    * @return the object or throw and exception if that object does not exist
    */
   T load(String identifier, String fetch);

  /**
   *
   * @param identifier
   *            Set the identifier of the object you would like to retrieve
   * @param fetch Set the fetch profile to use
   * @return the object or null if that object does not exist
   */
   T find(String identifier, String fetch);

  /**
   *
   * @param t The object to save.
   * @return the id of the object
   */
  T save(T t);

 /**
  *
  * @param t The object to save.
  */
  void saveOrUpdate(T t);

    /**
     * @param query
     *            A lucene query
     * @param spatialQuery
     *            A spatial query to filter the results by
     * @param pageSize
     *            The maximum number of results to return
     * @param pageNumber
     *            The offset (in pageSize chunks, 0-based) from the beginning of
     *            the recordset
     * @param facets
     *            The names of the facets you want to calculate
     * @param selectedFacets
     *            A map of facets which you would like to restrict the search by
     * @param sort
     *            A representation for the order results should be returned in
     * @return a Page from the resultset
     */
  Page<T> search(String query, String spatialQuery, Integer pageSize,
          Integer pageNumber, FacetName[] facets,
          Map<FacetName, Integer> selectedFacets, Sorting sort);

}
