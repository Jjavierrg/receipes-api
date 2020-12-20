package models.entities;

import io.ebean.Model;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.security.Timestamp;

@MappedSuperclass
public class BaseModel extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @JsonIgnore
    @CreatedTimestamp
    public Timestamp createdAt;

    @JsonIgnore
    @UpdatedTimestamp
    public Timestamp updatedAt;
}
