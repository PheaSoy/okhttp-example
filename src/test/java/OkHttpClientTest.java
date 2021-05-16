import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        System.out.println("My responses=>" + response.body().toString());
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
                .url(endpoint + "/401")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        assertThat(response.isSuccessful()).isEqualTo(false);
    }

    @Test
    public void testRetry_then_return_ok() throws Exception {

        var request = new Request.Builder()
                .url(endpoint + "/retry1")
                .build();
        var client = okHttpClient.newBuilder()
                .addInterceptor(new MyInterceptor()).addInterceptor(new LoggingInterceptor()).build();
        var response = client.newCall(request).execute();
        assertThat(response.isSuccessful()).isEqualTo(true);

    }

    class MyInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {

            Request request = chain.request();
            // try the request
            Response response = chain.proceed(request);

            int tryCount = 1;
            int maxLimit = 2; //Set your max limit here
            System.out.println("Method:"+request.method());

            while (!response.isSuccessful() && tryCount <= maxLimit) {

                System.out.println("intercept" + "Request failed - " + tryCount);
                tryCount++;

                // Adding test for different url for retry purpose request.url();
                Request request1 =new Request.Builder()
                        .url(endpoint + "/retry"+tryCount)
                        .build();
                // end custom
                // Retry the request
                response = chain.proceed(request1);
            }
            // otherwise just pass the original response on
            return response;
        }
    }
}



