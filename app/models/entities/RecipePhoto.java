package models.entities;

import javax.persistence.*;

@Entity
public class RecipePhoto extends BaseModel {
    private String title;
    private String url;
    private int width;
    private int height;

    @OneToOne()
    @JoinColumn(name = "recipe")
    private Recipe recipe;
}
