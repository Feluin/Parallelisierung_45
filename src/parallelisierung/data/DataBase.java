package parallelisierung.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public enum DataBase
{
    instance;
    private TreeSet<BigInteger> primeSet = new TreeSet<>();

    private Integer primesToGo=0;

    public synchronized void addPrimes(Set<BigInteger> primes)
    {
        if (primes != null)
        {
            int start =primeSet.size();
            primeSet.addAll(primes);
            int added=(primeSet.size()-start);
            primesToGo += added;
        }

    }

    public TreeSet<BigInteger> getPrimeSet()
    {
        return primeSet;
    }

    public List<BigInteger> getPrimesasList()
    {
        return new ArrayList<>(primeSet);
    }

    public boolean isPrime(final BigInteger integer)
    {
        return primeSet.contains(integer);

    }

    DataBase()
    {
    }

    public Integer getPrimesToGo()
    {
        return primesToGo;
    }

    public void setPrimesToGo(final Integer primesToGo)
    {
        this.primesToGo = primesToGo;
    }
}
