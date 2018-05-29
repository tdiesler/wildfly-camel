/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Tue May 29 08:24:13 BST 2018
 */
package org.wildfly.camel.test.salesforce.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist MyMultiselect__c
 */
public enum MyMultiselectEnum {

    // bar
    BAR("bar"),
    // cheese
    CHEESE("cheese"),
    // foo
    FOO("foo");

    final String value;

    private MyMultiselectEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static MyMultiselectEnum fromValue(String value) {
        for (MyMultiselectEnum e : MyMultiselectEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
