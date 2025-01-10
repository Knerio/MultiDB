package de.derioo.multidb.test;

import de.derioo.multidb.annotations.MongoId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@Entity
public class TestEntity {

    @Builder.Default
    @MongoId
    @Id
    ObjectId id = new ObjectId();

    int age;

    String name;


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestEntity entity)) return false;
        return entity.id.equals(id) && entity.age == age && entity.name.equals(name);
    }
}
