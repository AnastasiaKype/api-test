package ru.anastasiakype;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.anastasiakype.dao.BookingdatesRequest;
import ru.anastasiakype.dao.CreateTokenRequest;
import ru.anastasiakype.dao.createAccountRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static ru.anastasiakype.deleteBookingTests.faker;
import static ru.anastasiakype.deleteBookingTests.formater;

public class partialUpdateBookingTests {
    static String token;
    String id;
    private static final String PROPERTIES_FILE_PATH = "src/test/application.properties";
    private static CreateTokenRequest request;
    static Properties properties = new Properties();
    private static createAccountRequest accountRequest;
    private static BookingdatesRequest bookingdatesRequest;

    @BeforeAll
    static void beforeAll() throws IOException {
        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();

        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");

        bookingdatesRequest = BookingdatesRequest.builder()
                .checkin(formater.format(faker.date().birthday().getDate()))
                .checkout(formater.format(faker.date().birthday().getDate()))
                .build();
        accountRequest = createAccountRequest.builder()
                .firstname(faker.name().fullName())
                .lastname(faker.name().lastName())
                .totalprice(faker.hashCode())
                .depositpaid(true)
                .bookingdates(bookingdatesRequest)
                .additionalneeds(faker.chuckNorris().fact())
                .build();

        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.not(nullValue())))
                .when()
                .post("/auth")//шаг(и)
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();
    }

    @BeforeEach
    void setUp() {

        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(accountRequest)
                .expect()
                .statusCode(200)
                .when()
                .post("/booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString();
    }

    @AfterEach
    void tearDown() {
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    void updateBookingPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest)
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);


    }

    @Test
    void updateBookingFirstnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withFirstname("ava"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("ava"));
    }
    @Test
    void updateBookingFirstnameNegativeTest() {  //дает поставить числовое значение в имя
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withFirstname("165132"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("165132"));

    }

    @Test
    void updateBookingLastnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withLastname("kass"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("kass"));
    }

    @Test
    void updateBookingLastnameNegativeTest() {   //дает поставить числовое значение в фамилию
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withLastname("16513"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("16513"));
    }

    @Test
    void updateBookingCheckoutNegativeTest() {  //дает поставить дату выезда раньше, чем дата заезда
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withBookingdates(bookingdatesRequest.withCheckout("02-10-1600")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void updateBookingCheckinNegativeTest() {   //дает поставить дату заезда позже, чем дата выезда
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withBookingdates(bookingdatesRequest.withCheckin("02-10-2018")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void updateBookingdepositpaidPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withDepositpaid(false))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void updateBookingdepositpaidNegativeTest() {  // дает подставить null  в депозит
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withDepositpaid(null))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }
    @Test
    void updateBookingTotalpriceNegativeTest() {  //дает подставить отрицательное значение
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withTotalprice(-5896))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void updateBookingNamesOutPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withFirstname("Asya").withLastname("Petrova"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void updateBookingCheckoutWithChekinNegativeTest() {  //дает поставить дату выезда раньше, чем дата заезда
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(accountRequest.withBookingdates(bookingdatesRequest.withCheckout("02-10-1600").withCheckin("04-10-1601")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }
}
