/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Tue May 29 08:24:13 BST 2018
 */
package org.wildfly.camel.test.salesforce.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist Active__c
 */
public enum ActiveEnum {

    // No
    NO("No"),
    // Yes
    YES("Yes");

    final String value;

    private ActiveEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static ActiveEnum fromValue(String value) {
        for (ActiveEnum e : ActiveEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
