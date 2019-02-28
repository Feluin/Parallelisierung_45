package parallelisierung.resultout;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ResultReciever {

    private FileWriter writer;
    private boolean test = false;
    private List<ResultEntry> entrylist;

    public ResultReciever(FileWriter writer) {
        if (writer != null) {
            this.writer = writer;
        } else {
            test = true;
            entrylist = new ArrayList<>();
        }
    }

    public void recieve(List<List<BigInteger>> quartets) {
        ResultEntry entry=new ResultEntry(quartets);
        if (!test) {
            try {
                writer.write("\n"+entry.toString()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            entrylist.add(entry);
        }
    }

    @Deprecated //For testing
    public List<ResultEntry> getEntrylist() {
        return entrylist;
    }
    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
