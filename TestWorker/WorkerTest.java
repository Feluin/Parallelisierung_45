import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import parallelisierung.Worker;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class WorkerTest {

    @Test
    public void testWorkerPrimes() {
        Worker worker = new Worker(null, null);
        Set<BigInteger> primes = worker.calculatePrimes(BigInteger.valueOf(0), BigInteger.valueOf(1000));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(2)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(3)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(5)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(7)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(11)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(293)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(389)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(467)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(727)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(811)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(997)));
        Assertions.assertTrue(primes.contains(BigInteger.valueOf(977)));
    }
    @Test
    public void testWorkerQuad(){
        Worker worker = new Worker(null, null);
        worker.insertNextPrimesInDatabase(BigInteger.valueOf(0), BigInteger.valueOf(1000));
        List<BigInteger> quad = worker.checkForConsecutivePrimes(BigInteger.valueOf(2));
        Assertions.assertEquals(4,quad.size());
        Assertions.assertTrue(quad.contains(BigInteger.valueOf(2)));
        Assertions.assertTrue(quad.contains(BigInteger.valueOf(3)));
        Assertions.assertTrue(quad.contains(BigInteger.valueOf(5)));
        Assertions.assertTrue(quad.contains(BigInteger.valueOf(7)));
    }
}
