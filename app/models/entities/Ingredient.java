package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Ingredient extends BaseModel {
    public int quantity;

    @ManyToOne()
    public Food food;

    @ManyToOne()
    public Measure measure;

    @JsonIgnore()
    @ManyToOne()
    public Recipe recipe;
}
