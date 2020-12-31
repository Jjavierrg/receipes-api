package controllers;

import controllers.dto.CategoryDto;
import interpreter.ExpressionParser;
import interpreter.IParser;
import models.entities.*;
import models.repositories.BaseRepository;
import play.twirl.api.Content;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryController extends BaseController<Category, CategoryDto> {
    protected CategoryController() { super(Category.class, CategoryDto.class); }

    @Override
    protected IParser getParser() {
        var fieldMapping = new HashMap<String, String>();
        fieldMapping.put("children.id", "subCategories.id");
        fieldMapping.put("children.name", "subCategories.name");

        return new ExpressionParser(fieldMapping);
    }

    @Override
    protected CategoryDto toDto(Category entity) {
        if (entity == null)
            return null;

        var dto = new CategoryDto();
        dto.id = entity.id;
        dto.name = entity.name;

        if (entity.parent != null) {
            dto.parent = new CategoryDto();
            dto.parent.id = entity.parent.id;
            dto.parent.name = entity.parent.name;
        }

        if (entity.subCategories != null) {
            dto.children = entity.subCategories.stream().map(x -> {
                var child = new CategoryDto();
                child.id = x.id;
                child.name = x.name;
                return child;
            }).collect(Collectors.toList());
        }

        return dto;
    }

    @Override
    protected Category toEntity(CategoryDto dto) {
        if (dto == null)
            return null;

        var entity = new Category();
        entity.id = dto.id;
        entity.name = dto.name;

        if (dto.parent != null) {
            entity.parent = new Category();
            entity.parent.id = dto.parent.id;
            entity.parent.name = dto.parent.name;
        }

        if (dto.children != null) {
            entity.subCategories = dto.children.stream().map(x -> {
                var child = new Category();
                child.id = x.id;
                child.name = x.name;
                return child;
            }).collect(Collectors.toList());
        }

        return entity;
    }

    @Override
    protected Content getXMLListContent(List<CategoryDto> list) {
        return views.xml.categories.render(list);
    }

    @Override
    protected Content getXMLEntityContent(CategoryDto entity) {
        return views.xml.category.render(entity);
    }

    @Override
    public boolean canDelete(long id) {
        // check for related data
        var repo = new BaseRepository<>(Recipe.class);
        var existRecipes = repo.finder.query().fetch("categories").where().eq("categories.id", id).exists();
        if (existRecipes)
            return false;

        // check if category has children
        return !this.repository.finder.query().fetch("parent").where().eq("parent.id", id).exists();
    }
}