package org.emonocot.job.dwc;

import java.io.Serializable;

import org.emonocot.job.dwc.description.DescriptionProcessingException;
import org.emonocot.model.source.Source;
import org.emonocot.model.common.Annotation;
import org.emonocot.model.common.AnnotationType;
import org.emonocot.model.common.Base;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
/**
 *
 * @author ben
 *
 */
public class DwCProcessingExceptionProcessListener extends HibernateDaoSupport
        implements ItemProcessListener<Base, Base>,
        ItemReadListener<Base>, StepExecutionListener {
   /**
    *
    */
   private Logger logger = LoggerFactory.getLogger(
           DwCProcessingExceptionProcessListener.class);

   /**
    *
    */
   private StepExecution stepExecution;

   /**
    *
    */
   private TransactionTemplate transactionTemplate = null;

   /**
    *
    */
   private Source Source = null;

   /**
    *
    */
   private static final String PROCESS_CORE_FILE = "processCoreFile";

   /**
    *
    */
    private static final String PROCESS_DESCRIPTION_FILE
        = "processDescriptionFile";

    /**
     *
     */
    private static final String PROCESS_IMAGE_FILE
        = "processImageFile";

    /**
    *
    */
   private static final String PROCESS_REFERENCE_FILE
       = "processReferenceFile";

  /**
   *
   * @param SourceName Set the id of the Source
   */
   public final void setSourceName(final String SourceName) {
     Source = new Source();
     Source.setId(Long.parseLong(SourceName));
   }

   /**
    *
    * @param transactionManager Set the transaction manager
    */
   public final void setTransactionManager(
           final PlatformTransactionManager transactionManager) {
       Assert.notNull(transactionManager,
               "The 'transactionManager' argument must not be null.");
       this.transactionTemplate = new TransactionTemplate(transactionManager);
       this.transactionTemplate.setPropagationBehavior(
               TransactionDefinition.PROPAGATION_REQUIRES_NEW);
   }

  /**
   *
   * @param sessionFactory Set the session factory
   */
  public final void setHibernateSessionFactory(
          final SessionFactory sessionFactory) {
      this.setSessionFactory(sessionFactory);
  }

    /**
     * @param item the item to be processed
     */
    public void beforeProcess(final Base item) {

    }

    /**
     * @param item the item to be processed
     * @param result the resulting object
     */
    public void afterProcess(final Base item, final Base result) {

    }

    /**
     * @param item the item to be processed
     * @param e the exception thrown
     */
    public final void onProcessError(final Base item, final Exception e) {
        logger.debug("Process Error " + e.getMessage());
        if (e instanceof DarwinCoreProcessingException) {
            DarwinCoreProcessingException dwcpe
                = (DarwinCoreProcessingException) e;
            logger.debug(dwcpe.getCode() + " | " + dwcpe.getMessage());
            final Annotation annotation = new Annotation();
            annotation.setJobId(stepExecution.getJobExecutionId());
            annotation.setCode(dwcpe.getCode());
            annotation.setText(dwcpe.getMessage());
            annotation.setSource(Source);
            annotation.setType(dwcpe.getType());
            String stepName = stepExecution.getStepName();
            if (stepName.equals(PROCESS_CORE_FILE)) {
                annotation.setAnnotatedObjType("Taxon");
            } else if (stepName.equals(PROCESS_DESCRIPTION_FILE)) {
                annotation.setAnnotatedObjType("TextContent");
            } else if (stepName.equals(PROCESS_IMAGE_FILE)) {
                annotation.setAnnotatedObjType("Image");
            } else if (stepName.equals(PROCESS_REFERENCE_FILE)) {
                annotation.setAnnotatedObjType("Reference");
            }
            try {
                transactionTemplate
                        .execute(new TransactionCallback<Serializable>() {

                            public Serializable doInTransaction(
                                    final TransactionStatus status) {
                                return getSession().save(annotation);
                            }
                        });
            } catch (Throwable t) {
                logger.error(t.getMessage());
                throw new RuntimeException(t);
            }
        }
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

    /**
     * @param base the object read
     */
    public void afterRead(final Base base) {

    }

    /**
     *
     */
    public void beforeRead() {

    }

    /**
     * @param e the exception
     */
    public final void onReadError(final Exception e) {
        if (e instanceof FlatFileParseException) {
            FlatFileParseException ffpe = (FlatFileParseException) e;
            StringBuffer message = new StringBuffer();
            message.append(ffpe.getMessage());
            if (ffpe.getCause() != null) {
                message.append(" " + ffpe.getCause().getMessage());
                logger.debug("FlatFileParseException | " + ffpe.getMessage()
                        + " Cause " + ffpe.getCause().getMessage());
            } else {
                logger.debug("FlatFileParseException | " + ffpe.getMessage());
            }
            final Annotation annotation = new Annotation();
            annotation.setJobId(stepExecution.getJobExecutionId());
            annotation.setCode("FlatFileParseException");
            annotation.setSource(Source);
            annotation.setType(AnnotationType.Error);
            annotation.setText(message.toString());
            String stepName = stepExecution.getStepName();
            if (stepName.equals(PROCESS_CORE_FILE)) {
                annotation.setAnnotatedObjType("Taxon");
            } else if (stepName.equals(PROCESS_DESCRIPTION_FILE)) {
                annotation.setAnnotatedObjType("TextContent");
            } else if (stepName.equals(PROCESS_IMAGE_FILE)) {
                annotation.setAnnotatedObjType("Image");
            } else if (stepName.equals(PROCESS_REFERENCE_FILE)) {
                annotation.setAnnotatedObjType("Reference");
            }
            try {
                transactionTemplate
                        .execute(new TransactionCallback<Serializable>() {

                            public Serializable doInTransaction(
                                    final TransactionStatus status) {
                                return getSession().save(annotation);
                            }
                        });
            } catch (Throwable t) {
                logger.error(t.getMessage());
                throw new RuntimeException(t);
            }
        }
    }

}
