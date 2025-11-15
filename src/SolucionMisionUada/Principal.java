package SolucionMisionUada;

import MisionUada.Decision;
import MisionUada.Desplazamiento;
import MisionUada.Estacion;
import MisionUada.Movimiento;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Principal {
    public static void main(String[] args) {

        // Ejemplo de datos de entrada

        int bateriaInicial = 100;

        ArrayList<Estacion> lugaresDisponibles = new ArrayList<Estacion>();
        ArrayList<Estacion> lugaresObligatorios = new ArrayList<Estacion>();

        // Estación origen y fin
        Estacion e = new Estacion("Aula 633", 633, true);

        Estacion e1 = new Estacion("Starbucks planta baja", 001, false);
        Estacion e2 = new Estacion("Molinete Lima 3", 003, false);
        Estacion e3 = new Estacion("Aula 613", 712, true);
        Estacion e4 = new Estacion("Aula 918", 918, true);
        Estacion e5 = new Estacion("Sala de profesores", 002, false);

        lugaresDisponibles.add(e1);
        lugaresDisponibles.add(e2);
        lugaresDisponibles.add(e3);
        lugaresDisponibles.add(e4);
        lugaresDisponibles.add(e5);

        lugaresObligatorios.add(e1);
        lugaresObligatorios.add(e2);
        lugaresObligatorios.add(e3);

        ArrayList<Desplazamiento> desplazamientos = new ArrayList<Desplazamiento>();

        ArrayList<Movimiento> movimientosD = new ArrayList<Movimiento>();
        movimientosD.add(Movimiento.CAMINAR);
        movimientosD.add(Movimiento.SALTAR);
        Desplazamiento d = new Desplazamiento(e,e1,movimientosD,15);

        ArrayList<Movimiento> movimientosD1 = new ArrayList<Movimiento>();
        movimientosD1.add(Movimiento.PATAS_ARRIBA);
        movimientosD1.add(Movimiento.SALTAR);
        Desplazamiento d1 = new Desplazamiento(e1,e2,movimientosD1,11);

        ArrayList<Movimiento> movimientosD2 = new ArrayList<Movimiento>();
        movimientosD2.add(Movimiento.CAMINAR);
        movimientosD2.add(Movimiento.SALTAR);
        movimientosD2.add(Movimiento.PATAS_ARRIBA);
        Desplazamiento d2 = new Desplazamiento(e2,e3,movimientosD1,12);

        ArrayList<Movimiento> movimientosD3 = new ArrayList<Movimiento>();
        movimientosD3.add(Movimiento.PATAS_ARRIBA);
        movimientosD3.add(Movimiento.CAMINAR);
        Desplazamiento d3 = new Desplazamiento(e3,e,movimientosD3,10);

        desplazamientos.add(d);
        desplazamientos.add(d1);
        desplazamientos.add(d2);
        desplazamientos.add(d3);

        EncontrarRecorridoUadaImp recorridoUada = new EncontrarRecorridoUadaImp();

        ArrayList<Decision> secuenciaDecisiones = recorridoUada.encontrarSecuenciaRecorridoUada(bateriaInicial, e, lugaresDisponibles, lugaresObligatorios, desplazamientos);

        imprimirSecuenciaDecisiones(secuenciaDecisiones);

    }

    public static void imprimirSecuenciaDecisiones(ArrayList<Decision> secuenciaDecisiones) {
        StringBuilder sb = new StringBuilder();

        for (int i=0; i < secuenciaDecisiones.size();i++) {
            Decision decision = secuenciaDecisiones.get(i);
            int indice = i+1;
            String decisionString = "Decision numero "+indice+"\n"+"Origen: "+decision.getOrigen().getNombre()+"\n"+"Destino: "+decision.getDestino().getNombre()+"\n"+"Movimiento empleado: "+decision.getMovimientoEmpleado().toString()+"\n"+"Bateria Remanente: "+decision.getBateriaRemanente()+"\n"+"Tiempo Acumulado: "+decision.getTiempoAcumulado()+" segundos \n\n";
            sb.append(decisionString);
        }

        // Guardar en archivo al mismo nivel del programa
        String rutaArchivo = Paths.get(System.getProperty("user.dir"), "output.txt").toString();

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(sb.toString());
            System.out.println("Array de decisiones guardado en: " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo TXT: " + e.getMessage());
        }
    }
}
