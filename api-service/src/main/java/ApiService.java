/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ApiService implements BitmarkApi {

    private final HttpClient httpClient;

    public ApiService(String apiToken) {
        httpClient = new HttpClientImpl(apiToken);
    }

}
