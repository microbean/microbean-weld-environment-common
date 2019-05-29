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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Priority;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.IndexReader;
import org.jboss.jandex.UnsupportedVersion;

import org.jboss.weld.environment.deployment.discovery.BeanArchiveBuilder;
import org.jboss.weld.environment.deployment.discovery.BeanArchiveHandler;

import org.jboss.weld.environment.deployment.discovery.jandex.Jandex;

/**
 * A more correct and more flexible reimplementation of the
 * Jandex-based {@link BeanArchiveHandler} that ships with Weld.
 *
 * <p>Add this to a file named {@code
 * META-INF/services/org.jboss.weld.environment.deployment.discovery.BeanArchiveHandler}
 * and it will get picked up automatically.</p>
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
@Priority(10)
public class JandexBeanArchiveHandler implements BeanArchiveHandler {

  private static final String JANDEX_INDEX_NAME = "META-INF/jandex.idx";
  
  public JandexBeanArchiveHandler() {
    super();
  }

  /**
   * Creates and returns a new {@link BeanArchiveBuilder} appropriate
   * for the supplied filesystem path.
   *
   * <p>This method may return {@code null}.</p>
   *
   * @param beanArchiveReference normally a classpath root; may be
   * {@code null} in which case {@code null} will be returned
   *
   * @return a new {@link BeanArchiveBuilder}, or {@code null}
   */
  @Override
  public BeanArchiveBuilder handle(final String beanArchiveReference) {
    final BeanArchiveBuilder returnValue;
    final IndexView index = this.getIndexView(beanArchiveReference);
    if (index == null) {
      returnValue = null;
    } else {
      returnValue = new BeanArchiveBuilder();
      returnValue.setAttribute(Jandex.INDEX_ATTRIBUTE_NAME, index);
      for (final ClassInfo classInfo : index.getKnownClasses()) {
        if (classInfo != null) {
          final Object classInfoName = classInfo.name();
          if (classInfoName != null) {
            returnValue.addClass(classInfoName.toString());
          }
        }
      }
    }
    return returnValue;
  }

  private static final Path getPath(final String beanArchiveReference) {
    final Path returnValue;
    if (beanArchiveReference == null) {
      returnValue = null;
    } else {
      returnValue = Paths.get(beanArchiveReference);
    }
    return returnValue;
  }

  protected IndexView getIndexView(final String beanArchiveReference) {
    IndexView index = null;
    final Path beanArchivePath = getPath(beanArchiveReference);
    if (beanArchivePath != null) {
      try {
        if (Files.isDirectory(beanArchivePath)) {
          final Path jandexIndexPath = beanArchivePath.resolve(Paths.get(JANDEX_INDEX_NAME));
          assert jandexIndexPath != null;
          try (final InputStream inputStream = new BufferedInputStream(Files.newInputStream(jandexIndexPath))) {
            index = new IndexReader(inputStream).read();
          }
        } else {
          try (final ZipFile zipFile = new ZipFile(beanArchivePath.toFile())) {
            final ZipEntry jandexIndexEntry = zipFile.getEntry(JANDEX_INDEX_NAME);
            if (jandexIndexEntry != null) {
              index = new IndexReader(zipFile.getInputStream(jandexIndexEntry)).read();
            }
          }
        }
      } catch (final IllegalArgumentException invalidIndex) {

      } catch (final UnsupportedVersion unsupportedVersion) {

      } catch (final IOException ioException) {

      }
    }
    return index;
  }

}
