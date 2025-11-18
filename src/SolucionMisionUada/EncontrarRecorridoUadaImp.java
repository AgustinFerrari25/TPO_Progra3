package SolucionMisionUada;

import MisionUada.*;

import java.util.ArrayList;

public class EncontrarRecorridoUadaImp implements EncontrarRecorridoUada {

    private ArrayList<Decision> mejorSecuencia;
    private double tiempoMinimoLogrado;

    // Variables para no tener que pasarlas en cada llamada recursiva
    private ArrayList<Desplazamiento> todosLosDesplazamientos;
    private Estacion estacionFinal; // El Aula 633

    private long podaPorTiempo;
    private long podaPorBateria;
    private long podaPorRepetido;
    private long podaPorNoObligatorIOS;

    @Override
    public ArrayList<Decision> encontrarSecuenciaRecorridoUada(int bateriaInicial, Estacion origen, ArrayList<Estacion> lugaresDisponibles, ArrayList<Estacion> lugaresObligatorios, ArrayList<Desplazamiento> desplazamientos) {


        this.estacionFinal = origen;
        this.mejorSecuencia = new ArrayList<>();
        this.tiempoMinimoLogrado = Double.MAX_VALUE; // Equivale a "infinito"
        this.todosLosDesplazamientos = desplazamientos;
        this.podaPorTiempo = 0;
        this.podaPorBateria = 0;
        this.podaPorRepetido = 0;
        this.podaPorNoObligatorIOS = 0;
        ArrayList<Decision> recorridoActual = new ArrayList<>();
        ArrayList<Estacion> obligatoriosRestantes = new ArrayList<>(lugaresObligatorios);
        ArrayList<Estacion> visitados = new ArrayList<>();
        visitados.add(origen);


        misionUada(bateriaInicial, origen, // Estación actual
                recorridoActual, obligatoriosRestantes, visitados, 0.0 // Tiempo acumulado inicial
        );
        System.out.println("= REPORTE DE PODAS =");
        System.out.println("-> Caminos podados por Batería: " + this.podaPorBateria);
        System.out.println("-> Caminos podados por Tiempo (peor a la mejor): " + this.podaPorTiempo);
        System.out.println("-> Caminos podados por Lugar Repetido: " + this.podaPorRepetido);
        System.out.println("-> Caminos podados por Fin sin Obligatorios: " + this.podaPorNoObligatorIOS);
        System.out.println("======================");
        System.out.println("= FIN DE MISION UADA =");

        if (mejorSecuencia.isEmpty()) {
            System.out.println("No se encontró una solución válida.");
        } else {
            Decision ultimaDecision = mejorSecuencia.get(mejorSecuencia.size() - 1);
            System.out.println("Mejor tiempo encontrado: " + this.tiempoMinimoLogrado + "s");
            System.out.println("Batería restante final: " + ultimaDecision.getBateriaRemanente() + "%");
        }
        return this.mejorSecuencia;
    }


    private void misionUada(double bateriaActual, Estacion estacionActual, ArrayList<Decision> recorridoActual, ArrayList<Estacion> obligatoriosRestantes, ArrayList<Estacion> visitados, double tiempoAcumulado) {


        for (Desplazamiento d : this.todosLosDesplazamientos) {


            if (d.getOrigen().equals(estacionActual)) {


                for (Movimiento m : d.getMovimientosPermitidos()) {


                    double[] calculos = CalcularGastoYTiempo(d, m);
                    double tiempoMovimiento = calculos[0];
                    double gastoBateria = calculos[1];

                    double nuevoTiempo = tiempoAcumulado + tiempoMovimiento;
                    double nuevaBateria = bateriaActual - gastoBateria;


                    if (nuevaBateria > 0 && nuevoTiempo < this.tiempoMinimoLogrado) {
                        Estacion siguienteEstacion = d.getDestino();

                        double recarga = RecargarSiEsAula(siguienteEstacion);


                        if (nuevaBateria + recarga > 100) {
                            nuevaBateria = 100.0;
                        } else {
                            nuevaBateria += recarga;
                        }


                        if (siguienteEstacion.equals(this.estacionFinal)) {


                            if (obligatoriosRestantes.isEmpty()) {


                                if (nuevoTiempo < this.tiempoMinimoLogrado) {

                                    this.tiempoMinimoLogrado = nuevoTiempo;

                                    Decision ultimaDecision = new Decision(estacionActual, siguienteEstacion, m, (float) nuevaBateria, (float) nuevoTiempo);
                                    recorridoActual.add(ultimaDecision);
                                    this.mejorSecuencia = new ArrayList<>(recorridoActual);

                                    recorridoActual.remove(recorridoActual.size() - 1);
                                }
                            } else {
                                // --- PODA 4: LLEGÓ A LA META PERO FALTAN OBLIGATORIOS ---
                                this.podaPorNoObligatorIOS++;
                            }


                        } else if (!visitados.contains(siguienteEstacion)) {

                            Decision decision = new Decision(estacionActual, siguienteEstacion, m, (float) nuevaBateria, (float) nuevoTiempo);
                            recorridoActual.add(decision);
                            visitados.add(siguienteEstacion);

                            boolean fueEliminado = false;

                            if (obligatoriosRestantes.contains(siguienteEstacion)) {
                                obligatoriosRestantes.remove(siguienteEstacion);
                                fueEliminado = true;
                            }


                            misionUada(nuevaBateria, siguienteEstacion, recorridoActual, obligatoriosRestantes, visitados, nuevoTiempo);


                            recorridoActual.remove(recorridoActual.size() - 1);
                            visitados.remove(siguienteEstacion);


                            if (fueEliminado) {
                                obligatoriosRestantes.add(siguienteEstacion);
                            }
                        } else {
                            // --- PODA 3: LUGAR REPETIDO ---
                            // (El else del 'if (!visitados.contains(siguienteEstacion))')
                            this.podaPorRepetido++;
                        }

                    } else {
                        // --- PODA 1 Y 2: BATERÍA O TIEMPO ---
                        // (El else del 'if (nuevaBateria > 0 && nuevoTiempo < this.tiempoMinimoLogrado)')


                        if (nuevaBateria <= 0) {
                            this.podaPorBateria++;
                        } else if (nuevoTiempo >= this.tiempoMinimoLogrado) {
                            // Solo contamos la poda por tiempo si la batería estaba OK
                            this.podaPorTiempo++;
                        }
                    }

                }
            }
        }
    }


    /**
     * Funciones auxiliaress que cree
     */

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
        double tiempo = desplazamiento.getTiempoBase();
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
