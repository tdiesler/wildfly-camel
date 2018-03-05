/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.camel.test.hipchat;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.hipchat.HipchatComponent;
import org.apache.camel.component.hipchat.HipchatConsumer;
import org.apache.camel.component.hipchat.HipchatEndpoint;
import org.apache.camel.component.hipchat.HipchatProducer;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;

class HipchatEPSuccessTestSupport extends HipchatEndpoint {

    private PostCallback callback;
    private CloseableHttpResponse closeableHttpResponse;

    HipchatEPSuccessTestSupport(String uri, HipchatComponent component, PostCallback callback, CloseableHttpResponse consumerResponse) {
        super(uri, component);
        this.callback = callback;
        this.closeableHttpResponse = consumerResponse;
    }

    public Producer createProducer() throws Exception {
        return new HipchatProducer(this) {
            @Override
            protected StatusLine post(String urlPath, Map<String, String> postParam) throws IOException {
                callback.call(postParam);
                return new BasicStatusLine(new ProtocolVersion("any", 1, 1), 204, "");
            }
        };
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new HipchatConsumer(this, processor) {
            @Override
            protected CloseableHttpResponse executeGet(HttpGet httpGet) throws IOException {
                return closeableHttpResponse;
            }
        };
    }

    static <T> T assertIsInstanceOf(Class<T> expectedType, Object value) {
        Assert.assertNotNull("Expected an instance of type: " + expectedType.getName() + " but was null", value);
        Assert.assertTrue("Object should be of type " + expectedType.getName() + " but was: " + value
                + " with the type: " + value.getClass().getName(), expectedType.isInstance(value));
        return expectedType.cast(value);
    }

    static class PostCallback {
        public Map<String, String> called;

        public void call(Map<String, String> postParam) {
            this.called = postParam;
        }
    }
}
