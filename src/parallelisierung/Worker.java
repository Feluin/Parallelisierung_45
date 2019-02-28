package parallelisierung;

import parallelisierung.data.DataBase;

import parallelisierung.resultout.ResultReciever;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Worker extends Thread {
    private final ResultReciever receiver;
    private BigInteger current;
    private Integer stepsToGo;
    private CyclicBarrier barrier;
    private DataBase dataBase = DataBase.instance;
     private volatile boolean timeStop = false;


    private BigInteger startPrimes;
    private BigInteger stopPrimes;

    private boolean calcNextPrimes;

    public Worker(final CyclicBarrier barrier,
        ResultReciever receiver) {
        this.barrier = barrier;
        this.receiver = receiver;

    }

    public void run() {
        while (!timeStop) {
            if (calcNextPrimes) {
                insertNextPrimesInDatabase(startPrimes, stopPrimes);
                calcNextPrimes = false;
            } else {
                while (!timeStop&&current!=null) {
                    List<List<BigInteger>> conquartets=new ArrayList<>();
                    boolean con=false;
                    do{
                        List<BigInteger> quartets = checkForConsecutivePrimes(current);
                        stepsToGo--;
                        if (quartets != null) {
                            con=true;
                            conquartets.add(quartets);
                            current=dataBase.getPrimeSet().higher(current);
                        }else {
                            con=false;
                        }
                    }while (con);
                    if(conquartets.size()!=0)receiver.recieve(conquartets);
                    current=dataBase.getPrimeSet().higher(current);
                    if (stepsToGo <= 0) {
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


    public void loadNextSteps(BigInteger startVal,int stepsToGo) {
        current=startVal;
        this.stepsToGo=stepsToGo;
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
        Set<Integer> endings = new HashSet<>();
        TreeSet<BigInteger> primeSet = dataBase.getPrimeSet();
        List<BigInteger> quartets=new ArrayList<>();

        int i = 0;
        while (i < 4)
        {
            if(first==null)System.out.print(first);
            Integer endDigit =first.remainder(BigInteger.TEN).intValue();
            if (endings.contains(endDigit)) {
                break;
            }
            quartets.add(first);
            first=primeSet.higher(first);
            endings.add(endDigit);
            i++;
        }
        if (quartets.size() < 4)
            return null;
        else return quartets;

    }
}
