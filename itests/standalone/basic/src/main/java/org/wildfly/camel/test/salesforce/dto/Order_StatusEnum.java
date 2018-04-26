/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Wed May 02 14:43:21 CEST 2018
 */
package org.wildfly.camel.test.salesforce.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist Status
 */
public enum Order_StatusEnum {

    // Activated
    ACTIVATED("Activated"),
    // Draft
    DRAFT("Draft");

    final String value;

    private Order_StatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static Order_StatusEnum fromValue(String value) {
        for (Order_StatusEnum e : Order_StatusEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
