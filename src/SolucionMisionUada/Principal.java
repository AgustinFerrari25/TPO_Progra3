package SolucionMisionUada;

import MisionUada.Decision;
import MisionUada.Desplazamiento;
import MisionUada.Estacion;
import MisionUada.Movimiento;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


public class Principal {


    private static class MapaDePrueba {
        Estacion origen;
        ArrayList<Estacion> disponibles = new ArrayList<>();
        ArrayList<Estacion> obligatorios = new ArrayList<>();
        ArrayList<Desplazamiento> desplazamientos = new ArrayList<>();
        int bateriaInicial;
    }

    public static void main(String[] args) {

        // 1. Cargar los datos desde los archivos
        // AHORA ESTOS ARCHIVOS DEBEN EXISTIR EN LA RAÍZ DE TU PROYECTO
        MapaDePrueba mapa = cargarMapaDesdeArchivos(
                "estaciones.csv",
                "desplazamientos.csv",
                "config_prueba_1.txt"
        );

        if (mapa == null) {
            System.err.println("Error al cargar el mapa. Saliendo.");
            return;
        }

        System.out.println("=== INICIANDO EJECUCIÓN CON MAPA DE ARCHIVO ===");
        System.out.println("-> Origen: " + mapa.origen.getNombre());
        System.out.println("-> Obligatorios: " + mapa.obligatorios.size() + " lugares.");
        System.out.println("-> Desplazamientos cargados: " + mapa.desplazamientos.size());
        System.out.println("-> Batería inicial: " + mapa.bateriaInicial);

        System.out.println("-> Obligatorios (" + mapa.obligatorios.size() + " lugares):");
        if (mapa.obligatorios.isEmpty()) {
            System.out.println("   (Ninguno)");
        } else {
            for (Estacion est : mapa.obligatorios) {
                System.out.println("   - " + est.getNombre());
            }
        }

        long startTime = System.nanoTime();


        EncontrarRecorridoUadaImp recorridoUada = new EncontrarRecorridoUadaImp();
        ArrayList<Decision> secuenciaDecisiones = recorridoUada.encontrarSecuenciaRecorridoUada(
                mapa.bateriaInicial,
                mapa.origen,
                mapa.disponibles,
                mapa.obligatorios,
                mapa.desplazamientos
        );


        long endTime = System.nanoTime();
        double tiempoDeEjecucionMs = (endTime - startTime) / 1_000_000.0;

        System.out.println("=== EJECUCIÓN FINALIZADA ===");
        System.out.println("Tiempo de ejecución del algoritmo: " + tiempoDeEjecucionMs + " ms");


        // 3. Imprimir Resultados
        imprimirSecuenciaDecisiones(secuenciaDecisiones, "output_prueba_1.txt");
    }

    /**
     * Este es el "parser" que lee los archivos y crea los objetos.
     */
    private static MapaDePrueba cargarMapaDesdeArchivos(String fEstaciones, String fDesplaz, String fConfig) {

        MapaDePrueba mapa = new MapaDePrueba();

        Map<String, Estacion> estacionesMap = new HashMap<>();

        try {

            System.out.println("Leyendo " + fEstaciones + "...");
            try (BufferedReader br = new BufferedReader(new FileReader(fEstaciones))) {
                String line;
                br.readLine(); // Saltear la cabecera (idUnico,nombre,esAula,idNumerica)

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    if (values.length >= 4) {
                        String id = values[0].trim();
                        String nombre = values[1].trim();
                        boolean esAula = Boolean.parseBoolean(values[2].trim());
                        int idNum = Integer.parseInt(values[3].trim());

                        Estacion estacion = new Estacion(nombre, idNum, esAula);

                        estacionesMap.put(id, estacion);
                        mapa.disponibles.add(estacion);
                    }
                }
            }

            // --- 2. Leer config_prueba_1.txt ---
            System.out.println("Leyendo " + fConfig + "...");
            try (BufferedReader br = new BufferedReader(new FileReader(fConfig))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        switch (key) {
                            case "bateria":
                                mapa.bateriaInicial = Integer.parseInt(value);
                                break;
                            case "origen":
                                mapa.origen = estacionesMap.get(value);
                                if (mapa.origen == null) {
                                    System.err.println("ID de origen no encontrado: " + value);
                                    return null;
                                }
                                break;
                            case "obligatorios":
                                String[] idsObligatorios = value.split(",");
                                for (String idObl : idsObligatorios) {
                                    Estacion est = estacionesMap.get(idObl.trim());
                                    if (est != null) {
                                        mapa.obligatorios.add(est);
                                    } else {
                                        System.err.println("ID obligatorio no encontrado: " + idObl);
                                    }
                                }
                                break;
                        }
                    }
                }
            }

            // --- 3. Leer desplazamientos.csv ---
            System.out.println("Leyendo " + fDesplaz + "...");
            try (BufferedReader br = new BufferedReader(new FileReader(fDesplaz))) {
                String line;
                br.readLine(); // Saltear la cabecera

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    if (values.length >= 3) {
                        Estacion origen = estacionesMap.get(values[0].trim());
                        Estacion destino = estacionesMap.get(values[1].trim());
                        int tiempoBase = Integer.parseInt(values[2].trim());

                        ArrayList<Movimiento> movimientos = new ArrayList<>();
                        // Leer todos los movimientos (desde la columna 3 en adelante)
                        for (int i = 3; i < values.length; i++) {
                            String movStr = values[i].trim().toUpperCase();
                            if (!movStr.isEmpty()) {
                                try {
                                    movimientos.add(Movimiento.valueOf(movStr));
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Movimiento desconocido: " + movStr);
                                }
                            }
                        }

                        if (origen != null && destino != null && !movimientos.isEmpty()) {
                            Desplazamiento d = new Desplazamiento(origen, destino, movimientos, tiempoBase);
                            mapa.desplazamientos.add(d);
                        }
                    }
                }
            }

            System.out.println("¡Mapa cargado exitosamente!");
            return mapa;

        } catch (FileNotFoundException e) {
            System.err.println("Error: Archivo no encontrado. Asegúrate que los archivos .csv y .txt están en la raíz del proyecto.");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.err.println("Error de E/S al leer los archivos.");
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Error: Formato de número incorrecto en un archivo CSV (tiempoBase o idNumerica).");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Escribe la secuencia de decisiones en un archivo de salida.
     * (¡Actualicé esta función para que reciba el nombre del archivo!)
     */
    public static void imprimirSecuenciaDecisiones(ArrayList<Decision> secuenciaDecisiones, String archivoSalida) {
        StringBuilder sb = new StringBuilder();

        if (secuenciaDecisiones.isEmpty()) {
            sb.append("== NO SE ENCONTRÓ UNA SOLUCIÓN VÁLIDA ==");
        } else {
            sb.append("== MEJOR SECUENCIA DE DECISIONES ENCONTRADA ==\n\n");
        }

        for (int i=0; i < secuenciaDecisiones.size();i++) {
            Decision decision = secuenciaDecisiones.get(i);
            int indice = i+1;
            String decisionString = "Decision numero "+indice+"\n"+
                    "Origen: "+decision.getOrigen().getNombre()+"\n"+
                    "Destino: "+decision.getDestino().getNombre()+"\n"+
                    "Movimiento empleado: "+decision.getMovimientoEmpleado().toString()+"\n"+
                    "Bateria Remanente: "+decision.getBateriaRemanente()+"\n"+
                    "Tiempo Acumulado: "+decision.getTiempoAcumulado()+" segundos \n\n";
            sb.append(decisionString);
        }

        String rutaArchivo = Paths.get(System.getProperty("user.dir"), archivoSalida).toString();

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(sb.toString());
            System.out.println("\nResultados guardados en: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo TXT: " + e.getMessage());
        }
    }
}