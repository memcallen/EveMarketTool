package evemarginfinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 * @param <S> The input type
 * @param <T> The output type
 */
public class MultiThreadPool<S, T> extends Thread {

    public static class MultiThreadPoolBuilder<S, T> {

        private boolean blockin = false, blockout = false, auto_optimize = false, retry_on_ex = false;
        private int threshold_ticks = 3, num_threads = 0;
        private long milli_threshold = 200, delay = 50;
        private Supplier<? extends PoolThread> factory;

        /**
         * Creates the MultiThreadPool
         *
         * @param <S> The MultiThreadPool's input type
         * @param <T> the MultiThreadPool's output type
         * @return The MultiThreadPool
         */
        public MultiThreadPool<S, T> Create() {
            MultiThreadPool<S, T> pool = new MultiThreadPool<>(blockin, blockout);
            pool.tick_threshold = threshold_ticks;
            pool.thread_threshold = milli_threshold;
            pool.update_delay = delay;
            pool.auto_optimize = auto_optimize;
            pool.factory = factory;
            pool.num_threads = num_threads;
            pool.retry_on_exception = retry_on_ex;
            
            pool.setDaemon(true);
            pool.setName("MultiThreadPool-Main");
            return pool;
        }

        /**
         * Sets MultiThreadPool#queue to be blocking
         *
         * @return this
         */
        public MultiThreadPoolBuilder setBlockingInput() {
            blockin = true;
            return this;
        }

        /**
         * Sets MultiThreadPool#poll and MultiThreadPool#peek to be blocking
         *
         * @return this
         */
        public MultiThreadPoolBuilder setBlockingOutput() {
            blockout = true;
            return this;
        }

        /**
         * If the Thread pool's threads have a performance spike (up or down),
         * this is the number of threads required to change the number of
         * threads
         *
         * @param ticks The number of ticks required to update the thread pool
         * @return this
         */
        public MultiThreadPoolBuilder setThresholdTicks(int ticks) {
            assert (ticks >= 0);
            threshold_ticks = ticks;
            return this;
        }

        /**
         * The standard deviation of the pool threads' performance
         *
         * @param threshold the stddev in milliseconds
         * @return this
         */
        public MultiThreadPoolBuilder setMilliThreshold(long threshold) {
            assert (threshold >= 0);
            milli_threshold = threshold;
            return this;
        }

        /**
         * The delay between update ticks
         *
         * @param millis The delay in milliseconds
         * @return this
         */
        public MultiThreadPoolBuilder setUpdateDelay(long millis) {
            assert (millis >= 0);
            delay = millis;
            return this;
        }

        /**
         * Indicates the MultiThreadPool should auto optimize
         *
         * @return
         */
        public MultiThreadPoolBuilder setAutoOptimize() {
            auto_optimize = true;
            return this;
        }

        /**
         * Sets the number of threads to use, cannot be used with auto
         * optimization
         *
         * @param size The number of threads
         * @return
         */
        public MultiThreadPoolBuilder setStaticSize(int size) {
            num_threads = size;
            auto_optimize = false;
            return this;
        }

        public MultiThreadPoolBuilder setRetryOnException(boolean retry) {
            this.retry_on_ex = retry;
            return this;
        }

        /**
         * The pool thread factory
         *
         * @param factory The factory function
         * @return this
         */
        public MultiThreadPoolBuilder setThreadFactory(Supplier<? extends PoolThread<S, T>> factory) {
            Objects.requireNonNull(factory);
            this.factory = factory;
            return this;
        }

    }

    public static class Pair<S, T> {

        S first;
        T second;

        public Pair(S s, T t) {
            first = s;
            second = t;
        }
    }

    public static abstract class PoolThread<S, T> extends Thread {

        private Queue<S> in;
        private Queue<Pair<S, T>> out;
        private volatile long last_run = Long.MAX_VALUE, pre;
        private volatile boolean should_exit = false;
        private volatile boolean done = false;
        private boolean retry = false;

        public PoolThread() {
            super.setDaemon(true);
            super.setName("MultiThreadPool-Child");
        }

        public PoolThread setQueues(Queue<S> in, Queue<Pair<S, T>> out) {
            this.in = in;
            this.out = out;
            return this;
        }

        public PoolThread setRetry(boolean retry) {
            this.retry = retry;
            return this;
        }

        public long GetLastRun() {
            return last_run;
        }

        public boolean done() {
            return done;
        }

        public abstract T do_task(S s);

        public void setShouldExit(boolean exit) {
            should_exit = exit;
        }

        @Override
        public void run() {
            while (!should_exit) {
                pre = System.currentTimeMillis();
                done = true;
                S s = in.poll();
                done = false;
                if(s == null) {
                    done = true;
                    continue;
                }
                Pair<S, T> p = null;
                do {
                    //try {
                        p = new Pair(s, do_task(s));
                        break;
                    //} catch (Exception e) {
                    //    System.err.println("Received Exception");
                    //    e.printStackTrace();
                    //}
                } while (retry);
                out.add(p);
                last_run = System.currentTimeMillis() - pre;
            }
        }

    }

    public static <S, T> Supplier<PoolThread<S, T>> poolthread(Function<S, T> function) {
        return () -> {
            return new PoolThread<S, T>() {
                @Override
                public T do_task(S s) {
                    return function.apply(s);
                }
            };
        };
    }

    private Queue<S> input;
    private Queue<Pair<S, T>> output;

    private List<PoolThread> threads = new ArrayList<>();

    private long thread_threshold = 200;
    private int tick_threshold = 3;
    private boolean auto_optimize = true;
    private Supplier<? extends PoolThread> factory = null;
    private long update_delay = 50;
    private int num_threads = 0;
    private boolean retry_on_exception = false;

    private int trend = 0;
    private long last_tick = 0;
    private int waited_ticks = 0;
    private final int TREND_SPAWN = 1;
    private final int TREND_NEUT = 0;
    private final int TREND_KILL = -1;

    private MultiThreadPool(boolean blocking_input, boolean blocking_output) {
        if (blocking_input) {
            input = new SynchronousQueue();
        } else {
            input = new ConcurrentLinkedQueue();
        }

        if (blocking_output) {
            output = new SynchronousQueue();
        } else {
            output = new ConcurrentLinkedQueue();
        }
    }

    public void queue(S s) {
        input.add(s);
    }

    public Pair<S, T> poll() {
        return output.poll();
    }

    public Pair<S, T> peek() {
        return output.peek();
    }

    public Iterator<Pair<S, T>> output_iterator() {
        return output.iterator();
    }

    public void start_and_wait() throws InterruptedException {
        super.start();
        wait_until_finish();
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    public void wait_until_finish() throws InterruptedException {
        while (!input.isEmpty()) {
            Thread.sleep(10);
        }
        boolean done;
        do {
            done = true;
            for (int i = 0; i < threads.size(); i++) {
                done &= threads.get(i).done();
            }
        } while (!done);
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        if (!auto_optimize) {
            assert (num_threads > 0);
            for (int i = 0; i < num_threads; i++) {
                spawn_thread();
            }
            return;
        }

        while (true) {
            long sum_time = 0;
            num_threads = 0;
            for (PoolThread t : threads) {
                sum_time += t.GetLastRun();
                num_threads++;
            }
            long avg_time = sum_time / num_threads;

            if (last_tick == 0) {
                last_tick = avg_time;
            }

            if (avg_time < last_tick + thread_threshold) {
                //more threads
                if (trend == TREND_SPAWN) {
                    waited_ticks++;
                } else {
                    waited_ticks = 0;
                    trend = TREND_SPAWN;
                }
            } else if (avg_time > last_tick - thread_threshold) {
                //less threads
                if (trend == TREND_KILL) {
                    waited_ticks++;
                } else {
                    waited_ticks = 0;
                    trend = TREND_KILL;
                }
            } else {
                trend = TREND_NEUT;
                waited_ticks = 0;
            }

            //waited for an arbitrary time, do something
            if (waited_ticks > tick_threshold) {
                switch (trend) {
                    case TREND_NEUT:
                        break;
                    case TREND_SPAWN:
                        spawn_thread();
                        break;
                    case TREND_KILL:
                        kill_thread();
                }
            }

            last_tick = (last_tick * 9 + avg_time) / 10;

            try {
                Thread.sleep(update_delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void spawn_thread() {
        PoolThread t = factory.get().setQueues(input, output).setRetry(retry_on_exception);
        t.start();
        threads.add(t);
    }

    private void kill_thread() {
        //if the delay is < 10 millis, there's no point in finding the oldest thread
        if (last_tick > 10) {

            //find the thread with the most progress
            int smallest = 0;
            long oldest = Long.MAX_VALUE;
            for (int i = 0; i < threads.size(); i++) {
                if (threads.get(i).pre < oldest) {
                    smallest = i;
                }
            }

            threads.get(smallest).setShouldExit(true);
            threads.remove(smallest);

        } else {

            threads.get(threads.size() - 1);
            threads.remove(threads.size() - 1);

        }
    }

}
