package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class US101 {
    Faker randomGenerator = new Faker();
    RequestSpecification requestSpec;
    String countryId = "5baac28d91cefe05fc6e3fe6";

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io/";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .body(userCredential)
                        .contentType(ContentType.JSON)

                        .when()
                        .post("/auth/login")

                        .then()
                        .log().body()

                        .statusCode(200)

                        .body("access_token", instanceOf(String.class))
                        .body("token_type", instanceOf(String.class))
                        .body("refresh_token", instanceOf(String.class))
                        .body("expires_in", instanceOf(Integer.class))
                        .body("scope", instanceOf(String.class))
                        .body("passwordChange", instanceOf(Boolean.class))
                        .body("username", instanceOf(String.class))
                        .body("iat", instanceOf(Integer.class))
                        .body("jti", instanceOf(String.class))
                        .body("is_2fa_enabled", instanceOf(Boolean.class))

                        .body("access_token", notNullValue())
                        .body("token_type", notNullValue())
                        .body("refresh_token", notNullValue())
                        .body("expires_in", greaterThan(0))
                        .body("scope", notNullValue())
                        .body("passwordChange", notNullValue())
                        .body("username", notNullValue())
                        .body("iat", notNullValue())
                        .body("jti", notNullValue())
                        .body("is_2fa_enabled", notNullValue())

                        .extract().response().detailedCookies();

        requestSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build()
        ;
    }

    @Test
    public void countryList() {
        Map<String, String> countryList = new HashMap<>();
        countryList.put("name", "");
        countryList.put("countryId", countryId);

        given()

                .spec(requestSpec)
                .body(countryList)

                .when()
                .post("school-service/api/states/search/")

                .then()
                .log().body()

                .statusCode(200)
                .time(lessThan(1000L))

                .body("[0].id", instanceOf(String.class))
                .body("[0].country.id", instanceOf(String.class))

                .body("[0].id", notNullValue())
                .body("[0].country.id", notNullValue())

        ;
    }
    @Test
    public void createNewState() {
        String rndName = randomGenerator.country().name() + randomGenerator.number().digits(5);
        String rndShortName = randomGenerator.country().countryCode3();

        Map <String, String> newState = new HashMap<>();
        newState.put("name", rndName);
        newState.put("shortName", rndShortName);
        newState.put("country.id", "63a41a0dcb75ee5c2199a8bc");

        String stateId =
                given()

                        .spec(requestSpec)
                        .body(newState)

                        .when()
                        .post("school-service/api/states/")

                        .then()
                        .log().body()

                        .statusCode(201)

                 .body("[0].id", instanceOf(String.class))
                 .body("[0].country.id", instanceOf(String.class))

                 .body("[0].id", notNullValue())
                 .body("[0].country.id", notNullValue())

                        .extract().path("id")
        ;

}
}
