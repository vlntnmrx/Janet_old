/*
 * Copyright (c) 2018, tomat
 * Do not redistribute without permission.
 * Thx.
 */
package neuralnet;

/**
 * Ein Layer in einem Netzwerk
 *
 * @author Valentin
 */
public class layer {

    //Hat Neuronen
    //Hat einen Top und einen bottom Layer
    public neuron[] net;
    int id; //Layer Nummer, angefangen mit 0
    public int anzahl;

    /**
     * Construcor der layer Klasse
     *
     * @param anz
     * @param topLayer
     * @param id
     */
    public layer(int anz, layer topLayer, int id) {
        this.id = id;
        net = new neuron[anz];
        this.anzahl = anz;
        if (topLayer == null) {
            for (int i = 0; i < anz; i++) {
                net[i] = new neuron(i, 0, this.id);
            }
        } else {
            for (int i = 0; i < anz; i++) {
                net[i] = new neuron(i, topLayer.anzahl, this.id);
            }
        }
        if (net.length != anz) {
            System.err.println("Fehler: Array unvollständig!");
        }
    }

    /**
     * Bearbetet diesen Layer auf Basis des darüberliegenden
     *
     * @param top
     */
    void process(layer top) {
        int i;
        for (i = 0; i < this.net.length; i++) {
            net[i].doit(top);
        }
    }

    neuron getBiggest() {
        int i;
        neuron ret = null;
        double big = 0;
        for (i = 0; i < anzahl; i++) {
            if (net[i].value > big) {
                ret = net[i];
                big = net[i].value;
            }
        }
        return ret;
    }

    void learn(network netz, double[] exp) {
        //Lerne alle Neuronen des eigenen Layers ein
        for (int i = 0; i < this.net.length; i++) {
            this.net[i].learnRec(netz, exp);
        }
    }

    void outValues() {
        int i;
        for (i = 0; i < anzahl; i++) {
            System.out.println(net[i].value);
        }
    }

}
