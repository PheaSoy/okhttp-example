import com.google.common.net.HttpHeaders;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.*;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

public class AbstractTest {

    private static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.5.4");
    private static String actualVersion = MockServerClient.class.getPackage().getImplementationVersion();
    @Rule
    static protected MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE.withTag("mockserver-" + actualVersion));
    protected static MockServerClient client;
    protected String endpoint = mockServer.getEndpoint();

    @BeforeAll
    public static void setup() {
        mockServer.start();
        client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
        client.when(request().withPath("/test")).
                respond(response().withBody("Hello Song"));

        client.when(request().withPath("/songs")
                .withQueryStringParameter(Parameter.param("name", "Love yourself")))
                .respond(response().withBody("Hello from Love yourself song!").withHeaders(
                        new Header("Content-Type", "application/json; charset=utf-8")
                ));

        client.when(request().withPath("/401"))
                .respond(response()
                .withStatusCode(401));
    }
}
