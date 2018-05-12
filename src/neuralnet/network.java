/*
 * Copyright (c) 2018, tomat
 * Do not redistribute without permission.
 * Thx.
 */
package neuralnet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * I Bims, 1 Neuronales Netzwerk
 *
 * @author Valentin
 */
public class network {

    /*Hat Layer
    //Kann durchlaufen
    //Nimmt etwas entgegen und kann aus der letzten Schicht zurückgeben
     */
    layer[] lays;
    int anzLays;
    double lr = 2.0;//Learning Rate, mutliplikator für die Ableitung

    /**
     * Constructor der Netzwerk Klasse
     *
     * @param layers
     */
    public network(int... layers) {
        lays = new layer[layers.length];
        lays[0] = new layer(layers[0], null, 0);
        for (int i = 1; i < layers.length; i++) {
            lays[i] = new layer(layers[i], lays[i - 1], i);
        }
        this.anzLays = lays.length;
    }

    /**
     * Etwas durch das Netzwerk jagen
     */
    void passTh() {
        //lays[0].net = start.net;
        int i;
        for (i = 1; i < this.anzLays; i++) {
            lays[i].process(this.lays[i - 1]);
        }
        //return this.lays[i];
    }

    /**
     * Gebe die Layer aus
     */
    void outLayers() {
        int i;
        for (i = 1; i < this.anzLays; i++) {
            System.out.println("Layer " + i + ":");
            lays[i].outValues();
        }
    }

    /**
     * Lerne was neues
     *
     * @param lay0
     * @param exp
     */
    void learn(double[] exp) {
        //Normal den Layer0 vorbereiten
        passTh();
        int i;
        /*
        double fehler = 0;
        for (i = 0; i < this.lays[this.lays.length - 1].net.length; i++) {
            fehler += (exp[i] - lays[this.lays.length - 1].net[i].value) * (exp[i] - lays[this.lays.length - 1].net[i].value);
        }
        System.out.println("Fehler:" + fehler);// */
        for (i = 1; i < this.lays.length; i++) {
            this.lays[i].learn(this, exp);
        }
    }

    /**
     * Jagt @cycles bilder durch das Netzwerk zum lernen. Macht lernen viel
     * einfacher
     *
     * @param imgs
     * @param cycles
     * @throws IOException
     */
    void homeTrainer(imgLoader imgs, int cycles) throws IOException {
        double[] exp = new double[10];
        int lab;
        int right = 0, wrong = 0;
        double fehler;
        for (int i = 0; i < cycles; i++) {
            lab = imgs.readLabel();
            Arrays.fill(exp, 0);
            exp[lab] = 1.0;
            this.prep(imgs);
            this.learn(exp);
            fehler = 0;
            for (int j = 0; j < this.lays[this.lays.length - 1].net.length; j++) {
                fehler += Math.pow(exp[j] - this.lays[this.lays.length - 1].net[j].value, 2);
            }
            if (lab == (this.lays[this.lays.length - 1].getBiggest()).id) {
                System.out.println(i + " - -----CORRECT-----\t\t F:" + fehler);
                right++;
            } else {
                wrong++;
                System.out.println(i + " - " + (double) right / (double) wrong + " \t F:" + fehler);
            }
        }
        System.out.println("Verhältnis:" + (double) right / (double) wrong);

    }

    /**
     * Bereite den ersten Layer mit einem Bild vor
     *
     * @param img
     * @throws IOException
     */
    void prep(imgLoader img) throws IOException {
        int i;
        for (i = 0; i < 784; i++) {
            lays[0].net[i].value = ((double) img.readPixel() / 255);
        }
    }

    void restoreFile(File dat) throws FileNotFoundException, IOException {
        DataInputStream inp = new DataInputStream(new FileInputStream(dat));
        System.out.println("Read Config from File...");
        System.out.println("Magic Number: " + inp.readInt());
        double inpd;
        int i = 0;
        boolean fail = false;
        int inpi = inp.readInt();
        while (inpi != 0) {
            System.out.print("File Layer " + i + ": " + inpi);
            if (inpi == this.lays[i].net.length) {
                System.out.println(" - OK");
            } else {
                System.err.println(" - FAIL");
                fail = true;
            }
            inpi = inp.readInt();
            i++;
        }
        if (!fail) {
            //Es werde, für alle...
            //...für alle Layer...
            for (i = 0; i < this.lays.length; i++) {
                //...für alle Neuronen...
                for (int j = 0; j < this.lays[i].net.length; j++) {
                    //...für alle Weights...
                    for (int k = 0; k < this.lays[i].net[j].weights.length; k++) {
                        //...lese sie aus der Datei!
                        this.lays[i].net[j].weights[k] = inp.readDouble();
                    }
                    //Und häng noch den Bias hinten dran
                    this.lays[i].net[j].bias = inp.readDouble();
                }
            }
        } else {
            System.err.println("Layer aus Datei nicht gleich Netzwerk");
        }

        //Stream wieder schliessen
        inp.close();
    }

    void test(imgLoader testDaten, int cycles) throws IOException {
        double[] exp = new double[10];
        int lab;
        int right = 0, wrong = 0;
        double fehler;
        for (int i = 0; i < cycles; i++) {
            lab = testDaten.readLabel();
            Arrays.fill(exp, 0);
            exp[lab] = 1.0;
            this.prep(testDaten);
            this.passTh();
            fehler = 0;
            for (int j = 0; j < this.lays[this.lays.length - 1].net.length; j++) {
                fehler += Math.pow(exp[j] - this.lays[this.lays.length - 1].net[j].value, 2);
            }
            if (lab == (this.lays[this.lays.length - 1].getBiggest()).id) {
                System.out.println(i + " - -----CORRECT-----\t\t F:" + fehler);
                right++;
            } else {
                wrong++;
                System.out.println(i + " - " + (double) right / (double) wrong + " \t F:" + fehler);
            }
        }
        System.out.println("Treffsicherheit: " + ((double)right / (double)(right + wrong)) * 100.0 + "%");
    }

    void dumbFile(String name) throws IOException {
        File dumb = new File(name + ".dmp");
        dumb.createNewFile();
        FileOutputStream dumbstr = new FileOutputStream(dumb);
        DataOutputStream dumbdat = new DataOutputStream(dumbstr);
        dumbdat.writeInt(172); //Magic Number

        //Speichere alle Layergrößen als erstes
        for (int i = 0; i < this.lays.length; i++) {
            dumbdat.writeInt(this.lays[i].net.length);
        }
        dumbdat.writeInt(0);

        //Es werde, für alle...
        //...für alle Layer...
        for (int i = 0; i < this.lays.length; i++) {
            //...für alle Neuronen...
            for (int j = 0; j < this.lays[i].net.length; j++) {
                //...für alle Weights...
                for (int k = 0; k < this.lays[i].net[j].weights.length; k++) {
                    //...speichere sie in der Datei!
                    dumbdat.writeDouble(this.lays[i].net[j].weights[k]);
                    //Ende des Gedichts :)
                }
                //Und häng noch den Bias hinten dran
                dumbdat.writeDouble(this.lays[i].net[j].bias);
            }
        }

        dumbdat.close();
        dumbstr.close();

    }
}
