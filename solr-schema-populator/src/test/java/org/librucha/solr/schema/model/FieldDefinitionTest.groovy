package org.librucha.solr.schema.model

import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Title('FieldDefinition test')
class FieldDefinitionTest extends Specification {

    @Unroll
    def "Get all attributes as map field: '#name'"() {
        setup:
        def fieldDefinition = FieldDefinition.builder()
                .name(name)
                .type(type)
                .indexed(indexed)
                .defaultValue(defaultValue)
                .required(required)
                .build()
        when:
        def attributes = fieldDefinition.toAttributes()
        then:
        attributes == expected
        where:
        name          | type      | indexed | defaultValue | required || expected
        'textField'   | 'string'  | true    | null         | true     || [name: 'textField', type: 'string', indexed: true, stored: true, multiValued: false, required: true]
        'notIndexed'  | 'string'  | false   | null         | false    || [name: 'notIndexed', type: 'string', indexed: false, stored: true, multiValued: false, required: false]
        'withDefault' | 'integer' | false   | '100'        | false    || [name: 'withDefault', type: 'integer', indexed: false, stored: true, multiValued: false, required: false, default: '100']
        '*__dynamic'  | 'integer' | false   | '100'        | false    || [name: '*__dynamic', type: 'integer', indexed: false, stored: true, multiValued: false, required: false, default: '100']
    }

    @Unroll
    def "Construct definition from map field: '#name'"() {
        when:
        def fieldDefinition = FieldDefinition.fromAttributes(attributes)
        then:
        fieldDefinition.name == name
        fieldDefinition.type == type
        fieldDefinition.indexed == indexed
        fieldDefinition.defaultValue == defaultValue
        fieldDefinition.required == required
        fieldDefinition.dynamic == dynamic
        where:
        attributes                                                                                                                | name          | type      | indexed | defaultValue | required | dynamic
        [name: 'textField', type: 'string', indexed: true, stored: true, multiValued: false, required: true]                      | 'textField'   | 'string'  | true    | null         | true     | false
        [name: 'notIndexed', type: 'string', indexed: false, stored: true, multiValued: false, required: false]                   | 'notIndexed'  | 'string'  | false   | null         | false    | false
        [name: 'withDefault', type: 'integer', indexed: false, stored: true, multiValued: false, required: false, default: '100'] | 'withDefault' | 'integer' | false   | '100'        | false    | false
        [name: 'withNulls', type: 'integer']                                                                                      | 'withNulls'   | 'integer' | false   | null         | false    | false
        [name: '*__dynamic', type: 'integer']                                                                                     | '*__dynamic'  | 'integer' | false   | null         | false    | true
    }
}
