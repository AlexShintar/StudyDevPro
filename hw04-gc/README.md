В первом случае оптимальное значение достигло при 2048MB кэша,
значение 57095 - погрешность измерений, при повторных тестах оптимальное также около 2048
11:19:39.788 [main] INFO ru.calculator.JarLauncher -- Memory(MB) |   Time(ms)
11:19:39.790 [main] INFO ru.calculator.JarLauncher -- -----------------------
11:19:39.806 [main] INFO ru.calculator.JarLauncher --        256 |      59862
11:19:39.806 [main] INFO ru.calculator.JarLauncher --        512 |      54210
11:19:39.806 [main] INFO ru.calculator.JarLauncher --        768 |      52027
11:19:39.806 [main] INFO ru.calculator.JarLauncher --       1024 |      53042
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       1280 |      56325
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       1536 |      53083
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       1792 |      57095
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       2048 |      50856
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       2304 |      56214
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       2560 |      52936
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       2816 |      53080
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       3072 |      52743
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       3328 |      49878
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       3584 |      54342
11:19:39.808 [main] INFO ru.calculator.JarLauncher --       3840 |      50621

При оптимизации уже после 512Mb размер кэша не приводил к существенному ускорению.
11:31:57.432 [main] INFO ru.calculator.JarLauncher -- Memory(MB) |   Time(ms)
11:31:57.432 [main] INFO ru.calculator.JarLauncher -- -----------------------
11:31:57.432 [main] INFO ru.calculator.JarLauncher --        256 |      53032
11:31:57.432 [main] INFO ru.calculator.JarLauncher --        512 |      48832
11:31:57.432 [main] INFO ru.calculator.JarLauncher --        768 |      49339
11:31:57.432 [main] INFO ru.calculator.JarLauncher --       1024 |      53265
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       1280 |      47223
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       1536 |      55948
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       1792 |      46280
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       2048 |      51306
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       2304 |      48371
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       2560 |      46975
11:31:57.436 [main] INFO ru.calculator.JarLauncher --       2816 |      49774
11:31:57.438 [main] INFO ru.calculator.JarLauncher --       3072 |      45819
11:31:57.438 [main] INFO ru.calculator.JarLauncher --       3328 |      49164
11:31:57.438 [main] INFO ru.calculator.JarLauncher --       3584 |      47946
11:31:57.439 [main] INFO ru.calculator.JarLauncher --       3840 |      44316