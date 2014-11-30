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
     Input variables for algorithm
     */
    protected int antsNumber;
    protected long iterNumber;
    protected long eliteAntsNumber;
    protected double parameterAlpha;
    protected double parameterBeta;
    protected double pheromonesEvaporation;
    protected Matrix distancesArray;
    protected Matrix pheromonesDistribution;

    /*
      Variables, that can be calculated before optimization using input variables
     */
    protected double parameterQ;
    protected int townsNumber;
    protected Matrix visibility;


    protected Matrix antRoutes;
    protected Matrix routeLengths;
    protected int[] bestRoute;
    protected double bestLength;

    public AntColonyOptimization() {

    }

    /**
     * Constructor
     * @param antsNumber - number of ants
     * @param iterNumber - number of iteration
     * @param distancesArray - matrix with distances
     */
    public AntColonyOptimization(int antsNumber, long iterNumber, double[][] distancesArray,
                                 double parameterAlpha, double parameterBeta,
                                 double pheromonesEvaporation, long eliteAntsNumber,
                                 double initialPheromonesDistribution) {
        this.antsNumber = antsNumber;
        this.iterNumber = iterNumber;
        this.distancesArray = new Matrix(distancesArray);
        this.townsNumber = distancesArray[0].length;
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
        this.visibility = inverseMatrixElements(this.distancesArray);
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
                        distancesArray.getValue((int) antRoutes.getValue(i, j),
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
        //Get new simple ants pheromones
        for (int i = 0; i < antsNumber; i++)
            for (int j = 0; j < townsNumber; j++)
                tempPherDist.setValue((int) antRoutes.getValue(i, j), (int) antRoutes.getValue(i, j),
                        parameterQ / routeLengths.getValue(0, i));

        //Get new elite ants pheromones
        int[] minLengthIndexes = routeLengths.getMinValueIndexes();
        for (int i = 0; i < townsNumber; i++)
            tempElitePherDistr.setValue((int) antRoutes.getValue(minLengthIndexes[1], i),
                    (int) antRoutes.getValue(minLengthIndexes[1], i + 1),
                    parameterQ / bestLength);
        //Update pheromones
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

    /**
     * Calculate new ant routes and write them into antRoutes
     */
    private void makeNewAntRoutes() {
        for (int i = 0; i < antsNumber; i++) {
            for (int j = 1; j < townsNumber - 1; j++) {
                int currentTown = (int) antRoutes.getValue(i, j - 1);
                int[] nextTowns = new int[townsNumber - j];
                //Get ant next towns
                for (int k = 0; k < nextTowns.length; k++)
                    nextTowns[k] = (int) antRoutes.getValue(i, k);

                Matrix routePheromonesDistribution = getRoutePheromonesDistribution(currentTown, nextTowns);
                Matrix routeVisibility = getRouteVisibility(currentTown, nextTowns);

                Matrix prob1 = multiplyMatrixElements(powerMatrixElements(parameterAlpha, routePheromonesDistribution),
                        powerMatrixElements(parameterBeta, routeVisibility));

                double prob2 = sumMatrixElements(prob1);

                Matrix probability = divideMatrixElements(prob1, prob2);
                double happenedProbability = (new Random()).nextDouble();
                int newTown = 0;
                for (int z = 0; z < probability.getNCols(); z++)
                    if (happenedProbability < sumMatrixElements(probability, 0, 0, 0, z)) {
                        newTown = j - 1 + z;
                        break;
                    }
                int temp = (int) antRoutes.getValue(i, newTown);
                antRoutes.setValue(i, newTown, antRoutes.getValue(i, j));
                antRoutes.setValue(i, j, temp);
            }
        }
    }

}
