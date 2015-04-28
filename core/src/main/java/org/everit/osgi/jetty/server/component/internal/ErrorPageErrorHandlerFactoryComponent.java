/*
 * Copyright (C) 2015 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.osgi.jetty.server.component.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.everit.osgi.ecm.annotation.AttributeOrder;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.attribute.BooleanAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ConfigurationException;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.osgi.jetty.server.ErrorHandlerFactory;
import org.everit.osgi.jetty.server.component.ErrorPageErrorHandlerFactoryConstants;
import org.osgi.framework.Constants;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Component that makes it possible to instantiate a pre-configured {@link ErrorPageErrorHandler}
 * and register it as an OSGi service.
 */
@Component(componentId = ErrorPageErrorHandlerFactoryConstants.FACTORY_PID,
    configurationPolicy = ConfigurationPolicy.FACTORY,
    localizationBase = "OSGI-INF/metatype/errorPageErrorHandlerFactory")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes(@StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION, optional = true))
@AttributeOrder({
    ErrorPageErrorHandlerFactoryConstants.ATTR_ERROR_PAGES,
    ErrorPageErrorHandlerFactoryConstants.ATTR_CACHE_CONTROL,
    ErrorPageErrorHandlerFactoryConstants.ATTR_SHOW_MESSAGE_IN_TITLE,
    ErrorPageErrorHandlerFactoryConstants.ATTR_SHOW_STACKS,
    Constants.SERVICE_DESCRIPTION })
@Service
public class ErrorPageErrorHandlerFactoryComponent implements ErrorHandlerFactory {

  /**
   * Range of codes.
   */
  private static class CodeRange {

    public int from;

    public int to;

    public CodeRange(final int from, final int to) {
      this.from = from;
      this.to = to;
    }
  }

  private String cacheControl;

  private final Map<Object, String> errorPages = new LinkedHashMap<Object, String>();

  private boolean showMessageInTitle;

  private boolean showStacks;

  @Override
  public ErrorHandler createErrorHandler() {
    ErrorPageErrorHandler result = new ErrorPageErrorHandler();
    result.setCacheControl(cacheControl);
    result.setShowMessageInTitle(showMessageInTitle);
    result.setShowStacks(showStacks);

    Set<Entry<Object, String>> errorPageMappingEntries = errorPages.entrySet();
    for (Entry<Object, String> entry : errorPageMappingEntries) {
      Object mappingKey = entry.getKey();
      if (mappingKey instanceof CodeRange) {
        CodeRange codeRange = (CodeRange) mappingKey;
        result.addErrorPage(codeRange.from, codeRange.to, entry.getValue());
      } else {
        result.addErrorPage((String) mappingKey, entry.getValue());
      }
    }
    return result;
  }

  @StringAttribute(attributeId = ErrorPageErrorHandlerFactoryConstants.ATTR_CACHE_CONTROL,
      defaultValue = ErrorPageErrorHandlerFactoryConstants.DEFAULT_CACHE_CONTROL)
  public void setCacheControl(final String cacheControl) {
    this.cacheControl = cacheControl;
  }

  /**
   * Sets the error pages mapping by parsing the definition entries.
   *
   * @param errorPageMappings
   *          The error pages mappings in properties file like format.
   */
  @StringAttribute(attributeId = ErrorPageErrorHandlerFactoryConstants.ATTR_ERROR_PAGES,
      optional = true)
  public void setErrorPages(final String[] errorPageMappings) {
    if (errorPageMappings == null) {
      return;
    }

    for (String definition : errorPageMappings) {
      int indexOfEquals = definition.indexOf('=');
      if (!(indexOfEquals > 1 && indexOfEquals < definition.length() - 2)) {
        throw new ConfigurationException(
            "Syntax error in error page mapping: No equals found in text: " + definition);
      }

      String mappedValue = definition.substring(0, indexOfEquals);
      String uri = definition.substring(indexOfEquals + 1);

      int indexOfColon = mappedValue.indexOf('-');
      if (indexOfColon >= 0) {
        try {
          int from = 0;
          if (indexOfColon > 0) {
            String fromString = mappedValue.substring(0, indexOfColon);
            from = Integer.parseInt(fromString);
          }

          int to = Integer.MAX_VALUE;
          if (indexOfColon < mappedValue.length() - 1) {
            String toString = mappedValue.substring(indexOfColon + 1);
            to = Integer.parseInt(toString);
          }
          errorPages.put(new CodeRange(from, to), uri);
        } catch (NumberFormatException e) {
          throw new ConfigurationException("Invalid port range in mapping: " + definition, e);
        }
      } else {
        errorPages.put(mappedValue, uri);
      }
    }
  }

  @BooleanAttribute(attributeId = ErrorPageErrorHandlerFactoryConstants.ATTR_SHOW_MESSAGE_IN_TITLE,
      defaultValue = ErrorPageErrorHandlerFactoryConstants.DEFAULT_SHOW_MESSAGE_IN_TITLE)
  public void setShowMessageInTitle(final boolean showMessageInTitle) {
    this.showMessageInTitle = showMessageInTitle;
  }

  @BooleanAttribute(attributeId = ErrorPageErrorHandlerFactoryConstants.ATTR_SHOW_STACKS,
      defaultValue = ErrorPageErrorHandlerFactoryConstants.DEFAULT_SHOW_STACKS)
  public void setShowStacks(final boolean showStacks) {
    this.showStacks = showStacks;
  }
}
