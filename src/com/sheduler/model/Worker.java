package com.scheduler.model;

import java.util.ArrayList;
import java.util.List;

/** Данные работников.
 *@version 0.2
 * */
public class Worker {
	/**Идентификатор*/
	private final String id;
	/**Профессия*/
	private final String prof;
	/**Список доступных для работы моделей станков*/
	private final ArrayList<String> mash;
	/**Список отобранных операций (хранится номер в списке операций)*/
	private ArrayList<Integer> pool;
	/**Граница занятости в минутах от начала расчета расписания*/
	private int workTime;
	/**Номер текущей операции в списке операций*/
	private Integer WorkId;
	/**Флаг, показывающий статус распределения операций. 
	 * Если для всех операций в списке pool количество претендентов равно 1, то fin=true*/
	private boolean fin;
	/**Счетчик объектов типа Рабочий*/
	private static long counter;
	
	public Worker(String[] strok){
		WorkId=-1;
		this.mash=new ArrayList<String>();
		pool=new ArrayList<Integer>();
		id="(id W"+(++counter)+") "+strok[0];
		this.prof=strok[1];
		for (String m:strok[2].split("`")){
			this.mash.add(m);
		}
	}
	
	/**Запрос данных об id */
	public String getId(){return id;}
	/**Запрос данных об prof */
	public String getProf(){return prof;}
	/**Запрос данных об mash */
	public List<String> getMash(){return mash;}
	/**Запрос данных об pool */
	public List<Integer> getPool(){return pool;}
	/**Запрос данных об workTime */
	public int getWorkTime(){return workTime;}
	/**Запрос данных об WorkId */
	public Integer getWorkId(){return WorkId;}
	/**Запрос данных об fin */
	public boolean getFin(){return fin;}
	
//	/**Корректировка данных id */
//	public void setId(String data){id=data;}
//	/**Корректировка данных prof */
//	public void setProf(String data){prof=data;}
//	/**Добавление данных в mash */
/*	public int addMash(String data){
		mash.add(data);
		return mash.size();}*/
	/**Добавление данных в pool */
	public int addPool(int data){
		pool.add(data);
		return pool.size();
	}
	/**Удаление задачи из pool*/
	public int removePool(int data){
		pool.remove(data);
		return pool.size();
	}
	/**Корректирование записи в pool*/
	public int setPool(int index, int element){
		pool.set(index,element);
		return pool.size();
	}
	public void setWorkTime(int data){workTime=data;}
	/**Корректировка данных WorkId */
	public void setWorkId(Integer data){WorkId=data;}
	/**Корректировка данных fin */
	public void setFin(boolean data){fin=data;}
}
