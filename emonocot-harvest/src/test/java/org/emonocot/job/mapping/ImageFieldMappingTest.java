package org.emonocot.job.mapping;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;
import org.emonocot.harvest.common.HtmlSanitizer;
import org.emonocot.job.dwc.image.FieldSetMapper;
import org.emonocot.model.Image;
import org.emonocot.model.constants.MediaType;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindException;

public class ImageFieldMappingTest {

	private static final TestCase[] simpleTestCases = {
			// Standard DC terms
			/*new TestCase("http://purl.org/dc/terms/audience", "Audience").sets("Audience").on("audience"),
			new TestCase("http://purl.org/dc/terms/contributor", "Contributor").sets("Contributor").on("contributor"),
			new TestCase("http://purl.org/dc/terms/creator", "Creator").sets("Creator").on("creator"),
			new TestCase("http://purl.org/dc/terms/description", "Description").sets("Description").on("description"),
			new TestCase("http://purl.org/dc/terms/format", "Format").sets("Format").on("format"),
			new TestCase("http://purl.org/dc/terms/identifier", "Identifier").sets("Identifier").on("identifier"),
			new TestCase("http://purl.org/dc/terms/publisher", "Publisher").sets("Publisher").on("publisher"),
			new TestCase("http://purl.org/dc/terms/references", "References").sets("References").on("references"),
			new TestCase("http://purl.org/dc/terms/subject", "Subject").sets("Subject").on("subject"),
			new TestCase("http://purl.org/dc/terms/title", "Title").sets("Title").on("title"),
			new TestCase("http://purl.org/dc/terms/type", "Image").sets(MediaType.Image).on("mediaType"),
			// Audubon terms 
			new TestCase("http://rs.tdwg.org/ac/terms/caption", "Caption").sets("Caption").on("caption"),
			new TestCase("http://rs.tdwg.org/ac/terms/providerManagedID", "PMID").sets("PMID").on("providerManagedId"),
			new TestCase("http://rs.tdwg.org/ac/terms/subjectPart", "Subject Part").sets("Subject Part").on("subjectPart"),
			new TestCase("http://rs.tdwg.org/ac/terms/accessURI", "http://blargedy.com/").sets("http://blargedy.com").on("accessUri"),*/
			// Adobe XMP terms
			new TestCase("http://ns.adobe.com/xap/1.0/Rating", "1.0").sets(1.0).on("rating"),
			new TestCase("http://ns.adobe.com/xap/1.0/rights/Owner", "Owner").sets("Owner").on("owner"),
			};

	public void mockDependencies() {
		// expect(htmlSanitizer.sanitize("Description")).andReturn("Description");
		expect(conversionService.convert("1.0", Double.class)).andReturn(new Double(1));

		mapper.setConversionService(conversionService);
		// mapper.setHtmlSanitizer(htmlSanitizer);
		replay(conversionService, htmlSanitizer);
	}

	private final FieldSetMapper mapper = new FieldSetMapper();
	private final Image image = new Image();

	private final ConversionService conversionService = createMock(ConversionService.class);
	private final HtmlSanitizer htmlSanitizer = createMock(HtmlSanitizer.class);

	@Test
	public void testSimpleFieldMappings() throws BindException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		mockDependencies();
		for(TestCase test : simpleTestCases) {
			mapper.mapField(image, test.fieldName, test.value);
			assertEquals(test.expected, PropertyUtils.getSimpleProperty(image, test.propertyName));
		}
	}

}
