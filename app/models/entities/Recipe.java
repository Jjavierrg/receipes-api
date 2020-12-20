package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Recipe extends BaseModel {
    private String title;
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Category> categories;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    @OneToOne(mappedBy = "recipe")
    private RecipePhoto photo;
}
