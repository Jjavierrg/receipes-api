package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ingredient extends BaseModel {
    private int quantity;

    @ManyToOne()
    private Food food;

    @ManyToOne()
    private Measure measure;

    @ManyToMany(mappedBy = "ingredients", fetch = FetchType.LAZY)
    private List<Recipe> recipes;
}
