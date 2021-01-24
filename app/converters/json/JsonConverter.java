package converters.json;

import controllers.dto.BaseDto;
import converters.IConverter;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

import static play.mvc.Results.status;

public class JsonConverter<TDto extends BaseDto> implements IConverter<TDto> {
    private Class<TDto> dtoClass;

    public JsonConverter(Class<TDto> dtoClass) {
        this.dtoClass = dtoClass;
    }

    @Override
    public Result toResult(int statusCode, List<TDto> list) {
        return status(statusCode, Json.toJson(list));
    }

    @Override
    public Result toResult(int statusCode, TDto entity) {
        return status(statusCode, Json.toJson(entity));
    }

    @Override
    public TDto toEntity(Http.RequestBody body) {
        return Json.fromJson(body.asJson(), this.dtoClass);
    }
}
