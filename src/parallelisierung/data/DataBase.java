package parallelisierung.data;

import parallelisierung.BigMath;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum DataBase
{instance;
    private TreeSet<BigInteger> primeSet = new TreeSet<>();
    private BigInteger currentStepForPrime=BigInteger.ONE;


    public void addPrimes(Set<BigInteger> primes)
    {
        primeSet.addAll(primes);

    }

    public List<BigInteger> getPrimesasList()
    {
        return new ArrayList<>(primeSet);
    }

    public boolean isPrime(final BigInteger integer)
    {
        return primeSet.contains(integer);

    }

    public void calcNextPrimes(final BigInteger stepWidth)
    {
        BigInteger max =currentStepForPrime.add(stepWidth.add(BigInteger.TEN));
        Set<BigInteger> primes = new HashSet<>();
        while (currentStepForPrime.compareTo(max) < 0)
        {
            if (BigMath.returnPrime(currentStepForPrime))
            {
                primes.add(currentStepForPrime);
            }
            currentStepForPrime= currentStepForPrime.add(BigInteger.ONE);
        }
       primeSet.addAll(primes);
    }

    DataBase(){
        calcNextPrimes(BigInteger.valueOf(50));
    }
}
