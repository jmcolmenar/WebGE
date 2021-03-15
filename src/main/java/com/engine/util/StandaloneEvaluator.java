package com.engine.util;

import com.gramevapp.web.controller.ExperimentController;
import com.gramevapp.web.model.Run;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StandaloneEvaluator {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("\nInvalid number of arguments. Usage:\n\n\tjava -jar standaloneEvaluator.jar <downloaded results file> <dataset file>");
            System.exit(0);
        }

        String modelsFile = args[0];
        String dataSetFile = args[1];

        String[] splitContent = loadDataset(dataSetFile);

        ArrayList<ArrayList<String>> results = new ArrayList<>();

        results.add(targetVariable(splitContent));

        String line;
        String model;
        Run run = new Run();

        try {
            BufferedReader datReader = new BufferedReader(new FileReader(new File(modelsFile)));
            while ((line = datReader.readLine()) != null) {
                ArrayList<String> prediction = new ArrayList<>();

                String[] parts = line.trim().split(";");
                model = parts[1];
                if (!model.equals("Model")) {
                    prediction.add(model);

                    run.setModel(model);
                    List<Double> targetValues = new ArrayList<>();
                    List<Double> predictionValues = new ArrayList<>();
                    List<Double> stats = new ArrayList<>();
                    ExperimentController.processExperimentDataTypeInfo(splitContent, targetValues, predictionValues, stats, run);
                    // Stats are available in stats.

                    for (Double d : predictionValues) {
                        prediction.add(Double.toString(d));
                    }

                    results.add(prediction);

                }

            }
            datReader.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }

        // Print result
        for (int i=0; i<results.get(0).size(); i++) {
            for (int j=0; j<results.size(); j++) {
                System.out.print(results.get(j).get(i)+";");
            }
            System.out.println();
        }

    }

    private static ArrayList<String> targetVariable(String[] splitContent) {
        ArrayList<String> target = new ArrayList<>();
        target.add("Target");

        for (String s : splitContent) {
            if (!s.startsWith("#")) {
                target.add(s.split(";")[0]);
            }
        }

        return target;
    }

    private static String[] loadDataset(String dataSetFile) {

        ArrayList<String> data = new ArrayList<>();
        String line;
        try {
            BufferedReader datReader = new BufferedReader(new FileReader(new File(dataSetFile)));
            while ((line = datReader.readLine()) != null) {
                data.add(line);
            }
            datReader.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }

        String[] result = new String[data.size()];
        data.toArray(result);

        return result;
    }

}
