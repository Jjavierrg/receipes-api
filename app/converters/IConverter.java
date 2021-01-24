package converters;

import controllers.dto.BaseDto;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

public interface IConverter<TDto extends BaseDto> {
    /**
     * Get Http content response from a {@link List<TDto>}
     *
     * @param statusCode the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
     * @param list       {@link List<TDto>} of entities used for a http response
     * @return Http content response
     */
    Result toResult(int statusCode, List<TDto> list);

    /**
     * Get Http content response from a {@link TDto}
     *
     * @param statusCode the HTTP status for this result e.g. 200 (OK), 404 (NOT_FOUND)
     * @param entity     {@link TDto} entity used for a http response
     * @return Http content response
     */
    Result toResult(int statusCode, TDto entity);

    /**
     * Get a {@link TDto} from http request body
     *
     * @param body {@link Http.RequestBody} request body content
     * @return {@link TDto} entity from request body content
     */
    TDto toEntity(Http.RequestBody body);
}
