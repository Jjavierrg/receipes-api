package models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.ebean.Model;
import io.ebean.annotation.*;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
@JsonIgnoreProperties({"_ebean_intercept", "_$dbName", "version", "creationDate", "updateDate"})
public class BaseModel extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Version
    private long version;

    @WhenCreated
    private Instant creationDate;

    @WhenModified
    private Instant updateDate;

    public long getVersion() {
        return version;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }
}
