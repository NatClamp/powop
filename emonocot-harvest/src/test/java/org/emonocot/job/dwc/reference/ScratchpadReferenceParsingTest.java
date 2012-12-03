package org.emonocot.job.dwc.reference;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.emonocot.api.TaxonService;
import org.emonocot.model.Reference;
import org.emonocot.model.Taxon;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author ben
 *
 */
public class ScratchpadReferenceParsingTest {

   /**
    *
    */
   private Resource content = new ClassPathResource(
           "/org/emonocot/job/dwc/scratchpad_reference.txt");

   /**
    *
    */
   private TaxonService taxonService = null;

   /**
    *
    */
    private FlatFileItemReader<Reference> flatFileItemReader = new FlatFileItemReader<Reference>();

   /**
    * @throws Exception if there is a problem
    */
   @Before
   public final void setUp() throws Exception {

       String[] names = new String[] {
               "http://rs.tdwg.org/dwc/terms/taxonID",
               "http://purl.org/dc/terms/identifier",
               "",
               "http://purl.org/ontology/bibo/doi",
               "http://purl.org/ontology/bibo/isbn",
               "http://purl.org/ontology/bibo/issn",
               "",
               "",
               "http://purl.org/dc/terms/bibliographicCitation",
               "http://purl.org/dc/terms/title",
               "http://purl.org/dc/terms/creator",
               "http://purl.org/dc/terms/source",
               "http://purl.org/dc/terms/description",
               "http://purl.org/dc/terms/subject",
               "http://purl.org/dc/terms/language",
               "http://purl.org/dc/terms/type",
               "http://purl.org/dc/terms/date",
               "http://purl.org/dc/terms/created",
               "http://purl.org/dc/terms/modified"
       };
       DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
       tokenizer.setDelimiter(',');
       tokenizer.setQuoteCharacter('"');
       tokenizer.setNames(names);

       taxonService = EasyMock.createMock(TaxonService.class);

        FieldSetMapper fieldSetMapper = new FieldSetMapper();
        fieldSetMapper.setFieldNames(names);
        fieldSetMapper.setDefaultValues(new HashMap<String, String>());
        fieldSetMapper.setTaxonService(taxonService);
        DefaultLineMapper<Reference> lineMapper
            = new DefaultLineMapper<Reference>();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);

        flatFileItemReader.setEncoding("UTF-8");
        flatFileItemReader.setLinesToSkip(0);
        flatFileItemReader.setResource(content);
        flatFileItemReader.setLineMapper(lineMapper);
        flatFileItemReader.afterPropertiesSet();
   }

    /**
     * @throws Exception if there is a problem accessing the file
     */
    @Test
    public final void testRead() throws Exception {
        EasyMock.expect(taxonService.find(EasyMock.isA(String.class))).andReturn(new Taxon()).anyTimes();
        EasyMock.replay(taxonService);
        flatFileItemReader.open(new ExecutionContext());
        flatFileItemReader.read();

    }

}