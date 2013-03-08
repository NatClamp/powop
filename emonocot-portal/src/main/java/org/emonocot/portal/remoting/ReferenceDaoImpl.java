package org.emonocot.portal.remoting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emonocot.model.Reference;
import org.emonocot.pager.DefaultPageImpl;
import org.emonocot.pager.Page;
import org.emonocot.persistence.dao.ReferenceDao;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ben
 *
 */
@Repository
public class ReferenceDaoImpl extends DaoImpl<Reference> implements
        ReferenceDao {

    /**
     *
     */
    public ReferenceDaoImpl() {
        super(Reference.class, "reference");
    }

    /**
     * @param source The source of the reference you want to find
     * @return a reference or null if it does not exist
     */
    public final Reference findByBibliographicCitation(final String source) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected Page<Reference> page(Integer page, Integer size) {
    	HttpEntity<Reference> requestEntity = new HttpEntity<Reference>(httpHeaders);
    	Map<String,Object> uriVariables = new HashMap<String,Object>();
    	uriVariables.put("resource", resourceDir);
    	if(size == null) {
    		uriVariables.put("limit", "");
    	} else {
    		uriVariables.put("limit", size);
    	}
    	
    	if(page == null) {
    		uriVariables.put("start", "");
    	} else {
    		uriVariables.put("start", page);
    	}
    	
    	ParameterizedTypeReference<DefaultPageImpl<Reference>> typeRef = new ParameterizedTypeReference<DefaultPageImpl<Reference>>() {};
        HttpEntity<DefaultPageImpl<Reference>> responseEntity = restTemplate.exchange(baseUri + "/{resource}?limit={limit}&start={start}", HttpMethod.GET,
                requestEntity, typeRef,uriVariables);
        return responseEntity.getBody();
    }
    
    @Override
	public final List<Reference> list(final Integer page, final Integer size, final String fetch) {
    	return this.page(page, size).getRecords();
    }
}
