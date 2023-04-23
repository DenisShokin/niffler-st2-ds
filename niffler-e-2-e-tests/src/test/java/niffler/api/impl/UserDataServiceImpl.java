package niffler.api.impl;

import niffler.api.UserDataService;
import niffler.model.UserJson;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class UserDataServiceImpl {

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .build();

    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8089")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserDataService userDataService = retrofit.create(UserDataService.class);


    public Response<UserJson> updateUserData(UserJson userJson) {
        try {
            return userDataService.updateUserData(userJson)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Error update userData: " + e.getMessage());
        }
    }

}
