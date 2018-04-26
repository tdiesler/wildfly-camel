/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri May 04 12:17:35 CEST 2018
 */
package org.wildfly.camel.test.salesforce.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist Ownership
 */
public enum OwnershipEnum {

    // Other
    OTHER("Other"),
    // Private
    PRIVATE("Private"),
    // Public
    PUBLIC("Public"),
    // Subsidiary
    SUBSIDIARY("Subsidiary");

    final String value;

    private OwnershipEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static OwnershipEnum fromValue(String value) {
        for (OwnershipEnum e : OwnershipEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
