/*
 * Copyright (c) 2018, tomat
 * Do not redistribute without permission.
 * Thx.
 */
package neuralnet;

//import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Valentin
 */
public class NeuralNet {

    public static final int CYCLES = 5;

    /**
     * main class
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        network netz = new network(28 * 28, 16, 16, 10);
        imgLoader daten = new imgLoader("MNIST\\train-images.idx3-ubyte", "MNIST\\train-labels.idx1-ubyte");
        daten.outImgHeader();
        daten.outLabelHeader();
        System.out.println("-----------------------");
        System.out.println("-----------------------");
        double[] exp = new double[10];
        int lab;
        int right = 0, wrong = 0;
        double fehler;
        for (int i = 0; i < CYCLES; i++) {
            lab = daten.readLabel();
            Arrays.fill(exp, 0);
            exp[lab] = 1.0;
            netz.prep(daten);
            netz.learn(exp);
            fehler = 0;
            for (int j = 0; j < netz.lays[3].net.length; j++) {
                fehler += Math.pow(exp[j] - netz.lays[3].net[j].value, 2);
            }
            //System.out.println(i + ": Erwartet: " + lab + " Ergebnis: " + (netz.lays[3].getBiggest()).id + " mit Wert " + (netz.lays[3].getBiggest()).value);
            if (lab == (netz.lays[3].getBiggest()).id) {
                System.out.println(i + " - -----CORRECT-----\t\t F:" + fehler);
                right++;
            } else {
                wrong++;
                System.out.println(i + " - " + (double) right / (double) wrong + " \t F:" + fehler);
            }
        }
        netz.dumbFile("dumb1");
        netz.restoreFile(new File("dumb1.dmp"));
        //netz.outLayers();
        System.out.println("Verhältnis:" + (double) right / (double) wrong);
        System.out.println("---------ENDE!---------");
    }

}
