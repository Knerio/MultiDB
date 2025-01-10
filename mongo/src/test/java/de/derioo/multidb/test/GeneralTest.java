package de.derioo.multidb.test;

import de.derioo.multidb.DatabaseManager;
import de.derioo.multidb.method.sort.Sort;
import de.derioo.multidb.mongo.MongoCredentials;
import de.derioo.multidb.mongo.MongoManager;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class GeneralTest {

    public static TestRepo repo() {
//        DatabaseManager manager = new MongoManager(new MongoCredentials("mongodb://localhost:27017/testdb", "testdb"));
        DatabaseManager manager = new MongoManager(new MongoCredentials().readFromFile());
        return manager.create(TestRepo.class);
    }

    @BeforeEach
    public void drop() {
        repo().delete();
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        TestRepo repo = repo();

        TestEntity entity = TestEntity.builder().test(Map.of("3", 20, "4", -1)).age(20).name("Test name").build();
        assertThat(repo.save(entity)).isTrue();
        assertThat(repo.existsById(entity.getId())).isTrue();
        assertThat(repo.asyncFindFirstById(entity.getId()).get()).isEqualTo(entity);
        assertThat(repo.asyncFindFirstByIdAndAge(entity.getId(), entity.age).get()).isEqualTo(entity);
        assertThat(repo.asyncFindFirstByIdAndAge(entity.getId(), entity.age + 1).get()).isNull();
        assertThat(repo.findFirstByIdAndAgeNot(entity.getId(), entity.age)).isNull();
        assertThat(repo.findFirstByIdAndAgeNot(entity.getId(), entity.age + 1)).isEqualTo(entity);
        assertThat(repo.findFirstById(entity.getId())).isEqualTo(entity);
        entity.setAge(21);
        entity.setId(new ObjectId());
        assertThat(repo.save(entity)).isTrue();
        assertThat(repo.findMany(new Sort().order("age", false)).get(0).getAge()).isEqualTo(21);
        assertThat(repo.findMany(new Sort().order("age", false)).get(1).getAge()).isEqualTo(20);
    }

    @Test
    public void testDelete() {
        TestRepo repo = repo();

        TestEntity entity = TestEntity.builder().test(Map.of("3", 20, "4", -1)).age(20).name("Test name").build();
        assertThat(repo.save(entity)).isTrue();
        assertThat(repo.countMany()).isEqualTo(1);
        assertThat(repo.deleteById(entity.getId())).isTrue();
        assertThat(repo.countMany()).isEqualTo(0);
    }

}
