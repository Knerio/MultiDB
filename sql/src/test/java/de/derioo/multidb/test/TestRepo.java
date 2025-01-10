package de.derioo.multidb.test;

import de.derioo.multidb.method.sort.Sort;
import de.derioo.multidb.repository.Repository;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TestRepo extends Repository<TestEntity, ObjectId> {

    long countByName(String firstname);

    boolean delete();

    boolean existsById(ObjectId id);

    CompletableFuture<TestEntity> asyncFindFirstById(ObjectId id);

    CompletableFuture<TestEntity> asyncFindFirstByIdAndAge(ObjectId id, int age);

    TestEntity findFirstByIdAndAgeNot(ObjectId id, int age);

    List<TestEntity> findMany(Sort sort);

}
