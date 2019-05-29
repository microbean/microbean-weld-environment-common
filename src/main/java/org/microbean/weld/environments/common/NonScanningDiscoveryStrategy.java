/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2019 microBean™.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.microbean.weld.environments.common;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.LinkedHashSet;
import java.util.Set;

import java.util.regex.Pattern;

import org.jboss.weld.environment.deployment.discovery.jandex.JandexDiscoveryStrategy;

public class NonScanningDiscoveryStrategy extends DefaultDiscoveryStrategy {

  private static final Pattern splitPattern = Pattern.compile("\\s*,\\s*");
  
  public NonScanningDiscoveryStrategy() {
    super();
    final Set<? extends Path> beanArchivePaths = this.acquireBeanArchivePaths();
    if (beanArchivePaths != null && !beanArchivePaths.isEmpty()) {
      this.setScanner(new BeanArchiveRepository(beanArchivePaths));
    }
  }

  @Override
  protected JandexDiscoveryStrategy acquireDelegate() {
    return new JandexDiscoveryStrategy(this.resourceLoader, this.bootstrap, this.initialBeanDefiningAnnotations);
  }

  protected Set<? extends Path> acquireBeanArchivePaths() {
    final Set<Path> returnValue;
    String beanArchivePathsProperty = System.getenv("BEAN_ARCHIVE_PATHS");
    if (beanArchivePathsProperty == null) {
      beanArchivePathsProperty = System.getProperty("beanArchivePaths");
    }
    if (beanArchivePathsProperty == null) {
      returnValue = null;
    } else {
      final String[] parts = splitPattern.split(beanArchivePathsProperty);
      if (parts == null || parts.length <= 0) {
        returnValue = null;
      } else {
        returnValue = new LinkedHashSet<>();
        for (final String part : parts) {
          returnValue.add(Paths.get(part).toAbsolutePath());
        }
      }
    }
    return returnValue;
  }
  
}
