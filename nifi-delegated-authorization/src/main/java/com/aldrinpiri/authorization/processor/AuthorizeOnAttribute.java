package com.aldrinpiri.authorization.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import com.aldrinpiri.authorization.service.DelegatedAuthorizationProviderService;
import com.aldrinpiri.authorization.util.AuthorizationToken;
import com.aldrinpiri.authorization.util.StandardAuthorizationToken;

@EventDriven
@SideEffectFree
@SupportsBatching
@InputRequirement(Requirement.INPUT_REQUIRED)
@Tags({"authorization", "token", "route"})
@SeeAlso({DelegatedAuthorizationProviderService.class})
@CapabilityDescription("")
public class AuthorizeOnAttribute extends AbstractProcessor {

    public static final PropertyDescriptor AUTHORIZATION_PROVIDER_SERVICE_PROP = new PropertyDescriptor.Builder()
        .name("Authorization Provider Service")
        .description("")
        .identifiesControllerService(DelegatedAuthorizationProviderService.class)
        .required(true)
        .build();

    public static final PropertyDescriptor TOKEN_ATTRIBUTE_NAME_PROP = new PropertyDescriptor.Builder()
        .name("Token Attribute Name")
        .description("The attribute to be used in determining needed access.")
        .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
        .required(true)
        .build();

    public static final Relationship AUTHORIZED_REL = new Relationship.Builder()
        .name("Authorized")
        .description("All FlowFiles that have the attribute value contained within the "
            + "authorization token set provided by the configured authorization provider service are sent to this relationship")
        .build();

    public static final Relationship UNAUTHORIZED_REL = new Relationship.Builder()
        .name("Unauthorized")
        .description("All FlowFiles that DO NOT have the attribute value contained within the "
            + "authorization token set provided by the configured authorization provider service are sent to this relationship")
        .build();

    @Override
    public Set<Relationship> getRelationships() {
        final Set<Relationship> relationships = new HashSet<>();
        relationships.add(AUTHORIZED_REL);
        relationships.add(UNAUTHORIZED_REL);
        return relationships;
    }

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        final List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(AUTHORIZATION_PROVIDER_SERVICE_PROP);
        properties.add(TOKEN_ATTRIBUTE_NAME_PROP);
        return properties;
    }

    @Override
    public void onTrigger(ProcessContext ctx, ProcessSession session) throws ProcessException {

        FlowFile flowFile = session.get();

        if (flowFile == null) {
            return;
        }

        final DelegatedAuthorizationProviderService authProviderSvc = ctx.getProperty(AUTHORIZATION_PROVIDER_SERVICE_PROP).asControllerService(DelegatedAuthorizationProviderService.class);
        final String tokenValueAttribute = ctx.getProperty(TOKEN_ATTRIBUTE_NAME_PROP).getValue();

        final String tokenValue = flowFile.getAttribute(tokenValueAttribute);
        if (StringUtils.isEmpty(tokenValue)) {
            session.transfer(flowFile, UNAUTHORIZED_REL);
            return;
        }

        final AuthorizationToken authorizationToken = new StandardAuthorizationToken(tokenValue);
        final Set<AuthorizationToken> authorizationTokens = authProviderSvc.getAuthorizationTokens();

        session.transfer(flowFile, authorizationTokens.contains(authorizationToken) ? AUTHORIZED_REL : UNAUTHORIZED_REL);
    }
}