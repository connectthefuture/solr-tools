package org.librucha.solr.schema.request;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;

import java.io.IOException;
import java.util.Collection;

public class SystemInfoRequest extends SolrRequest<SystemInfoResponse> {

    private final SolrParams params;

    public SystemInfoRequest() {
        this(new ModifiableSolrParams());
    }

    public SystemInfoRequest(SolrParams params) {
        super(METHOD.GET, "/admin/info/system");
        this.params = params;
    }

    @Override
    public SolrParams getParams() {
        return params;
    }

    @Override
    public Collection<ContentStream> getContentStreams() throws IOException {
        return null;
    }

    @Override
    protected SystemInfoResponse createResponse(SolrClient client) {
        if (client instanceof EmbeddedSolrServer) {
            throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Embedded Solr server doesn't support this request");
        }
        return new SystemInfoResponse();
    }
}
