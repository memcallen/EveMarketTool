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
package evemarginfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class AuthHandler {

    private enum TokenState {
        UNSET,
        SET_UNKNOWN,
        VALID;
    }
    
    private Path auth_file = Paths.get("auth.cfg");

    private String refresh_token;
    private String character_id;
    private TokenState refresh_state = TokenState.UNSET;
    private TokenState id_state = TokenState.UNSET;
    
    
    public void Load() throws IOException {

        Files.lines(auth_file).forEach(this::ParseLine);

        
        
    }

    private void GetNewCharID() {
        
    }
    
    private void ParseLine(String s) {

        s = s.trim();

        if (s.isEmpty()) {
            return;
        }

        if (s.startsWith("#")) {
            return;
        }

        String[] halves = s.split("=");
        
        halves[0] = halves[0].trim();
        halves[1] = halves[1].trim();
        
        if(halves[0].equals("refresh_token")) {
            refresh_token = halves[1];
            refresh_state = TokenState.SET_UNKNOWN;
        }
        
        if(halves[0].equals("character_id")) {
            character_id = halves[1];
            id_state = TokenState.SET_UNKNOWN;
        }
        
    }

}
