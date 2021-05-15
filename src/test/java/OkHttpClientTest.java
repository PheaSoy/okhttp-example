import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OkHttpClientTest extends AbstractTest {

    private final OkHttpClient okHttpClient = new OkHttpClient();

    @Test
    @DisplayName("Check mockserver is running?")
    public void checkMockServer_isRunning() {
        System.out.println("mockServer");
        assertThat(client.isRunning()).isEqualTo(true);
    }

    @Test
    @DisplayName("Easy test!")
    public void when_SendingCorrect_Should_Return_Success() throws Exception {
        Request request = new Request.Builder()
                .url(endpoint + "/test").build();
        Response response = okHttpClient.newCall(request).execute();
        System.out.println("My responses=>"+response.body().toString());
        assertThat(response.isSuccessful()).isEqualTo(true);
    }

    @Test
    @DisplayName("Okhttp with path param!")
    public void testWithPathParam() throws Exception {
        HttpUrl httpUrl = HttpUrl.parse(endpoint + "/songs")
                .newBuilder().addQueryParameter("name", "Love yourself").build();
        Request request = new Request.Builder()
                .url(httpUrl.toString())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        assertThat(response.isSuccessful()).isEqualTo(true);
        assertThat(response.body().string()).contains("Hello from Love yourself song");
    }

    @Test
    @DisplayName("Test 401 status code")
    public void test_404() throws Exception {
        Request request = new Request.Builder()
                .url(endpoint+"/401")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        assertThat(response.isSuccessful()).isEqualTo(false);
    }

}
