package rest_api.presentation.logging.request;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents the log of an individual request to the server
 */
@Getter
@Entity
class RequestLog {

    /**
     * Request ID
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    /**
     * HTTP method used
     */
    @NotNull
    private String method;

    /**
     * URL used
     */
    @NotNull
    private String url;

    /**
     * Headers present
     */
    @Column(columnDefinition = "TEXT")
    private String headers;

    /**
     * Body of the request
     */
    private String body;

    /**
     * Constructor
     *
     * @param method method
     * @param url url
     * @param headers headers
     * @param body body
     */
    RequestLog(String method, String url, String headers, String body) {
        this.method  = method;
        this.url     = url;
        this.headers = headers;
        this.body    = body;
    }
}
