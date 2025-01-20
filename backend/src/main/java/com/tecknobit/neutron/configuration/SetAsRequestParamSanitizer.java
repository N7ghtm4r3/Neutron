package com.tecknobit.neutron.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Set;

// TODO: 20/01/2025 TO COMMENT
@ControllerAdvice
public class SetAsRequestParamSanitizer {

    /**
     * {@inheritDoc}
     */
    @InitBinder
    public void sanitize(WebDataBinder binder) {
        binder.registerCustomEditor(Set.class, new CustomCollectionEditor(Set.class) {
            @SuppressWarnings("DataFlowIssue")
            @Override
            protected Object convertElement(@NotNull Object element) {
                if ("[]".equals(element)) {
                    return null;
                }
                return super.convertElement(element);
            }
        });
    }

}