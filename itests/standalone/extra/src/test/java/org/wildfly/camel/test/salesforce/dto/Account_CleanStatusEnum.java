/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri May 04 15:50:21 CEST 2018
 */
package org.wildfly.camel.test.salesforce.dto;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist CleanStatus
 */
@Generated("org.apache.camel.maven.CamelSalesforceMojo")
public enum Account_CleanStatusEnum {

    // Acknowledged
    ACKNOWLEDGED("Acknowledged"),
    // Different
    DIFFERENT("Different"),
    // Inactive
    INACTIVE("Inactive"),
    // Matched
    MATCHED("Matched"),
    // NotFound
    NOTFOUND("NotFound"),
    // Pending
    PENDING("Pending"),
    // SelectMatch
    SELECTMATCH("SelectMatch"),
    // Skipped
    SKIPPED("Skipped");

    final String value;

    private Account_CleanStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static Account_CleanStatusEnum fromValue(String value) {
        for (Account_CleanStatusEnum e : Account_CleanStatusEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
