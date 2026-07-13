package com.testautomation.apitesting.tests.DELETE;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.utils.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.FileNameConstants;

import java.io.File;
import java.io.IOException;

import static utils.FileNameConstants.PATCH_API_REQUEST_BODY;
import static utils.FileNameConstants.POST_API_REQUEST_BODY;

public class EndToEndAPITestt extends BaseTest {

    @Test
    public void deleteAPIRequest() {

        try {

            File file = new File(POST_API_REQUEST_BODY);
            System.out.println(file.getAbsolutePath());
            System.out.println(file.exists());
            File file1 = new File(PATCH_API_REQUEST_BODY);
            System.out.println(file1.getAbsolutePath());
            System.out.println(file1.exists());

            String postAPIRequestBody = FileUtils.readFileToString(new File(POST_API_REQUEST_BODY),"UTF-8");

            String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY),"UTF-8");

            String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),"UTF-8");

            String patchAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PATCH_API_REQUEST_BODY),"UTF-8");

            //post api call
            Response response =
                    RestAssured
                            .given()
                            .contentType(ContentType.JSON)
                            .body(postAPIRequestBody)
                            .baseUri("https://restful-booker.herokuapp.com/booking")
                            .when()
                            .post()
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .extract()
                            .response();

            System.out.println("POST "+response.path("booking.firstname"));

            JSONArray jsonArray = JsonPath.read(response.body().asString(),"$.booking..firstname");
            String firstName = (String) jsonArray.get(0);

            Assert.assertEquals(firstName, "api testing");

            int bookingId = JsonPath.read(response.body().asString(),"$.bookingid");
            System.out.println("BookingID "+bookingId);

            //get api call
            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                    .get("/{bookingId}",bookingId)
                    .then()
                    .assertThat()
                    .statusCode(200);

            //token generation
            Response tokenAPIResponse =
                    RestAssured
                            .given()
                            .contentType(ContentType.JSON)
                            .body(tokenAPIRequestBody)
                            .baseUri("https://restful-booker.herokuapp.com/auth")
                            .when()
                            .post()
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .extract()
                            .response();

            String token = JsonPath.read(tokenAPIResponse.body().asString(),"$.token");

            //put api call
            Response rpt=RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(putAPIRequestBody)
                    .header("Cookie", "token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                    .put("/{bookingId}",bookingId)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("PutUpdate"))
                    .body("lastname", Matchers.equalTo("Selenium C#"))
                    .extract()
                    .response();

            System.out.println("PUT  "+rpt.path("firstname"));

            //patch api call
            Response rp= RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(patchAPIRequestBody)
                    .header("Cookie", "token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                    .patch("/{bookingId}",bookingId)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname", Matchers.equalTo("PatchUpdate"))
                    .extract()
                    .response();

            System.out.println("Patch "+rp.path("firstname"));

            //delete api
            Response rpd = RestAssured
                    .given()
                    .header("Cookie", "token=" + token)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                    .delete("/{bookingId}", bookingId)
                    .then()
                    .statusCode(201)
                    .extract()
                    .response();

            Assert.assertEquals(rpd.asString(), "Created");

            System.out.println("Delete Response : " + rpd.asString());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}