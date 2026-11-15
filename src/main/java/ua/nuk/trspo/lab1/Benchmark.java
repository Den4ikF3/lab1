package ua.nuk.trspo.lab1;

import java.util.Locale;
import java.util.function.Consumer;


public class Benchmark {

    public static final double A = 0.0;
    public static final double B = 1.0;
    public static final double EXACT = 2 * Math.log(2) - 1;

    public static void run(long nSmall, long nLarge, int maxThreads, Consumer<String> out) {
        Function f = new Function();
        new IntegralCalculator(2).calculate(A, B, 1_000_000, f);

        out.accept(String.format(Locale.ROOT, "Інтеграл ln(t+1) на [0;1], метод правих прямокутників, точне значення = %.10f", EXACT));
        out.accept(String.format(Locale.ROOT, "%-8s | %-22s | %-22s", "Потоки",
                "n=" + nSmall + ", мс", "n=" + nLarge + ", мс"));
        out.accept("---------+------------------------+-----------------------");
        for (int t = 1; t <= maxThreads; t++) {
            IntegralCalculator calc = new IntegralCalculator(t);
            double msSmall = time(calc, nSmall, f);
            double msLarge = time(calc, nLarge, f);
            out.accept(String.format(Locale.ROOT, "%-8d | %-22.4f | %-22.2f", t, msSmall, msLarge));
        }
    }

    private static double time(IntegralCalculator calc, long n, Function f) {
        long t0 = System.nanoTime();
        calc.calculate(A, B, n, f);
        return (System.nanoTime() - t0) / 1e6;
    }

    public static void main(String[] args) {
        long nSmall = args.length > 0 ? Long.parseLong(args[0]) : 500;
        long nLarge = args.length > 1 ? Long.parseLong(args[1]) : 100_000_000L;
        run(nSmall, nLarge, 20, System.out::println);
    }
}
