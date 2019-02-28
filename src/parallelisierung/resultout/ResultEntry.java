package parallelisierung.resultout;

import java.math.BigInteger;

import java.util.List;

public class ResultEntry {
    private final List<List<BigInteger>> quartets;


    public ResultEntry(List<List<BigInteger>> quartets) {
        this.quartets = quartets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        quartets.forEach(quartet -> {
            quartet.forEach(bigInteger -> builder.append(bigInteger.toString()).append(","));
            builder.deleteCharAt(builder.lastIndexOf(","));
            builder.append("\n");

        });

        return builder.toString();
    }
}
