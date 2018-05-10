/*
 * Copyright (c) 2018, tomat
 * Do not redistribute without permission.
 * Thx.
 */
package neuralnet;

import java.util.Random;

/**
 * Ein Neuron in einem Layer in einem Netzwerk
 *
 * @author Valentin
 */
public class neuron {

    //Hat verknüpfungen zu anderen Neuronen
    //Hat einen Wert gespeichert
    //Hat Gewichte für jede Verknüpfung
    public double value;
    double bias;
    int id; //Die eigene Nummer im Layer
    int layerId;
    double[] weights;
    public static final boolean DIFFBIAS = true;

    /**
     * Constructor für ein Neuron
     *
     * @param id
     * @param topLayer
     * @param layerId
     */
    public neuron(int id, int topLayer, int layerId) {
        Random rd = new Random();
        bias = 2 - rd.nextInt(4);
        this.id = id;
        this.layerId = layerId;
        weights = new double[topLayer];
        for (int i = 0; i < topLayer; i++) {
            weights[i] = ((double) rd.nextInt(100) / 100.0) - 0.5;
        }
    }

    /**
     * Propagate foreward
     *
     * @param top
     */
    void doit(layer top) {
        int i;
        double sum = 0.0;
        for (i = 0; i < top.anzahl; i++) {
            sum += top.net[i].value * this.weights[i];
        }
        this.value = sig(sum + bias);
    }

    /**
     * Lenre Rekursiv alle Weights ein
     *
     * @param netz
     * @param exp
     */
    void learnRec(network netz, double[] exp) {
        //System.out.println("Es lernt N:" + this.id + "aus L:" + this.layerId);
        //Für alle Weights, lerne...
        double delta;
        int layerAnz = netz.lays.length;
        //Sonderfall, falls dieses Neuron im Ausgabelayer liegt:
        if (this.layerId == layerAnz - 1) {
            for (int w = 0; w < this.weights.length; w++) {
                //Leite alle Elemte der Fehlerfunktion ab
                delta = netz.lr * (exp[id] - this.value) * sigdiff(this.value) * netz.lays[this.layerId - 1].net[w].value;
                this.weights[w] += delta;
            }
            //Bias anpassen:
            this.bias += netz.lr * (exp[id] - this.value) * sigdiff(this.value);
        } else {
            //Normalfall, dass dieses Neuron tiefer liegt
            for (int w = 0; w < this.weights.length; w++) {
                //Leite alle Elemte der Fehlerfunktion ab
                delta = 0;
                for (int f = 0; f < netz.lays[layerAnz - 1].net.length; f++) {
                    delta += (exp[f] - netz.lays[layerAnz - 1].net[f].value) * netz.lays[layerAnz - 1].net[f].derive(netz, this.layerId, this.id, w);
                }
                this.weights[w] += netz.lr * delta;
                //System.out.println("Für " + this.id + " in " + this.layerId + " weight " + w + " mit Delta: " + delta);
            }
            //Bias Ableiten
            //Leite alle Elemte der Fehlerfunktion ab
            delta = 0;
            for (int f = 0; f < netz.lays[layerAnz - 1].net.length; f++) {
                delta += (exp[f] - netz.lays[layerAnz - 1].net[f].value) * netz.lays[layerAnz - 1].net[f].derive(netz, this.layerId, this.id, 0, DIFFBIAS);
            }
            this.bias += netz.lr * delta;
        }
    }

    /**
     * Leite nach einem WEIGHT ab und setzte bias auf False
     *
     * @param netz
     * @param layer
     * @param id
     * @param weight
     * @return
     */
    double derive(network netz, int layer, int id, int weight) {
        return derive(netz, layer, id, weight, false);
    }

    /**
     * Leitet nach einem Weight ab, ist dabei Rekursiv
     *
     * @param netz
     * @param layer
     * @param id
     * @param weight
     * @param bias
     * @return
     */
    double derive(network netz, int layer, int id, int weight, boolean bias) {
        double delta = 0;
        //Wenn das gesuchte Gewicht NICHT in diesem oder dem Folgenden Layer liegt:
        if (layer != this.layerId && layer != this.layerId - 1) {
            //Für alle Weights leite ab danach
            delta = 0;
            for (int w = 0; w < this.weights.length; w++) {
                delta += this.weights[w] * netz.lays[this.layerId - 1].net[w].derive(netz, layer, id, weight, bias);
            }
            delta = delta * sigdiff(this.value);
        }
        //Wenn das gesuchte Gewicht IM FOLGENDEN Layer liegt
        if (layer == this.layerId - 1) {
            delta = sigdiff(this.value) * this.weights[id] * netz.lays[this.layerId - 1].net[id].derive(netz, layer, id, weight, bias);
        }
        //Wenn das gesuchte Gewicht in diesem Layer liegt
        if (layer == this.layerId) {
            delta = sigdiff(this.value);
            if (!bias) {
                delta = delta * netz.lays[this.layerId - 1].net[weight].value;
            }
        }
        return delta;
    }

    /**
     * Die Logistische Funktion
     *
     * @param eing
     * @return
     */
    double sig(double eing) {
        return (1.0 / (1.0 + Math.exp(0.0 - eing)));
        /*if (eing < 0) {
            return 0;
        }else{
            return eing;
        }*/
    }

    /**
     * Die Ableitung der Logistischen Funktion
     *
     * @param eing
     * @return
     */
    double sigdiff(double eing) {
        return sig(eing) * (1.0 - sig(eing));
    }
}
