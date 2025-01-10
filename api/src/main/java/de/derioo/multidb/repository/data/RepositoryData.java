package de.derioo.multidb.repository.data;

import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.repository.Repository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class RepositoryData<E, ID> {

    private final Set<IndexedMethod> methods;


    private final Class<E> entityClass;
    private final Class<ID> idClass;

}
