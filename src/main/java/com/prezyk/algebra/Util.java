package com.prezyk.algebra;

import java.lang.reflect.Array;

class Util {

    static <T> T[] removeValueFromArray(T[] array, int indexToRemove, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T[] arrayWithoutElement = (T[]) Array.newInstance(clazz, array.length - 1);
        if (indexToRemove == 0) {
            System.arraycopy(array, indexToRemove + 1, arrayWithoutElement, 0, array.length - 1);
        } else if (indexToRemove == array.length - 1) {
            System.arraycopy(array, 0, arrayWithoutElement, 0, array.length - 1);
        } else {
            System.arraycopy(array, 0, arrayWithoutElement, 0, indexToRemove);
            System.arraycopy(array, indexToRemove + 1, arrayWithoutElement, indexToRemove, array.length - indexToRemove - 1);
        }
        return arrayWithoutElement;
    }


}
