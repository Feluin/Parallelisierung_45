package parallelisierung;

public enum Configuration {
    INSTANCE;
    Integer startVal = 1;
    final Integer stepWidth = 10000;
    String filepath = "./data/out.txt";
}
