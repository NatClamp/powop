package org.emonocot.job.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.emonocot.harvest.common.GetResourceClient;
import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.retry.RetryCallback;
import org.springframework.batch.retry.RetryContext;
import org.springframework.batch.retry.RetryListener;
import org.xml.sax.SAXException;

/**
 *
 * @author ben
 *
 */
public class GetResourceClientIntegrationTest {
	
    private static final BaseDateTime PAST_DATETIME = new DateTime(2010, 11, 1, 9, 0, 0, 0);

    private GetResourceClient getResourceClient;

    @Before
    public final void setUp() throws IOException {    	
        getResourceClient = new GetResourceClient();
        
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

        ExitStatus exitStatus = getResourceClient
                .getResource("http://build.e-monocot.org/",
                        "http://build.e-monocot.org/test/test.zip",
                        Long.toString(PAST_DATETIME.getMillis()),
                        tempFile.getAbsolutePath());

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
        

        ExitStatus exitStatus = getResourceClient
                .getResource("http://build.e-monocot.org/",
                        "http://build.e-monocot.org/test/test.zip",
                        Long.toString(new Date().getTime() - 60000L),
                        tempFile.getAbsolutePath());

        assertNotNull("ExitStatus should not be null", exitStatus);
        assertEquals("ExitStatus should be NOT_MODIFIED",
                exitStatus.getExitCode(), "NOT_MODIFIED");
    }

    /**
     *
     @throws IOException
     *             if a temporary file cannot be created or if there is a http
     *             protocol error.
     */
    @Test
    public final void testGetDocumentAnyOtherStatus() throws IOException {
    	AttemptCountingRetryListener retryListener = new AttemptCountingRetryListener();
        File tempFile = File.createTempFile("test", "zip");
        tempFile.deleteOnExit();
        getResourceClient.setRetryListeners(new RetryListener[] {
        		retryListener
        });

        ExitStatus exitStatus = getResourceClient
                .getResource("http://example.com/",
                        "http://example.com/test.zip",
                        Long.toString(new Date().getTime()),
                        tempFile.getAbsolutePath());

        assertNotNull("ExitStatus should not be null", exitStatus);
        assertEquals("ExitStatus should be FAILED", exitStatus,
                ExitStatus.FAILED);
        assertEquals("There should be three retry attempts",3,retryListener.getErrors());
    }
    
    class AttemptCountingRetryListener implements RetryListener {
    	
    	private int errors = 0;
    	
    	public int getErrors() {
    		return errors;
    	}

		@Override
		public <T> boolean open(RetryContext context, RetryCallback<T> callback) {			
			return true;
		}

		@Override
		public <T> void close(RetryContext context, RetryCallback<T> callback,
				Throwable throwable) {
			
		}

		@Override
		public <T> void onError(RetryContext context,
				RetryCallback<T> callback, Throwable throwable) {
			errors++;
		}
    	
    }

}