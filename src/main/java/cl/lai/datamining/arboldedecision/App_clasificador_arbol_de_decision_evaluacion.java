package cl.lai.datamining.arboldedecision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.smartcardio.ATR;

import weka.associations.Apriori;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.LWL;
import weka.classifiers.trees.J48;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;


public class App_clasificador_arbol_de_decision_evaluacion 
{
	public static String file = "/Users/chunhaulai/Desktop/datamining-weka/src/resources/arriendo_dpto_categoria_numerica_5atributos_516registros.csv";
	public static void main( String[] args ) throws Exception{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
    	//Lectura de archivo para obtener los valores nominales
    	 
    	Map<Integer,String> correspondencia = new HashMap<Integer,String>();
    	
    	String aux = reader.readLine();
    	int totalRegistros = 516;
    	
    	String arrays[] = aux.split(";");
    	
    	Instances dptos = null;
    	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    	//omitir el primer, segundo atributo y el último atributo, y considerar los 4 atributos restantes: 
    	for(int i=2; i<arrays.length-1;i++){
    		attributes.add(new Attribute(arrays[i]));
    	}
    	//considerar el ultimo atributo como target 
    	ArrayList<String> clasesPreviamenteDefinida = new ArrayList<String>(3); 
    	clasesPreviamenteDefinida.add("BAJO"); 
    	clasesPreviamenteDefinida.add("INTERMEDIO"); 
    	clasesPreviamenteDefinida.add("ALTO");
    	Attribute classAttribute = new Attribute("SECTOR",clasesPreviamenteDefinida);
    	attributes.add(classAttribute);
    	
    	Instances isTrainingSet = new Instances("traning", attributes, totalRegistros);
    	isTrainingSet.setClassIndex(classAttribute.index());
    	
    	Instances isTestSet = new Instances("test", attributes, 117);
    	isTestSet.setClassIndex(classAttribute.index());
    	
    	int filas = 1;
    	//Lectura de cada instancia
      	while((aux=reader.readLine())!=null){
     		arrays = aux.split(";");
     		correspondencia.put(filas,arrays[0]);
     		
     		DenseInstance inst = new DenseInstance(5);
     		for(int at=0,   i=2; i<arrays.length-1;i++,at++){
     			double valor = Double.parseDouble(arrays[i]);
     			inst.setValue(attributes.get(at), valor);
        	}
     		inst.setValue(classAttribute, arrays[arrays.length-1]);
     		 
     		if(filas<=350){
     			isTrainingSet.add(inst);
     		}else 
     			isTestSet.add(inst);
     		filas++;
    		 
    	} 
      	
      	J48 tree = new J48();
        String[] options = new String[1];
        options[0] = "-U"; 
        tree.setOptions(options);
        tree.buildClassifier(isTrainingSet);
		 
       
        
        
        Evaluation evalTraning = new Evaluation(isTrainingSet);
        evalTraning.crossValidateModel(tree, isTrainingSet, 10, new Random(1));
		
        System.out.println(evalTraning.toMatrixString());
        
        System.out.println("===Evaluación Modelo Arbol de Decisión Datos Entrenamiento==="); 
	    System.out.println(evalTraning.toSummaryString());
	    for (int i = 0; i < clasesPreviamenteDefinida.size(); ++i) {
	    	System.out.printf("Results for class %d (%s):\n", i, clasesPreviamenteDefinida.get(i));
	    	System.out.printf("  True positives : %8.0f\n", evalTraning.numTruePositives(i));
	    	System.out.printf("  False positives: %8.0f\n", evalTraning.numFalsePositives(i));
	    	System.out.printf("  True negatives : %8.0f\n", evalTraning.numTrueNegatives(i));
	    	System.out.printf("  False negatives: %8.0f\n", evalTraning.numFalseNegatives(i));
	    	System.out.printf("  Recall:    %6.4f\n", evalTraning.recall(i));
	    	System.out.printf("  Precision: %6.4f\n", evalTraning.precision(i));
	    	System.out.printf("  F-Measure: %6.4f\n", evalTraning.fMeasure(i));
	    	System.out.println();
       	}
	    
		Evaluation evalTest = new Evaluation(isTestSet);
		evalTest.evaluateModel(tree, isTestSet);
		
		 
	    System.out.println("===Evaluación Modelo Arbol de Decisión Datos Test==="); 
	    System.out.println(evalTest.toSummaryString());
		
	    for (int i = 0; i < clasesPreviamenteDefinida.size(); ++i) {
	    	System.out.printf("Results for class %d (%s):\n", i, clasesPreviamenteDefinida.get(i));
	    	System.out.printf("  True positives : %8.0f\n", evalTest.numTruePositives(i));
	    	System.out.printf("  False positives: %8.0f\n", evalTest.numFalsePositives(i));
	    	System.out.printf("  True negatives : %8.0f\n", evalTest.numTrueNegatives(i));
	    	System.out.printf("  False negatives: %8.0f\n", evalTest.numFalseNegatives(i));
	    	System.out.printf("  Recall:    %6.4f\n", evalTest.recall(i));
	    	System.out.printf("  Precision: %6.4f\n", evalTest.precision(i));
	    	System.out.printf("  F-Measure: %6.4f\n", evalTest.fMeasure(i));
	    	System.out.println();
       	}
	    
	    
    }
}
