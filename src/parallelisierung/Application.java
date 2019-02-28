package parallelisierung;

import parallelisierung.data.DataBase;
import parallelisierung.resultout.ResultReciever;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CyclicBarrier;

public class Application {

    private ResultReciever reciever;

    private BigInteger startVal = BigInteger.TWO;
    private BigInteger stepWidth = new BigInteger("10000");
    private String filepath = "./data/out.txt";
    private List<Worker> allworkers = new ArrayList<>();
    private BigInteger primeStart=BigInteger.ONE;

    public static void main(String[] args) {
        Application application = new Application();
        application.start();
    }

    public void start() {
        Timer stop = new Timer();
        try {
            File file = new File(filepath);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            reciever = new ResultReciever(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int cores = Runtime.getRuntime().availableProcessors();

        CyclicBarrier barrier = new CyclicBarrier(cores, () -> {
            reciever.flush();
            if (primeStart.compareTo(startVal.add(stepWidth.divide(BigInteger.TEN)))<0) {
                BigInteger stepWidthtoCalcPrime = stepWidth.divide(BigInteger.valueOf(cores)).add(BigInteger.ONE);

                for (int i = 0; i < allworkers.size(); i++) {
                    Worker worker = allworkers.get(i);
                    worker.loadNextPrimesToCalculate(primeStart, primeStart.add(stepWidthtoCalcPrime));
                    primeStart=primeStart.add(stepWidthtoCalcPrime);
                }
            } else {
                for (Worker worker : allworkers) {
                    worker.nextStep(startVal, startVal.add(stepWidth));
                }
                startVal = startVal.add(stepWidth);
            }


        });
        for (int i = 0; i < cores; i++) {
            Worker worker = new Worker(barrier, cores, reciever, i, stepWidth);
            allworkers.add(worker);
        }
        stop.schedule(new TimerTask() {
            @Override
            public void run() {
                allworkers.forEach(Worker::stopThread);
                barrier.reset();
            }
        }, 90000);
        BigInteger stepWidthtoCalcPrime = stepWidth.divide(BigInteger.valueOf(cores)).add(BigInteger.ONE);
        for (int i = 0; i < allworkers.size(); i++) {
            Worker worker = allworkers.get(i);
            worker.loadNextPrimesToCalculate(primeStart, primeStart.add(stepWidthtoCalcPrime));
            primeStart=primeStart.add(stepWidthtoCalcPrime);
            worker.start();
        }
    }
}
