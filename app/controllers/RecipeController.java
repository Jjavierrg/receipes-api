package controllers;

import models.entities.Recipe;
import play.mvc.*;

public class RecipeController extends BaseController<Recipe> {

    protected RecipeController() {
        super(Recipe.class);
    }

//    public Result GetUser(int id) {
//        var user = this.GetUserById(id);
//        if (!user.isPresent())
//            return notFound();
//
//        // return this.GetResponse(ok(Json.toJson(user)));
//        return this.GetResponse(ok(views.xml.user.render(user.get())));
//    }
//
//    public Result CreateUser(Http.Request request) {
//        var form = this.formFactory.form(User.class).bindFromRequest(request);
//
//        // var json = request.body().asJson();
//        // var user = Json.fromJson(json, User.class);
//        var user = form.get();
//
//        if (!form.hasErrors())
//            return badRequest(form.errorsAsJson());
//
//        if (!this.IsValid(user))
//            return badRequest();
//
//        if (this.NickAlreadyExist(user.getNick()))
//            return Results.status(CONFLICT);
//
//        this.storage.users.add(user);
//
//        return this.GetResponse(created(Json.toJson(user)));
//    }
//
//    public Result UpdateUser(Http.Request request, int id) {
//        var user = this.GetUserById(id);
//        if (!user.isPresent())
//            return notFound();
//
//        var json = request.body().asJson();
//        var newValues = Json.fromJson(json, User.class);
//
//        if (!this.IsValid(newValues))
//            return badRequest();
//
//        if (this.NickAlreadyExist(newValues.getNick(), id))
//            return Results.status(CONFLICT);
//
//        var idx = this.storage.users.indexOf(user.get());
//        this.storage.users.remove(user.get());
//        this.storage.users.add(idx, newValues);
//
//        return this.GetResponse(noContent());
//    }
//
//    public Result DeleteUser(int id) {
//        var user = this.GetUserById(id);
//
//        if (!user.isPresent())
//            return notFound();
//
//        this.storage.users.remove(user.get());
//        return this.GetResponse(noContent());
//    }
//
//    private boolean IsValid(User user) {
//        if (user == null)
//            return false;
//
//        if (user.getName() == null || user.getNick() == null || user.getAge() <= 0)
//            return false;
//
//        return true;
//    }
//
//    private Result GetResponse(Result baseResponse) {
//        return baseResponse.withHeader("X-User-Count", String.valueOf(this.storage.users.size()));
//    }
//
//    private boolean NickAlreadyExist(String nick) {
//        return this.NickAlreadyExist(nick, 0);
//    }
//
//    private boolean NickAlreadyExist(String nick, int excludeId) {
//        return this.storage.users.stream()
//                .filter(u -> u.getNick().equals(nick) && u.id != excludeId)
//                .findFirst()
//                .isPresent();
//    }
//
//    private Optional<User> GetUserById(int id) {
//        if (id == 0)
//            return Optional.empty();
//
//        return this.storage.users.stream()
//                .filter(u -> u.id == id)
//                .findFirst();
//    }

}
