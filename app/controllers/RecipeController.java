package controllers;

import com.google.inject.Inject;
import controllers.dto.*;
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
 * Controller for {@link models.entities.Recipe} model
 */
public class RecipeController extends BaseController<Recipe, RecipeDto> {

    @Inject
    protected RecipeController(IMapper<Recipe, RecipeDto> mapper) {
        super(Recipe.class, RecipeDto.class, mapper);
    }
}
