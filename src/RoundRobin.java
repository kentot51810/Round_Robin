import static java.lang.System.*;

import java.text.*;
import java.util.*;

public class RoundRobin {

    //Declaration of global variables
    private static int numberOfProcess;
    private static int quantum;
    private static float turnaroundTime, waitingTime;
    private static DecimalFormat decimalFormat;
    private static List<Integer> listOfBurstTime;
    private static List<Float> waitTimeList;
    private static List<Float> turnaroundTimeList;
    private static List<Map<String, Integer>> processBurstList;
    private static List<Map<String, Float>> processTurnaroundList;
    private static String[] processName;


    private static void initializeInstanceVariables() {
        listOfBurstTime = new ArrayList<>();
        processBurstList = new ArrayList<>();
        waitTimeList = new ArrayList<>();
        decimalFormat = new DecimalFormat("0.#");
        processTurnaroundList = new ArrayList<>();
        turnaroundTimeList = new ArrayList<>();
    }

    public static void main(String[] args) {
        initializeInstanceVariables();

        out.println("\n\t\tROUND ROBIN SCHEDULING ALGORITHM");
        out.print("\nEnter the total number of process: ");

        numberOfProcess = getInput(numberOfProcess);

        out.print("\n\nNumber of quantum:  ");
        quantum = getInput(quantum);

        out.println("\n\nEnter the burst time:\n");

        setTheBurstTime();
        saveProcessBurstList();

        getTATList();
        saveProcessTATList();

        showGanttChart();

        getAverageTurnAroundTime();
        out.println("THE AVERAGE TURNAROUND TIME IS: " + decimalFormat.format(turnaroundTime));
        getAverageWaitingTime();
        out.println("THE AVERAGE WAITING TIME IS: " + decimalFormat.format(waitingTime));

        out.print("\n\n");

    }

    private static void getAverageWaitingTime() {
        List<Float> waitTime = new ArrayList<>();
        List<Float> toBeSubtracted = new ArrayList<>();
        List<Float> totalWaitingTime = new ArrayList<>();
        for (int index = 0; index < processName.length; index++){
            ListIterator<Map<String, Float>> mapListIterator = processTurnaroundList.listIterator();
            while (mapListIterator.hasNext()){
                Map.Entry<String, Float> entry = mapListIterator.next().entrySet().iterator().next();
                String key = entry.getKey();
                //iterate all and save the values to list.
                if (key.equals(processName[index])){
                    //TODO: Understand the context that I was trying to come up here a couple days ago.
//                    if (mapListIterator.previousIndex() == 0 && index == 0)
//                        toBeSubtracted.add(0f);
                    float value = entry.getValue();
                    toBeSubtracted.add(value);

                    if (mapListIterator.previousIndex() != 0){
                        mapListIterator.previous();
                        Map.Entry<String, Float> next = mapListIterator.previous().entrySet().iterator().next();

                        waitTime.add(next.getValue());
                        mapListIterator.next();
                        mapListIterator.next();
                    }

                }
                //if the map Iterator reached the last limit of the process[index] then do the computation.
                if (!mapListIterator.hasNext()){
                    float value = 0;
                    if (index == 0){
                        for (int i = 0; i < waitTime.size(); i++) {
                            float subtrahend = toBeSubtracted.get(i);
                            float waitTimeValue = waitTime.get(i);
                            value += (waitTimeValue - subtrahend);
                        }
                    }else {
                        for (int i = 0; i < waitTime.size() - 1; i++) {
                            int k = i +1;
                            float waitTimeValue = waitTime.get(k);
                            float subtrahend = toBeSubtracted.get(i);
                            value += (waitTimeValue - subtrahend);
                        }
                        value += waitTime.get(0);
                    }
                    waitTime.clear();
                    toBeSubtracted.clear();
                    totalWaitingTime.add(value);
                }
            }
        }

        for (float value : totalWaitingTime){
            waitingTime += value;
        }
        waitingTime /= numberOfProcess;

    }

    private static void showGanttChart() {
        out.println("--------------------------------Gantt Chart--------------------------------");

        out.print("PROCESS  ");
        for (int index = 0; index < processBurstList.size(); index++) {
            Map.Entry<String, Integer> entries = processBurstList.get(index).entrySet().iterator().next();
            out.format("|%2s%5s%5s","",entries.getKey(),"");
        }
        out.println("|");

        out.print("BURST    ");
        for (int index = 0; index < processBurstList.size(); index++) {
            Map.Entry<String, Integer> entries = processBurstList.get(index).entrySet().iterator().next();
            out.format("|%2s%5s%5s","",entries.getValue(),"");
        }
        out.println("|");


        out.format("%10s",0);
        for (int index = 0; index < processBurstList.size(); index++) {
            out.format("%13s",decimalFormat.format(turnaroundTimeList.get(index)));
        }

//        for (int index = 0; index < processBurstList.size(); index++){
//            Map.Entry<String, Integer> entries = processBurstList.get(index).entrySet().iterator().next();
//            out.print("PROCESS  ");
//            out.format("|%2s%5s%5s","",entries.getKey(),"");
//        }

        out.println("\n\n");
    }

    private static void saveProcessBurstList() {

        processName = new String[numberOfProcess];

        for (int index = 0; index < numberOfProcess; index++) {
            processName[index] = "P" + (index + 1);
        }

        int index = 0;
        while (listOfBurstTime.get(index) != 0) {
            Map<String, Integer> cpuRR = new HashMap<>();

            int rr = 0;
            if (listOfBurstTime.get(index) > quantum) {
                rr = listOfBurstTime.get(index) - quantum;
                cpuRR.put(processName[index], quantum);
            } else {
                //if the value of burst time is less than the quantum then subtract the value of burst time to itself.
                rr = 0;
                cpuRR.put(processName[index], listOfBurstTime.get(index));
            }
            if (listOfBurstTime.get(index) == 0) {
                index++;
                if (index == numberOfProcess) {
                    index = 0;
                }
                continue;
            }
            processBurstList.add(cpuRR);

            listOfBurstTime.set(index, rr);


            index++;
            if (index == numberOfProcess)
                index = 0;
        }
    }

    private static void saveProcessTATList() {
        for (int index = 0; index < processBurstList.size(); index++){
            Map.Entry<String, Integer> s = processBurstList.get(index).entrySet().iterator().next();
            String processName = s.getKey();
            float tat = turnaroundTimeList.get(index);
            Map<String, Float> processTATMap = new HashMap<>();
            processTATMap.put(processName, tat);

            processTurnaroundList.add(processTATMap);
        }
    }

    private static void setTheBurstTime() {
        for (int index = 0; index < numberOfProcess; index++) {
            out.print("P[" + (index + 1) + "]: ");
            int burstTime = 0;
            burstTime = getInput(burstTime);
            listOfBurstTime.add(burstTime);
        }

    }

    private static int getInput(int input) {
        Scanner scn = new Scanner(in);
        boolean continueLoop = true;
        while (continueLoop) {
            try {
                input = scn.nextInt();
                continueLoop = false;
            } catch (InputMismatchException e) {
                scn.nextLine();
                err.println("Error! Please enter integers only");
            }
        }
        return input;
    }

    private static void getTATList() {
        for (int index = 0; index < processBurstList.size(); index++) {
            Map.Entry<String, Integer> s = processBurstList.get(index).entrySet().iterator().next();
            turnaroundTime += s.getValue();
            turnaroundTimeList.add(turnaroundTime);
        }

        // return theWaitingTime;
    }

    private static void getAverageTurnAroundTime() {
        turnaroundTime=0;
        for (int index = 0; index < processName.length; index++){
            ListIterator<Map<String, Float>> mapListIterator = processTurnaroundList.listIterator(processBurstList.size());
            while (mapListIterator.hasPrevious()){
                Map.Entry<String, Float> entry = mapListIterator.previous().entrySet().iterator().next();
                String key = entry.getKey();
                if (key.equals(processName[index])){
//                    Map<String, Float> previous = mapListIterator.previous();
//                    Set<Map.Entry<String, Float>> entries = previous.entrySet();
//                    Iterator<Map.Entry<String, Float>> iterator = entries.iterator();
//                    Map.Entry<String, Float> next = iterator.next();


//                    Map.Entry<String, Float> processTAT = mapListIterator.previous().entrySet().iterator().next();
                    turnaroundTime += entry.getValue();
                    break;
                }
            }
        }
        turnaroundTime /= numberOfProcess;
    }
}
