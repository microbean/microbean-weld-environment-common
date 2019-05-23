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

import org.jboss.jandex.IndexView;

import org.jboss.weld.environment.deployment.discovery.jandex.JandexClassFileServices;
import org.jboss.weld.environment.deployment.discovery.jandex.JandexDiscoveryStrategy;

public class IndexPreservingJandexClassFileServices extends JandexClassFileServices {

  private volatile IndexView indexView;
  
  public IndexPreservingJandexClassFileServices(final JandexDiscoveryStrategy jandexDiscoveryStrategy) {
    super(jandexDiscoveryStrategy);
    assert jandexDiscoveryStrategy != null;
    this.indexView = jandexDiscoveryStrategy.getCompositeJandexIndex();
  }

  public IndexView getIndexView() {
    return this.indexView;
  }

  public void clearIndexView() {
    this.indexView = null;
  }

  @Override
  public void cleanupAfterBoot() {
    super.cleanupAfterBoot();
    if (!Boolean.getBoolean("org.microbean.weld.preserveJandexIndex")) {
      this.clearIndexView();
    }
  }
  
  @Override
  public void cleanup() {
    super.cleanup();
    this.clearIndexView();
  }

}
