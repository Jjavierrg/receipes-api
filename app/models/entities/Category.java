package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;

@Entity
public class Category extends BaseModel {
    public String name;

    @JsonIgnore()
    @ManyToOne()
    public Category parent;

    @OneToMany(mappedBy="parent")
    public List<Category> subCategories;

    @JsonIgnore()
    @ManyToMany(mappedBy = "categories", cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Recipe> recipes;
}
