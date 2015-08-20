package com.rw.util;
import java.io.*;
/**Коллекция упрощенных вызовов функций вывода на печать*/
public class Print {
  // Print with a newline:
	/**Вывод текста без переноса строки
	 * @param obj Текстовая строка*/
	public static void print(Object obj) {
		System.out.print(obj);
	}
	/**Вывод текста с переносом строки
	 * @param obj Текстовая строка*/
	public static void println(Object obj) {
		System.out.println(obj);
	}
	/**Вывод пустой строки*/
	public static void println() {
		System.out.println();
	}
	/**Вывод текста по формату без переноса строки
	 * @param format Формат вывода
	 * @param args Текстовые строка*/
 	public static PrintStream
 	printf(String format, Object... args) {
 		return System.out.printf(format, args);
 	}
 	/**Вывод текста по формату с переносом строки
 	 * @param format Формат вывода
	 * @param args Текстовые строка*/
	public static PrintStream
  	printfln(String format, Object... args) {
 		return System.out.printf(format+"\n", args);
 	}
}
