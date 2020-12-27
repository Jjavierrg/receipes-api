package controllers;

import models.entities.Food;

public class FoodController extends BaseController<Food> {
    protected FoodController() {
        super(Food.class);
    }
}