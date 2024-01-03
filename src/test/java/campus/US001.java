package campus;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.*;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import java.util.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class US001 {
    RequestSpecification requestSpec;
    @DataProvider
    Object [][] UserData(){
        Object[][] data={
                {"turkeyts","technostudy123","true"},
                {"TurkeyTs","Technostudy123","true"},
                {"TurkeyTs","technostudy123","true"},
                {"","TechnoStudy123","true"},
                {"turkeyts","","true"},
                {"","","true"},
        };
        return data;
    }
    @Test (dataProvider = "UserData")
    public void LoginNegative(String username, String password, String rememberMe) {
        baseURI = "https://test.mersys.io/";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", username);
        userCredential.put("password", password);
        userCredential.put("rememberMe", rememberMe);

        Cookies cookies =
                given()
                        .body(userCredential)
                        .contentType(ContentType.JSON)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(401)
                        .log().body()
                        .body("type", containsStringIgnoringCase("invalid_username_password"))
                        .body("title", containsStringIgnoringCase("Invalid username or password"))
                        .extract().response().detailedCookies()
                ;
    }
    @Test
    public void LoginPositive() {
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
                        .statusCode(200)
                        .log().body()
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
                        .extract().response().detailedCookies()
                ;
        requestSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build()
        ;
    }
}
