package niffler.test.web;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.AllureId;
import niffler.jupiter.annotation.GenerateCategory;
import niffler.jupiter.annotation.GenerateSpend;
import niffler.jupiter.extension.GenerateCategoryExtension;
import niffler.jupiter.extension.GenerateSpendExtension;
import niffler.model.CurrencyValues;
import niffler.model.SpendJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ExtendWith({GenerateSpendExtension.class, GenerateCategoryExtension.class})
public class SpendsWebTest extends BaseWebTest {

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        $("a[href*='redirect']").click();
        $("input[name='username']").setValue("dima");
        $("input[name='password']").setValue("12345");
        $("button[type='submit']").click();
    }

    @GenerateCategory(
            username = "dima",
            category = "Fisher"
    )

    @GenerateSpend(
            username = "dima",
            description = "QA GURU ADVANCED VOL 2",
            currency = CurrencyValues.RUB,
            amount = 52000.00,
            category = "Fisher"
    )
    @AllureId("501")
    @Test
    void spendShouldBeDeletedByActionInTable(SpendJson spend) {
        $(".spendings-table tbody").$$("tr")
                .find(text(spend.getDescription()))
                .$("td")
                .scrollTo()
                .click();

        $$(".button_type_small").find(text("Delete selected"))
                .click();

        $(".spendings-table tbody")
                .$$("tr")
                .shouldHave(CollectionCondition.size(0));
    }
}
