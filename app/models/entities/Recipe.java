package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Recipe extends BaseModel {
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Category> categories;

    @OneToMany(mappedBy="recipe")
    private List<Ingredient> ingredients;

    @OneToOne(mappedBy = "recipe")
    private RecipePhoto photo;
}
