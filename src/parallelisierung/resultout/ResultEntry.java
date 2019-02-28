package parallelisierung.resultout;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ResultEntry {
    List<List<BigInteger>> quartets = new ArrayList<>();


    public ResultEntry(List<BigInteger> unparsedQuartets) {
        for (int i = 0; i < unparsedQuartets.size() - 4; i++) {
            quartets.add(unparsedQuartets.subList(i, i + 3));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        quartets.forEach(quartets ->{
            quartets.forEach(bigInteger ->builder.append(bigInteger.toString()).append(","));
            builder.deleteCharAt(builder.lastIndexOf(","));
            builder.append("\n");

        });
        return builder.toString();
    }
}
