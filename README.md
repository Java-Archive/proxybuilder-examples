# ProxyBuilder Examples

## DynamicProxy Examples

### DynamicProxy-V001
Here you will get a DynamicProxy with no additionally functionality.
It will only delegate the Method-Calls to the Delegator.
The Delegator will be created before the Proxy, because the Instance is
part of the Builder-MethodCall.

```java
     final ServiceV001 serviceV001 = DynamicProxyBuilder
         .createBuilder(ServiceV001.class, new ServiceV001Impl())
         .build();
```

System.out:
 * constructed 2015-11-19T13:46:50.438
 * Proxy created 2015-11-19T13:46:50.450
 * ServiceV001Impl - invoke 2015-11-19T13:46:50.450
 
### DynamicProxy-V002
  Here we will extend DynamicProxy-V001 with an PreAction.
  PreActions are called before every invoke on every method.

```java
    final ServiceV001 serviceV001 = DynamicProxyBuilder
        .createBuilder(ServiceV001.class, new ServiceV001Impl())
        .addIPreAction((original, method, args1) -> {
          System.out.println("PreAction = " + LocalDateTime.now());
        })
        .build();
```

System.out:
* constructed 2015-11-19T13:49:58.644
* Proxy created 2015-11-19T13:49:58.658
* PreAction = 2015-11-19T13:49:58.658
* ServiceV001Impl - invoke 2015-11-19T13:49:58.658
* PreAction = 2015-11-19T13:49:58.658
* ServiceV001Impl - invoke 2015-11-19T13:49:58.658

### DynamicProxy-V003
Now we will use PostActions. PostActions are invoked after the delegator method call.

```java
    final ServiceV001 serviceV001 = DynamicProxyBuilder
        .createBuilder(ServiceV001.class, new ServiceV001Impl())
        .addIPostAction((original, method, args1)
            -> System.out.println("PostAction = " + LocalDateTime.now()))
        .build();
```

System.out:
* constructed 2015-11-19T13:54:54.453
* Proxy created 2015-11-19T13:54:54.468
* doWork = invoke - 2015-11-19T13:54:54.468
* PostAction = 2015-11-19T13:54:54.468
* ServiceV001Impl - invoke 2015-11-19T13:54:54.468
* doWork = invoke - 2015-11-19T13:54:54.468
* PostAction = 2015-11-19T13:54:54.468
* ServiceV001Impl - invoke 2015-11-19T13:54:54.468

### DynamicProxy-V004
You can add Security Rules. This Rule will be executed before every method call.
If the Rule will return true, the delegator-method will be invoked.

```java
    final ServiceV001 serviceV001 = DynamicProxyBuilder
        .createBuilder(ServiceV001.class, new ServiceV001Impl())
        .addSecurityRule(() -> {
          System.out.println("checkRule = " + LocalDateTime.now());
          return true;
        })
        .build();
```

System.out:
* constructed 2015-11-19T13:59:20.751
* Proxy created 2015-11-19T13:59:20.765
* checkRule = 2015-11-19T13:59:20.765
* doWork = invoke - 2015-11-19T13:59:20.765
* ServiceV001Impl - invoke 2015-11-19T13:59:20.766

### DynamicProxy-V005
You can add Security Rules. This Rule will be executed before every method call.
If the Rule will return false, the delegator-method will not be invoked.

```java
    final ServiceV001 serviceV001 = DynamicProxyBuilder
        .createBuilder(ServiceV001.class, new ServiceV001Impl())
        .addSecurityRule(() -> {
          System.out.println("checkRule = " + LocalDateTime.now());
          return false;
        })
        .build();
```

System.out:
* constructed 2015-11-19T14:03:57.070
* Proxy created 2015-11-19T14:03:57.084
* checkRule = 2015-11-19T14:03:57.085
* null 2015-11-19T14:03:57.085

### DynamicProxy-V006
To add Metrics (Dropwizard) you could use the .addMetrics()
Method. Metrics are registered internaly by Dropwizard-Metrics.
For every Method there will be a Histogramm.
 
```java
  public static void main(String[] args) {
    final ServiceV001 serviceV001 = DynamicProxyBuilder
        .createBuilder(ServiceV001.class, new ServiceV006Impl())
        .addMetrics()
        .build();
    System.out.println("Proxy created " + LocalDateTime.now());
    System.out.println(serviceV001.doWork("invoke") + " " + LocalDateTime.now());
    final MetricRegistry metrics = MetricsRegistry.getInstance().getMetrics();
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
```

System.out:

```
19.11.15 14:11:17 ==============================================================
-- Histograms ------------------------------------------------------------------
  ServiceV001.doWork
               count = 1507936
                 min = 21
                 max = 196
                mean = 38,28
              stddev = 11,58
              median = 36,00
                75% <= 41,00
                95% <= 50,00
                98% <= 59,00
                99% <= 63,00
              99.9% <= 177,00
19.11.15 14:11:18 ==============================================================
-- Histograms ------------------------------------------------------------------
ServiceV001.doWork
             count = 3733355
               min = 21
               max = 168
              mean = 34,93
            stddev = 9,04
            median = 34,00
              75% <= 36,00
              95% <= 45,00
              98% <= 57,00
              99% <= 63,00
            99.9% <= 166,00
```             


