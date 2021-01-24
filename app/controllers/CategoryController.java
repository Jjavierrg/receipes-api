package controllers;

import com.google.inject.Inject;
import controllers.dto.CategoryDto;
import interpreter.ExpressionParser;
import interpreter.IParser;
import mappers.IMapper;
import models.entities.*;
import models.repositories.BaseRepository;
import play.twirl.api.Content;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for {@link models.entities.Category} model
 */
public class CategoryController extends BaseController<Category, CategoryDto> {
    @Inject
    protected CategoryController(IMapper<Category, CategoryDto> mapper) {
        super(Category.class, CategoryDto.class, mapper);
    }

    @Override
    public boolean canDelete(long id) {
        // check for related data
        var repo = this.repositoryFactory.getRepository(Recipe.class);
        var existRecipes = repo.finder.query().fetch("categories").where().eq("categories.id", id).exists();
        if (existRecipes)
            return false;

        // check if category has children
        return !this.getRepository().finder.query().fetch("parent").where().eq("parent.id", id).exists();
    }
}