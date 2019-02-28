package parallelisierung;

import parallelisierung.data.DataBase;

import parallelisierung.resultout.ResultReciever;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {
    private final ResultReciever receiver;
    private BigInteger startVal;
    private CyclicBarrier barrier;
    private int offset;
    private DataBase dataBase = DataBase.instance;
    private volatile boolean timeStop = false;
    private int cores;
    private BigInteger stepWidth;
    private BigInteger stepStop;

    private BigInteger startPrimes;
    private BigInteger stopPrimes;

    private boolean calcNextPrimes;

    public Worker(final CyclicBarrier barrier, int cores, ResultReciever receiver, final int offset, BigInteger stepWidth) {
        this.barrier = barrier;
        this.offset = offset;
        this.cores = cores;
        this.receiver = receiver;
        this.stepWidth = stepWidth;
    }

    public void run() {
        while (!timeStop) {
            if (calcNextPrimes) {
                insertNextPrimesInDatabase(startPrimes, stopPrimes);
                calcNextPrimes = false;
            } else {
                while (!timeStop) {
                    //do fancy berechnungen
                    List<BigInteger> quartets = checkForConsecutivePrimes(startVal);
                    if (quartets != null) {
                        receiver.recieve(quartets);
                    }
                    startVal = startVal.add(BigInteger.valueOf(cores));
                    if (startVal.compareTo(stepStop) > 0) {
                        break;
                    }
                }

            }

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ignored) {
            }
        }
    }

    public void nextStep(BigInteger from, BigInteger until) {
        startVal = from.add(BigInteger.valueOf(offset));
        stepStop = startVal.add(stepWidth);
    }

    public Set<BigInteger> calculatePrimes(BigInteger from, BigInteger until) {
        Set<BigInteger> primeSet = new HashSet<>();
        while (from.compareTo(until) < 0) {
            if (BigMath.returnPrime(from)) {
                primeSet.add(from);
            }
            from = from.add(BigInteger.ONE);
        }
        return primeSet;
    }

    public void insertNextPrimesInDatabase(BigInteger from, BigInteger until) {
        Set<BigInteger> primes = calculatePrimes(from, until);
        dataBase.addPrimes(primes);
    }

    public void stopThread() {
        this.timeStop = true;
    }

    public void loadNextPrimesToCalculate(BigInteger from, BigInteger until) {
        startPrimes = from;
        stopPrimes = until;
        calcNextPrimes = true;
    }

    public List<BigInteger> checkForConsecutivePrimes(BigInteger first) {
        List<BigInteger> primes = dataBase.getPrimesasList();
        Set<Integer> endings = new HashSet<>();
        int index = primes.indexOf(first);

        while (true) {

            Integer endDigit = primes.get(index).remainder(BigInteger.TEN).intValue();
            if (endings.contains(endDigit)) {
                break;
            }
            endings.add(endDigit);
            index++;
        }
        if (endings.size() < 4)
            return null;
        else return primes.subList(primes.indexOf(first), index);

    }
}
