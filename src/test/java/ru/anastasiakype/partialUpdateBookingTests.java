package ru.anastasiakype;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class partialUpdateBookingTests {
    static String token;
    String id;

    @BeforeAll
    static void beforeAll() {
        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body("{\n"
                        + "    \"username\" : \"admin\",\n"
                        + "    \"password\" : \"password123\"\n"
                        + "}")
                .expect()
                .statusCode(200)
                .body("token", is(CoreMatchers.not(nullValue())))
                .when()
                .post("https://restful-booker.herokuapp.com/auth")//шаг(и)
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
                .body("{\n"
                        + "    \"firstname\" : \"Jim\",\n"
                        + "    \"lastname\" : \"Brown\",\n"
                        + "    \"totalprice\" : 111,\n"
                        + "    \"depositpaid\" : true,\n"
                        + "    \"bookingdates\" : {\n"
                        + "        \"checkin\" : \"2018-01-01\",\n"
                        + "        \"checkout\" : \"2019-01-01\"\n"
                        + "    },\n"
                        + "    \"additionalneeds\" : \"Breakfast\"\n"
                        + "}")
                .expect()
                .statusCode(200)
                .when()
                .post("https://restful-booker.herokuapp.com/booking")
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
                .delete("https://restful-booker.herokuapp.com/booking/" + id)
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
                .body("{\n" +
                        "    \"firstname\" : \"Nastya\",\n" +
                        "    \"lastname\" : \"Kype\",\n" +
                        "    \"totalprice\" : 123,\n" +
                        "    \"depositpaid\" : false,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2022-09-12\",\n" +
                        "        \"checkout\" : \"2022-09-20\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"allinclusive\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Nastya"))
                .body("lastname", equalTo("Kype"))
                .body("totalprice", equalTo(Integer.valueOf("123")))
                .body("depositpaid", equalTo(Boolean.valueOf("false")))
                .body("bookingdates.checkin", is(CoreMatchers.equalTo("2022-09-12")))
                .body("bookingdates.checkout", is(CoreMatchers.equalTo("2022-09-20")))
                .body("additionalneeds", equalTo("allinclusive"));
    }

    @Test
    void updateBookingFirstnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"Anastasia\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Anastasia"));
    }
    @Test
    void updateBookingFirstnameNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"1656584\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("1656584"));

    }

    @Test
    void updateBookingLastnamePositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"lastname\" : \"Eremina\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Eremina"));
    }

    @Test
    void updateBookingLastnameNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"lastname\" : \"16513\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("16513"));
    }

    @Test
    void updateBookingCheckoutNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkout\" : \"2021-09-22\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);

    }

    @Test
    void updateBookingCheckinNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2022-09-22\",\n" +
                        "         \"checkout\" : \"2021-09-05\"\n"+
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);

    }

    @Test
    void updateBookingdepositpaidPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"depositpaid\" : \"true\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @Test
    void updateBookingdepositpaidNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"depositpaid\" : \"null\"\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200);

    }
    @Test
    void updateBookingTotalpriceNegativeTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"totalprice\" : -589,\n" +
                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);

    }

    @Test
    void updateBookingNamesOutPositiveTest() {
        given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body("{\n" +
                        "    \"firstname\" : \"\",\n" +
                        "    \"lastname\" : \"\",\n" +

                        "}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(400);

    }


}
