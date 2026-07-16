package com.testautomation.apitesting.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import listener.RestAssuredListener;
import com.testautomation.apitesting.POJOs.Booking;
import com.testautomation.apitesting.POJOs.BookingDates;
import utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DataDrivenTestingUsingExcelFile {

    @Test (dataProvider = "ExcelmaptestData")
    public void DataDrivenTesting(Map<String,String> maptestData) {

        int totalprice = Integer.parseInt(maptestData.get("TotalPrice"));

        try {
            BookingDates bookingDates = new BookingDates("2023-03-25", "2023-03-30");
            Booking booking = new Booking(maptestData.get("FirstName"), maptestData.get("LastName"), "breakfast", totalprice, true, bookingDates);

            //serialization
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

            Response response =
                    RestAssured
                            .given().filter(new RestAssuredListener())
                            .contentType(ContentType.JSON)
                            .body(requestBody)
                            .baseUri("https://restful-booker.herokuapp.com/booking")
                            .when()
                            .post()
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .extract()
                            .response();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Excel File
    //     ↓
    //Fillo Query
    //     ↓
    //Recordset
    //     ↓
    //Row 1 → Map
    //Row 2 → Map
    //Row n → Map
    //     ↓
    //List<Map<String,String>>
    //     ↓
    //Object[][]
    //     ↓
    //TestNG DataProvider
    //     ↓
    //Multiple Test Executions

    @DataProvider(name = "ExcelmaptestData")
    public Object[][] getmaptestData(){

        String query = "select * from Sheet1 where Run='Yes'";

        Object[][] objArray = null;
        Map<String,String> maptestData = null;
        List<Map<String,String>> listtestData = null;

        Fillo fillo = new Fillo(); // use to read query
        Connection connection = null; // like DB connection , excel connection will be made
        Recordset recordset = null; // read all rows

        try {
            connection = fillo.getConnection(FileNameConstants.EXCEL_TEST_DATA);
            recordset = connection.executeQuery(query);

            listtestData = new ArrayList<Map<String,String>>();

            while(recordset.next()) {
                maptestData = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);

                for (String field : recordset.getFieldNames()) { // read all column names
                    maptestData.put(field, recordset.getField(field));
                }

                listtestData.add(maptestData);
            }

            objArray = new Object[listtestData.size()][1];

            for (int i = 0; i < listtestData.size(); i++) {
                objArray[i][0] = listtestData.get(i);
            }

        } catch (FilloException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return objArray;
    }
}












