package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Recipe extends BaseModel {
    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Category> categories;

    @OneToMany(mappedBy="recipe", cascade = CascadeType.ALL)
    public List<Ingredient> ingredients;

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL)
    public RecipePhoto photo;
}
