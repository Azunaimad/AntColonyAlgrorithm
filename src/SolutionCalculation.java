import OptimizationTSP.AntColonyOptimization;
import OptimizationTSP.OptimizationMethodContext;

import java.io.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import matrix.Matrix;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Class for calculating optimal paths using some input information
 * with Ant Colony Optimization algorithm
 */
public class SolutionCalculation extends AntColonyOptimization {
    private static String xmlPathsFiles = "data_paths.xml";
    private static double constBestLength = 999999.0;

    public int[] actualIndexes;
    public String[] filePaths;//Input and output files paths

    public SolutionCalculation(){
        filePaths = new String[3];
        filePaths = readInputAndOutputFilesPath();
        double[][] matrix = readInputFile(filePaths[1]);
        double[][] parameters = readInputFile(filePaths[0]);

        this.actualIndexes = new int[matrix[0].length];
        for(int i=0; i<matrix[0].length;i++)
            this.actualIndexes[i] = (int) matrix[0][i];

        double[][] distancesArray = new double[matrix.length-1][matrix[1].length];
        for(int i=0; i<matrix.length-1;i++)
            System.arraycopy(matrix[i + 1], 0, distancesArray[i], 0, matrix[1].length);

        this.distancesArray = new Matrix(distancesArray);
        this.townsNumber = distancesArray[0].length;
        this.antsNumber = (int)parameters[0][0];
        this.iterNumber = (long)parameters[0][1];
        this.parameterAlpha = parameters[0][2];
        this.parameterBeta = parameters[0][3];
        this.pheromonesEvaporation = parameters[0][4];
        this.eliteAntsNumber = (long)parameters[0][5];
        this.visibility = inverseMatrixElements(this.distancesArray);
        this.routeLengths = new Matrix(1, antsNumber);
        this.bestLength = constBestLength;
        this.bestRoute = new int[townsNumber + 1];

        double initialPheromonesDistribution = parameters[0][6];
        this.pheromonesDistribution = new Matrix(townsNumber, townsNumber);
        for (int i = 0; i < townsNumber; i++) {
            this.pheromonesDistribution.setValue(i, i, 0.0);
            for (int j = 0; j < townsNumber; j++)
                if (i != j)
                    this.pheromonesDistribution.setValue(i, j, initialPheromonesDistribution);
        }
    }

    /**
     * Match path indexes from AntColonyOptimization with real indexes
     * @param indexes - indexes from AntColonyOptimization
     * @return real indexes in the same sequence
     */
    public int[] matchIndexes(int[] indexes){
        int[] result = new int[indexes.length];

        for(int i=0; i<actualIndexes.length;i++)
        {
            result[i]=actualIndexes[indexes[i]];
        }
        result[result.length-1] = result[0];
        return result;
    }

    /**
     * Function for get input and output files path from xmlPathsFiles
     * @return parameters, solution and transport_matrix files paths
     */
    public String[] readInputAndOutputFilesPath(){
        String[] inputFilesPath = new String[3];
        try{

            File xmlPathsFile = new File(xmlPathsFiles);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlPathsFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("data");
            Node nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                inputFilesPath[0] = eElement.getElementsByTagName("parameters").item(0).getTextContent();
                inputFilesPath[1] = eElement.getElementsByTagName("matrix").item(0).getTextContent();
                inputFilesPath[2] = eElement.getElementsByTagName("solution").item(0).getTextContent();
            }
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (SAXException e){
            e.printStackTrace();
        }
        catch (ParserConfigurationException e){
            e.printStackTrace();
        }
        return inputFilesPath;
    }

    /**
     * Read numeric data from input file
     * @param filePath - file path
     * @return 2D double array from file
     */
    public double[][] readInputFile(String filePath){
        double[][] res=null;
        try {
            FileReader csvFile = new FileReader(filePath);
            CSVReader csvReader = new CSVReader(csvFile);
            List content = csvReader.readAll();
            String[] row = null;
            Object obj = content.get(0);
            res = new double[content.size()][((String[])obj).length];
            for (int i = 0; i < content.size(); i++) {
                Object object = content.get(i);
                row = (String[]) object;
                for (int j = 0; j < row.length; j++)
                    res[i][j] = Double.parseDouble(row[j]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Write solution into file
     * @param sb - string with solution with "," separator
     */
    public void writeSolution(StringBuilder sb){
        try{
            CSVWriter csvWriter = new CSVWriter(new FileWriter(filePaths[2]),',');
            String[] out = sb.toString().split(",");
            csvWriter.writeNext(out);
            csvWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        OptimizationMethodContext omc = new OptimizationMethodContext();
        SolutionCalculation solutionCalculation = new SolutionCalculation();
        int[] solutionInActualIndexes = null;

        omc.setOptimizationMethod(solutionCalculation);
        solutionInActualIndexes = solutionCalculation.matchIndexes(omc.getOptimalPath());
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(omc.getBestLength()));
        for (int anActualInd : solutionInActualIndexes) {
            sb.append(",");
            sb.append(anActualInd);
        }
        solutionCalculation.writeSolution(sb);

    }
}
