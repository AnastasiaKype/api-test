package ru.anastasiakype;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.anastasiakype.dao.BookingDatesRequest;
import ru.anastasiakype.dao.CreateTokenRequest;
import ru.anastasiakype.dao.CreateAccountRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static ru.anastasiakype.DeleteBookingTests.faker;
import static ru.anastasiakype.DeleteBookingTests.formater;

public class PartialUpdateBookingTests {
    static String token;
    String id;
    private static final String PROPERTIES_FILE_PATH = "src/test/application.properties";
    private static CreateTokenRequest request;
    static Properties properties = new Properties();
    private static CreateAccountRequest accountRequest;
    private static BookingDatesRequest bookingdatesRequest;

    @BeforeAll
    static void BeforeAll() throws IOException {
        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();

        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");

        bookingdatesRequest = BookingDatesRequest.builder()
                .checkin(formater.format(faker.date().birthday().getDate()))
                .checkout(formater.format(faker.date().birthday().getDate()))
                .build();
        accountRequest = CreateAccountRequest.builder()
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
    void SetUp() {

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
    void TearDown() {
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
    void UpdateBookingPositiveTest() {
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
    void UpdateBookingFirstnamePositiveTest() {
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
    void UpdateBookingFirstnameNegativeTest() {  //дает поставить числовое значение в имя
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
    void UpdateBookingLastnamePositiveTest() {
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
    void UpdateBookingLastnameNegativeTest() {   //дает поставить числовое значение в фамилию
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
    void UpdateBookingCheckoutNegativeTest() {  //дает поставить дату выезда раньше, чем дата заезда
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
    void UpdateBookingCheckinNegativeTest() {   //дает поставить дату заезда позже, чем дата выезда
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
    void UpdateBookingdepositpaidPositiveTest() {
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
    void UpdateBookingdepositpaidNegativeTest() {  // дает подставить null  в депозит
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
    void UpdateBookingTotalpriceNegativeTest() {  //дает подставить отрицательное значение
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
    void UpdateBookingNamesOutPositiveTest() {
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
    void UpdateBookingCheckoutWithChekinNegativeTest() {  //дает поставить дату выезда раньше, чем дата заезда
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
