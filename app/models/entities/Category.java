package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Category extends BaseModel {
    private String name;

    @ManyToOne()
    private Category parent;

    @OneToMany(mappedBy="parent")
    private List<Category> subCategories;

    @ManyToMany(mappedBy = "categories", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recipe> recipes;
}
