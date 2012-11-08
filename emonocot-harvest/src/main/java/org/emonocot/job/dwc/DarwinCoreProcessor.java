package org.emonocot.job.dwc;


import org.emonocot.api.TaxonService;
import org.emonocot.harvest.common.AuthorityAware;
import org.emonocot.model.Base;
import org.emonocot.model.BaseData;
import org.emonocot.model.Taxon;
import org.emonocot.model.constants.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ben
 * @param <T> the type of object validated
 */
public abstract class DarwinCoreProcessor<T extends BaseData> extends AuthorityAware implements
        ItemProcessor<T, T> {
    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(DarwinCoreProcessor.class);

    /**
     *
     */
    private TaxonService taxonService;
    
    private String family;
    
    private String subfamily;
    
    private String tribe;
    
    private String subtribe;
    
    /**
     *
     * @param family Set the family
     */
    public final void setFamily(final String family) {
    	this.family = family;
    }
    
    
    /**
	 * @param subfamily the subfamily to set
	 */
	public void setSubfamily(String subfamily) {
		this.subfamily = subfamily;
	}


	/**
	 * @param tribe the tribe to set
	 */
	public void setTribe(String tribe) {
		this.tribe = tribe;
	}


	/**
	 * @param subtribe the subtribe to set
	 */
	public void setSubtribe(String subtribe) {
		this.subtribe = subtribe;
	}


	/**
     *
     * @param recordType Set the record type
     * @param taxon Set the 
     * @throws DarwinCoreProcessingException
     */
    protected void checkTaxon(final RecordType recordType, final Base record, final Taxon taxon) throws DarwinCoreProcessingException {
    	if(taxon == null) {
    		throw new RequiredFieldException(record + " has no Taxon set", recordType, getStepExecution().getReadCount());
    	} else if(subtribe != null && (taxon.getSubtribe() == null || !taxon.getSubtribe().equals(subtribe))) {
    		throw new OutOfScopeTaxonException("Expected content to be related to " + subtribe + " but found content related to " + taxon + " which is in " + taxon.getSubtribe(),
                    recordType, getStepExecution().getReadCount());
    	} else if(tribe != null && (taxon.getTribe() == null || !taxon.getTribe().equals(tribe))) {
    		throw new OutOfScopeTaxonException("Expected content to be related to " + tribe + " but found content related to " + taxon + " which is in " + taxon.getTribe(),
                    recordType, getStepExecution().getReadCount());
    	} else if(subfamily != null && (taxon.getSubfamily() == null || !taxon.getSubfamily().equals(subfamily))) {
    		throw new OutOfScopeTaxonException("Expected content to be related to " + subfamily + " but found content related to " + taxon + " which is in " + taxon.getSubfamily(),
                    recordType, getStepExecution().getReadCount());
    	} else if(family != null && (taxon.getFamily() == null || !taxon.getFamily().equals(family))) {
    		throw new OutOfScopeTaxonException("Expected content to be related to " + family + " but found content related to " + taxon + " which is in " + taxon.getFamily(),
                    recordType, getStepExecution().getReadCount());
    	}
    }

    /**
     * @param taxonService set the taxon service
     */
    @Autowired
    public final void setTaxonService(TaxonService taxonService) {
        this.taxonService = taxonService;
    }

    /**
     *
     * @return the taxon service set
     */
    public final TaxonService getTaxonService() {
        return taxonService;
    }

    /**
     * @param t an object
     * @throws Exception if something goes wrong
     * @return an object of class T
     */
    public abstract T process(final T t) throws Exception;
}
