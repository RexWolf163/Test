package com.scheduler.model;

import java.util.ArrayList;
import java.util.List;

/** Данные работников.
 *@version 0.2
 * */
public class Worker {
	/**Идентификатор*/
	private String id;
	/**Профессия*/
	private String prof;
	/**Список доступных для работы моделей станков*/
	private List<String> mash;
	/**Список отобранных операций (хранится номер в списке операций)*/
	private List<Integer> pool;
	/**Граница занятости в минутах от начала расчета расписания*/
	private int workTime;
	/**Номер текущей операции в списке операций*/
	private int WorkId;
	/**Флаг, показывающий статус распределения операций. 
	 * Если для всех операций в списке pool количество претендентов равно 1, то fin=true*/
	private boolean fin;
	
	public Worker(){
		this.WorkId=-1;
	}
	
	/**Запрос данных об id */
	public String getId(){return this.id;}
	/**Запрос данных об prof */
	public String getProf(){return this.prof;}
	/**Запрос данных об mash */
	public List<String> getMash(){return this.mash;}
	/**Запрос данных об pool */
	public List<Integer> getPool(){return this.pool;}
	/**Запрос данных об workTime */
	public int getWorkTime(){return this.workTime;}
	/**Запрос данных об WorkId */
	public int getWorkId(){return this.WorkId;}
	/**Запрос данных об fin */
	public boolean getFin(){return this.fin;}
	
	/**Корректировка данных id */
	public void setId(String data){this.id=data;}
	/**Корректировка данных prof */
	public void setProf(String data){this.prof=data;}
	/**Добавление данных в mash */
	public int addMash(String data){
		this.mash.add(data);
		return this.mash.size();}
	/**Добавление данных в pool */
	public int addPool(int data){
		this.pool.add(data);
		return this.pool.size();}
	/**Удаление задачи из pool*/
	public int removePool(int data){
		this.pool.remove(data);
		return this.pool.size();
	}
	/**Корректировка данных workTime */
	public void setWorkTime(int data){this.workTime=data;}
	/**Корректировка данных WorkId */
	public void setWorkId(int data){this.WorkId=data;}
	/**Корректировка данных fin */
	public void setFin(boolean data){this.fin=data;}
}
