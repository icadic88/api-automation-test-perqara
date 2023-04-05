import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class ApiJavaAutomationTest {

  /* API Documentation https://punkapi.com/documentation/v2,
  - Case 1 => Create automation script for this endpoint “https://api.punkapi.com/v2/beers?page=2&per_page=<number of data>”.
  Test this API using <number_of_data> values of 20, 5, and 1
  and then verify that data returned for each example is valid and got status code 200
  - Case 2 => Verify json schema is match with templates/schema.json
  - Case 3 => Print all returned “name” of list that endpoint data */

  public final static String URL = "https://api.punkapi.com";

  List<String> numberOfData = Arrays.asList("20", "5", "1");

  public void beersControllerWithParam(String data) {
    Response response = given().log().all().baseUri(URL).basePath("/").contentType(ContentType.JSON)
        .accept(ContentType.JSON).queryParam("per_page", data)
        .queryParam("page", "2").get("/v2/beers");

    int getArray = response.then().extract().jsonPath().getList("$").size();

    MatcherAssert.assertThat("total array is not equals!", data, Matchers.equalTo(String.valueOf(getArray)));
    Assert.assertEquals(response.getStatusCode(), 200);
    System.out.println("Total array is: " + getArray);
  }

  public Response beersController() {
    Response response = given().log().all().baseUri(URL).basePath("/").contentType(ContentType.JSON)
            .accept(ContentType.JSON).get("/v2/beers");

    return response;
  }

  @Test
  public void verifyDataIsValid() {
    numberOfData.forEach(datas -> { beersControllerWithParam(datas); });
  }

  @Test
  public void verifyJsonSchema() {
    Response result = beersController();
    MatcherAssert.assertThat(result.then().extract().body().asString(),
            matchesJsonSchemaInClasspath("templates/schema.json"));
  }

  @Test
  public void printAllNames() {
    List<List> list = beersController().body().jsonPath().getList("");
    for (int i = 0; i < list.size(); i++) {
      System.out.println(((LinkedHashMap) list.get(i)).get("name"));
    }
  }
}
