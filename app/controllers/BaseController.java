package controllers;

import models.entities.BaseModel;
import models.repositories.BaseRepository;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;


public class BaseController<T extends BaseModel> extends Controller {

    @Inject
    protected FormFactory formFactory;
    protected BaseRepository<T> repository;
    private Class<T> type;

    protected BaseController(Class<T> type) {
        this.type = type;
        this.repository = new BaseRepository<>(type);
    }

    public Result getAll(Http.Request request) {
        var entities = this.repository.findAll();
        return ok(Json.toJson(entities));
    }

    public Result getSingle(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        var entity = this.repository.getById(id);
        if (entity == null)
            return notFound();

        return ok(Json.toJson(entity));
    }

    public Result create(Http.Request request) {
        var form = this.formFactory.form(this.type).bindFromRequest(request);
        var entity = form.get();

        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        var result = this.repository.insert(entity);
        return created(Json.toJson(result));
    }

    public Result update(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        if (!this.repository.existId(id))
            return notFound();

        var form = this.formFactory.form(this.type).bindFromRequest(request);
        var entity = form.get();

        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        if (entity.id != id)
            return badRequest();

        this.repository.update(entity);
        return noContent();
    }

    public Result updatePartial(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        if (!this.repository.existId(id))
            return notFound();

        var form = this.formFactory.form(this.type).bindFromRequest(request);
        var entity = form.get();

        if (form.hasErrors())
            return badRequest(form.errorsAsJson());

        if (entity.id != id)
            return badRequest();

        var result = this.repository.updatePartial(entity, id);
        return noContent();
    }

    public Result delete(Http.Request request, long id) {
        if (id <= 0)
            return badRequest();

        if (!this.repository.existId(id))
            return notFound();

        if (!this.repository.deleteById(id))
            return internalServerError();

        return noContent();
    }
}
