package org.librucha.solr.schema.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Data
@Builder
public class FieldDefinition {

    @NonNull
    private final String name;
    @NonNull
    private final String type;
    private boolean indexed = true;
    private boolean stored = true;
    private boolean multiValued = false;
    private boolean required = false;
    private String defaultValue;

    public static FieldDefinition fromAttributes(Map<String, Object> attributes) {
        requireNonNull(attributes, "attributes must not be null");
        return FieldDefinition.builder()
                .name(requireNonNull(fromObject(attributes.get("name"), null)))
                .type(requireNonNull(fromObject(attributes.get("type"), null)))
                .indexed(fromObject(attributes.get("indexed"), false))
                .stored(fromObject(attributes.get("stored"), false))
                .multiValued(fromObject(attributes.get("multiValued"), false))
                .defaultValue(fromObject(attributes.get("default"), null))
                .required(fromObject(attributes.get("required"), false))
                .build();
    }

    @SuppressWarnings("unchecked")
    private static <T> T fromObject(Object object, Object defaultValue) {
        return object == null ? (T) defaultValue : (T) object;
    }

    public boolean isDynamic() {
        return isDynamic(name);
    }

    public Map<String, Object> toAttributes() {
        Map<String, Object> attributes = new LinkedHashMap<>(7);
        attributes.put("name", name);
        attributes.put("type", type);
        attributes.put("indexed", indexed);
        attributes.put("stored", stored);
        attributes.put("multiValued", multiValued);
        attributes.put("default", defaultValue);
        attributes.put("required", required);
        return attributes.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static boolean isDynamic(String fieldName) {
        return fieldName != null && (fieldName.startsWith("*") || fieldName.endsWith("*"));
    }

    @SuppressWarnings("unused")
    public static class FieldDefinitionBuilder {

        private boolean indexed = true;
        private boolean stored = true;
    }
}
