package controllers;

import models.entities.Category;

public class CategoryController extends BaseController<Category> {
    protected CategoryController() {
        super(Category.class);
    }
}