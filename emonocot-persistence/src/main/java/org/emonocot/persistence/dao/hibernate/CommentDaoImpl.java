/**
 * 
 */
package org.emonocot.persistence.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.emonocot.model.Comment;
import org.emonocot.model.hibernate.Fetch;
import org.emonocot.persistence.dao.CommentDao;
import org.hibernate.FetchMode;
import org.springframework.stereotype.Repository;

/**
 * @author jk00kg
 *
 */
@Repository
public class CommentDaoImpl extends DaoImpl<Comment> implements CommentDao {

   /**
    *
    */
   private static Map<String, Fetch[]> FETCH_PROFILES;

   static {
       FETCH_PROFILES = new HashMap<String, Fetch[]>();
       FETCH_PROFILES.put("aboutData", new Fetch[] {
               new Fetch("aboutData", FetchMode.SELECT)
       });
   }
    
    public CommentDaoImpl() {
        super(Comment.class);
    }

    /* (non-Javadoc)
     * @see org.emonocot.persistence.dao.hibernate.DaoImpl#getProfile(java.lang.String)
     */
    @Override
    protected Fetch[] getProfile(String profile) {
        Fetch[] fetch = FETCH_PROFILES.get(profile); 
        if(fetch != null) {
            return fetch; 
        } else {
            return FETCH_PROFILES.get("aboutData");
        }
    }

}
