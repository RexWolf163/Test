package com.rw.util;

import java.util.ArrayList;

import com.scheduler.model.Operation;

/**Сортировка массива по ключу*/
public class Sort {
	/**Сортировка массива по ключу в сторону уменьшения значений
	 * @param args Массив, подлежащий сортировке
	 * @param key Массив-ключ. Формируется, например, строкой for (Integer i:ArrayListOfValue) key.add(i.getValue());
	 * */
	public static void arrayMultiSort(ArrayList<?> args, ArrayList<Integer> key){
		arrayMultiSort(args, key, false);
	}
	/**Сортировка массива по ключу в сторону увеличения или уменьшения значений
	 * @param args Массив, подлежащий сортировке
	 * @param key Массив-ключ. Формируется, например, строкой for (Integer i:ArrayListOfValue) key.add(i.getValue());
	 * @param reverse флаг направления сортировки. false - сортировка по возрастанию значений key, true - сортировка по уменьшению
	 * */
	public static void arrayMultiSort(ArrayList<?> args, ArrayList<Integer> key, boolean reverse){
		Integer stek = null;
		Object stekWorker = null;
		ArrayList<Object> ar = (ArrayList<Object>) args;
		for (int index=0;index<key.size();index++){
			if (((!reverse)&&(stek!=null)&&(stek>key.get(index)))||((reverse)&&(stek!=null)&&(stek<key.get(index)))){
				stekWorker=ar.get(index-1);
				ar.set(index-1,ar.get(index));
				ar.set(index, stekWorker);
				stek=key.get(index-1);
				key.set(index-1,key.get(index));
				key.set(index, stek);
				index-=2;
				
				if (index<0)index=0;
			}
			stek=key.get(index);
		}
	}
}
