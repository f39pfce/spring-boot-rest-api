package rest_api.presentation.logging.request;

import org.apache.commons.io.IOUtils;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * Wrapper for HttpServletRequest that allows the request body to be read multiple times. This is done by returning
 * a new CachedServletInputStream every time a call is made to getInputStream, thereby never depleting the original
 */
public class MultipleReadHttpRequest extends HttpServletRequestWrapper {

    /**
     * This is where the repositories from the original request body is stored and copied from
     */
    private ByteArrayOutputStream cachedBytes;

    /**
     * Constructor
     *
     * @param request http request
     */
    MultipleReadHttpRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * Most important functionality of this class, overrides getInputStream to provide a new input stream each time
     *
     * @return ServletInputStream
     * @throws IOException on read/write failure
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (cachedBytes == null)
            cacheInputStream();

        return new CachedServletInputStream();
    }

    /**
     * As the response body can be read via the getReader or getInputStream must also provide any calls to getReader
     * a copy of the new input stream per call as well
     *6
     * @return BufferedReader
     * @throws IOException on read/write failure
     */
    @Override
    public BufferedReader getReader() throws IOException{
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    /**
     * Store the contents of the original input stream in the cache
     *
     * @throws IOException on read/write failure
     */
    private void cacheInputStream() throws IOException {
        cachedBytes = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), cachedBytes);
    }

    /**
     * Effectively a copy of the input stream from the response body
     */
    public class CachedServletInputStream extends ServletInputStream {

        /**
         * Input stream the copied repositories is saved in
         */
        private ByteArrayInputStream input;

        /**
         * Constructor
         */
        CachedServletInputStream() {
            input = new ByteArrayInputStream(cachedBytes.toByteArray());
        }

        /**
         * As a new input stream is gathered every time, this stream is always finished on traditional checks
         *
         * @return boolean
         */
        @Override
        public boolean isFinished() {
            return true;
        }

        /**
         * As a new input stream is gathered every time, this stream is always ready on traditional checks
         *
         * @return boolean
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * Required override, not used
         *
         * @param listener ReadListener
         */
        @Override
        public void setReadListener(ReadListener listener) { }

        /**
         * Read the contents of the cached input stream
         *
         * @return int
         * @throws IOException on read/write failure
         */
        @Override
        public int read() throws IOException {
            return input.read();
        }
    }
}