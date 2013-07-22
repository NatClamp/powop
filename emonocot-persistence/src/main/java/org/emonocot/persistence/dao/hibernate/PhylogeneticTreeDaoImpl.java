package org.emonocot.persistence.dao.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.emonocot.model.PhylogeneticTree;
import org.emonocot.model.hibernate.Fetch;
import org.emonocot.persistence.dao.PhylogeneticTreeDao;
import org.hibernate.FetchMode;
import org.springframework.stereotype.Repository;

/**
 * @author ben
 */
@Repository
public class PhylogeneticTreeDaoImpl extends
        DaoImpl<PhylogeneticTree> implements PhylogeneticTreeDao {

    /**
     *
     */
    private static Map<String, Fetch[]> FETCH_PROFILES;

    static {
        FETCH_PROFILES = new HashMap<String, Fetch[]>();
        FETCH_PROFILES.put("object-page", new Fetch[] {
                new Fetch("taxa", FetchMode.SELECT),
                new Fetch("leaves", FetchMode.SELECT),
                new Fetch("authority", FetchMode.JOIN),
                new Fetch("comments", FetchMode.SELECT)});
    }

    /**
     *
     */
    public PhylogeneticTreeDaoImpl() {
        super(PhylogeneticTree.class);
    }

    @Override
    public final Fetch[] getProfile(final String profile) {
        return FETCH_PROFILES.get(profile);
    }

}
