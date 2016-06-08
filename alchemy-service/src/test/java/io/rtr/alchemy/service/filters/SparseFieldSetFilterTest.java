package io.rtr.alchemy.service.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SparseFieldSetFilterTest {
    private ContainerRequest request;
    private ContainerResponse response;
    private ObjectMapper mapper;
    private SparseFieldSetFilter filter;
    private JsonNode responseEntity;

    @Before
    public void setUp() {
        request = mock(ContainerRequest.class);
        response = mock(ContainerResponse.class);
        mapper = Jackson.newObjectMapper();
        filter = new SparseFieldSetFilter(mapper);
    }

    private void ensureMediaType() {
        doReturn(MediaType.APPLICATION_JSON_TYPE).when(response).getMediaType();
    }

    private void doFilter(String ... fields) {
        final MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.put("fields", Lists.newArrayList(fields));
        doReturn(queryParams).when(request).getQueryParameters();

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
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                responseEntity = (ObjectNode) invocation.getArguments()[0];
                return null;
            }
        }).when(response).setEntity(Matchers.anyObject());

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
