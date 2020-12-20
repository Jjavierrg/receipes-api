package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Measure extends BaseModel {
    public String description;

    @JsonIgnore()
    @OneToMany(mappedBy="measure", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Ingredient> ingredients;
}
