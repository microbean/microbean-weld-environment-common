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

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.Arrays;
import java.util.Set;

import org.jboss.weld.bootstrap.api.Bootstrap;

import org.jboss.weld.environment.logging.CommonLogger;

import org.jboss.weld.environment.deployment.WeldBeanDeploymentArchive;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveScanner;
import org.jboss.weld.environment.deployment.discovery.DiscoveryStrategy;
import org.jboss.weld.environment.deployment.discovery.DiscoveryStrategyFactory; // for javadoc only
import org.jboss.weld.environment.deployment.discovery.ReflectionDiscoveryStrategy;

import org.jboss.weld.environment.deployment.discovery.jandex.Jandex;

import org.jboss.weld.resources.spi.ResourceLoader;

/**
 * A {@link DelegatingDiscoveryStrategy} that delegates to a {@link
 * DiscoveryStrategy} that is virtually 100% compatible with the
 * default Weld behavior.
 *
 * <p>This class exists primarily for those cases where end users wish
 * to provide their own {@link DiscoveryStrategy} via the Java SPI
 * mechanism, but need to fall back to default behavior, whatever that
 * may be, if something is not right.</p>
 *
 * <p>This class has been designed like other {@link
 * DiscoveryStrategy} implementations to be instantiated by the {@link
 * DiscoveryStrategyFactory} class.  Normally that means its instances
 * are found in a Java SPI {@code META-INF/services} service provider
 * file.  Normally a {@link DiscoveryStrategy} implementation is
 * instantiated, and then its {@link
 * #setResourceLoader(ResourceLoader)}, {@link
 * #setBootstrap(Bootstrap)}, {@link
 * #setInitialBeanDefiningAnnotations(Set)} and {@link
 * #setScanner(BeanArchiveScanner)} methods are called.  Subclasses or
 * callers should be prepared for any fields that might be set by
 * these calls to be {@code null} at {@link #performDiscovery()} time.</p>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>Instances of this class are not safe for concurrent use by
 * multiple threads.</p>
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 *
 * @see DelegatingDiscoveryStrategy
 *
 * @see DiscoveryStrategyFactory
 */
public class DefaultDiscoveryStrategy extends DelegatingDiscoveryStrategy {

  protected ResourceLoader resourceLoader;

  protected Bootstrap bootstrap;

  protected Set<Class<? extends Annotation>> initialBeanDefiningAnnotations;

  protected BeanArchiveScanner beanArchiveScanner;

  public DefaultDiscoveryStrategy() {
    this(null);
  }

  public DefaultDiscoveryStrategy(final DiscoveryStrategy delegate) {
    super(delegate);
  }

  @Override
  public void setResourceLoader(final ResourceLoader resourceLoader) {
    super.setResourceLoader(resourceLoader);
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setBootstrap(final Bootstrap bootstrap) {
    super.setBootstrap(bootstrap);
    this.bootstrap = bootstrap;
  }

  @Override
  public void setInitialBeanDefiningAnnotations(final Set<Class<? extends Annotation>> initialBeanDefiningAnnotations) {
    super.setInitialBeanDefiningAnnotations(initialBeanDefiningAnnotations);
    this.initialBeanDefiningAnnotations = initialBeanDefiningAnnotations;
  }

  @Override
  public void setScanner(final BeanArchiveScanner beanArchiveScanner) {
    super.setScanner(beanArchiveScanner);
    if (this.beanArchiveScanner == null) {
      this.beanArchiveScanner = beanArchiveScanner;
    }
  }

  @Override
  public Set<WeldBeanDeploymentArchive> performDiscovery() {
    this.delegate = acquireDelegate();
    return super.performDiscovery();
  }

  protected DiscoveryStrategy acquireDelegate() {
    DiscoveryStrategy returnValue = null;
    if (this.delegate == null) {
      if (Jandex.isJandexAvailable(this.resourceLoader)) {
        if (isJandexStrategyDisabled()) {
          CommonLogger.LOG.jandexDiscoveryStrategyDisabled();
        } else {
          CommonLogger.LOG.usingJandex();
          try {
            returnValue = Jandex.createJandexDiscoveryStrategy(this.resourceLoader, this.bootstrap, this.initialBeanDefiningAnnotations);
          } catch (final Exception exception) {
            throw CommonLogger.LOG.unableToInstantiate(Jandex.JANDEX_DISCOVERY_STRATEGY_CLASS_NAME,
                                                       Arrays.toString(new Object[] { this.resourceLoader,
                                                                                      this.bootstrap,
                                                                                      this.initialBeanDefiningAnnotations }),
                                                       exception);
          }
        }
      }
      if (returnValue == null) {
        returnValue = new ReflectionDiscoveryStrategy(this.resourceLoader, this.bootstrap, this.initialBeanDefiningAnnotations);
      }
      assert returnValue != null;
      if (this.beanArchiveScanner != null) {
        returnValue.setScanner(this.beanArchiveScanner);
      }
      this.delegateAcquired(returnValue);
    } else {
      returnValue = this.delegate;
    }
    return returnValue;
  }

  protected void delegateAcquired(final DiscoveryStrategy delegate) {

  }

  private static final boolean isJandexStrategyDisabled() {
    return (Boolean)AccessController.doPrivileged((PrivilegedAction)() -> Boolean.getBoolean(Jandex.DISABLE_JANDEX_DISCOVERY_STRATEGY));
  }

}
