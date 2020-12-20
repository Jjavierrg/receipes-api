package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Food extends BaseModel {
    private String name;

    @OneToMany(mappedBy="food", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ingredient> ingredients;
}
