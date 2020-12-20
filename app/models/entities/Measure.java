package models.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Measure extends BaseModel {
    private String description;

    @OneToMany(mappedBy="measure", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ingredient> ingredients;
}
