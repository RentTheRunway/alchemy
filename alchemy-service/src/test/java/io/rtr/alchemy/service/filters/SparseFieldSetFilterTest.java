package io.rtr.alchemy.service.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import io.dropwizard.jackson.Jackson;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class SparseFieldSetFilterTest {
    private ContainerRequestContext request;
    private ContainerResponseContext response;
    private ObjectMapper mapper;
    private SparseFieldSetFilter filter;
    private JsonNode responseEntity;

    @Before
    public void setUp() {
        request = mock(ContainerRequestContext.class);
        response = mock(ContainerResponseContext.class);
        mapper = Jackson.newObjectMapper();
        filter = new SparseFieldSetFilter(mapper);
    }

    private void ensureMediaType() {
        doReturn(MediaType.APPLICATION_JSON_TYPE).when(response).getMediaType();
    }

    private void doFilter(String... fields) {
        final MultivaluedMap<String, String> queryParams = new MultivaluedStringMap();
        queryParams.put("fields", Lists.newArrayList(fields));
        doReturn(queryParams).when(request).getHeaders();

        final ObjectNode entity = mapper.createObjectNode();
        entity.put("name", "foo");

        final ArrayNode arrayNode = mapper.createArrayNode();
        entity.put("array", arrayNode);

        final ObjectNode personNode = mapper.createObjectNode();
        arrayNode.add(personNode);

        personNode.put("age", 32);
        personNode.put("name", "Gene");
        entity.put("person", personNode);

        doReturn(entity).when(response).getEntity();
        doAnswer(
                        invocation -> {
                            responseEntity = (ObjectNode) invocation.getArguments()[0];
                            return null;
                        })
                .when(response)
                .setEntity(any());

        filter.filter(request, response);
    }

    @Test
    public void testWrongMediaType() {
        doReturn(MediaType.TEXT_PLAIN_TYPE).when(request).getMediaType();

        doFilter();
        verify(response, never()).getEntity();
    }

    @Test
    public void testCorrectMediaType() {
        doReturn(MediaType.APPLICATION_JSON_TYPE).when(response).getMediaType();

        doFilter();
        verify(response).getEntity();
    }

    @Test
    public void testCompatibleMediaType() {
        final MediaType mediaType = MediaType.valueOf("application/json; charset=utf-8");
        doReturn(mediaType).when(response).getMediaType();

        doFilter();
        verify(response).getEntity();
    }

    @Test
    public void testFilterFieldsBasic() {
        ensureMediaType();
        doFilter("name");

        assertNotNull(responseEntity);
        assertEquals(1, responseEntity.size());
        assertTrue(responseEntity.has("name"));
    }

    @Test
    public void testFilterFieldsComplex() {
        ensureMediaType();
        doFilter("name", "person.name", "array.name");

        assertNotNull(responseEntity);
        assertEquals(3, responseEntity.size());
        assertTrue(responseEntity.has("name"));
        assertTrue(responseEntity.has("person"));
        assertTrue(responseEntity.has("array"));

        final ObjectNode personNode = (ObjectNode) responseEntity.get("person");
        final ArrayNode arrayNode = (ArrayNode) responseEntity.get("array");

        assertEquals(1, arrayNode.size());
        assertEquals(personNode, arrayNode.get(0));
        assertTrue(personNode.has("name"));
    }
}
