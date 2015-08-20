package com.rw.util;

/**Генератор массива int*/
public class Range {
	  /**Создает массив int от 0 до n
	  * @param n Верхняя граница массива
	  * @return int[0..n]*/
	  public static int[] range(int n) {
	    int[] result = new int[n];
	    for(int i = 0; i < n; i++)
	      result[i] = i;
	    return result;
	  }
	  /**Создает массив int от start до end
	  * @param start Нижняя граница массива
	  * @param end Верхняя граница массива
	  * @return int[start..end]*/
	  public static int[] range(int start, int end) {
	    int sz = end - start;
	    int[] result = new int[sz];
	    for(int i = 0; i < sz; i++)
	      result[i] = start + i;
	    return result;
	  }
	  /**Создает массив int от start до end с шагом step
	  * @param start Нижняя граница массива
	  * @param end Верхняя граница массива
	  * @param step Шаг массива
	  * @return int[start..end] с шагом step*/
	  public static int[] range(int start, int end, int step) {
	    int sz = (end - start)/step;
	    int[] result = new int[sz];
	    for(int i = 0; i < sz; i++)
	      result[i] = start + (i * step);
	    return result;
	  }
	}
