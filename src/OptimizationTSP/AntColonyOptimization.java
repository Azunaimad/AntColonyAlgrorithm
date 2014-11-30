package OptimizationTSP;


import matrix.Matrix;

import java.util.Random;

/**
 * Created by sf2013 on 26.11.14.
 * https://code.google.com/p/efficient-java-matrix-library/
 * http://commons.apache.org/proper/commons-math/
 */
public class AntColonyOptimization extends Matrix implements Optimization {
    /*
    * Input variables for algorithm
     */
    protected int antsNumber; //m
    protected long iterNumber; //maxt
    protected long eliteAntsNumber; //e
    protected double parameterAlpha; //a
    protected double parameterBeta; //b
    protected double pheromonesEvaporation; //p
    protected Matrix destinationArray; //D
    protected Matrix pheromonesDistribution; //tau

    protected double parameterQ; //Q
    protected int townsNumber; //n
    protected Matrix visibility; //eta
    protected Matrix antRoutes; //T(m,n+1)

    protected double bestLength; //Lbest
    protected Matrix routeLengths;

    protected int[] bestRoute;

    public AntColonyOptimization() {

    }

    public AntColonyOptimization(int antsNumber, long iterNumber, double[][] destinationArray,
                                 double parameterAlpha, double parameterBeta,
                                 double pheromonesEvaporation, long eliteAntsNumber,
                                 double initialPheromonesDistribution) {
        this.antsNumber = antsNumber;
        this.iterNumber = iterNumber;
        this.destinationArray = new Matrix(destinationArray);
        this.townsNumber = destinationArray[0].length;
        this.parameterAlpha = parameterAlpha;
        this.parameterBeta = parameterBeta;
        this.pheromonesEvaporation = pheromonesEvaporation;
        this.eliteAntsNumber = eliteAntsNumber;
        this.pheromonesDistribution = new Matrix(townsNumber, townsNumber);
        for (int i = 0; i < townsNumber; i++) {
            this.pheromonesDistribution.setValue(i, i, 0.0);
            for (int j = 0; j < townsNumber; j++)
                if (i != j)
                    this.pheromonesDistribution.setValue(i, j, initialPheromonesDistribution);
        }
        this.visibility = divideMatrixElements(1.0, this.destinationArray);
        routeLengths = new Matrix(1, antsNumber);
        bestLength = 999999.0;
        this.bestRoute = new int[townsNumber + 1];
    }

    private void countBestLength() {
        double Lmin = routeLengths.getMinValue();
        if (Lmin < bestLength) bestLength = Lmin;
    }

    private void countRouteLengths() {
        for (int i = 0; i < antsNumber; i++) {
            routeLengths.setValue(0, i, 0);
            for (int j = 0; j < townsNumber; j++)
                routeLengths.setValue(0, i, routeLengths.getValue(0, i) +
                        destinationArray.getValue((int) antRoutes.getValue(i, j),
                                (int) antRoutes.getValue(i, j + 1)));
        }
    }

    @Override
    public double getBestLength() {
        return bestLength;
    }

    private void getBestRoute() {
        for (int i = 0; i < antsNumber; i++)
            if (routeLengths.getValue(0, i) <= bestLength)
                for (int j = 0; j < antRoutes.getNCols(); j++)
                    bestRoute[j] = (int) antRoutes.getValue(i, j);
    }

    private void getNewPheromones() {
        Matrix tempPherDist = new Matrix(townsNumber);
        Matrix tempElitePherDistr = new Matrix(townsNumber);
        for (int i = 0; i < antsNumber; i++) {
            for (int j = 0; j < townsNumber; j++) {
                //tautemp(T(k,c),T(k,c+1))=Q/L(k,1);
                tempPherDist.setValue((int) antRoutes.getValue(i, j), (int) antRoutes.getValue(i, j),
                        parameterQ / routeLengths.getValue(0, i));
            }
        }

        int[] LminIndex = routeLengths.getMinValueIndexes();
        for (int i = 0; i < townsNumber; i++) {
            tempElitePherDistr.setValue((int) antRoutes.getValue(LminIndex[1], i), (int) antRoutes.getValue(LminIndex[1], i + 1),
                    parameterQ / bestLength);
        }

        pheromonesDistribution.multiplyMatrixElements(1 - pheromonesEvaporation, pheromonesDistribution);
        pheromonesDistribution.sumMatrix(pheromonesDistribution, tempPherDist);
        tempElitePherDistr.multiplyMatrixElements(eliteAntsNumber, tempElitePherDistr);
        pheromonesDistribution.sumMatrix(pheromonesDistribution, tempElitePherDistr);

    }

    @Override
    public int[] getOptimalPath() {
        antRoutes = getRandPermutation(antsNumber, townsNumber);
        antRoutes = resizeMatrix(antRoutes, antRoutes.getNRows(), antRoutes.getNCols() + 1);
        for (int i = 0; i < antRoutes.getNRows(); i++) {
            antRoutes.setValue(i, antRoutes.getNCols() - 1, antRoutes.getValue(i, 0));
        }
        countRouteLengths();
        parameterQ = routeLengths.getValue(0, 0);
        for (int i = 0; i < iterNumber; i++) {
            makeNewAntRoutes();
            countRouteLengths();
            countBestLength();
            getNewPheromones();
            getBestRoute();
        }
        return bestRoute;
    }

    private Matrix getRoutePheromonesDistribution(int currentTown, int[] nextTowns) {
        Matrix result = new Matrix(1, nextTowns.length);
        for (int i = 0; i < result.getNCols(); i++) {
            result.setValue(0, i, pheromonesDistribution.getValue(currentTown, nextTowns[i]));
        }
        return result;
    }

    private Matrix getRouteVisibility(int currentTown, int[] nextTowns) {
        Matrix result = new Matrix(1, nextTowns.length);
        for (int i = 0; i < result.getNCols(); i++)
            result.setValue(0, i, visibility.getValue(currentTown, nextTowns[i]));
        return result;
    }

    private void makeNewAntRoutes() {
        for (int i = 0; i < antsNumber; i++) {
            for (int j = 1; j < townsNumber - 1; j++) {
                int st = (int) antRoutes.getValue(i, j - 1);
                int[] nxt = new int[townsNumber - j];
                for (int k = 0; k < nxt.length; k++) {
                    nxt[k] = (int) antRoutes.getValue(i, k);
                }
                Matrix routePheromonesDistribution = getRoutePheromonesDistribution(st, nxt);
                Matrix routeVisibility = getRouteVisibility(st, nxt);

                Matrix prob1 = multiplyMatrixElements(powerMatrixElements(parameterAlpha, routePheromonesDistribution),
                        powerMatrixElements(parameterBeta, routeVisibility));

                double prob2 = sumMatrixElements(prob1);

                Matrix prob = divideMatrixElements(prob1, prob2);
                Random rand = new Random();
                double rcity = rand.nextDouble();
                int newcity = 0;
                for (int z = 0; z < prob.getNCols(); z++) {
                    if (rcity < sumMatrixElements(prob, 0, 0, 0, z)) {
                        newcity = j - 1 + z;
                        break;
                    }
                }
                int temp = (int) antRoutes.getValue(i, newcity);
                antRoutes.setValue(i, newcity, antRoutes.getValue(i, j));
                antRoutes.setValue(i, j, temp);
            }
        }
    }

}
