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
    private final List<Worker> allWorkers = new ArrayList<>();
    private Integer currentIndex = 1;

    public static void main(String[] args) {
        Application application = new Application();
        application.start();
    }

    public void start() {
        Timer stop = new Timer();
        try {
            File file = new File(Configuration.INSTANCE.filepath);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            reciever = new ResultReciever(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int cores = Runtime.getRuntime().availableProcessors();

        CyclicBarrier barrier = new CyclicBarrier(cores, () -> {
            reciever.flush();
            if (DataBase.instance.getPrimesToGo() < Configuration.INSTANCE.stepWidth) {
                BigInteger stepWidthToCalcPrime = BigInteger.valueOf((Configuration.INSTANCE.stepWidth / cores) + 1);
                BigInteger primeStart = DataBase.instance.getPrimeSet().last();
                for (Worker worker : allWorkers) {
                    worker.loadNextPrimesToCalculate(primeStart, primeStart.add(stepWidthToCalcPrime));
                    primeStart = primeStart.add(stepWidthToCalcPrime.add(BigInteger.ONE));
                }
            } else {
                for (final Worker worker : allWorkers) {
                    worker.loadNextSteps(DataBase.instance.getPrimesAsList().get(currentIndex), DataBase.instance.getPrimesToGo() / cores);
                    currentIndex = currentIndex + DataBase.instance.getPrimesToGo() / cores;
                }
                DataBase.instance.setPrimesToGo(0);
            }

        });
        for (int i = 0; i < cores; i++) {
            Worker worker = new Worker(barrier, reciever);
            allWorkers.add(worker);
        }
        stop.schedule(new TimerTask() {
            @Override
            public void run() {
                allWorkers.forEach(Worker::stopThread);
                barrier.reset();
            }
        }, 900000);

        BigInteger stepWidthToCalcPrime = BigInteger.valueOf(Configuration.INSTANCE.stepWidth / cores + 1);
        BigInteger primeStart = BigInteger.ONE;
        for (Worker worker : allWorkers) {
            worker.loadNextPrimesToCalculate(primeStart, primeStart.add(stepWidthToCalcPrime));
            primeStart = primeStart.add(stepWidthToCalcPrime);
            worker.start();
        }
    }
}
