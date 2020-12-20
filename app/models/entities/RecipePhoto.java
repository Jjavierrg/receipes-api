package models.entities;

import javax.persistence.*;

@Entity
public class RecipePhoto extends BaseModel {
    public String title;
    public String url;
    public int width;
    public int height;

    @OneToOne()
    @JoinColumn(name = "recipe")
    private Recipe recipe;
}
