package de.derioo.multidb.test;

import de.derioo.multidb.annotations.MongoId;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class TestEntity {

    @Builder.Default
    @MongoId
    ObjectId id = new ObjectId();

    int age;

    String name;

    Map<String, Integer> test;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestEntity entity)) return false;
        return entity.id.equals(id) && entity.age == age && entity.name.equals(name);
    }
}
