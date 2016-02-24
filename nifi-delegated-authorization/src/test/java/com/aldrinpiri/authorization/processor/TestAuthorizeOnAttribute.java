/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aldrinpiri.authorization.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;

import com.aldrinpiri.authorization.service.DelegatedAuthorizationProviderService;
import com.aldrinpiri.authorization.util.AuthorizationToken;
import com.aldrinpiri.authorization.util.StandardAuthorizationToken;

public class TestAuthorizeOnAttribute {

    @Test
    public void testNoValueForAttribute() throws Exception {

        // Establish values used throughout our test
        final String controllerServiceName = "mock-auth-provider";
        final String attributeNameProperty = "access.required";

        TestRunner testRunner = TestRunners.newTestRunner(AuthorizeOnAttribute.class);
        DelegatedAuthorizationProviderService mockService = new MockDelegatedAuthorizationProviderService();
        // Establish the controller service instance to provide the authorization values
        testRunner.addControllerService(controllerServiceName, mockService, new HashMap<String, String>());
        testRunner.enableControllerService(mockService);

        testRunner.setProperty(AuthorizeOnAttribute.AUTHORIZATION_PROVIDER_SERVICE_PROP, controllerServiceName);
        testRunner.setProperty(AuthorizeOnAttribute.TOKEN_ATTRIBUTE_NAME_PROP, attributeNameProperty);

        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(attributeNameProperty, "");
        testRunner.enqueue(new byte[]{}, attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(AuthorizeOnAttribute.UNAUTHORIZED_REL);

    }

    @Test
    public void testAuthorizedValue() throws Exception {
        // Establish values used throughout our test
        final String controllerServiceName = "mock-auth-provider";
        final String attributeNameProperty = "access.required";

        TestRunner testRunner = TestRunners.newTestRunner(AuthorizeOnAttribute.class);
        DelegatedAuthorizationProviderService mockService = new MockDelegatedAuthorizationProviderService();
        // Establish the controller service instance to provide the authorization values
        testRunner.addControllerService(controllerServiceName, mockService, new HashMap<String, String>());
        testRunner.enableControllerService(mockService);

        testRunner.setProperty(AuthorizeOnAttribute.AUTHORIZATION_PROVIDER_SERVICE_PROP, controllerServiceName);
        testRunner.setProperty(AuthorizeOnAttribute.TOKEN_ATTRIBUTE_NAME_PROP, attributeNameProperty);

        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(attributeNameProperty, "us");
        testRunner.enqueue(new byte[]{}, attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(AuthorizeOnAttribute.AUTHORIZED_REL);

    }

    @Test
    public void testUnauthorizedValue() throws Exception {
        // Establish values used throughout our test
        final String controllerServiceName = "mock-auth-provider";
        final String attributeNameProperty = "access.required";

        TestRunner testRunner = TestRunners.newTestRunner(AuthorizeOnAttribute.class);
        DelegatedAuthorizationProviderService mockService = new MockDelegatedAuthorizationProviderService();
        // Establish the controller service instance to provide the authorization values
        testRunner.addControllerService(controllerServiceName, mockService, new HashMap<String, String>());
        testRunner.enableControllerService(mockService);

        testRunner.setProperty(AuthorizeOnAttribute.AUTHORIZATION_PROVIDER_SERVICE_PROP, controllerServiceName);
        testRunner.setProperty(AuthorizeOnAttribute.TOKEN_ATTRIBUTE_NAME_PROP, attributeNameProperty);

        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(attributeNameProperty, "jp");
        testRunner.enqueue(new byte[]{}, attributes);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(AuthorizeOnAttribute.UNAUTHORIZED_REL);

    }


    public class MockDelegatedAuthorizationProviderService extends AbstractControllerService implements DelegatedAuthorizationProviderService {
        @Override
        public Set<AuthorizationToken> getAuthorizationTokens() {
            final String[] tokenValues = new String[]{"us", "ca", "gb", "mx"};
            Set<AuthorizationToken> tokens = new HashSet<>();
            for (String tokenValue : tokenValues) {
                tokens.add(new StandardAuthorizationToken(tokenValue));
            }
            return tokens;
        }

        @Override
        public void execute() throws ProcessException {
        }
    }
}
