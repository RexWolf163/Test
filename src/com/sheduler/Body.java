package com.sheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.scheduler.model.*;
import com.rw.util.Sort;

import static com.rw.util.Print.*;
import static com.rw.util.Range.*;

/**Основной класс*/
public class Sheduler {
	public static ArrayList<Mashine> Models = new ArrayList<Mashine>();		
	public static ArrayList<Worker> Workers = new ArrayList<Worker>();		
	public static ArrayList<Operation> Operations = new ArrayList<Operation>();
	public static BufferedReader br;
	public static List<String> Records = new ArrayList<String>();
	public static int Current =0;
	public static int WorkTime=0;
	public static Date date1 = new Date();
	public static Date date2 = new Date();
	
	/**Загрузка данных из файлов
	 * @param debag если true - дублирует загруженные строки в консоль
	 * @return int количество "ведомых" операций
	 * */
	public static int Loading(boolean debag){
		int result=0;
		
		String[] files = {"mashines.txt","workers.txt","Operations.txt"}; //список файлов для обработки
		String[] stroke; //оперативная строковая переменная
		
		int WaitNum = 0;//количество "ожидающих" операций (просто для статистики)
		
		for (String Load:files){
			try {
				String sCurrentLine;
				int i=0;	
				br = new BufferedReader(new FileReader(Load));
				
				while ((sCurrentLine = br.readLine()) != null) {
					stroke=sCurrentLine.split("\t");
					if (stroke[0].length()==0) continue;//проверка на "пустую" строку
					switch (Load){
						case "mashines.txt":{
							for (int k : range(Integer.valueOf(stroke[1]))){
								Models.add(new Mashine(stroke));
								}
							break;
						}
						case "workers.txt":{
							Workers.add(new Worker(stroke));								
							break;
						}
						case "Operations.txt":{
							Operations.add(new Operation(stroke));
							i=Operations.size()-1;
							if (Operations.get(i).getWait()){
								WaitNum++;
								Operations.get(i-1).setWeight(Math.round((Operations.get(i-1).getWeight()+Operations.get(i).getWeight())/2));
								Operations.get(i).setWeight(Operations.get(i-1).getWeight());
							}
							break;
						}
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
				println("Файл данных не найден");
				System.exit(0);
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(0);
				}
			}
		}

		if (debag)
			{println("Загружены данные:"
				+ "\nОборудование:");
			for (Mashine Model : Models) {
				printfln("%s - %s",
						Model.getId(),
						Model.getType());
				}
			println("Работники:");
			for (Worker Worker : Workers) {
				printfln("%s (%s - %s)",
						Worker.getId(),
						Worker.getProf(),
						Worker.getMash());
				}
			println("Поставленные задачи:");
			for (Operation Operation : Operations) {
				printfln("%s (%s - %s/ %sмин - %sрублей)",
						Operation.getId(),
						Operation.getProf(),
						Operation.getMash(),
						Operation.getTime(),
						Operation.getCost());
				}
			}
		result=WaitNum;
		return result;
	}
	/**Проверка загруженных данных
	 * */
	public static void Check(){// проверка соответствий названий агрегатов и профессий
		boolean errorPresent=false;//в массиве обнаружена ошибка
		boolean hasErrors=false;//при проверке ошибки были обнаружены
		
		for (Worker Worker:Workers)
			for (String mash:Worker.getMash()){
				errorPresent=true;
				for (Mashine Model:Models)
					if (Model.getType().equals(mash)){ 
						errorPresent=false;
						break;
					}
				if(errorPresent) {
					printfln("Оборудование указаное в записи %s %s в базе данных не обнаружено",
							Worker.getId(),
							Worker.getMash());
					hasErrors=true;
				}
		}
		for (Operation Operation:Operations){
			errorPresent=true;
			for (Mashine Model:Models)
				if (Model.getType().equals(Operation.getMash())) {
					errorPresent=false;
					break;
			}
			if(errorPresent) {printfln("Оборудование указаное в записи %s %s в базе данных не обнаружено",
					Operation.getId(),
					Operation.getMash());
				hasErrors=true;
			}
		}
		for (Operation Operation:Operations){
			errorPresent=true;
			for (Worker Worker:Workers)
				if (Worker.getProf().equals(Operation.getProf())){
					errorPresent=false;
					break;
				}
			if(errorPresent) {printfln("Профессия указаная в записи %s %s среди персонала не встречается",
					Operation.getId(),
					Operation.getProf());
				hasErrors=true;
			}
		}
		for (Operation Operation:Operations){
			if (Operation.getWait()) {
				if (errorPresent) {
					println("Обнаружена цепочка длиной более двух операций");
					hasErrors=true;
					break;
				}
				errorPresent=true;
			}
			else errorPresent=false;
		}
		
		if (!hasErrors) println("Проблемы не обнаружены");
		else {
			println("Расчет окончен по ошибке в данных");
			System.exit(0);
		}		
	}
	/**Считывание даты с клавиатуры
	 * @param debag если true - Вместо считывания даты с клавиатуры возвращает "1.1.1"
	 * @return Date в формате "dd.MM.yyyy" или null при ошибке
	 * */
	public static Date RDate(boolean debag){
		br=new BufferedReader(new InputStreamReader(System.in));
		SimpleDateFormat format = new SimpleDateFormat();
		format.applyPattern("dd.MM.yyyy");
		Date date = null;
		try {
			String stroke;
			if (!debag) stroke=br.readLine();
			else stroke="1.1.1";
			
			try {				
				date= format.parse(stroke);
			} catch (ParseException e) {
				println("Неправильно введена дата");
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} 
		return date;

	}
	/**Заполнение Пула работников задачами
	 * Тезис: Максимальная прибыль достигается при максимальной загруженности всех работников и станков.
         		Соответственно необходимо обеспечить равномерное распределение задач по персоналу.
         		Этого можно достигнуть, если наименее загруженный работник имеет приоритетное право на решение задачи. 
         
         Алгоритм:
         Сперва каждый работник собирает себе операции на выполнение без каких либо ограничений.
         		Номер операции ставится в Пул работника. Время операции суммируется к занятости работника. Количество претендентов 
         		на операцию увеличивается. Затем выбирается работник с занятостью больше чем у других. Перебираются его операции. 
         		У первой операции с большим чем 1 количеством претендентов снижается кол-во претендентов и она убирается из пула 
         		работника. Ее время вычитается из занятости. 
         		Данный пункт повторяется до тех пор, пока не будет достигнута "1" в претендентах у всех операций.
        @param Worker объект-рабочий
        */
	public static void FillPool(Worker Wor){//
		for (Operation Op:Operations){
			if (Wor.getProf().equals(Op.getProf())){
				if (Wor.getMash().indexOf(Op.getMash())!=-1){
					Wor.setWorkTime(Wor.getWorkTime()+Op.getTime());
					Op.setPretendents(Op.getPretendents()+1);
					Wor.addPool(Operations.indexOf(Op));
				}
			}
		}
	}

	/**Сброс лишней операции из пула. Лишней считается первая операция с количеством претендентов более 1го
	 * Функция должна вызываться для рабочегос  наибольшей временной загрузкой
	 * @param Worker объект-рабочий
	 * @return true - если не найдено спорных работ, false - если найдена и сброшена спорная работа
	 * */
	public static boolean DropPool(Worker Wor){
		boolean bool=true;
		for (int IDOp:Wor.getPool()){
			if (Operations.get(IDOp).getPretendents()>1){
				bool=false;
				Wor.setWorkTime(Wor.getWorkTime()-Operations.get(IDOp).getTime());
				Wor.removePool(Wor.getPool().indexOf(IDOp));
				Operations.get(IDOp).setPretendents(Operations.get(IDOp).getPretendents()-1);
				break;
			}
		}
		return bool;
	}

	/**Распределения работы меж работников:
         * Тезис: Максимальная прибыль достигается при максимальной загруженности всех работников и станков.
         		Соответственно необходимо обеспечить равномерное распределение задач по персоналу.
         		Этого можно достигнуть, если наименее загруженный работнки имеет приоритетное право на решение задачи. 
         
         Алгоритм:
         Сперва каждый работник собирает себе операции на выполнение без каких либо ограничений.
         		Номер операции ставится в Пул работника. Время операции суммируется к занятости работника. Количество претендентов 
         		на операцию увеличивается. Затем выбирается работник с занятостью больше чем у других. Перебираются его операции. 
         		У первой операции с большим чем 1 количеством претендентов снижается кол-во претендентов и она убирается из пула 
         		работника. Ее время вычитается из занятости. 
         		Данный пункт повторяется до тех пор, пока не будет достигнута "1" в претендентах у всех операций.
       
	*/
	public static void WorkersDuty(){
				
        int BiggerTime = 0; //Наибольшее время загрузки
        int BiggerID=-1; //Индекс работника с наибольшим временем загрузки в коллекции
        // первичное распределение операций
        for (Worker Wor:Workers){
        	FillPool(Wor);
        	//сравнение BiggerTin=me со временем загрузки данного работника
        	//Если новое время выше, BiggerTime переписывается и запоминается новый BiggerID
        	if (BiggerTime<Wor.getWorkTime()){
        		BiggerTime=Wor.getWorkTime();
        		BiggerID=Workers.indexOf(Wor);
        	}
        }
        
        //отказ от спорных операций
        boolean fin=false;
        while(!fin){
        	//проверка количества претендентов на операцию
        	fin=true;
        	for (Operation Op:Operations){
        		//если не все операции распределены, то флаг fin=false 
        		if (Op.getPretendents()>1){
        			fin=false;
        			break;
        		}
        	}
        	Workers.get(BiggerID).setFin(DropPool(Workers.get(BiggerID))); 
        	//сброс операции от самого занятого
        	//и обнуление переменных
        	BiggerTime=0;
       		BiggerID=-1;
       		//ищем следующего "жадину"
            for (Worker Wor:Workers){
            	if ((!Wor.getFin())&&(BiggerTime<Wor.getWorkTime())){
            		BiggerTime=Wor.getWorkTime();
            		BiggerID=Workers.indexOf(Wor);
            	}
            }
        }		
	}
	
	/**Обновление данных о статусе работ
	 * @return Время в минутах от начала расчета расписания, в которое завершена, как минимум одна работа
	 * */
	public static int RefreshWork(){
		//Изначально отметка времени ставится на минуту позже верхней границы периода расчета
		int TimeStamp=WorkTime+1;
		//флаг пропуска ожидающих "ведомых" операций
		boolean skip=false;
		for (Worker Wor:Workers){
		//проверка на превышение текущего времени над временем завершения работы. 
		//Если истинно, то из пула удаляется выполненная операция
			if ((Wor.getWorkTime()<=Current)&&(Wor.getWorkId()!=null)){
				Wor.removePool(Wor.getPool().indexOf(Wor.getWorkId()));
				Wor.setWorkId(null);
			}
			//если нет назначенной операции, то происходит выбор новой операции из пула
			if (Wor.getWorkId()==null){
				label1:
				for (int idOperation:Wor.getPool()){
					skip=false;
					//проводится проверка на выполнение ведущей операции, если данная является ведомой
					if (Operations.get(idOperation).getWait()){
						skip=true;
						if ((Operations.get(idOperation-1).getStart()!=null)&&
								(Current>=
								(Operations.get(idOperation-1).getStart()+Operations.get(idOperation-1).getTime()))){
							skip=false;
						}
					}
					//при получении "разрешения" Операции в коллекции Operations приписывается время старта.
					if (!skip){
						for (Mashine Mod:Models){
							if ((Mod.getBusy()<=Current)&&(Mod.getType().equals(Operations.get(idOperation).getMash()))){
								Wor.setWorkTime(Operations.get(idOperation).getTime()+Current);
								Mod.setBusy(Operations.get(idOperation).getTime()+Current);
								Wor.setWorkId(idOperation);
								Operations.get(idOperation).setStart(Current);
								Records.add(String.format("%s по %s %s=> занимает %s для %s :%sмин",
										DateTrans(Current),
										DateTrans(Wor.getWorkTime()),
										Wor.getId(),
										Mod.getId(),
										Operations.get(idOperation).getId(),
										Operations.get(idOperation).getTime()));
								break label1;
							}
						}
					}
				}
			}
		}
		//производится перебор рабочих, для определения минимального времени завершения
		for (Worker Wor:Workers){
			if((Wor.getWorkId()!=null)&&(Wor.getPool().size()>0)&&(TimeStamp>Wor.getWorkTime())) TimeStamp=Wor.getWorkTime();
		}
		
		return TimeStamp;
	}
	/**раскидываем операции по машинам.
		
		Алгоритм:
		Для каждого работника набор операций выстраивается по снижению веса (сортировка по пузырьковому алгоритму)
		
		После этого работнику назначается станок. При недоступности станка, берется следующая операция в пуле. Проверяется
		наличие зависимости ("цепочки")*/
	public static void MashinesDuty(){
		//Очистка переменных для учета
		for (Worker Wor:Workers){
			Wor.setWorkTime(0);
			Wor.setWorkId(null);
		}
		
		//сортировка завершена.
		boolean bool=false;
		int step=0;
		while (step<100000){
			step+=1;//страховка от вечного цикла
			Current=RefreshWork();
			bool=true;
			for (Worker Wor:Workers)
				if (Wor.getPool().size()>0) {
					bool=false;
					break;
				}
			if ((bool)||(Current>WorkTime)) break;
		}
	}
	
	/**Вывод точного времени
	 * Считаем началом дня 8:00, конец 17:00, а обед 12:00-13:00
	 * @param M время в минутах с начала расчета расписания
	 * @return Время в формате "HH:mm dd.MM.yyyyг " */
	public static String DateTrans(Integer M){
		if (M==null) return "-";
		int Days=Math.floorDiv(M, 8*60);
		int Hours=Math.floorDiv(M-Days*8*60, 60)+8;
		int Minutes=M-Days*8*60-(Hours-8)*60;
		if (Hours>=12)Hours+=1;

		Calendar NDate = Calendar.getInstance();
		
		NDate.setTime(date1);
		NDate.add(Calendar.DAY_OF_MONTH, Days);
		NDate.add(Calendar.HOUR, Hours);
		NDate.add(Calendar.MINUTE, Minutes);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyyг ");	
		return (sdf.format(NDate.getTime()));

	}
	
	public static void main(String[] args) {
		
		//Загрузка данных из файлов
		int WaitNum=Loading(true);
		printfln("\nНайдено %s сцепленных операций"
				+ "\n--------------------------------------------------\n"
				+ "Проверка корректности данных",
				WaitNum);	
		// проверка соответствий названий агрегатов и профессий
		Check();
		println("Проверка корректности данных завершена"
				+ "\n--------------------------------------------------\n\n\n"
				+ "Введите дату начала расчета в формате ДД.ММ.ГГГГ");
		//Делаем запрос на диапазон расчета
		date1=RDate(false);
        println("\nВведите дату конца расчета");
        date2=RDate(false);
        if (date2.getTime()<date1.getTime()){
        	println("Конец расчета должен быть позже начала!");
        	System.exit(0);
        }
        long Days = 1+Math.abs(date1.getTime()-date2.getTime())/(1000*60*60*24);
        printfln("\nПланирование производится на %s дней (%s рабочих минут)"
        		+ "\n\n", Days, Days*8*60);
        
        WorkTime = (int) Days*8*60;//рабочий период (пригодится)
        
        /*Сортируем рабочих по количеству Станков а операции по относительной стоимости минуты. 
        Таким образом приоритет получат более дорогие операции для более узких спецов
        Последнее важно, так как широкий спец найдет альтернативное занятие легче узкого
        */
       
        ArrayList<Integer> key = new ArrayList<Integer>();
        for (Worker i:Workers) key.add(i.getMash().size());
        Sort.arrayMultiSort(Workers, key);
        
        key.clear();
        for (Operation i:Operations) key.add(i.getWeight());
        Sort.arrayMultiSort(Operations, key, true);

        /*Integer stek = null;
        Worker stekWorker=new Worker();
        for (int index=0;index<Workers.size();index++){
			if ((stek!=null)&&(stek>Workers.get(index).getMash().size())){
				stekWorker=Workers.get(index-1);
				Workers.set(index-1,Workers.get(index));
				Workers.set(index, stekWorker);
				index-=2;
				if (index<0)index=0;
			}
			stek=Workers.get(index).getMash().size();
		}*/

		/*Operation stekOperation=new Operation();
		stek=null;
		for (int index=0; index<Operations.size();index++){
			if ((stek!=null)&&(stek<Operations.get(index).getWeight())){
				stekOperation=Operations.get(index-1);
				Operations.set(index-1,Operations.get(index));
				Operations.set(index, stekOperation);
				index-=2;
				if (index<0)index=0;
			}
			stek=Operations.get(index).getWeight();
		}*/
		//распределяем задачи меж сотрудников 
        WorkersDuty();
        //расставляем работников по местам
        MashinesDuty();

        //Сохраняем результаты расчета
        File file = new File("PLAN.txt");
        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
            	filePrintf(out,"Расписание составляется на период с %s по %s",
            			DateTrans(0),
            			DateTrans(WorkTime-1));
            	filePrintf(out,"");
                for (String R:Records){
                	println(R);
                	filePrintf(out,R);
                }
                println("Не выполнено:");
                filePrintf(out,"-------------------------------------------------------------");
                filePrintf(out,"Не выполнено:");
                for(Operation Op:Operations){
                	if (((Op.getStart()==null)||(Op.getStart()+Op.getTime())>WorkTime)){
                		printfln("%s \tДолжно начаться %s \tСтоимость %sруб \tДлительность %sмин",
                				Op.getId(),
                				DateTrans(Op.getStart()),
                				Op.getCost(),
                				Op.getTime());
                		filePrintf(out,"%s \tДолжно начаться %s \tСтоимость %sруб \tДлительность %sмин",
                				Op.getId(),
                				DateTrans(Op.getStart()),
                				Op.getCost(),
                				Op.getTime());
                	}
                }
            	
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
	}
}
