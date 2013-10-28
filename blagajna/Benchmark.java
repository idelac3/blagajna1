package blagajna;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * This class if useful to insert benchmarks into a code and measure execution time.
 * @author eigorde
 *
 */
public class Benchmark {
        
    private DecimalFormat dF = new DecimalFormat("0.00");
    
    private long startValue;
    
    /**
     * Start measuring.
     */
    public void start() {
        startValue = new Date().getTime();
    }
    
    /**
     * Return result.
     * @return value in msec. as <I>long</I> type.
     */
    public long result() {
        long endValue = new Date().getTime();
        return endValue - startValue;
    }
    
    /**
     * Return result.
     * @return value in msec.
     */
    public String getMSec() {    
        return dF.format(result());
    }
    
    /**
     * Return result.
     * @return value in sec.
     */
    public String getSec() {
        return dF.format(result() / 1000);
    }

}
