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

import com.google.gson.JsonElement;
import static evemarginfinder.DatabaseManager.parser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class SkillManager {
    
    private String character_id;
    private String refresh_token;
    private int a, b, c;
    
    public void UpdateSkills(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public double GetTradePercent() {
        return 1;
    }
    
    public void UpdateSkills() {
        
        String URL = null;
        
    }
    
    public static JsonElement read(String string) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(string).openConnection();
        conn.addRequestProperty("User-Agent", "Mozilla/4.0");
        JsonElement ret = parser.parse(new BufferedReader(new InputStreamReader(conn.getInputStream())));
        conn.disconnect();
        return ret;
    }

    
}
