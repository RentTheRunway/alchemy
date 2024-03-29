package io.rtr.alchemy.service.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;

/**
 * A filter to support returning partial results by specifying a 'fields' query parameter with a
 * comma-separated list of field names, or dot-delimited field names (for nested fields)
 */
public class SparseFieldSetFilter implements ContainerResponseFilter {
    private static final Splitter SUB_FIELD_SPLITTER = Splitter.on(".");
    private static final Joiner SUB_FIELD_JOINER = Joiner.on(".");
    private final ObjectMapper mapper;

    public SparseFieldSetFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    private static void filterFields(String parent, ObjectNode objectNode, Set<String> fields) {
        final Iterator<Entry<String, JsonNode>> iterator = objectNode.fields();

        while (iterator.hasNext()) {
            final Entry<String, JsonNode> entry = iterator.next();
            final String name = entry.getKey();
            final JsonNode value = entry.getValue();
            final String fieldName = parent.isEmpty() ? name : parent + "." + name;

            if (!fields.contains(fieldName)) {
                iterator.remove();
                continue;
            }

            if (value.isObject()) {
                filterFields(fieldName, (ObjectNode) value, fields);
            } else if (value.isArray()) {
                filterFields(fieldName, (ArrayNode) value, fields);
            }
        }
    }

    private static void filterFields(String parent, ArrayNode arrayNode, Set<String> fields) {
        for (JsonNode node : arrayNode) {
            if (node.isObject()) {
                filterFields(parent, (ObjectNode) node, fields);
            } else if (node.isArray()) {
                filterFields(parent, (ArrayNode) node, fields);
            }
        }
    }

    private static Set<String> expandFields(List<String> fieldsParam) {
        final Set<String> fields = Sets.newHashSet();

        for (String field : fieldsParam) {
            fields.addAll(subFields(field));
        }

        return fields;
    }

    private static Set<String> subFields(String field) {
        final List<String> parts = SUB_FIELD_SPLITTER.splitToList(field);
        final Set<String> subFields = Sets.newHashSet();

        for (int i = 0; i < parts.size(); ++i) {
            subFields.add(SUB_FIELD_JOINER.join(parts.subList(0, i + 1)));
        }

        return subFields;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext response) {
        if (!MediaType.APPLICATION_JSON_TYPE.isCompatible(response.getMediaType())) {
            return;
        }

        final List<String> fieldsParam = requestContext.getHeaders().get("fields");
        if (fieldsParam == null) {
            return;
        }

        final Object entity = response.getEntity();

        if (entity == null) {
            return;
        }

        final JsonNode tree = mapper.convertValue(entity, JsonNode.class);
        final Set<String> fields = expandFields(fieldsParam);

        if (tree.isObject()) {
            filterFields("", (ObjectNode) tree, fields);
            response.setEntity(tree);
        } else if (tree.isArray()) {
            filterFields("", (ArrayNode) tree, fields);
            response.setEntity(tree);
        }
    }
}
