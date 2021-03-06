package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.dto.BaseDto;
import io.ebean.Ebean;
import org.junit.Test;
import org.xml.sax.InputSource;
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

public abstract class ControllerTest<T extends BaseDto> extends WithApplication {
    protected String singleRoute;
    protected String generalRoute;
    protected Application app;

    public ControllerTest(String singleRoute, String generalRoute) {
        this.singleRoute = singleRoute;
        this.generalRoute = generalRoute;
        this.initializeApp();
    }

    private void initializeApp(){
        this.app =fakeApplication(inMemoryDatabase());

        // Feed Database
        try {
            var stream = getClass().getResourceAsStream("/Seeder.sql");
            var script = readStream(stream);

            Ebean.execute(Ebean.createCallableSql(script));
        } catch (IOException e) { }
    }

    public abstract T getValidDto();
    public abstract T getInvalidDto();

    public Long getIdForGetOperation() { return 1L; };
    public Long getIdForPutOperation() { return 1L; };
    public Long getIdForPatchOperation() { return 1L; };
    public Long getIdForDeleteOperation() { return 1L; };

    @Override
    protected Application provideApplication() {
        return  this.app;
    }

    @Test
    public void shouldGetAll() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/json")
                .method(GET).uri(this.generalRoute);

        Result result = route(app, request);
        validateJsonResponse(result, OK, true);
    }

    @Test
    public void shouldGetAllAsXML() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/xml")
                .method(GET).uri(this.generalRoute);

        Result result = route(app, request);
        validateXmlResponse(result, OK);
    }

    @Test
    public void shouldGetOne() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/json")
                .method(GET).uri(this.singleRoute + "/1");

        Result result = route(app, request);
        validateJsonResponse(result, OK, false);
    }

    @Test
    public void shouldGetOneAsXML() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/xml")
                .method(GET).uri(this.singleRoute + "/1");

        Result result = route(app, request);
        validateXmlResponse(result, OK);
    }

    @Test
    public void shouldDelete() {
        var id = this.getIdForDeleteOperation().toString();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/xml")
                .method(DELETE).uri(this.singleRoute + "/" + id);

        Result result = route(app, request);
        assertEquals(NO_CONTENT, result.status());
    }

    @Test
    public void shouldNotDeleteNotFoundId() {
        var id = this.getIdForDeleteOperation().toString();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/xml")
                .method(DELETE).uri(this.singleRoute + "/9999");

        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void shouldNotCreateInvalidBody() {
        var dto = this.getInvalidDto();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute)
                .method(POST)
                .bodyJson(jsonDto);

        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void shouldCreate() {
        var dto = this.getValidDto();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute)
                .method(POST)
                .bodyJson(jsonDto);

        Result result = route(app, request);
        var id = validateJsonResponse(result, CREATED, false);
        assertTrue(id > 0);
    }

    @Test
    public void shouldCreateAndReturnAsXML() {
        var dto = this.getValidDto();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .header("Accept", "application/xml")
                .uri(this.singleRoute)
                .method(POST)
                .bodyJson(jsonDto);

        Result result = route(app, request);
        validateXmlResponse(result, CREATED);
    }

    @Test
    public void shouldNotUpdateInvalidBody() {
        var dto = this.getInvalidDto();
        dto.id = this.getIdForPutOperation();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute + "/" + dto.id.toString())
                .method(PUT)
                .bodyJson(jsonDto);

        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void shouldUpdate() {
        var dto = this.getValidDto();
        dto.id = this.getIdForPutOperation();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute + "/" + dto.id.toString())
                .method(PUT)
                .bodyJson(jsonDto);

        Result result = route(app, request);
        assertEquals(NO_CONTENT, result.status());
    }

    @Test
    public void shouldNotUpdatePartialInvalidBody() {
        var dto = this.getInvalidDto();
        dto.id = this.getIdForPatchOperation();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute + "/" + dto.id.toString())
                .method("PATCH")
                .bodyJson(jsonDto);

        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void shouldUpdatePartial() {
        var dto = this.getValidDto();
        dto.id = this.getIdForPatchOperation();
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute + "/" + dto.id.toString())
                .method("PATCH")
                .bodyJson(jsonDto);

        Result result = route(app, request);
        assertEquals(NO_CONTENT, result.status());
    }

    /// VALIDATIONS AND HELPERS
    protected int validateJsonResponse(Result result, int expectedResponseCode, boolean requiredArray) {
        assertEquals(expectedResponseCode, result.status());

        var body = contentAsString(result);
        var jsonObject = this.getJsonObject(body);

        // Validate is Json
        assertTrue(jsonObject.isPresent());

        // Validate Type
        var validType = requiredArray ? jsonObject.get().isArray() : jsonObject.get().isObject();
        assertTrue(validType);

        //return Id Value if object
        return requiredArray ? -1 : jsonObject.get().get("id").intValue();
    }

    protected void validateXmlResponse(Result result, int expectedResponseCode) {
        assertEquals(expectedResponseCode, result.status());

        var body = contentAsString(result);
        assertTrue(isXML(body));
    }

    protected boolean isXML(String string) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            var is = new InputSource(new StringReader(string));
            var doc = builder.parse(is);

            return  doc != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected Optional<JsonNode> getJsonObject(String string) {
        try {
            var stream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
            return Optional.ofNullable(Json.parse(stream));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String readStream(InputStream in) throws IOException {
        var input = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = input.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

}
