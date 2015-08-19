public class Operation {
	public String ID = new String();
	public String Prof = new String();
	public String Mash = new String();
	public int Cost = 0;
	public int Time = 1;
	public boolean Wait=false; //"Ожидание предыдущего". При формирования файла операций, поставленный в конце знак # показывает, что данная операция обязательно выполняется только после предыдущей
	public int Complete=0; //время завершения операции в минутах от начала расчета.
			
	public int Weight=0; //Удельный вес операции Стоимость/Время (коп/минуту). Операции с более высоким весом имеют приоритет. Цепочки берутся по общему весу
	public int Busy = -1; // Номер оператора по списку
	public int Start = -1;
	
	//тест-вариант
	public int Pretendents=0; //Количество претендентов

}
