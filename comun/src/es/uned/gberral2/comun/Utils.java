/* 
 * Guillermo Berral Candel
 * gberral2@alumno.unes.es
 */

package es.uned.gberral2.comun;

import java.util.Scanner;
/*
 * Clase para guardar los elementos compartidos por todas las
 * clases del programa
 */
public class Utils {

	public static String ip = "0.0.0.1";
	public static int PUERTO = 8121; 
	public static Scanner scan = new Scanner(System.in);
	
	// Devuelve el numero seleccionado en el menu
	public static int menu (String nombre, String[] opciones) {
		int op = -2;
		mostrarMenu (nombre, opciones);
		op = getOpcion(opciones.length);
		return op;
	}
	// Muestra el menu con el nombre y las opciones que se le pasen
	public static void mostrarMenu(String nombre, String[] opciones) {
			// Imprimimos las opciones por pantalla
			System.out.println();
			System.out.println("--- " + nombre + " ---");
			System.out.println();
			for (int i = 0; i <= opciones.length - 1; i++) {
				System.out.println(i+1 + ".-  " + opciones[i]);
			}
	}
	
	// Enseña la pregunta que le pasemos 
	// devuelve true si respondemos con s
	public static boolean confirmar(String pregunta) {
		System.out.println(pregunta);
		String conf = "";
		while ( !( conf.equals("s") || conf.equals("n") ) ) {
			System.out.println("[Confirmar(s/n)]");
			conf = getDato();
		}
		if (conf.equals("s")) {
			return true;
		} else {
			return false;
		}
	}
	
	// Devuelve el string que hayamos escrito
	public static String getDato() {
		String dato = "";
		while (true) {
			if (scan.hasNextLine()) {
				dato = scan.nextLine();
				if (!dato.trim().isEmpty()) {
					System.out.println("Dato introducido: " + dato);
					return dato;
				}
			}
		}
	}
	// Comprueba que la opcion que se seleciona esta dentro del limite
	// y la devuelve como valor entero
	public static int getOpcion(int nOpciones) {
		try {
			boolean correcto = false;
			while (correcto == false) {
				System.out.println("Seleccione una opcion del 1 al " + nOpciones);
				String opcion = getDato();
				int op = Integer.parseInt(opcion);
				if (op <= nOpciones && op > 0) {
					correcto = true;
					return op;
				}else {
					System.out.println("Opcion fuera de limites");
				}
			}
			return -2;
		} catch (Exception e) {
			return -2;
		}
	}
	
	// Imprime por pantalla el vector de Strings que le pasemos
	// un elemento por linea
	public static void listar(String[] lista) {
		for (int i = 0; i < lista.length; i++) {
			if (lista[i] != null) {
				System.out.println(lista[i]);
			}
		}
	}
	// Comprueba si las coordenadas que se han pasado son correctas
	public static boolean isCoordenadas(String coordenadas) {
		try {
			String[] c = coordenadas.split(" ", 2);
			int x = Integer.parseInt(c[0]);
			if(x > 0 && x < 11) {
				if(c[1].equals("a") || c[1].equals("b") ||c[1].equals("c") ||c[1].equals("d") ||
						c[1].equals("e") ||c[1].equals("f") ||c[1].equals("g") ||c[1].equals("h") ||
						c[1].equals("i") ||c[1].equals("j")) {
					return true;
				}
			}
			return false;
		}catch (Exception e) {
			return false;
		}
		
	}
	// Devuelve las coordenadas introducidas
	// traducida la y a numero para que sea mas sencilla su utilizacion
	public static String getCoordenadas() {
		String coordenadas = "";
		boolean noCorrecto = true;
		while(noCorrecto) {
			System.out.println("Introduzca coordenadas separadas por un espacio:");
			System.out.println("Para el eje x : 1-10");
			System.out.println("Para el eje y : a-j");
			coordenadas = getDato();
			if (isCoordenadas(coordenadas))
				noCorrecto = false;
		}
		return getCoordenadas(coordenadas);
	}
	// Traduce las coordenadas numero-letra a numero-numero
	public static String getCoordenadas(String coordenadas) {
		String[] c = coordenadas.trim().split(" ", 2);
		int x = Integer.parseInt(c[0]);
		String y = c[1];
		switch (y) {
		case "a":
			return x + " " + 1;
		case "b":
			return x + " " + 2;
		case "c":
			return x + " " + 3;
		case "d":
			return x + " " + 4;
		case "e":
			return x + " " + 5;
		case "f":
			return x + " " + 6;
		case "g":
			return x + " " + 7;
		case "h":
			return x + " " + 8;
		case "i":
			return x + " " + 9;
		case "j":
			return x + " " + 10;

		default:
			throw new IllegalArgumentException("Unexpected value: " + y);
		}
	}
	
	// Devuelve el parametro x de unas coordenadas pasadas
	public static int getX(String coordenadas) {
		String[] c = coordenadas.split(" ", 2);
		return Integer.parseInt(c[0]) - 1;
	}
	// Devuelve el parametro y de unas coordenadas pasadas
	public static int getY(String coordenadas) {
		String[] c = coordenadas.split(" ", 2);
		return Integer.parseInt(c[1]) - 1;
	}
}

	
