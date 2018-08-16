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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class AuthServer implements Runnable, Closeable {

    public static int PORT = 21567;

    public static File AUTH_PAGE = new File("auth_page.html");

    private HttpServer server;

    private EventQueue equeue;

    public static void main(String[] args) {
        new AuthServer(null).run();
    }

    public AuthServer(EventQueue equeue) {
        this.equeue = equeue;
    }

    private void handle_callback(HttpExchange he) {

        if ("GET".equals(he.getRequestMethod())) {

            try {

                he.sendResponseHeaders(200, -1);

                OutputStream out = he.getResponseBody();
                FileInputStream fis = new FileInputStream(AUTH_PAGE);
                int i;
                
                while((i = fis.read()) != -1) {
                    out.write(i);
                }
                
                fis.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger("AuthServer").log(Level.SEVERE, null, ex);
            }

        }

        he.close();
    }

    @Override
    public void run() {

        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 1);

            server.createContext("/callback", this::handle_callback);

            server.start();
        } catch (IOException ex) {
            Logger.getLogger("AuthServer").log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() throws IOException {
        server.stop(0);
    }

}
