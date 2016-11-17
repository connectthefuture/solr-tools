package org.librucha.solr.schema

import org.apache.solr.client.solrj.impl.HttpSolrClient
import spock.lang.Shared
import spock.lang.Specification

import static spock.genesis.Gen.string

abstract class SolrServerTest extends Specification {

    static final def CORE_NAME = string(~'\\w').take(20).join('')

    @Shared
    def HttpSolrClient solrClient

    def setupSpec() {
        solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build()
    }

    def cleanupSpec() {
        solrClient.close()
    }
}
