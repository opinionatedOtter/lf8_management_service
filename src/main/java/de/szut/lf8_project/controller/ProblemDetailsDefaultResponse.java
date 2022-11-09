package de.szut.lf8_project.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static java.util.Map.entry;

@Component
public class ProblemDetailsDefaultResponse extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        return Map.ofEntries(
                entry("title", errorAttributes.get("error")),
                entry("detail", errorAttributes.get("message")),
                entry("status", errorAttributes.get("status"))
        );
    }
}
