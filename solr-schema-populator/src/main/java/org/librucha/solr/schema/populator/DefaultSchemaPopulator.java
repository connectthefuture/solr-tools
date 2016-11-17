package org.librucha.solr.schema.populator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.librucha.solr.schema.model.FieldDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class DefaultSchemaPopulator implements SchemaPopulator {

    public static final String DEFAULT_CONFIG_SET = "data_driven_schema_configs";
    private final SolrClient solrClient;

    public DefaultSchemaPopulator(SolrClient solrClient) {
        this.solrClient = requireNonNull(solrClient, "solrClient must not be null");
    }

    @Override
    public boolean createCore(@Nonnull String coreName) throws IOException, SolrServerException {
        return createCore(coreName, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean createCore(@Nonnull String coreName, @Nullable String configSet) throws IOException, SolrServerException {
        requireNonNull(coreName, "coreName must not be null");
        CoreAdminRequest.Create coreRequest = new CoreAdminRequest.Create();
        coreRequest.setConfigSet(configSet == null ? DEFAULT_CONFIG_SET : configSet);
        coreRequest.setCoreName(coreName);
        CoreAdminResponse response = coreRequest.process(solrClient);
        return wasSuccessful(response);
    }

    @Override
    public boolean reloadCore(@Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(coreName, "coreName must not be null");
        CoreAdminResponse response = CoreAdminRequest.reloadCore(coreName, solrClient);
        return wasSuccessful(response);
    }

    @Override
    public boolean unloadCore(@Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(coreName, "coreName must not be null");
        CoreAdminResponse response = CoreAdminRequest.unloadCore(coreName, true, true, solrClient);
        return wasSuccessful(response);
    }

    @Override
    public List<FieldDefinition> getFields(@Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(coreName, "coreName must not be null");
        ModifiableSolrParams params = new ModifiableSolrParams(singletonMap("includeDynamic", new String[]{"true"}));
        SchemaResponse.FieldsResponse response = new SchemaRequest.Fields(params).process(solrClient, coreName);
        return resolveFieldDefinitions(response);
    }

    private List<FieldDefinition> resolveFieldDefinitions(SchemaResponse.FieldsResponse fieldsResponse) {
        if (fieldsResponse == null) {
            return null;
        }
        return fieldsResponse.getFields().stream().map(FieldDefinition::fromAttributes).collect(toList());
    }

    @Override
    public boolean addField(@Nonnull FieldDefinition fieldDefinition, @Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(fieldDefinition, "fieldDefinition must not be null");
        requireNonNull(coreName, "coreName must not be null");
        SchemaResponse.UpdateResponse response = createAddFieldRequest(fieldDefinition).process(solrClient, coreName);
        return wasSuccessful(response);
    }

    @Override
    public int addFields(@Nonnull Collection<FieldDefinition> fieldDefinitions, @Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(fieldDefinitions, "fieldDefinitions must not be null");
        requireNonNull(coreName, "coreName must not be null");
        List<SchemaRequest.Update> updates = fieldDefinitions.stream()
                .map(this::createAddFieldRequest)
                .collect(toList());
        SchemaResponse.UpdateResponse response = new SchemaRequest.MultiUpdate(updates).process(solrClient, coreName);
        return wasSuccessful(response) ? updates.size() : 0;
    }

    private SchemaRequest.Update createAddFieldRequest(FieldDefinition fieldDefinition) {
        SchemaRequest.Update request;
        if (fieldDefinition.isDynamic()) {
            request = new SchemaRequest.AddDynamicField(fieldDefinition.toAttributes());
        } else {
            request = new SchemaRequest.AddField(fieldDefinition.toAttributes());
        }
        return request;
    }

    @Override
    public boolean deleteField(@Nonnull String fieldName, @Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(fieldName, "fieldName must not be null");
        requireNonNull(coreName, "coreName must not be null");
        SchemaResponse.UpdateResponse response = createDeleteFieldRequest(fieldName).process(solrClient, coreName);
        return wasSuccessful(response);
    }

    @Override
    public int deleteFields(@Nonnull Collection<String> fieldNames, @Nonnull String coreName) throws IOException, SolrServerException {
        requireNonNull(fieldNames, "fieldNames must not be null");
        requireNonNull(coreName, "coreName must not be null");
        List<SchemaRequest.Update> updates = fieldNames.stream()
                .map(this::createDeleteFieldRequest)
                .collect(toList());
        SchemaResponse.UpdateResponse response = new SchemaRequest.MultiUpdate(updates).process(solrClient, coreName);
        return wasSuccessful(response) ? updates.size() : 0;
    }

    private SchemaRequest.Update createDeleteFieldRequest(String fieldName) {
        SchemaRequest.Update request;
        if (FieldDefinition.isDynamic(fieldName)) {
            request = new SchemaRequest.DeleteDynamicField(fieldName);
        } else {
            request = new SchemaRequest.DeleteField(fieldName);
        }
        return request;
    }

    private boolean wasSuccessful(SolrResponseBase response) {
        return response != null && response.getStatus() == 0 && response.getResponse().get("errors") == null;
    }
}
