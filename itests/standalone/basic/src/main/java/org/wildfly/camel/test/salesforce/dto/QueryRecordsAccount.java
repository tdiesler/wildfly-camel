/*
 * Salesforce Query DTO generated by camel-salesforce-maven-plugin
 * Generated on: Fri May 04 12:17:35 CEST 2018
 */
package org.wildfly.camel.test.salesforce.dto;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase;

import java.util.List;

/**
 * Salesforce QueryRecords DTO for type Account
 */
public class QueryRecordsAccount extends AbstractQueryRecordsBase {

    @XStreamImplicit
    private List<Account> records;

    public List<Account> getRecords() {
        return records;
    }

    public void setRecords(List<Account> records) {
        this.records = records;
    }
}