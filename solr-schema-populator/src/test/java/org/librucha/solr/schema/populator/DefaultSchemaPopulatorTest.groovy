package org.librucha.solr.schema.populator

import org.librucha.solr.schema.SolrServerTest
import org.librucha.solr.schema.model.FieldDefinition
import spock.lang.Shared
import spock.lang.Title
import spock.lang.Unroll

import static spock.genesis.Gen.string

@Title('DefaultSchemePopulator test')
class DefaultSchemaPopulatorTest extends SolrServerTest {

    @Shared
    def DefaultSchemaPopulator populator

    def setupSpec() {
        populator = new DefaultSchemaPopulator(solrClient)
    }


    def setup() {
        def created = populator.createCore(CORE_NAME)
        assert created
    }

    void cleanup() {
        def unloaded = populator.unloadCore(CORE_NAME)
        assert unloaded
    }

    @Unroll
    def "create and unload core"() {
        def coreName = string(~'\\w').take(20).join('')

        when:
        def created = populator.createCore(coreName)
        then:
        created
        when:
        def unloaded = populator.unloadCore(coreName)
        then:
        unloaded
    }

    @Unroll
    def "reload core"() {
        when:
        def reloaded = populator.reloadCore(CORE_NAME)
        then:
        reloaded
    }

    @Unroll
    def "get all fields"() {
        when:
        def fields = populator.getFields(CORE_NAME)
        then:
        !fields.empty
        fields.size() >= 87
    }

    @Unroll
    def "add one field"() {
        setup:
        def fieldCountBefore = populator.getFields(CORE_NAME).size()
        def fieldDefinition = FieldDefinition.builder()
                .name(string(~'\\w').take(10).join(''))
                .type('string')
                .build()
        when:
        def added = populator.addField(fieldDefinition, CORE_NAME)
        then:
        added
        populator.getFields(CORE_NAME).size() == fieldCountBefore + 1
    }

    @Unroll
    def "add one dynamic field"() {
        setup:
        def fieldCountBefore = populator.getFields(CORE_NAME).size()
        def fieldDefinition = FieldDefinition.builder()
                .name('*__' + string(~'\\w').take(10).join(''))
                .type('string')
                .build()
        when:
        def added = populator.addField(fieldDefinition, CORE_NAME)
        then:
        added
        populator.getFields(CORE_NAME).size() == fieldCountBefore + 1
    }

    @Unroll
    def "add multiple fields"() {
        setup:
        def fieldCountBefore = populator.getFields(CORE_NAME).size()
        def fieldDefinition1 = FieldDefinition.builder()
                .name(string(~'\\w').take(10).join(''))
                .type('string')
                .build()
        def fieldDefinition2 = FieldDefinition.builder()
                .name('*__' + string(~'\\w').take(10).join(''))
                .type('long')
                .build()
        when:
        def addedCount = populator.addFields([fieldDefinition1, fieldDefinition2], CORE_NAME)
        then:
        addedCount == 2
        populator.getFields(CORE_NAME).size() == fieldCountBefore + 2
    }

    @Unroll
    def "delete one field"() {
        setup:
        def fieldCountBefore = populator.getFields(CORE_NAME).size()
        def fieldDefinition = FieldDefinition.builder()
                .name(string(~'\\w').take(10).join(''))
                .type('string')
                .build()
        def added = populator.addField(fieldDefinition, CORE_NAME)
        assert added
        assert populator.getFields(CORE_NAME).size() == fieldCountBefore + 1
        when:
        def deleted = populator.deleteField(fieldDefinition.name, CORE_NAME)
        then:
        deleted
        populator.getFields(CORE_NAME).size() == fieldCountBefore
    }

    @Unroll
    def "delete multiple fields"() {
        setup:
        def fieldCountBefore = populator.getFields(CORE_NAME).size()
        def fieldDefinition1 = FieldDefinition.builder()
                .name(string(~'\\w').take(10).join(''))
                .type('string')
                .build()
        def fieldDefinition2 = FieldDefinition.builder()
                .name('*__' + string(~'\\w').take(10).join(''))
                .type('long')
                .build()
        def addedCount = populator.addFields([fieldDefinition1, fieldDefinition2], CORE_NAME)
        assert addedCount == 2
        assert populator.getFields(CORE_NAME).size() == fieldCountBefore + 2
        when:
        def deletedCount = populator.deleteFields([fieldDefinition1.name, fieldDefinition2.name], CORE_NAME)
        then:
        deletedCount == 2
        populator.getFields(CORE_NAME).size() == fieldCountBefore
    }
}
