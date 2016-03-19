package org.rapidpm.proxybuilder.examples.dynamic.v006;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.rapidpm.proxybuilder.core.metrics.RapidPMMetricsRegistry;
import org.rapidpm.proxybuilder.examples.dynamic.model.ServiceV001;
import org.rapidpm.proxybuilder.type.dymamic.DynamicProxyBuilder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 *  Copyright (C) 2012 RapidPM
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * Created by RapidPM - Team on 19.11.15.
 */
public class MainV006 {

  private MainV006() {
  }

  public static void main(String[] args) {
    final ServiceV001 serviceV001 = DynamicProxyBuilder
        .createBuilder(ServiceV001.class, new ServiceV006Impl())
        .addMetrics()
        .build();
    System.out.println("Proxy created " + LocalDateTime.now());
    System.out.println(serviceV001.doWork("invoke") + " " + LocalDateTime.now());
    final MetricRegistry metrics = RapidPMMetricsRegistry.getInstance().getMetrics();
    final ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.NANOSECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.start(1, TimeUnit.SECONDS);
    IntStream.range(0, 10_000_000).forEach(i -> serviceV001.doWork("" + i));
  }

  private static class ServiceV006Impl implements ServiceV001 {
    @Override
    public String doWork(final String txt) {
      return txt;
    }
  }
}
