package ru.anastasiakype;

import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.anastasiakype.dao.BookingDatesRequest;
import ru.anastasiakype.dao.CreateAccountRequest;
import ru.anastasiakype.dao.CreateTokenRequest;
import ru.anastasiakype.dao.CreateAccountRequest;
import com.github.javafaker.Faker;

import java.io.FileInputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;



public class DeleteBookingTests {

    private static final String PROPERTIES_FILE_PATH = "src/test/application.properties";
    static Properties properties = new Properties();
    private static CreateTokenRequest request;
    private static CreateAccountRequest accountRequest;
    private static BookingDatesRequest bookingdatesRequest;
    static Faker faker = new Faker();



    static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");

    static String token;
    String id;
    @BeforeAll
    static void BeforeAll() throws IOException {

        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");



        request = CreateTokenRequest.builder()
                    .username("admin")
                    .password("password123")
                    .build();

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
                .post("/auth")
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

    @Test
    void DeleteTokenPositiveTest() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    void DeleteTokenNegativeTest() {
        given()
                .log()
                .all()
                .header("Cookie", "token=" + token + 1)
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }

    @Test
    void DeleteBookingAuthorizationPositiveTest() {
        given()
                .log()
                .all()
                .header("Authorization", "token")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }


}
