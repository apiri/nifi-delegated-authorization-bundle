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
package com.aldrinpiri.authorization.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.reporting.InitializationException;

import com.aldrinpiri.authorization.util.AuthorizationToken;
import com.aldrinpiri.authorization.util.StandardAuthorizationToken;

@Tags({"example"})
@CapabilityDescription("Example ControllerService implementation of DelegatedAuthorizationProviderService.")
public class FileBasedDelegatedAuthorizationProviderService extends AbstractControllerService implements DelegatedAuthorizationProviderService {

    public static final PropertyDescriptor AUTHORIZATION_FILE_PATH = new PropertyDescriptor.Builder()
        .name("File")
        .description("Location of the tokens file")
        .required(true)
        .addValidator(StandardValidators.FILE_EXISTS_VALIDATOR)
        .build();

    private static final List<PropertyDescriptor> properties;

    static {
        final List<PropertyDescriptor> props = new ArrayList<>();
        props.add(AUTHORIZATION_FILE_PATH);
        properties = Collections.unmodifiableList(props);
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return properties;
    }


    private volatile Set<AuthorizationToken> authorizationTokens = new HashSet<>();

    /**
     * @param context the configuration context
     * @throws InitializationException if unable to create a database connection
     */
    @OnEnabled
    public void onEnabled(final ConfigurationContext context) throws InitializationException {
        Set<AuthorizationToken> tokensToUse = new HashSet<>();

        final String authorizationFilePath = context.getProperty(AUTHORIZATION_FILE_PATH).getValue();

        try (Scanner authsScanner = new Scanner(new File(authorizationFilePath))) {
            authsScanner.useDelimiter(",");
            while (authsScanner.hasNext()) {
                tokensToUse.add(new StandardAuthorizationToken(authsScanner.next()));
            }
        } catch (IOException ioe) {
            throw new InitializationException("Could not initialize " + this.getClass().getSimpleName() + " when trying to acquire authorizations.", ioe);
        }
        getLogger().info("Providing authorities for the following: {}", new Object[]{tokensToUse});
        this.authorizationTokens = tokensToUse;
    }

    @Override
    public Set<AuthorizationToken> getAuthorizationTokens() {
        return Collections.unmodifiableSet(this.authorizationTokens);
    }

}
