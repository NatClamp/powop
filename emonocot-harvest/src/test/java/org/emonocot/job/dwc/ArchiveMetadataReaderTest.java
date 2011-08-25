package org.emonocot.job.dwc;

import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author ben
 */
public class ArchiveMetadataReaderTest {

   /**
    *
    */
   private Resource content = new ClassPathResource(
           "/org/emonocot/job/dwc/test");

   /**
    *
    */
   private ArchiveMetadataReader archiveMetadataReader
       = new ArchiveMetadataReader();

    /**
     * @throws Exception if there is a problem accessing the file
     */
    @Test
    public final void testReadMetadata() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        JobExecution jobExecution = new JobExecution(0L);
        jobExecution.setExecutionContext(executionContext);

        archiveMetadataReader.beforeStep(
                new StepExecution("test", jobExecution));
        archiveMetadataReader.readMetadata(content.getFile().getAbsolutePath());

        assertNotNull("core file must be present",
                executionContext.getString("dwca.core.file"));
        assertEquals("fieldsTerminatedBy must be present", "\t",
                executionContext.getString("dwca.core.fieldsTerminatedBy"));
        assertEquals("encoding must be present", "UTF-8",
                executionContext.getString("dwca.core.encoding"));
        assertEquals("ignoreHeaderLines must be present", 0,
                executionContext.getInt("dwca.core.ignoreHeaderLines"));
        assertNotNull("field names must be present",
                executionContext.get("dwca.core.fieldNames"));
        String[] actualCoreFieldNames = (String[])
                executionContext.get("dwca.core.fieldNames");
        String[] expectedCoreFieldNames = new String[]{
                "http://rs.tdwg.org/dwc/terms/taxonID",
                "http://rs.tdwg.org/dwc/terms/scientificName",
                "http://rs.tdwg.org/dwc/terms/scientificNameID",
                "http://rs.tdwg.org/dwc/terms/scientificNameAuthorship",
                "http://rs.tdwg.org/dwc/terms/taxonRank",
                "http://rs.tdwg.org/dwc/terms/taxonomicStatus",
                "http://rs.tdwg.org/dwc/terms/parentNameUsageID",
                "http://rs.tdwg.org/dwc/terms/acceptedNameUsageID",
                "http://rs.tdwg.org/dwc/terms/genus",
                "http://rs.tdwg.org/dwc/terms/subgenus",
                "http://rs.tdwg.org/dwc/terms/specificEpithet",
                "http://rs.tdwg.org/dwc/terms/infraspecificEpithet",
                "http://purl.org/dc/terms/identifier",
                "http://purl.org/dc/terms/modified",
                "http://purl.org/dc/terms/source"
        };
        for (String expectedCoreFieldName : expectedCoreFieldNames) {
            assertThat(actualCoreFieldNames,
                hasItemInArray(expectedCoreFieldName));
        }
        assertNotNull("description file must be present",
                executionContext.getString("dwca.description.file"));
        assertEquals("fieldsTerminatedBy must be present", "\t",
                executionContext.getString(
                        "dwca.description.fieldsTerminatedBy"));
        assertEquals("encoding must be present", "UTF-8",
                executionContext.getString("dwca.description.encoding"));
        assertEquals("ignoreHeaderLines must be present", 0,
                executionContext.getInt("dwca.description.ignoreHeaderLines"));
        assertNotNull("field names must be present",
                executionContext.get("dwca.description.fieldNames"));

        String[] expectedDescriptionFieldNames = new String[] {
                "http://rs.tdwg.org/dwc/terms/taxonID",
                "http://purl.org/dc/terms/created",
                "http://purl.org/dc/terms/modified",
                "http://purl.org/dc/terms/description",
                "http://purl.org/dc/terms/type",
                "http://purl.org/dc/terms/source"
        };

        String[] actualDescriptionFieldNames = (String[])
        executionContext.get("dwca.description.fieldNames");

        for (String expectedDescriptionFieldName
                : expectedDescriptionFieldNames) {
            assertThat(actualDescriptionFieldNames,
                    hasItemInArray(expectedDescriptionFieldName));
        }

        assertNotNull("image file must be present",
                executionContext.getString("dwca.image.file"));
        assertEquals("fieldsTerminatedBy must be present", "\t",
                executionContext.getString(
                        "dwca.image.fieldsTerminatedBy"));
        assertEquals("encoding must be present", "UTF-8",
                executionContext.getString("dwca.image.encoding"));
        assertEquals("ignoreHeaderLines must be present", 0,
                executionContext.getInt("dwca.image.ignoreHeaderLines"));
        assertNotNull("field names must be present",
                executionContext.get("dwca.image.fieldNames"));

        String[] expectedImageFieldNames = new String[] {
                "http://rs.tdwg.org/dwc/terms/taxonID",
                "http://purl.org/dc/terms/created",
                "http://purl.org/dc/terms/modified",
                "http://purl.org/dc/terms/identifier",
                "http://purl.org/dc/terms/title"
        };

        String[] actualImageFieldNames = (String[])
        executionContext.get("dwca.image.fieldNames");

        for (String expectedImageFieldName
                : expectedImageFieldNames) {
            assertThat(actualImageFieldNames,
                    hasItemInArray(expectedImageFieldName));
        }

        assertNotNull("distribution file must be present",
                executionContext.getString("dwca.distribution.file"));
        assertEquals("fieldsTerminatedBy must be present", "\t",
                executionContext.getString(
                        "dwca.distribution.fieldsTerminatedBy"));
        assertEquals("encoding must be present", "UTF-8",
                executionContext.getString("dwca.distribution.encoding"));
        assertEquals("ignoreHeaderLines must be present", 0,
                executionContext.getInt("dwca.distribution.ignoreHeaderLines"));
        assertNotNull("field names must be present",
                executionContext.get("dwca.distribution.fieldNames"));

        String[] expectedDistributionFieldNames = new String[] {
                "http://rs.tdwg.org/dwc/terms/taxonID",
                "http://purl.org/dc/terms/created",
                "http://purl.org/dc/terms/modified",
                "http://rs.tdwg.org/dwc/terms/occurrenceStatus",
                "http://rs.tdwg.org/dwc/terms/locationID",
                "http://purl.org/dc/terms/source"
        };

        String[] actualDistributionFieldNames = (String[])
        executionContext.get("dwca.distribution.fieldNames");

        for (String expectedDistributionFieldName
                : expectedDistributionFieldNames) {
            assertThat(actualDistributionFieldNames,
                    hasItemInArray(expectedDistributionFieldName));
        }

        assertNotNull("reference file must be present",
                executionContext.getString("dwca.reference.file"));
        assertEquals("fieldsTerminatedBy must be present", "\t",
                executionContext.getString(
                        "dwca.reference.fieldsTerminatedBy"));
        assertEquals("encoding must be present", "UTF-8",
                executionContext.getString("dwca.reference.encoding"));
        assertEquals("ignoreHeaderLines must be present", 0,
                executionContext.getInt("dwca.reference.ignoreHeaderLines"));
        assertNotNull("field names must be present",
                executionContext.get("dwca.reference.fieldNames"));

        String[] expectedReferenceFieldNames = new String[] {
                "http://rs.tdwg.org/dwc/terms/taxonID",
                "http://purl.org/dc/terms/created",
                "http://purl.org/dc/terms/modified",
                "http://purl.org/dc/terms/identifier",
                "http://purl.org/dc/terms/title",
                "http://purl.org/dc/terms/type"
        };

        String[] actualReferenceFieldNames = (String[])
        executionContext.get("dwca.reference.fieldNames");

        for (String expectedReferenceFieldName
                : expectedReferenceFieldNames) {
            assertThat(actualReferenceFieldNames,
                    hasItemInArray(expectedReferenceFieldName));
        }

    }

}