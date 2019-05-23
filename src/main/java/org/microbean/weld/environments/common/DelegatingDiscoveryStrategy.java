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

import java.lang.annotation.Annotation;

import java.util.Collections;
import java.util.Set;

import org.jboss.weld.bootstrap.api.Bootstrap;

import org.jboss.weld.environment.deployment.WeldBeanDeploymentArchive;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveHandler;
import org.jboss.weld.environment.deployment.discovery.BeanArchiveScanner;
import org.jboss.weld.environment.deployment.discovery.DiscoveryStrategy;

import org.jboss.weld.resources.spi.ClassFileServices;
import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * An abstract {@link DiscoveryStrategy} implementation that delegates
 * all calls to another {@link DiscoveryStrategy}.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see DiscoveryStrategy
 */
public abstract class DelegatingDiscoveryStrategy implements DiscoveryStrategy {

  protected volatile DiscoveryStrategy delegate;

  /**
   * Creates a new {@link DelegatingDiscoveryStrategy}.
   *
   * @param delegate the {@link DiscoveryStrategy} to which all calls
   * will be delegated; may be {@code null}
   */
  protected DelegatingDiscoveryStrategy(final DiscoveryStrategy delegate) {
    super();
    this.delegate = delegate;
  }

  @Override
  public void setResourceLoader(final ResourceLoader resourceLoader) {
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate != null) {
      delegate.setResourceLoader(resourceLoader);
    }
  }

  @Override
  public void setBootstrap(final Bootstrap bootstrap) {
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate != null) {
      delegate.setBootstrap(bootstrap);
    }
  }

  @Override
  public void setInitialBeanDefiningAnnotations(final Set<Class<? extends Annotation>> initialBeanDefiningAnnotations) {
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate != null) {
      delegate.setInitialBeanDefiningAnnotations(initialBeanDefiningAnnotations);
    }
  }

  @Override
  public void setScanner(final BeanArchiveScanner beanArchiveScanner) {
    // Of note: this method is called with a hard-coded scanner by
    // Weld SE, so implementations may want to ignore it.
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate != null) {
      delegate.setScanner(beanArchiveScanner);
    }
  }

  @Override
  public void registerHandler(final BeanArchiveHandler handler) {
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate != null) {
      delegate.registerHandler(handler);
    }
  }

  @Override
  public Set<WeldBeanDeploymentArchive> performDiscovery() {
    final Set<WeldBeanDeploymentArchive> returnValue;
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate == null) {
      returnValue = Collections.emptySet();
    } else {
      returnValue = delegate.performDiscovery();
    }
    return returnValue;
  }

  @Override
  public ClassFileServices getClassFileServices() {
    final ClassFileServices returnValue;
    final DiscoveryStrategy delegate = this.delegate;
    if (delegate == null) {
      returnValue = null;
    } else {
      returnValue = delegate.getClassFileServices();
    }
    return returnValue;
  }

}
