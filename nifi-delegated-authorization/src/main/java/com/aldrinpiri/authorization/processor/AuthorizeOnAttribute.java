package com.aldrinpiri.authorization.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.behavior.SupportsBatching;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;

import com.aldrinpiri.authorization.service.DelegatedAuthorizationProviderService;

@EventDriven
@SideEffectFree
@SupportsBatching
@InputRequirement(Requirement.INPUT_REQUIRED)
@Tags({"authorization", "token", "route"})
@SeeAlso({DelegatedAuthorizationProviderService.class})
@CapabilityDescription("")
public class AuthorizeOnAttribute extends AbstractProcessor {

    public static final PropertyDescriptor AUTHORIZATION_PROVIDER_SERVICE = new PropertyDescriptor.Builder()
        .name("Authorization Provider Service")
        .description("")
        .identifiesControllerService(DelegatedAuthorizationProviderService.class)
        .required(true)
        .build();

    @Override
    protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        final List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(AUTHORIZATION_PROVIDER_SERVICE);
        return properties;
    }

    @Override
    public void onTrigger(ProcessContext processContext, ProcessSession processSession) throws ProcessException {

    }

}