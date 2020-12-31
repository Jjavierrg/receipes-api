package controllers;

import controllers.dto.BaseDto;
import controllers.dto.validators.*;
import interpreter.ExpressionParser;
import interpreter.IParser;
import models.entities.BaseModel;
import models.repositories.BaseRepository;
import play.api.http.MediaRange;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;
import services.SearchService;

import javax.inject.Inject;
import javax.validation.groups.Default;
import java.lang.reflect.MalformedParametersException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public abstract class BaseController<T extends BaseModel, TDto extends BaseDto> extends Controller {

    @Inject
    protected FormFactory formFactory;
    protected BaseRepository<T> repository;
    private Class<T> type;
    private Class<TDto> typeDto;

    protected BaseController(Class<T> type, Class<TDto> typeDto) {
        this.type = type;
        this.typeDto = typeDto;
        this.repository = new BaseRepository<>(type);
    }

    public Result getAll(Http.Request request) {
        var searchService = new SearchService(getParser());
        try {
            var query = searchService.EvaluateFilters(request, this.repository);
            var entities = this.repository.findAll(query);
            return this.getResult(request, entities, OK);
        } catch (MalformedParametersException|IndexOutOfBoundsException e) {
            return badRequest("Malformed Query string");
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public Result getSingle(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        var entity = this.repository.getById(id);
        if (entity == null)
            return notFound();

        return this.getResult(request, entity, OK);
    }

    public Result create(Http.Request request) {
        var form = this.formFactory.form(this.typeDto, IPostValidator.class, Default.class).bindFromRequest(request);
        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        var dto = form.get();
        var transaction = this.repository.beginTransaction();

        try {
            var entity = this.toEntity(dto);
            var result = this.repository.insert(entity);
            transaction.commit();

            return this.getResult(request, result, CREATED);
        } catch (Exception e) {
            transaction.rollback();
            return internalServerError(e.getMessage());
        } finally {
            transaction.end();
        }
    }

    public Result update(Http.Request request, long id) {
        return this.updateInternal(request, id, (x) -> this.repository.update(x), IPutValidator.class, Default.class);
    }

    public Result updatePartial(Http.Request request, long id) {
        return this.updateInternal(request, id, (x) -> this.repository.updatePartial(x), IPatchValidator.class, Default.class);
    }

    public Result delete(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        if (!deletionAllowed(id))
            return status(METHOD_NOT_ALLOWED);

        if (!canDelete(id))
            return status(CONFLICT, "Entity already in use");

        if (!this.repository.existId(id))
            return notFound();

        var transaction = this.repository.beginTransaction();
        try {
            if (!this.repository.deleteById(id))
                throw new Exception("Cannot delete selected entity");

            transaction.commit();
            return noContent();
        } catch (Exception e) {
            transaction.rollback();
            return internalServerError(e.getMessage());
        } finally {
            transaction.end();
        }
    }

    public boolean deletionAllowed(long id) { return true; }
    public boolean canDelete(long id) { return true; }

    protected IParser getParser() { return new ExpressionParser(); }

    protected abstract TDto toDto(T entity);
    protected abstract T toEntity(TDto dto);
    protected abstract Content getXMLListContent(List<TDto> list);
    protected abstract Content getXMLEntityContent(TDto entity);

    protected List<TDto> toDto(List<T> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    protected List<T> toEntity(List<TDto> entities) {
        return entities.stream().map(this::toEntity).collect(Collectors.toList());
    }

    protected Result getResult(Http.Request request, List<T> entities, int statusCode) {
        var dtos = this.toDto(entities);
        return this.getResultInternal(request, dtos, statusCode);
    }

    protected Result getResult(Http.Request request, T entity, int statusCode) {
        var dto = this.toDto(entity);
        return this.getResultInternal(request, dto, statusCode);
    }

    private Result updateInternal(Http.Request request, long id, Function<T, T> updateFunc, Class<?>... validationGroups) {
        if (id <= 0)
            return badRequest();

        if (!this.repository.existId(id))
            return notFound();

        var form = this.formFactory.form(this.typeDto, validationGroups).bindFromRequest(request);

        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        var dto = form.get();
        if (dto.id != id)
            return badRequest();

        var transaction = this.repository.beginTransaction();
        try {
            var entity = this.toEntity(dto);
            updateFunc.apply(entity);
            transaction.commit();

            return noContent();
        } catch (Exception e) {
            transaction.rollback();
            return internalServerError(e.getMessage());
        } finally {
            transaction.end();
        }
    }

    private Result getResultInternal(Http.Request request, Object data, int statusCode) {
        var type = this.getFirstAcceptedType(request);
        if (type.equals("application/xml"))
        {
            var content = data instanceof List<?> ? this.getXMLListContent((List<TDto>) data) : this.getXMLEntityContent((TDto) data);
            return status(statusCode, content);
        }

        return status(statusCode, Json.toJson(data));
    }

    private String getFirstAcceptedType(Http.Request request) {
        for (MediaRange type: request.acceptedTypes()) {
            if (type.toString().equals("*/*"))
                continue;
            if (type.accepts("application/json"))
                return "application/json";
            else if (type.accepts("application/xml"))
                return "application/xml";
        }

        return "application/json";
    }
}
