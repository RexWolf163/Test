public class Operation {
	String ID = new String();
	String Prof = new String();
	String Mash = new String();
	int Cost = 0;
	int Time = 1;
	boolean Wait=false; //"Ожидание предыдущего". При формирования файла операций, поставленный в конце знак # показывает, что данная операция обязательно выполняется только после предыдущей
	int Complete=0; //время завершения операции в минутах от начала расчета.
			
	int Weight=0; //Удельный вес операции Стоимость/Время (коп/минуту). Операции с более высоким весом имеют приоритет. Цепочки берутся по общему весу
	int Busy = -1; // Номер оператора по списку
	int Start = -1;
	
	//тест-вариант
	int Pretendents=0; //Количество претендентов

}