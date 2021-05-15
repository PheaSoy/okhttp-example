import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpExample {

    private final static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws Exception{
        String url = "http://localhost:8080/songs";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
    }
}
