package parallelisierung;

import parallelisierung.data.DataBase;

public enum Configuration {
    INSTANCE;
    Integer startVal = 1;
    Integer stepWidth = 10000;
    String filepath = "./data/out.txt";
}
