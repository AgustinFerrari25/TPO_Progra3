package SolucionMisionUada;

import MisionUada.Decision;
import MisionUada.Desplazamiento;
import MisionUada.EncontrarRecorridoUada;
import MisionUada.Estacion;
import MisionUada.Movimiento;

import java.util.ArrayList;

public class EncontrarRecorridoUadaImp implements EncontrarRecorridoUada {



    /**
    * Funcion ya viene creada
    * */
@Override
    public ArrayList<Decision> encontrarSecuenciaRecorridoUada(
            int bateriaInicial,
            Estacion origen,
            ArrayList<Estacion> lugaresDisponibles,
            ArrayList<Estacion> lugaresObligatorios,
            ArrayList<Desplazamiento> desplazamientos) {

        ArrayList<Decision> secuenciaDecisiones = new ArrayList<>();

        System.out.println("= INICIO DE MISION UADA =");
        System.out.println("Batería inicial: " + bateriaInicial + "%");
        System.out.println("Origen: " + origen.getNombre());
        System.out.println("------------------------------");

        // 🔹 Recorremos todos los desplazamientos que salen del origen
        for (Desplazamiento d : desplazamientos) {
            if (d.getOrigen().equals(origen)) {
                System.out.println("Desde " + d.getOrigen().getNombre() +
                                   " hacia " + d.getDestino().getNombre());

                // 🔹 Para cada movimiento permitido en ese desplazamiento
                for (Movimiento m : d.getMovimientosPermitidos()) {
                    double[] tiempoYGasto = CalcularGastoYTiempo(d, m);
                    double tiempo = tiempoYGasto[0];
                    double gasto = tiempoYGasto[1];
                    double nuevaBateria = bateriaInicial - gasto;

                    System.out.println("   Movimiento: " + m +
                                       " | Tiempo: " + tiempo +
                                       " | Gasto: " + gasto +
                                       " | Batería restante: " + nuevaBateria);

                    // 🔹 Crear una "Decision" de ejemplo (con enteros como pide la librería)
                    Decision decision = new Decision(
                            d.getOrigen(),
                            d.getDestino(),
                            m,
                            (int) Math.round(nuevaBateria),   // conversión a int
                            (int) Math.round(tiempo)          // conversión a int
                    );

                    secuenciaDecisiones.add(decision);

                        secuenciaDecisiones.add(decision);
                }

                System.out.println("------------------------------");
            }
        }

        System.out.println("=== FIN DE MISION UADA ===");
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
        double gasto = 0.0;

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
