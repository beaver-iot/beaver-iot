package com.milesight.beaveriot.integration.msc.util

import com.milesight.beaveriot.context.integration.enums.EntityType
import com.milesight.beaveriot.context.integration.enums.EntityValueType
import com.milesight.cloud.sdk.client.model.*
import spock.lang.Specification

class MscTslUtilsTest extends Specification {


    def "given thing spec when calling getPropertiesEntities then should return property entities"() {
        given:
        def properties = [
                new TslPropertySpec()
                        .id("data")
                        .name("Data")
                        .accessMode(TslPropertySpec.AccessModeEnum.RW)
                        .dataSpec(new TslDataSpec()
                                .dataType(TslDataSpec.DataTypeEnum.STRUCT)),
                new TslPropertySpec()
                        .id("data.long_value")
                        .name("Long Value")
                        .accessMode(TslPropertySpec.AccessModeEnum.RW)
                        .dataSpec(new TslDataSpec()
                                .parentId("data")
                                .dataType(TslDataSpec.DataTypeEnum.LONG)
                                .fractionDigits(2)
                                .validator(new TslDataValidatorSpec()
                                        .min(BigDecimal.valueOf(9))
                                        .max(BigDecimal.valueOf(23)))),
                new TslPropertySpec()
                        .id("data.enum_value")
                        .name("Enum Value")
                        .accessMode(TslPropertySpec.AccessModeEnum.W)
                        .dataSpec(new TslDataSpec()
                                .parentId("data")
                                .dataType(TslDataSpec.DataTypeEnum.ENUM)
                                .mappings([
                                        new TslKeyValuePair().key("a").value("1"),
                                        new TslKeyValuePair().key("b").value("2"),
                                ])),
                new TslPropertySpec()
                        .id("data.struct_value")
                        .name("Struct Value")
                        .accessMode(TslPropertySpec.AccessModeEnum.R)
                        .dataSpec(new TslDataSpec()
                                .parentId("data")
                                .dataType(TslDataSpec.DataTypeEnum.STRUCT)),
                new TslPropertySpec()
                        .id("data.struct_value.string_value")
                        .name("String Value")
                        .accessMode(TslPropertySpec.AccessModeEnum.R)
                        .dataSpec(new TslDataSpec()
                                .parentId("data.struct_value")
                                .dataType(TslDataSpec.DataTypeEnum.STRING)
                                .validator(new TslDataValidatorSpec()
                                        .minSize(5)
                                        .maxSize(15))),
                new TslPropertySpec()
                        .id("data.array_value")
                        .name("Array Value")
                        .accessMode(TslPropertySpec.AccessModeEnum.R)
                        .dataSpec(new TslDataSpec()
                                .parentId("data")
                                .dataType(TslDataSpec.DataTypeEnum.ARRAY)
                                .elementDataType(TslDataSpec.ElementDataTypeEnum.INT)
                                .validator(new TslDataValidatorSpec()
                                        .minSize(5)
                                        .maxSize(15))),
        ]
        def thingSpec = new ThingSpec()
                .properties(properties)

        when:
        def result = MscTslUtils.getPropertiesEntities("", "", thingSpec)

        then:
        result.size() == 1
        def dataEntity = result.get(0)
        dataEntity.identifier == "data"
        dataEntity.type == EntityType.PROPERTY
        dataEntity.valueType == EntityValueType.OBJECT

        def children = dataEntity.children
        children.size() == 4

        def enumValueEntity = children.get(0)
        enumValueEntity.identifier == "data.enum_value"
        enumValueEntity.type == EntityType.PROPERTY
        enumValueEntity.valueType == EntityValueType.STRING
        enumValueEntity.attributes["enum"]["a"] == "1"
        enumValueEntity.attributes["enum"]["b"] == "2"

        def longValueEntity = children.get(1)
        longValueEntity.identifier == "data.long_value"
        longValueEntity.type == EntityType.PROPERTY
        longValueEntity.valueType == EntityValueType.LONG
        longValueEntity.attributes["fraction_digits"] == 2
        longValueEntity.attributes["min"] == 9
        longValueEntity.attributes["max"] == 23

        def structValueEntity = children.get(2)
        structValueEntity.identifier == "data.struct_value"
        structValueEntity.type == EntityType.PROPERTY
        structValueEntity.valueType == EntityValueType.OBJECT
        structValueEntity.children.size() == 0
        structValueEntity.attributes.isEmpty()

        def stringValueEntity = children.get(3)
        stringValueEntity.identifier == "data.struct_value@string_value"
        stringValueEntity.type == EntityType.PROPERTY
        stringValueEntity.valueType == EntityValueType.STRING
        stringValueEntity.attributes["min_length"] == 5
        stringValueEntity.attributes["max_length"] == 15
    }

}
