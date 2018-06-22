/*
 * The MIT License
 *
 * Copyright 2018 memcallen
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
package evemarginfinder;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class bridges the gap between GUI and backend, and provides support to
 * easily modify either end without major refactoring.
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class EventQueue {

    // A list of all Events that can be pushed to this queue
    public static enum EventType {
        DESELECT_ALL,
        REVALIDATE_ITEMLIST,
        REVALIDATE_TABLE_HEADERS,
        REVALIDATE_TABLE_DATA,
        REVALIDATE_COMBOBOXES,
        START_QUERY,
        GROUP_SEARCH(String.class),
        STOP_GROUP_SEARCH,
        ITEM_SEARCH(String.class),
        STOP_ITEM_SEARCH,
        SET_SYSTEM(String.class),
        SET_SYS_CALLBACK(String.class, Consumer.class),
        SET_PARSER(String.class),
        SET_GENERATOR(String.class),
        RELOAD_CURR_CONFIG,
        FORCE_RELOAD_LUA,
        SAVE_CURR_CONFIG,
        SET_CONFIG(String.class),
        OPEN_NEW_CONFIG(File.class, Consumer.class),
        CREATE_CONFIG(String.class),
        REMOVE_CURR_CONFIG(Boolean.class),
        EDIT_FILTERS;

        private Class[] clazz;

        public Class[] GetParamTypes() {
            return clazz;
        }

        private EventType() {
            clazz = null;
        }

        private EventType(Class... clazzes) {
            clazz = clazzes;
        }
    }

    private static class Task {

        private final EventType event;
        private final Object[] args;

        public Task(EventType event, Object... args) {
            this.event = event;
            this.args = args;
        }
    }

    private HashMap<EventType, Consumer<Object[]>> events = new HashMap<>();

    private LinkedBlockingQueue<Task> tasks = new LinkedBlockingQueue<>();

    private Thread task_runner;

    // Used to wrap any functions which don't need parameters, so they can fit in the hashmap
    public static Consumer<Object[]> NoArgs(Runnable func) {
        return (a) -> func.run();
    }
    
    // Used to wrap any functions which take one non-primitive parameter
    public static <T> Consumer<Object[]> OneArg(Consumer<T> func) {
        return (a) -> func.accept((T)a[0]);
    }

    /**
     * Registers an event handler
     * @param event The event
     * @param func The handler function
     * @return <code>true</code> if the event already had a handler, <code>false</code> otherwise.
     */
    public boolean registerEventFunction(EventType event, Consumer<Object[]> func) {
        boolean overriding = events.containsKey(event);
        
        events.put(event, func);
        
        return overriding;
    }
    
    public static boolean TypesMatch(Class[] clazz, Object[] objs) {
        
        //an array of length 0 is the same as a null array
        boolean a_null = clazz != null ? clazz.length == 0 : clazz == null;
        boolean b_null = objs != null ? objs.length == 0 : objs == null;
        
        // if both are null, true
        if(a_null && b_null) {
            return true;
        }
        
        // if one but not two are null, false
        if(a_null || b_null) {
            return false;
        }
        
        if(clazz.length != objs.length) {
            return false;
        }
        
        for(int i = 0; i < clazz.length; i++) {
            if(objs[i].getClass() != clazz[i]) {
                return false;
            }
        }
        
        return true;
        
    }
    
    public void run() {

        if (task_runner != null) {
            task_runner.interrupt();
        }

        task_runner = new Thread(this::ThreadBody);

        task_runner.setName("Task-EventQueue");
        task_runner.setDaemon(true);

        task_runner.start();
    }

    private void ThreadBody() {

        while (true) {
            Task task;
            try {
                task = tasks.take();
                if (task == null) {
                    continue;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(EventQueue.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            synchronized (task.event) {

                events.get(task.event).accept(task.args);

            }
        }

    }

    /**
     * Queues an event to be ran
     * @param type The event type
     * @param arguments The event arguments
     * @return The number of events ahead of the queued event
     * 
     * @throws IllegalArgumentException If the supplied argument types don't match the required types
     * @throws NullPointerException If the event doesn't have a function to handle it
     */
    public int queueEvent(EventType type, Object... arguments) {

        if(!TypesMatch(type.GetParamTypes(), arguments)) {
            throw new IllegalArgumentException("EventQueue Arguments don't match required types for " + type.name());
        }
        
        if (events.containsKey(type)) {
            tasks.add(new Task(type, arguments));
            return tasks.size() - 1;
        } else {
            throw new NullPointerException("Error: EventType " + type.name() + " doesn't have a registered consumer");
        }
    }

}
