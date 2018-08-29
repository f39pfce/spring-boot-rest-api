package rest_api.presentation.logging.request;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.test.context.junit4.SpringRunner;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
public class RequestDatabaseLoggerTest {

    private static EntityManager entityManager;

    private static RequestDatabaseLogger requestDatabaseLogger;

    @BeforeClass
    public static void setUp() {
        entityManager = mock(EntityManager.class);
        requestDatabaseLogger = new RequestDatabaseLogger(entityManager);
    }

    @Test
    public void givenAllWebRequests_whenLogRequest_RequestLogObjectPersisted() throws Exception {
        String body   = "test body";
        String URI    = "theURI";
        String method = "post";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenReturn(
                new DelegatingServletInputStream(
                        new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8))
                )
        );
        when(request.getRequestURI()).thenReturn(URI);
        when(request.getMethod()).thenReturn(method);

        requestDatabaseLogger.logRequest(request);

        ArgumentCaptor<RequestLog> capturedRequestLog = ArgumentCaptor.forClass(RequestLog.class);
        verify(entityManager, times(1)).persist(capturedRequestLog.capture());
        assertThat(capturedRequestLog.getValue().getClass()).isEqualTo(RequestLog.class);
        assertThat(capturedRequestLog.getValue().getBody()).isEqualTo(body);
        assertThat(capturedRequestLog.getValue().getHeaders()).isEqualTo("{}");
        assertThat(capturedRequestLog.getValue().getMethod()).isEqualTo(method);
        assertThat(capturedRequestLog.getValue().getUrl()).isEqualTo(URI);
    }
}
