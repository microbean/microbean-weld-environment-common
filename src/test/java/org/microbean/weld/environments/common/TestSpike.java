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

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TestSpike {

  private AutoCloseable container;
  
  public TestSpike() {
    super();
  }

  @Before
  public void startContainer() throws Exception {
    this.stopContainer();
    this.container = SeContainerInitializer.newInstance()
      .addBeanClasses(Foo.class)
      .initialize();
    assertNotNull(this.container);
  }

  @After
  public void stopContainer() throws Exception {
    if (this.container != null) {
      this.container.close();
      this.container = null;
    }
  }

  @Test
  public void test() {

  }

  private static final class Foo {

    private Foo() {
      super();
    }
    
  }
  
}
