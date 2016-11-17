package org.librucha.solr.schema.request

import org.librucha.solr.schema.SolrServerTest
import spock.lang.Title
import spock.lang.Unroll

@Title("SystemInfoRequest test")
class SystemInfoRequestTest extends SolrServerTest {

    @Unroll
    def "test GET system info"() {
        given:
        def SystemInfoRequest request = new SystemInfoRequest()
        when:
        def response = request.process(solrClient)
        then:
        response.status == 0
        response.mode == 'std'
        response.solrHome != null
        response.solrSpecVersion == '6.3.0'
        response.luceneSpecVersion == '6.3.0'
    }
}
