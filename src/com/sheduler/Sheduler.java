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
	public static ArrayList<Mashine> models = new ArrayList<Mashine>();		
	public static ArrayList<Worker> workers = new ArrayList<Worker>();		
	public static ArrayList<Operation> operations = new ArrayList<Operation>();
	public static BufferedReader br;
	public static List<String> records = new ArrayList<String>();
	public static int current =0;
	public static int workTime=0;
	public static Date date1 = new Date();
	public static Date date2 = new Date();
	
	/**Загрузка данных из файлов
	 * @param debag если true - дублирует загруженные строки в консоль
	 * @return int количество "ведомых" операций
	 * */
	private int Loading(boolean debag){
		int result=0;
		Operation operation1;
		Operation operation2;
		
		String[] files = {"mashines.txt","workers.txt","Operations.txt"}; //список файлов для обработки
		String[] stroke; //оперативная строковая переменная
		
		int waitNum = 0;//количество "ожидающих" операций (просто для статистики)
		
		for (String load:files){
			try {
				String sCurrentLine;
				int i=0;	
				br = new BufferedReader(new FileReader(load));
				
				while ((sCurrentLine = br.readLine()) != null) {
					stroke=sCurrentLine.split("\t");
					if (stroke[0].length()==0) continue;//проверка на "пустую" строку
					switch (load){
						case "mashines.txt":{
							for (int k : range(Integer.valueOf(stroke[1]))){
								models.add(new Mashine(stroke));
								}
							break;
						}
						case "workers.txt":{
							workers.add(new Worker(stroke));								
							break;
						}
						case "Operations.txt":{
							operations.add(new Operation(stroke));
							i=operations.size()-1;
							operation1=operations.get(i);
							if (operation1.getWait()){
								operation2=operations.get(i-1);
								waitNum++;
								operation2.setWeight(Math.round((operation2.getWeight()+operation1.getWeight())/2));
								operation1.setWeight(operation2.getWeight());
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
			for (Mashine model : models) {
				printfln("%s - %s",
						model.getId(),
						model.getType());
				}
			println("Работники:");
			for (Worker worker : workers) {
				printfln("%s (%s - %s)",
						worker.getId(),
						worker.getProf(),
						worker.getMash());
				}
			println("Поставленные задачи:");
			for (Operation operation : operations) {
				printfln("%s (%s - %s/ %sмин - %sрублей)",
						operation.getId(),
						operation.getProf(),
						operation.getMash(),
						operation.getTime(),
						operation.getCost());
				}
			}
		result=waitNum;
		return result;
	}
	/**Проверка загруженных данных
	 * */
	private void Check(){// проверка соответствий названий агрегатов и профессий
		boolean errorPresent=false;//в массиве обнаружена ошибка
		boolean hasErrors=false;//при проверке ошибки были обнаружены
		
		for (Worker worker:workers)
			for (String mash:worker.getMash()){
				errorPresent=true;
				for (Mashine model:models)
					if (model.getType().equals(mash)){ 
						errorPresent=false;
						break;
					}
				if(errorPresent) {
					printfln("Оборудование указаное в записи %s %s в базе данных не обнаружено",
							worker.getId(),
							worker.getMash());
					hasErrors=true;
				}
		}
		for (Operation operation:operations){
			errorPresent=true;
			for (Mashine model:models)
				if (model.getType().equals(operation.getMash())) {
					errorPresent=false;
					break;
			}
			if(errorPresent) {printfln("Оборудование указаное в записи %s %s в базе данных не обнаружено",
					operation.getId(),
					operation.getMash());
				hasErrors=true;
			}
		}
		for (Operation operation:operations){
			errorPresent=true;
			for (Worker worker:workers)
				if (worker.getProf().equals(operation.getProf())){
					errorPresent=false;
					break;
				}
			if(errorPresent) {printfln("Профессия указаная в записи %s %s среди персонала не встречается",
					operation.getId(),
					operation.getProf());
				hasErrors=true;
			}
		}
		for (Operation operation:operations){
			if (operation.getWait()) {
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
	private Date RDate(){
		br=new BufferedReader(new InputStreamReader(System.in));
		SimpleDateFormat format = new SimpleDateFormat();
		format.applyPattern("dd.MM.yyyy");
		Date date = new Date();
		try {
			String stroke;
			stroke=br.readLine();
			if (stroke.length()==0) return format.parse(format.format(date));
			try {				
				date= format.parse(stroke);
			} catch (ParseException e) {
				println("Неправильно введена дата");
				System.exit(0);
			}
		} catch (IOException | ParseException e) {
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
	private void FillPool(Worker worker){//
		for (Operation operation:operations){
			if (worker.getProf().equals(operation.getProf())){
				if (worker.getMash().indexOf(operation.getMash())!=-1){
					worker.setWorkTime(worker.getWorkTime()+operation.getTime());
					operation.setPretendents(operation.getPretendents()+1);
					worker.addPool(operations.indexOf(operation));
				}
			}
		}
	}

	/**Сброс лишней операции из пула. Лишней считается первая операция с количеством претендентов более 1го
	 * Функция должна вызываться для рабочегос  наибольшей временной загрузкой
	 * @param Worker объект-рабочий
	 * @return true - если не найдено спорных работ, false - если найдена и сброшена спорная работа
	 * */
	private boolean DropPool(Worker worker){
		boolean bool=true;
		Operation operationTempVar;
		for (int IDOp:worker.getPool()){
			operationTempVar=operations.get(IDOp);
			if (operationTempVar.getPretendents()>1){
				bool=false;
				worker.setWorkTime(worker.getWorkTime()-operationTempVar.getTime());
				worker.removePool(worker.getPool().indexOf(IDOp));
				operationTempVar.setPretendents(operationTempVar.getPretendents()-1);
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
	private void WorkersDuty(){
				
        int biggerTime = 0; //Наибольшее время загрузки
        int biggerID=-1; //Индекс работника с наибольшим временем загрузки в коллекции
        // первичное распределение операций
        for (Worker Wor:workers){
        	FillPool(Wor);
        	//сравнение BiggerTin=me со временем загрузки данного работника
        	//Если новое время выше, biggerTime переписывается и запоминается новый biggerID
        	if (biggerTime<Wor.getWorkTime()){
        		biggerTime=Wor.getWorkTime();
        		biggerID=workers.indexOf(Wor);
        	}
        }
        
        //отказ от спорных операций
        boolean fin=false;
        while(!fin){
        	//проверка количества претендентов на операцию
        	fin=true;
        	for (Operation operation:operations){
        		//если не все операции распределены, то флаг fin=false 
        		if (operation.getPretendents()>1){
        			fin=false;
        			break;
        		}
        	}
        	workers.get(biggerID).setFin(DropPool(workers.get(biggerID))); 
        	//сброс операции от самого занятого
        	//и обнуление переменных
        	biggerTime=0;
       		biggerID=-1;
       		//ищем следующего "жадину"
            for (Worker Wor:workers){
            	if ((!Wor.getFin())&&(biggerTime<Wor.getWorkTime())){
            		biggerTime=Wor.getWorkTime();
            		biggerID=workers.indexOf(Wor);
            	}
            }
        }		
	}
	
	/**Обновление данных о статусе работ
	 * @return Время в минутах от начала расчета расписания, в которое завершена, как минимум одна работа
	 * */
	private int RefreshWork(){
		//Изначально отметка времени ставится на минуту позже верхней границы периода расчета
		int timeStamp=workTime+1;
		//флаг пропуска ожидающих "ведомых" операций
		boolean skip=false;
		
		Operation operation1;
		Operation operation2;
		for (Worker worker:workers){
		//проверка на превышение текущего времени над временем завершения работы. 
		//Если истинно, то из пула удаляется выполненная операция
			if ((worker.getWorkTime()<=current)&&(worker.getWorkId()!=null)){
				worker.removePool(worker.getPool().indexOf(worker.getWorkId()));
				worker.setWorkId(null);
			}
			//если нет назначенной операции, то происходит выбор новой операции из пула
			if (worker.getWorkId()==null){
				label1:
				for (int idOperation:worker.getPool()){
					skip=false;
					//проводится проверка на выполнение ведущей операции, если данная является ведомой
					operation1=operations.get(idOperation);
					if (operation1.getWait()){
						operation2=operations.get(idOperation-1);
						skip=((operation2.getStart()!=null)&&
								(current>=(operation2.getStart()+operation2.getTime())))
								?false:true;
					}
					//при получении "разрешения" Операции в коллекции Operations приписывается время старта.
					if (!skip){
						for (Mashine model:models){
							if ((model.getBusy()<=current)&&(model.getType().equals(operation1.getMash()))){
								worker.setWorkTime(operation1.getTime()+current);
								model.setBusy(operation1.getTime()+current);
								worker.setWorkId(idOperation);
								operation1.setStart(current);
								records.add(String.format("%s по %s %s=> занимает %s для %s :%sмин",
										DateTrans(current),
										DateTrans(worker.getWorkTime()),
										worker.getId(),
										model.getId(),
										operation1.getId(),
										operation1.getTime()));
								break label1;
							}
						}
					}
				}
			}
		}
		//производится перебор рабочих, для определения минимального времени завершения
		for (Worker worker:workers){
			if((worker.getWorkId()!=null)&&(worker.getPool().size()>0)&&(timeStamp>worker.getWorkTime())) timeStamp=worker.getWorkTime();
		}
		
		return timeStamp;
	}
	/**раскидываем операции по машинам.
		
		Алгоритм:
		Для каждого работника набор операций выстраивается по снижению веса (сортировка по пузырьковому алгоритму)
		
		После этого работнику назначается станок. При недоступности станка, берется следующая операция в пуле. Проверяется
		наличие зависимости ("цепочки")*/
	private void MashinesDuty(){
		//Очистка переменных для учета
		for (Worker worker:workers){
			worker.setWorkTime(0);
			worker.setWorkId(null);
		}
		
		//сортировка завершена.
		boolean bool=false;
		int step=0;
		while (step<100000){
			step+=1;//страховка от вечного цикла
			current=RefreshWork();
			bool=true;
			for (Worker worker:workers)
				if (worker.getPool().size()>0) {
					bool=false;
					break;
				}
			if ((bool)||(current>workTime)) break;
		}
	}
	
	/**Вывод точного времени
	 * Считаем началом дня 8:00, конец 17:00, а обед 12:00-13:00
	 * @param M время в минутах с начала расчета расписания
	 * @return Время в формате "HH:mm dd.MM.yyyyг " */
	private String DateTrans(Integer m){
		if (m==null) return "-";
		int days=Math.floorDiv(m, 8*60);
		int hours=Math.floorDiv(m-days*8*60, 60)+8;
		int minutes=m-days*8*60-(hours-8)*60;
		if (hours>=12)hours+=1;

		Calendar NDate = Calendar.getInstance();
		
		NDate.setTime(date1);
		NDate.add(Calendar.DAY_OF_MONTH, days);
		NDate.add(Calendar.HOUR, hours);
		NDate.add(Calendar.MINUTE, minutes);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyyг ");	
		return (simpleDateFormat.format(NDate.getTime()));

	}
	
	public void MakeShedule() {
		
		//Загрузка данных из файлов
		int waitNum=Loading(true);
		printfln("\nНайдено %s сцепленных операций"
				+ "\n--------------------------------------------------\n"
				+ "Проверка корректности данных",
				waitNum);	
		// проверка соответствий названий агрегатов и профессий
		Check();
		println("Проверка корректности данных завершена"
				+ "\n--------------------------------------------------\n\n\n"
				+ "Введите дату начала расчета в формате ДД.ММ.ГГГГ  (или нажмите Enter для сегодняшней даты)");
		//Делаем запрос на диапазон расчета
		date1=RDate();
        println("\nВведите дату конца расчета (или нажмите Enter для сегодняшней даты)");
        date2=RDate();
        if (date2.getTime()<date1.getTime()){
        	println("Конец расчета должен быть позже начала!");
        	System.exit(0);
        }
        long days = 1+Math.abs(date1.getTime()-date2.getTime())/(1000*60*60*24);
        printfln("\nПланирование производится на %s дней (%s рабочих минут)"
        		+ "\n\n", days, days*8*60);
        
        workTime = (int) days*8*60;//рабочий период (пригодится)
        
        /*Сортируем рабочих по количеству Станков а операции по относительной стоимости минуты. 
        Таким образом приоритет получат более дорогие операции для более узких спецов
        Последнее важно, так как широкий спец найдет альтернативное занятие легче узкого
        */
       
        ArrayList<Integer> key = new ArrayList<Integer>();
        for (Worker worker:workers) key.add(worker.getMash().size());
        Sort.arrayMultiSort(workers, key);
        
        key.clear();
        for (Operation operation:operations) key.add(operation.getWeight());
        Sort.arrayMultiSort(operations, key, true);
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
            			DateTrans(workTime-1));
            	filePrintf(out,"");
                for (String record:records){
                	println(record);
                	filePrintf(out,record);
                }
                println("Не выполнено:");
                filePrintf(out,"-------------------------------------------------------------");
                filePrintf(out,"Не выполнено:");
                for(Operation operation:operations){
                	if (((operation.getStart()==null)||(operation.getStart()+operation.getTime())>workTime)){
                		printfln("%s \tДолжно начаться %s \tСтоимость %sруб \tДлительность %sмин",
                				operation.getId(),
                				DateTrans(operation.getStart()),
                				operation.getCost(),
                				operation.getTime());
                		filePrintf(out,"%s \tДолжно начаться %s \tСтоимость %sруб \tДлительность %sмин",
                				operation.getId(),
                				DateTrans(operation.getStart()),
                				operation.getCost(),
                				operation.getTime());
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
