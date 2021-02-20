package controllers;

import controllers.dto.BaseDto;
import controllers.dto.validators.*;
import converters.ConverterFactory;
import interpreter.ExpressionParser;
import interpreter.IParser;
import mappers.IMapper;
import models.entities.BaseModel;
import models.repositories.BaseRepository;
import models.repositories.RepositoryFactory;
import play.api.http.MediaRange;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.SearchService;

import javax.inject.Inject;
import javax.validation.groups.Default;
import java.lang.reflect.MalformedParametersException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic base class for controllers with basic CRUD functionality
 *
 * @param <T>    Model entity associated to controller
 * @param <TDto> DTO representation for model entity
 */
public class BaseController<T extends BaseModel, TDto extends BaseDto> extends Controller {
    @Inject
    protected FormFactory formFactory;
    @Inject
    protected ConverterFactory converterFactory;
    @Inject
    protected RepositoryFactory repositoryFactory;

    protected IMapper<T, TDto> mapper;

    private BaseRepository<T> repository;
    private Class<T> type;
    private Class<TDto> typeDto;

    protected BaseController(Class<T> type, Class<TDto> typeDto, IMapper<T, TDto> mapper) {
        this.type = type;
        this.typeDto = typeDto;
        this.mapper = mapper;
    }

    public BaseRepository<T> getRepository() {
        if (this.repository == null)
            this.repository = this.repositoryFactory.getRepository(this.type);

        return this.repository;
    }

    /**
     * Get all entities and apply query params operations
     *
     * @param request Request made by client
     * @return Ok with a {@link List<TDto>} representation (according to content negotiation) if success
     */
    public Result getAll(Http.Request request) {
        var searchService = new SearchService(getParser());
        try {
            var query = searchService.EvaluateFilters(request, this.getRepository());
            var entities = this.getRepository().findAll(query);
            return this.getResult(request, entities, OK);
        } catch (MalformedParametersException | IndexOutOfBoundsException e) {
            return badRequest("Malformed Query string");
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    /**
     * Get the entity with the desired Id
     *
     * @param request Request made by client
     * @param id      identification number for desired entity
     * @return Ok with {@link TDto} representation (according to content negotiation) if success
     */
    public Result getSingle(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        var entity = this.getRepository().getById(id);
        if (entity == null)
            return notFound();

        return this.getResult(request, entity, OK);
    }

    /**
     * Create operation for new entities according to request body content
     *
     * @param request Request made by client
     * @return Ok with created status code if success
     */
    public Result create(Http.Request request) {
        var form = this.formFactory.form(this.typeDto, IPostValidator.class, Default.class).bindFromRequest(request);

        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        var dto = form.get();
        var transaction = this.getRepository().beginTransaction();

        try {
            var entity = this.mapper.toEntity(dto);
            var result = this.getRepository().insert(entity);
            transaction.commit();

            return this.getResult(request, result, CREATED);
        } catch (Exception e) {
            transaction.rollback();
            return internalServerError(e.getMessage());
        } finally {
            transaction.end();
        }
    }

    /**
     * Update entire entity according to request body content
     *
     * @param request Request made by client
     * @param id      identification number for desired entity
     * @return Ok with no content status code if success
     */
    public Result update(Http.Request request, long id) {
        return this.updateInternal(request, id, (x) -> this.getRepository().update(x), IPutValidator.class, Default.class);
    }

    /**
     * Update only specified entity properties according to request body content
     *
     * @param request Request made by client
     * @param id      identification number for desired entity
     * @return Ok with no content status code if success
     */
    public Result updatePartial(Http.Request request, long id) {
        return this.updateInternal(request, id, (x) -> this.getRepository().updatePartial(x), IPatchValidator.class, Default.class);
    }

    /**
     * Check for deletion and delete the desired entity
     *
     * @param request Request made by client
     * @param id      identification number for desired entity
     * @return Ok with no content status code if success
     */
    public Result delete(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        if (!deletionAllowed())
            return status(METHOD_NOT_ALLOWED);

        if (!canDelete(id))
            return status(CONFLICT, "Entity already in use");

        if (!this.getRepository().existId(id))
            return notFound();

        var transaction = this.getRepository().beginTransaction();
        try {
            if (!this.getRepository().deleteById(id))
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

    /**
     * Check if deletion operation is enabled for desired endpoint
     *
     * @return true if deletion is permitted. Otherwise false.
     */
    public boolean deletionAllowed() {
        return true;
    }

    /**
     * Check if deletion operation is enabled for desired entity
     *
     * @return true if deletion is permitted. Otherwise false.
     */
    public boolean canDelete(long id) {
        return true;
    }

    /**
     * Get filter params parser for current endpoint
     */
    protected IParser getParser() {
        return new ExpressionParser(this.mapper.getFieldRelations());
    }

    /**
     * Convert operation from {@link List<T>} to {@link List<TDto>}
     *
     * @param entities {@link List<T>} to be converted
     * @return {@link List<TDto>} with entity information
     */
    protected List<TDto> toDto(List<T> entities) {
        return entities.stream().map((x) -> this.mapper.toDto(x)).collect(Collectors.toList());
    }

    /**
     * Convert operation from {@link List<TDto>} to {@link List<T>}
     *
     * @param entities {@link List<TDto>} to be converted
     * @return {@link List<T>} with entity information
     */
    protected List<T> toEntity(List<TDto> entities) {
        return entities.stream().map((x) -> this.mapper.toEntity(x)).collect(Collectors.toList());
    }

    /**
     * Get client result according to content negotiation
     *
     * @param request    Request made by client
     * @param entities   List of entities to include in response
     * @param statusCode Status code sent to client
     * @return Response with information included in desired format
     */
    protected Result getResult(Http.Request request, List<T> entities, int statusCode) {
        var dtos = this.toDto(entities);
        return this.getResultInternal(request, dtos, statusCode);
    }

    /**
     * Get client result according to content negotiation
     *
     * @param request    Request made by client
     * @param entity     Entity to include in response
     * @param statusCode Status code sent to client
     * @return Response with information included in desired format
     */
    protected Result getResult(Http.Request request, T entity, int statusCode) {
        var dto = this.mapper.toDto(entity);
        return this.getResultInternal(request, dto, statusCode);
    }

    /**
     * Validate request and perform update operation
     *
     * @param request          Request made by client
     * @param id               identification number for desired entity
     * @param updateFunc       update function to be applied to {@link T} (total or partial)
     * @param validationGroups groups to include during entity validation
     * @return Ok with no content status code if success
     */
    private Result updateInternal(Http.Request request, long id, Function<T, T> updateFunc, Class<?>... validationGroups) {
        if (id <= 0)
            return badRequest();

        if (!this.getRepository().existId(id))
            return notFound();

        var form = this.formFactory.form(this.typeDto, IPutValidator.class, Default.class).bindFromRequest(request);

        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        var dto = form.get();
        if (dto.id != id)
            return badRequest();

        var transaction = this.getRepository().beginTransaction();
        try {
            var entity = this.mapper.toEntity(dto);
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

    /**
     * Evaluate client request and set information in the requested format (json by default)
     *
     * @param request    Request made by client
     * @param data       Information to include in response
     * @param statusCode Status code sent to client
     * @return Response with information included in desired format
     */
    private Result getResultInternal(Http.Request request, Object data, int statusCode) {
        var type = this.getFirstAcceptedType(request);
        var converter = this.converterFactory.getConverter(this.typeDto, type);

        return data instanceof List<?> ? converter.toResult(statusCode, (List<TDto>) data) : converter.toResult(statusCode, (TDto) data);
    }

    /**
     * Get first accepted type according client request. If no type is specified, json is returned
     *
     * @param request Request made by client
     * @return first accepted type
     */
    private String getFirstAcceptedType(Http.Request request) {
        for (MediaRange type : request.acceptedTypes()) {
            if (type.toString().equals("*/*"))
                continue;
            if (type.accepts("application/json"))
                return "application/json";
            else if (type.accepts("application/xml"))
                return "application/xml";
        }

        return "application/json";
    }

    /**
     * Get a instance of {@link Form<TDto>} with the entity passed in the request
     *
     * @param request Http request made by client
     * @return {@link Form<TDto>}
     * @deprecated Validation seems not working
     */
    private Form<TDto> getFormFromRequest(Http.Request request, Class<?>... groups) {
        var requestContentType = request.contentType().orElse("application/json");
        var converter = this.converterFactory.getConverter(this.typeDto, requestContentType);

        var dto = converter.toEntity(request.body());
        return this.formFactory.form(this.typeDto, groups).fill(dto);
    }
}
