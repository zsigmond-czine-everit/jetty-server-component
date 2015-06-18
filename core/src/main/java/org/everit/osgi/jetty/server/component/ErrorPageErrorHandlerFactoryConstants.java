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
package org.everit.osgi.jetty.server.component;

/**
 * Constants of ErrorPageErrorHandlerFactory component.
 */
public final class ErrorPageErrorHandlerFactoryConstants {

  public static final String ATTR_CACHE_CONTROL = "cacheControl";

  public static final String ATTR_ERROR_PAGES = "errorPages";

  public static final String ATTR_SHOW_MESSAGE_IN_TITLE = "showMessageInTitle";

  public static final String ATTR_SHOW_STACKS = "showStacks";

  public static final String DEFAULT_CACHE_CONTROL = "must-revalidate,no-cache,no-store";

  public static final boolean DEFAULT_SHOW_MESSAGE_IN_TITLE = true;

  public static final boolean DEFAULT_SHOW_STACKS = true;

  public static final String FACTORY_PID =
      "org.everit.osgi.jetty.server.component.ErrorPageErrorHandlerFactory";

  private ErrorPageErrorHandlerFactoryConstants() {
  }
}