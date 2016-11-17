package org.librucha.solr.schema.populator;

import org.apache.solr.client.solrj.SolrServerException;
import org.librucha.solr.schema.model.FieldDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface SchemaPopulator {

    boolean createCore(@Nonnull String coreName) throws IOException, SolrServerException;

    boolean createCore(@Nonnull String coreName, @Nullable String configSet) throws IOException, SolrServerException;

    boolean reloadCore(@Nonnull String coreName) throws IOException, SolrServerException;

    boolean unloadCore(@Nonnull String coreName) throws IOException, SolrServerException;

    List<FieldDefinition> getFields(@Nonnull String coreName) throws IOException, SolrServerException;

    boolean addField(@Nonnull FieldDefinition fieldDefinition, @Nonnull String coreName) throws IOException, SolrServerException;

    int addFields(@Nonnull Collection<FieldDefinition> fieldDefinitions, @Nonnull String coreName) throws IOException, SolrServerException;

    boolean deleteField(@Nonnull String fieldName, @Nonnull String coreName) throws IOException, SolrServerException;

    int deleteFields(@Nonnull Collection<String> fieldNames, @Nonnull String coreName) throws IOException, SolrServerException;
}
