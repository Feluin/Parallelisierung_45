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

public class Application
{

    private ResultReciever reciever;
    private List<Worker> allworkers = new ArrayList<>();
    private Integer currentIndex = 1;

    public static void main(String[] args)
    {
        Application application = new Application();
        application.start();
    }

    public void start()
    {
        Timer stop = new Timer();
        try
        {
            File file = new File(Configuration.INSTANCE.filepath);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            reciever = new ResultReciever(fileWriter);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        int cores = Runtime.getRuntime().availableProcessors();

        CyclicBarrier barrier = new CyclicBarrier(cores, () -> {
            reciever.flush();
            if (DataBase.instance.getPrimesToGo() < Configuration.INSTANCE.stepWidth)
            {
                BigInteger stepWidthtoCalcPrime = BigInteger.valueOf((Configuration.INSTANCE.stepWidth / cores)+1);
                BigInteger primeStart = DataBase.instance.getPrimeSet().last();
                for (Worker worker : allworkers)
                {
                    worker.loadNextPrimesToCalculate(primeStart, primeStart.add(stepWidthtoCalcPrime));
                    primeStart = primeStart.add(stepWidthtoCalcPrime.add(BigInteger.ONE));
                }
            } else
            {
                for (final Worker worker : allworkers)
                {
                    worker.loadNextSteps(DataBase.instance.getPrimesasList().get(currentIndex), DataBase.instance.getPrimesToGo() / cores);
                    currentIndex = currentIndex + DataBase.instance.getPrimesToGo() / cores;
                }
                DataBase.instance.setPrimesToGo(0);
            }

        });
        for (int i = 0; i < cores; i++)
        {
            Worker worker = new Worker(barrier, reciever);
            allworkers.add(worker);
        }
        stop.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                allworkers.forEach(Worker::stopThread);
                barrier.reset();
            }
        }, 900000);

        BigInteger stepWidthtoCalcPrime = BigInteger.valueOf(Configuration.INSTANCE.stepWidth / cores + 1);
        BigInteger primeStart = BigInteger.ONE;
        for (Worker worker : allworkers)
        {
            worker.loadNextPrimesToCalculate(primeStart, primeStart.add(stepWidthtoCalcPrime));
            primeStart = primeStart.add(stepWidthtoCalcPrime);
            worker.start();
        }
    }
}
