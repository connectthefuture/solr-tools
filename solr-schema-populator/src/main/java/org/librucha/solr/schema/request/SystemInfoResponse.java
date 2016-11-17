package org.librucha.solr.schema.request;

import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.common.util.SimpleOrderedMap;

public class SystemInfoResponse extends SolrResponseBase {

    public String getMode() {
        return (String) getResponse().get("mode");
    }

    public String getSolrHome() {
        return (String) getResponse().get("solr_home");
    }

    public String getSolrSpecVersion() {
        return (String) ((SimpleOrderedMap) getResponse().get("lucene")).findRecursive("solr-spec-version");
    }

    public String getLuceneSpecVersion() {
        return (String) ((SimpleOrderedMap) getResponse().get("lucene")).findRecursive("lucene-spec-version");
    }
}
