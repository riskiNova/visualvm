/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.tools.visualvm.heapviewer.java;

import java.util.Objects;
import org.netbeans.lib.profiler.heap.FieldValue;
import org.netbeans.lib.profiler.heap.Heap;
import org.netbeans.lib.profiler.heap.Instance;
import com.sun.tools.visualvm.heapviewer.model.DataType;
import com.sun.tools.visualvm.heapviewer.model.HeapViewerNode;

/**
 *
 * @author Jiri Sedlacek
 */
public abstract class PrimitiveNode extends HeapViewerNode {
    
    private String fieldName;
    
    
    public String getFieldName() {
        if (fieldName == null) fieldName = computeFieldName();
        return fieldName;
    }
    
    protected abstract String computeFieldName();
    
    public abstract String getType();
    
    public abstract String getValue();
    
    
    public String toString() {
        return getFieldName();
    }
    
    protected Object getValue(DataType type, Heap heap) {
        if (type == DataType.NAME) return getFieldName();
        if (type == DataType.OWN_SIZE) return DataType.OWN_SIZE.getNoValue();
        if (type == DataType.RETAINED_SIZE) return DataType.RETAINED_SIZE.getNoValue();
        if (type == DataType.OBJECT_ID) return DataType.OBJECT_ID.getNoValue();
        
        return super.getValue(type, heap);
    }
    
    
    
    public boolean isLeaf() {
        return true;
    }
    
    
    public static class Field extends PrimitiveNode {
        
        private final FieldValue field;
        
        public Field(FieldValue field) {
            this.field = field;
        }
        
        public String getType() {
            return field.getField().getType().getName();
        }
        
        public String getValue() {
            return field.getValue();
        }
        
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Field)) return false;
            return field.equals(((Field)o).field);
        }

        public int hashCode() {
            return field.hashCode();
        }
        
        protected String computeFieldName() {
            return (field.getField().isStatic() ? "static " : "") + field.getField().getName();
        }
    }
    
    public static class ArrayItem extends PrimitiveNode {
        
        private final int index;
        private final String type;
        private final String value;
        private final Instance owner;
        
        public ArrayItem(int index, String type, String value, Instance owner) {
            this.index = index;
            this.type = type;
            this.value = value;
            this.owner = owner;
        }
        
        public String getType() {
            return type;
        }
        
        public String getValue() {
            return value;
        }
        
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ArrayItem)) return false;

            ArrayItem oo = (ArrayItem)o;
            return owner.equals(oo.owner) && index == oo.index;
        }

        public int hashCode() {
            return Objects.hash(owner, index);
        }
        
        protected String computeFieldName() {
            return "[" + index + "]";
        }
        
    }
    
}
