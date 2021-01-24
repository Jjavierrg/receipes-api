package mappers;

import controllers.dto.CategoryDto;
import models.entities.Category;

import java.util.HashMap;
import java.util.stream.Collectors;

public class CategoryMapper implements IMapper<Category, CategoryDto> {
    @Override
    public CategoryDto toDto(Category entity) {
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
    public Category toEntity(CategoryDto dto) {
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
    public HashMap<String, String> getFieldRelations() {
        var fieldMapping = new HashMap<String, String>();
        fieldMapping.put("children.id", "subCategories.id");
        fieldMapping.put("children.name", "subCategories.name");

        return fieldMapping;
    }
}
