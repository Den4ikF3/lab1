package ua.nuk.trspo.lab1;

public class IntegralCalculator {

    private final int threads;

    public IntegralCalculator(int threads) {
        if (threads < 1) {
            throw new IllegalArgumentException("Кількість потоків має бути >= 1");
        }
        this.threads = threads;
    }

    public int getThreads() {
        return threads;
    }

    /**
     * @param a кінець інтервалу (початок)
     * @param b кінець інтервалу (кінець)
     * @param n кількість кроків розбиття
     * @param f підінтегральна функція
     */
    public double calculate(double a, double b, long n, Function f) {
        if (n < 1) {
            throw new IllegalArgumentException("Кількість кроків має бути >= 1");
        }
        if (f == null) {
            throw new IllegalArgumentException("Функція не задана");
        }
        double h = (b - a) / n;

        int count = (int) Math.min(threads, n);
        double[] partial = new double[count];
        Thread[] workers = new Thread[count];

        long base = n / count;
        long remainder = n % count;
        long next = 1;
        for (int k = 0; k < count; k++) {
            long size = base + (k < remainder ? 1 : 0);
            workers[k] = new Thread(new Worker(a, h, next, next + size - 1, f, partial, k),
                    "integral-worker-" + k);
            next += size;
        }

        for (Thread w : workers) {
            w.start();
        }
        try {
            for (Thread w : workers) {
                w.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Обчислення перервано", e);
        }

        double sum = 0;
        for (double p : partial) {
            sum += p;
        }
        return sum * h;
    }

    private static final class Worker implements Runnable {
        private final double a;
        private final double h;
        private final long from;
        private final long to;
        private final Function f;
        private final double[] result;
        private final int index;

        Worker(double a, double h, long from, long to, Function f, double[] result, int index) {
            this.a = a;
            this.h = h;
            this.from = from;
            this.to = to;
            this.f = f;
            this.result = result;
            this.index = index;
        }

        @Override
        public void run() {
            double s = 0;
            for (long i = from; i <= to; i++) {
                s += f.calculate(a + i * h);
            }
            result[index] = s;
        }
    }
}
