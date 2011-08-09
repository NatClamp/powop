package org.emonocot.ws;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

/**
 *
 * @author ben
 *
 */
public class GetResourceClientTest {

    /**
     *
     */
    private GetResourceClient getResourceClient = new GetResourceClient();
    /**
     *
     */
    private HttpClient httpClient = EasyMock.createMock(HttpClient.class);
    /**
     *
     */
    private BasicHttpResponse httpResponse = new BasicHttpResponse(
            new BasicStatusLine(HttpVersion.HTTP_1_0, HttpStatus.SC_OK, "OK"));
    /**
     *
     */
    private Resource content = new ClassPathResource(
            "/org/emonocot/job/dwca/test.zip");

    /**
     *
     * @throws IOException
     *             if the test file cannot be found
     */
    @Before
    public final void setUp() throws IOException {
        getResourceClient.setHttpClient(httpClient);
        httpResponse.setEntity(new FileEntity(content.getFile(),
                "application/zip"));
    }

    /**
     *
     * @throws IOException
     *             if a temporary file cannot be created or if there is a http
     *             protocol error.
     * @throws SAXException
     *             if the content retrieved is not valid xml.
     */
    @Test
    public final void testGetResourceSuccessfully() throws IOException,
            SAXException {
        File tempFile = File.createTempFile("test", "zip");
        tempFile.deleteOnExit();

        EasyMock.expect(httpClient.getParams())
                .andReturn(new BasicHttpParams());
        EasyMock.expect(httpClient.execute(EasyMock.isA(HttpGet.class)))
                .andReturn(httpResponse);
        EasyMock.replay(httpClient);

        ExitStatus exitStatus = getResourceClient
                .getResource("http://129.67.24.160/",
                        "http://129.67.24.160/test/test.zip",
                        Long.toString(new Date().getTime()),
                        tempFile.getAbsolutePath());

        EasyMock.verify(httpClient);

        assertNotNull("ExitStatus should not be null", exitStatus);
        assertEquals("ExitStatus should be COMPLETED", exitStatus,
                ExitStatus.COMPLETED);
    }

    /**
     *
     * @throws IOException
     *             if a temporary file cannot be created or if there is a http
     *             protocol error.
     */
    @Test
    public final void testGetResourceNotModified() throws IOException {
        File tempFile = File.createTempFile("test", "zip");
        tempFile.deleteOnExit();
        httpResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_0,
                HttpStatus.SC_NOT_MODIFIED, "Not Modified"));

        EasyMock.expect(httpClient.getParams())
                .andReturn(new BasicHttpParams());
        EasyMock.expect(httpClient.execute(EasyMock.isA(HttpGet.class)))
                .andReturn(httpResponse);
        EasyMock.replay(httpClient);

        ExitStatus exitStatus = getResourceClient
                .getResource("http://129.67.24.160/",
                        "http://129.67.24.160/test/test.zip",
                        Long.toString(new Date().getTime()),
                        tempFile.getAbsolutePath());

        EasyMock.verify(httpClient);

        assertNotNull("ExitStatus should not be null", exitStatus);
        assertEquals("ExitStatus should be NOT MODIFIED",
                exitStatus.getExitCode(), "NOT MODIFIED");
    }

    /**
     *
     @throws IOException
     *             if a temporary file cannot be created or if there is a http
     *             protocol error.
     */
    @Test
    public final void testGetDocumentAnyOtherStatus() throws IOException {
        File tempFile = File.createTempFile("test", "zip");
        tempFile.deleteOnExit();
        httpResponse.setStatusLine(new BasicStatusLine(HttpVersion.HTTP_1_0,
                HttpStatus.SC_BAD_REQUEST, "Bad Request"));

        EasyMock.expect(httpClient.getParams())
                .andReturn(new BasicHttpParams());
        EasyMock.expect(httpClient.execute(EasyMock.isA(HttpGet.class)))
                .andReturn(httpResponse);
        EasyMock.replay(httpClient);

        ExitStatus exitStatus = getResourceClient
                .getResource("http://129.67.24.160/",
                        "http://129.67.24.160/test/test.zip",
                        Long.toString(new Date().getTime()),
                        tempFile.getAbsolutePath());

        EasyMock.verify(httpClient);

        assertNotNull("ExitStatus should not be null", exitStatus);
        assertEquals("ExitStatus should be FAILED", exitStatus,
                ExitStatus.FAILED);
    }

}
