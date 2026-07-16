package listener;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.logging.LogRecord;

public class RestAssuredListener implements Filter {

    private static final Logger logger = LogManager.getLogger(RestAssuredListener.class);

    @Override
    //To print EndPoint method with URL + request response in Log use this TestNG listener
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response r = ctx.next(requestSpec, responseSpec);

     //   if (r.getStatusCode() != 200 || r.getStatusCode() != 201) {
            logger.info(
                    "\n Method =>" + requestSpec.getMethod() +
                            "\n URI => " + requestSpec.getURI() +
                            "\n Request body => " + requestSpec.getBody() +
                            "\n Response body => " + r.getBody().prettyPrint());

      //      }
            return r;

        }
    }
