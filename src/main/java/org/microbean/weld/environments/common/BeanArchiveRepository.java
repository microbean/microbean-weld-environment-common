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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.bootstrap.spi.Scanning;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveScanner;
import org.jboss.weld.environment.deployment.discovery.BeanArchiveScanner.ScanResult;

import org.jboss.weld.metadata.BeansXmlImpl;

public class BeanArchiveRepository implements BeanArchiveScanner {

  private final Set<? extends Path> paths;
  
  public BeanArchiveRepository(final Set<? extends Path> paths) {
    super();
    if (paths == null || paths.isEmpty()) {
      this.paths = Collections.emptySet();
    } else {
      this.paths = new HashSet<>(paths);
    }
  }
  
  @Override
  public List<ScanResult> scan() {
    List<ScanResult> returnValue = null;
    for (final Path path : this.paths) {
      if (path != null) {
        if (returnValue == null) {
          returnValue = new ArrayList<>();
        }
        returnValue.add(new ScanResult(new BeansXmlImpl(Collections.emptyList(), /* enabled alternatives */
                                                        Collections.emptyList(), /* enabled alternative stereotypes */
                                                        Collections.emptyList(), /* enabled decorators */
                                                        Collections.emptyList(), /* enabled interceptors */
                                                        Scanning.EMPTY_SCANNING,
                                                        null, /* url; not actually used in Weld */
                                                        BeanDiscoveryMode.ANNOTATED,
                                                        "2.0",
                                                        false /* is trimmed */),
                                       path.toString(), /* bean archive ref */
                                       null /* bean archive id; never consulted if beanArchiveRef is set */));
      }
    }      
    return returnValue;
  }
  
}
