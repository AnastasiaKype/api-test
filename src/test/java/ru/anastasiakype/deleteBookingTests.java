package ru.anastasiakype;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.anastasiakype.dao.BookingdatesRequest;
import ru.anastasiakype.dao.CreateTokenRequest;
import ru.anastasiakype.dao.createAccountRequest;
import com.github.javafaker.Faker;

import java.io.FileInputStream;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

@Severity(SeverityLevel.BLOCKER)
@Story("Удалили бронирование")
@Feature("Тестируем удаленние бронирования")

public class deleteBookingTests extends BaseTest{


    private static CreateTokenRequest request;
    private static createAccountRequest accountRequest;
    private static BookingdatesRequest bookingdatesRequest;
    static Faker faker = new Faker();



    static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");

    static String token;
    String id;
    @BeforeAll
    static void beforeAll() throws IOException {


        request = CreateTokenRequest.builder()
                    .username("admin")
                    .password("password123")
                    .build();

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
                .post("/auth")
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

    @Test
    void deleteTokenPositiveTest() {
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
    void deleteTokenNegativeTest() {
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
    void deleteBookingAuthorizationPositiveTest() {
        given()
                .log()
                .all()
                .header("Authorization", "Basic b3b8e87abd2cf25")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }


}
