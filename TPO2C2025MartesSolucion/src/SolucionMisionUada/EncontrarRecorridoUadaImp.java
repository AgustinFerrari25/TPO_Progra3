package SolucionMisionUada;

import MisionUada.Decision;
import MisionUada.Desplazamiento;
import MisionUada.EncontrarRecorridoUada;
import MisionUada.Estacion;

import java.util.ArrayList;

public class EncontrarRecorridoUadaImp implements EncontrarRecorridoUada {



    /**
    * Funcion ya viene creada
    * */
    @Override
    public ArrayList<Decision> encontrarSecuenciaRecorridoUada(int bateriaInicial, Estacion origen, ArrayList<Estacion> lugaresDisponibles, ArrayList<Estacion> lugaresObligatorios, ArrayList<Desplazamiento> desplazamientos) {
        ArrayList<Decision> secuenciaDecisiones = new ArrayList<Decision>();

        return secuenciaDecisiones;
    }


    /**
    * Funciones auxiliaress que cree
    * */

    private int SumaDeDigitos(int n) {
        int suma = 0;
        int num = n;

        if (num == 0) {
            return 0;
        }

        while (num > 0) {
            int restoDelNumero = num / 10;

            int ultimoDigito = num - (10 * restoDelNumero);

            suma = suma + ultimoDigito;
            num = restoDelNumero;
        }
        return suma;
    }


    private double RecargarSiEsAula(Estacion estacion) {
        if (estacion.getEsAula()) {
            int sumaDigitos = SumaDeDigitos(estacion.getIdentificadorNumerico());

            double recarga = (double) sumaDigitos / 5.0;
            return recarga;
        } else {
            return 0.0;
        }
    }


    private double[] CalcularGastoYTiempo(Desplazamiento desplazamiento, Movimiento movimiento) {
        double tiempo = (double) desplazamiento.getTiempoBase();
        double gasto;

        switch (movimiento) {
            case CAMINAR:
                gasto = tiempo / 60.0;
                break;

            case SALTAR:
                tiempo = tiempo / 2.0;
                gasto = (tiempo / 60.0) * 2.2;
                break;

            case PATAS_ARRIBA:
                tiempo = tiempo * 2.0;
                gasto = (tiempo / 60.0) * (1.0 - 0.55);
                break;
        }

        return new double[]{tiempo, gasto};
    }

}
