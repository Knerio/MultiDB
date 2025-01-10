package de.derioo.multidb;

import de.derioo.multidb.annotations.Transient;
import de.derioo.multidb.method.IndexedMethod;
import de.derioo.multidb.method.MethodDelimiter;
import de.derioo.multidb.method.MethodPrefix;
import de.derioo.multidb.method.PredefinedMethodResultCount;
import de.derioo.multidb.annotations.Transform;
import de.derioo.multidb.method.arg.ArgumentData;
import de.derioo.multidb.repository.Repository;
import de.derioo.multidb.repository.data.RepositoryData;
import de.derioo.multidb.repository.execution.ExecutionHandler;
import de.derioo.multidb.utils.GenericUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.regex.Pattern;

public abstract class DatabaseManager {

    public static final List<String> IGNORED_DEFAULT_METHODS = Arrays.asList(
            "notify", "notifyAll", "wait", "finalize", "clone"
    );

    private final Map<Class<? extends Repository<?, ?>>, Repository<?, ?>> repositoryClassMap = new HashMap<>();


    private final Credentials credentials;

    protected DatabaseManager(Credentials credentials) {
        this.credentials = credentials;
    }

    @SuppressWarnings("unchecked")
    public <ENTITY, ID, R extends Repository<ENTITY, ID>> R create(Class<R> repoClazz) {
        final List<Class<?>> classes = GenericUtils.getGenericTypes(repoClazz).get(Repository.class);
        final Class<ENTITY> entityClass = (Class<ENTITY>) classes.getFirst();
        final Class<ID> idClass = (Class<ID>) classes.get(1);

        final Set<IndexedMethod> methods = new HashSet<>();
        for (final Method method : repoClazz.getMethods()) {
            if (IGNORED_DEFAULT_METHODS.contains(method.getName())) continue;
            String methodName = method.getName();
            if (method.isAnnotationPresent(Transform.class)) {
                methodName = method.getAnnotation(Transform.class).value();
            }
            final Set<MethodPrefix> prefixes = new HashSet<>();
            for (MethodPrefix prefix : MethodPrefix.values()) {
                if (methodName.startsWith(prefix.getPrefix())) {
                    if (!prefix.isCompatible() && prefixes.contains(prefix)) throw new IllegalStateException("Multiple Prefixes in method");
                    prefixes.add(prefix);
                    methodName = methodName.toLowerCase().replaceFirst(prefix.getPrefix(), "");
                }
            }
            Long definedMethodResultCount = 0L;
            for (PredefinedMethodResultCount value : PredefinedMethodResultCount.values()) {
                if (methodName.startsWith(value.name().toLowerCase())) {
                    methodName = methodName.replaceFirst(value.name().toLowerCase(), "");
                    definedMethodResultCount = value.getResultCount();
                    if (definedMethodResultCount == null) {
                        Pattern pattern = Pattern.compile("(\\d+).*");
                        definedMethodResultCount = Long.parseLong(pattern.matcher(methodName).group());
                    }
                }
            }
            methodName = methodName.replaceFirst("by", "");


            MethodDelimiter delimiter = null;
            for (MethodDelimiter value : MethodDelimiter.values()) {
                if (methodName.contains(value.name().toLowerCase())) {
                    if (delimiter != null && !delimiter.equals(value)) throw new IllegalStateException("Method name contains AND and OR");
                    delimiter = value;
                }
            }
            if (delimiter == null) delimiter = MethodDelimiter.OR;

            List<ArgumentData> datas = new ArrayList<>();
            for (String value : methodName.split(delimiter.name().toLowerCase())) {
                if (value.isEmpty()) continue;
                ArgumentData.ArgumentOption option = ArgumentData.ArgumentOption.EQ;
                Field field = null;
                boolean not = false;
                if (value.endsWith("not")) {
                    not = true;
                    value = value.replace("not", "");
                }
                for (ArgumentData.ArgumentOption argumentOption : ArgumentData.ArgumentOption.values()) {
                    if (argumentOption.equals(ArgumentData.ArgumentOption.EQ)) continue;
                    if (!value.toLowerCase().endsWith(argumentOption.getKeyword())) continue;
                    option = argumentOption;
                    value = value.toLowerCase().replace(option.getKeyword(), "");
                    break;
                }
                for (Field curr : entityClass.getDeclaredFields()) {
                    if (curr.isAnnotationPresent(Transient.class)) continue; // continue if field "doesnt exist"
                    if (!curr.getName().equalsIgnoreCase(value)) continue; // Currently only equals ignore case, fucked if it got two fields with the same name
                    field = curr;
                    break;
                }
                if (field == null) throw new NullPointerException("Field " + value + " not found");
                datas.add(new ArgumentData(field, not, option));
            }
            IndexedMethod indexedMethod = new IndexedMethod(method, definedMethodResultCount, delimiter, datas, prefixes);
            methods.add(indexedMethod);
        }
        RepositoryData<ENTITY, ID> data = new RepositoryData<>(methods, entityClass, idClass);

        R repo = (R) Proxy.newProxyInstance(
                repoClazz.getClassLoader(),
                new Class[]{repoClazz},
                new ExecutionHandler<>(data, this, factory().newHandler(data)));

        this.repositoryClassMap.put(repoClazz, repo);

        return repo;
    }

    public abstract AbstractExecutionHandlerFactory factory();

}
