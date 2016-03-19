/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.rapidpm.proxybuilder.examples.dynamic.v007;

import org.rapidpm.proxybuilder.type.dymamic.DynamicProxyBuilder;
import org.rapidpm.proxybuilder.type.dymamic.SecurityRule;
import org.rapidpm.proxybuilder.type.dymamic.virtual.VirtualDynamicProxyInvocationHandler;

import java.lang.reflect.Method;

import static java.lang.System.out;
import static org.rapidpm.proxybuilder.type.dymamic.virtual.CreationStrategy.SOME_DUPLICATES;

public class Main007 {

  private Main007() {
  }

  public static void main(String[] args) {
    final Service service = createProxyPreJDK8();
    final String s = service.doWork("and gogogo");
    out.println("s = " + s);
  }

  private static Service createProxyPreJDK8() {
    return DynamicProxyBuilder
        .createBuilder(Service.class, SOME_DUPLICATES, new VirtualDynamicProxyInvocationHandler.ServiceFactory<Service>() {
          @Override
          public Service createInstance() {
            return new ServiceImpl();
          }
        })
        .addIPreAction(new DynamicProxyBuilder.PreAction<Service>() {
          @Override
          public void execute(final Service original, final Method method, final Object[] args) throws Throwable {
            out.println("PreAction - " + method.getName());
          }
        })
        .addIPostAction(new DynamicProxyBuilder.PostAction<Service>() {
          @Override
          public void execute(final Service original, final Method method, final Object[] args) throws Throwable {
            out.println("PostAction 01 - " + method.getName());
          }
        })
        .addIPostAction(new DynamicProxyBuilder.PostAction<Service>() {
          @Override
          public void execute(final Service original, final Method method, final Object[] args) throws Throwable {
            out.println("PostAction 02 - " + method.getName());
          }
        })
        .addSecurityRule(new SecurityRule() {
          @Override
          public boolean checkRule() {
            out.println("Sec Rule 001 ");
            return true;
          }
        })
        .addSecurityRule(new SecurityRule() {
          @Override
          public boolean checkRule() {
            out.println("Sec Rule 002 ");
            return true;
          }
        })
        .addMetrics()
        .build();
  }

  private static Service createProxyJDK8() {
    return DynamicProxyBuilder
        .createBuilder(Service.class, SOME_DUPLICATES, ServiceImpl::new)
        .addIPreAction((original, method, args) -> out.println("PreAction - " + method.getName()))
        .addIPostAction((original, method, args) -> out.println("PostAction 01 - " + method.getName()))
        .addIPostAction((original, method, args) -> out.println("PostAction 02 - " + method.getName()))
        .addSecurityRule(() -> {
          out.println("Sec Rule 001 ");
          return true;
        })
        .addSecurityRule(() -> {
          out.println("Sec Rule 002 ");
          return true;
        })
        .addMetrics()
        .build();
  }


  public interface Service {
    String doWork(String txt);
  }

  public static class ServiceImpl implements Service {

    @Override
    public String doWork(final String txt) {
      return txt + " from orig ;-)";
    }
  }

}
