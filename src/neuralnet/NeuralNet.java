/*
 * Copyright (c) 2018, tomat
 * Do not redistribute without permission.
 * Thx.
 */
package neuralnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Valentin
 */
public class NeuralNet {

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
        //imgLoader testDaten = new imgLoader("MNIST\\t10k-images.idx3-ubyte", "MNIST\\t10k-labels.idx1-ubyte");
        System.out.println("---------START!---------");

        //netz.restoreFile(new File("saves\\dumb_110518_10000.dmp"));
        netz.lr = 3;
        netz.homeTrainer(daten, 10000);
        //netz.test(testDaten, 500);
        netz.dumbFile("dumb_130518_10000");
        System.out.println("----------END!----------");
    }

}
