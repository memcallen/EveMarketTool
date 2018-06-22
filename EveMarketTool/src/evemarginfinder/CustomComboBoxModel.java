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

import java.util.List;
import java.util.function.Function;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * @author Memcallen Kahoudi/Recursive Pineapple
 */
public class CustomComboBoxModel<T, R> extends AbstractListModel implements ComboBoxModel{

    private Object selected;
    private List<T> data;
    
    private Function<T, R> extractor;
    
    public CustomComboBoxModel(List<T> data) {
        this.data = data;
    }
    
    public CustomComboBoxModel(List<T> data, Function<T, R> elementExtractor) {
        this(data);
        this.extractor = elementExtractor;
    }
    
    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        if(extractor != null) {
            return extractor.apply(data.get(index));
        }else{
            return data.get(index);
        }
    }

    public void fireDataChanged() {
        fireContentsChanged(this, 0, getSize() - 1);
    }
    
    public void setSelectedItem2(T item) {
        setSelectedItem(extractor != null ? extractor.apply(item) : item);
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        selected = anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }
    
}
