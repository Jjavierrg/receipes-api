package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;

@Entity
public class Food extends BaseModel {
    public String name;

    @JsonIgnore()
    @OneToMany(mappedBy="food", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Ingredient> ingredients;
}
