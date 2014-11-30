package OptimizationTSP;

/**
 * Created by sf2013 on 26.11.14.
 */
public class OptimizationMethodContext {
    private Optimization optimization;
    public OptimizationMethodContext() {}

    public void setOptimizationMethod(Optimization optimization){
        this.optimization = optimization;
    }

    public int[] getOptimalPath(){
        return optimization.getOptimalPath();
    }
    public double getBestLength(){
        return optimization.getBestLength();
    }
}
