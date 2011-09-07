package org.emonocot.job.dwc.taxon;

import org.emonocot.model.authority.Authority;
import org.emonocot.model.common.Annotation;
import org.emonocot.model.common.AnnotationType;
import org.emonocot.model.taxon.Taxon;
import org.emonocot.service.TaxonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ben
 *
 */
public class TaxonValidator implements ItemProcessor<Taxon, Taxon>,
        StepExecutionListener {
    /**
     *
     */
    private Logger logger = LoggerFactory.getLogger(TaxonValidator.class);

    /**
     *
     */
    private TaxonService taxonService;

    /**
     *
     */
    private StepExecution stepExecution;

    /**
     *
     */
    private Authority authority;

    /**
     *
     */
    @Autowired
    public final void setTaxonService(TaxonService taxonService) {
        this.taxonService = taxonService;
    }

    /**
    *
    * @param authorityName Set the id of the authority
    */
    public final void setAuthorityName(String authorityName) {
      authority = new Authority();
      authority.setId(Long.parseLong(authorityName));
    }

    /**
     * @param taxon a taxon object
     * @throws Exception if something goes wrong
     * @return Taxon a taxon object
     */
    public final Taxon process(final Taxon taxon) throws Exception {
        logger.info("Processing " + taxon.getIdentifier());
        if (taxon.getIdentifier() == null) {
            throw new NoIdentifierException(taxon);
        }
        Taxon persistedTaxon = taxonService.find(taxon.getIdentifier());
        if (persistedTaxon == null) {
            throw new CannotFindRecordException(taxon.getIdentifier());
        }

        boolean anAnnotationPresent = false;
        for (Annotation annotation : persistedTaxon.getAnnotations()) {
            if (annotation.getJobId().equals(
                    stepExecution.getJobExecutionId())) {
                if (annotation.getType().equals(AnnotationType.Present)) {
                    throw new TaxonAlreadyProcessedException("Taxon "
                            + taxon.getIdentifier()
                            + " already found once in this archive");
                }
                annotation.setType(AnnotationType.Present);
                anAnnotationPresent = true;
                break;
            }
        }

        if (!anAnnotationPresent) {
            throw new UnexpectedTaxonException(taxon.getIdentifier());
        } else {
            /**
             * Using java.util.Collection.contains() does not work on lazy
             * collections.
             */
            boolean contains = false;
            for (Authority auth : persistedTaxon.getAuthorities()) {
                if (auth.equals(authority)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                persistedTaxon.getAuthorities().add(authority);
            }
        }
        return persistedTaxon;
    }

    /**
     * @param newStepExecution Set the step execution
     */
    public final void beforeStep(final StepExecution newStepExecution) {
        this.stepExecution = newStepExecution;
    }

    /**
     * @param newStepExecution Set the step execution
     * @return the exit status
     */
    public final ExitStatus afterStep(final StepExecution newStepExecution) {
        return null;
    }

}
