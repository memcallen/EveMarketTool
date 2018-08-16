/*
 * The MIT License
 *
 * Copyright 2018 azalac0020.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package extra;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class Launcher {

    public static enum ArgType {
        Single, Named, Positional;
    }

    public static class ArgumentParser {

        private final HashMap<String, ArgType> arg_types = new HashMap<>();

        private String[] args;

        private final List<String> extra_args = new LinkedList<>();
        private final HashMap<String, String> named_args = new HashMap<>();
        private final List<Character> single_args = new LinkedList<>();

        /**
         * @param type The argument type
         * @param root The argument name
         * @return this
         */
        public ArgumentParser AddArgument(ArgType type, String root) {
            arg_types.put(root, type);

            return this;
        }

        public void SetArguments(String[] args) {
            this.args = args;
            ProcessArgs();
        }

        private void ProcessArgs() {

            String positional_name = null;

            for (String curr : args) {

                if (positional_name != null) {
                    named_args.put(positional_name, curr);
                    positional_name = null;
                    continue;
                }

                if (curr.startsWith("--")) { //named arg

                    if (curr.contains("=")) {
                        String[] halves = curr.split("=");
                        String name = halves[0].trim().substring(2);

                        if (arg_types.get(name) == ArgType.Named) {
                            named_args.put(name, halves[1].trim());
                        } else {
                            throw new IllegalArgumentException("Argument " + curr
                                    + " is named format but isn't named type");
                        }
                    } else {
                        named_args.put(curr.substring(2).trim(), "");
                    }

                } else if (curr.startsWith("-")) { //single arg or positional arg

                    String text = curr.substring(1);

                    if (arg_types.get(text) == ArgType.Single) {

                        for (Character c : curr.substring(1).toCharArray()) {
                            single_args.add(c);
                        }

                    } else {

                        positional_name = text;

                    }

                } else { // misc arg

                    extra_args.add(curr);

                }
            }

        }

        public boolean hasSingle(char c) {
            return single_args.contains(c);
        }

        public String getNamed(String name) {
            return named_args.get(name);
        }

        public String getExtra(int index) {
            return extra_args.get(index);
        }

        public int num_extra() {
            return extra_args.size();
        }

        public ArgType getType(String name) {
            return arg_types.get(name);
        }

    }

    private HashMap<String, Runnable> tasks = new LinkedHashMap<>();

    private String default_task;

    private ArgumentParser parser = new ArgumentParser();

    /**
     * @param type The argument's type
     * @param name The task's name
     * @param run The task's function
     * @return this
     */
    public Launcher AddTask(ArgType type, String name, Runnable run) {
        tasks.put(name, run);
        parser.AddArgument(type, name);

        return this;
    }

    public Launcher SetDefault(String name) {
        this.default_task = name;

        return this;
    }

    public void run(String[] args) {

        parser.SetArguments(args);

        // if anything other than default was requested
        boolean extra = false;

        for (Entry<String, Runnable> e : tasks.entrySet()) {
            String name = e.getKey();

            switch (parser.getType(name)) {

                case Single:
                    if (parser.hasSingle(name.charAt(0))) {
                        run_task(name);
                        extra = true;
                    }
                    break;

                case Named:
                case Positional:
                    if (parser.getNamed(name) != null) {
                        run_task(name);
                        extra = true;
                    }
                    break;

                default:
                    throw new AssertionError(parser.getType(name).name());

            }
        }

        if (!extra) {
            run_task(default_task);
        }

    }

    public void run_task(String name) {
        run_task(name, false);
    }

    public void run_task(String name, boolean verbose) {
        Runnable run = tasks.get(name);

        Objects.requireNonNull(run, name + " is not a task");

        if (verbose) {
            System.out.println("Running task " + name);
        }

        run.run();

        if (verbose) {
            System.out.println("Finished task " + name);
        }

    }

}
