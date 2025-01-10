package de.derioo.multidb.mongo.convention;

import de.derioo.multidb.annotations.MongoId;
import de.derioo.multidb.annotations.Transform;
import de.derioo.multidb.annotations.Transient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;

import java.lang.annotation.Annotation;

/**
 * This convention implementation enables the usage of the annotations from en2do
 * inside entity classes. This convention checks the annotations in the class model builder
 * and modifies it accordingly.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AnnotationConvention implements Convention {

    /**
     * @param classModelBuilder the ClassModelBuilder to apply the convention to
     * @see Convention
     */
    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            for (Annotation readAnnotation : propertyModelBuilder.getReadAnnotations()) {
                switch (readAnnotation) {
                    case Transient ignored -> {
                        propertyModelBuilder.readName(null);
                        continue;
                    }
                    case Transform ann -> {
                        propertyModelBuilder.readName(ann.value());
                        continue;
                    }
                    case MongoId ignored -> classModelBuilder.idPropertyName(propertyModelBuilder.getName());
                    default -> {}
                }
            }
            for (Annotation writeAnnotation : propertyModelBuilder.getWriteAnnotations()) {
                if (writeAnnotation instanceof Transient) {
                    propertyModelBuilder.readName(null);
                    continue;
                }
                if (writeAnnotation instanceof Transform transform) {
                    propertyModelBuilder.writeName(transform.value());
                }
                if (writeAnnotation instanceof MongoId) {
                    classModelBuilder.idPropertyName(propertyModelBuilder.getName());
                }
            }
        }
    }
}
