package com.github.aloutta.backend;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SmokeTest extends ContainerTest {

  @Test
  void name() {
    RestAssured.given()
        .accept(ContentType.JSON)
        .when()
        .get("/user")
        .then()
        .statusCode(HttpStatus.OK.value());
  }
}
