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

import java.util.Collection;

import org.jboss.jandex.IndexView;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveBuilder;

import org.jboss.weld.environment.deployment.discovery.jandex.JandexDiscoveryStrategy;

public class IndexPreservingJandexDiscoveryStrategy extends JandexDiscoveryStrategy {

  private IndexPreservingJandexClassFileServices classFileServices;
  
  public IndexPreservingJandexDiscoveryStrategy() {
    super(null, null, null);
  }

  @Override
  protected void beforeDiscovery(final Collection<BeanArchiveBuilder> beanArchiveBuilders) {
    super.beforeDiscovery(beanArchiveBuilders);
    this.classFileServices = new IndexPreservingJandexClassFileServices(this);
  }

  @Override
  public IndexPreservingJandexClassFileServices getClassFileServices() {
    return this.classFileServices;
  }
  
}
