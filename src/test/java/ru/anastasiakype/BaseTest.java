package ru.anastasiakype;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import ru.anastasiakype.dao.BookingdatesRequest;
import ru.anastasiakype.dao.CreateTokenRequest;
import ru.anastasiakype.dao.CreateTokenResponse;
import ru.anastasiakype.dao.createAccountRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class BaseTest {

    protected static CreateTokenRequest request;
    protected static CreateTokenResponse tokenResponse;
    protected static createAccountRequest accountRequest;
    protected static BookingdatesRequest bookingdatesRequest;
    private static String PROPERTIES_FILE_PATH = "src/test/application.properties";
    static private String baseUrl;


    static Properties properties = new Properties();
    protected static Faker faker = new Faker();
    protected String id;


    @BeforeAll
    @Step("Data preparation")
    static void beforeAllTests() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());


        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");


    }
}
