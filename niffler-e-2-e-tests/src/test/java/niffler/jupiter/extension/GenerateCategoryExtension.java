package niffler.jupiter.extension;

import io.qameta.allure.AllureId;
import niffler.api.SpendService;
import niffler.jupiter.annotation.GenerateCategory;
import niffler.model.CategoryJson;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GenerateCategoryExtension implements BeforeEachCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace
            .create(GenerateCategoryExtension.class);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .build();

    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8093")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendService spendService = retrofit.create(SpendService.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        GenerateCategory annotation = context.getRequiredTestMethod()
                .getAnnotation(GenerateCategory.class);

        if (annotation != null) {
            CategoryJson category = new CategoryJson();
            category.setUsername(annotation.username());
            category.setCategory(annotation.category());

            CategoryJson created = spendService.addCategory(category).execute().body();
            String allureTestId = context.getRequiredTestMethod().getAnnotation(AllureId.class).value();
            context.getStore(NAMESPACE).put("category" + allureTestId, created);
        }
    }

}
