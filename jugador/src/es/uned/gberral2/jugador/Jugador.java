/* 
 * Guillermo Berral Candel
 * gberral2@alumno.unes.es
 */

package es.uned.gberral2.jugador;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import es.uned.gberral2.comun.CallbackJugadorInterface;
import es.uned.gberral2.comun.ServicioAutenticacionInterface;
import es.uned.gberral2.comun.ServicioGestorInterface;
import es.uned.gberral2.comun.Utils;
import es.uned.gberral2.jugador.CallbackJugadorImpl;

public class Jugador {

	ServicioAutenticacionInterface autenticacion;
	ServicioGestorInterface gestion;
	CallbackJugadorInterface servCall;
	int idJugador;

	public Jugador(ServicioAutenticacionInterface autenticacion, ServicioGestorInterface gestion,
			CallbackJugadorInterface servCall, int idJugador) {
		this.autenticacion = autenticacion;
		this.gestion = gestion;
		this.servCall = servCall;
		this.idJugador = idJugador;
	}

	public static void main(String[] args) throws RemoteException {
		try {
			// 1. Seleccionamos el registro en el que esta el servidor
			Registry registry = LocateRegistry.getRegistry(Utils.PUERTO);

			// 2. Seleccionamos ServidorAutenticacion y ServicioGestion creado en el
			// registro
			String ip = Utils.ip;
			int rmiport = Utils.PUERTO;

			ServicioAutenticacionInterface autenticacion = (ServicioAutenticacionInterface) registry
					.lookup("rmi://" + ip + ":" + rmiport + "/" + "ServicioAutenticacion");
			System.out.println("Conectado al servicio de autenticacion");
			ServicioGestorInterface gestion = (ServicioGestorInterface) registry
					.lookup("rmi://" + ip + ":" + rmiport + "/" + "ServicioGestor");
			System.out.println("Conectado al servicio de gestion");

			// 3. Ponemos en marcha el Callback del jugador
			CallbackJugadorImpl servCall = new CallbackJugadorImpl();
			CallbackJugadorInterface stub = (CallbackJugadorInterface) UnicastRemoteObject.exportObject(servCall, 0);

			// 4. Cogemos el ID para reconocer a este terminal y lo añadimos a la URL
			int identificadorCallback = autenticacion.getIdToConect();
			servCall.setIDCallback(identificadorCallback);
			String urlCallback = "rmi://" + ip + ":" + rmiport + "/" + "CallbackJugador" + "/" + identificadorCallback;
			registry.rebind(urlCallback, stub);

			System.out.println("Terminal conectada");
			System.out.println();

			// 5. Conectamos el callback con el servidor
			autenticacion.conexion(identificadorCallback);

			// 6. Parte central del programa, nos metemos en un bucle que pide eventos al
			// servidor
			Jugador jugador = new Jugador(autenticacion, gestion, servCall, -1);
			
			// Bucle para recibir eventos
			jugador.autenticar(servCall);
			
			// 7. Damos de baja el servicio de callback
			registry.unbind(urlCallback);
			UnicastRemoteObject.unexportObject(servCall, true);

		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
	
	private void esperarEventos(int idJugador) throws RemoteException, NotBoundException, InterruptedException {
		while (true) {
			if (servCall.hayEventos()) {
				String evento = servCall.getEvento();
				if (!evento.isEmpty()) {
					procesarEvento(evento);
				}
			}
			Thread.sleep(2000);
		}
	}
	private void procesarEvento(String evento) throws RemoteException, NotBoundException, InterruptedException {
		switch (evento) {
		case "menu principal":
			menuPrincipal(servCall, idJugador);
			break;
		case "jugar":
			gestion.jugar(idJugador); 
			break;
		case "poner barcos":
			colocarBarco(idJugador, idJugador);
		case "Has Perdido":
			menuPrincipal(servCall, idJugador);
			break;
		case "Has Ganado":
			menuPrincipal(servCall, idJugador);
			break;
		case "esperar":
			Thread.sleep(2000);
		case "disparar":
			disparar(idJugador);
		}
	}

	// Funcion para identificarnos con el servidor y la base de datos
	private void autenticar(CallbackJugadorInterface jugador) throws RemoteException, NotBoundException, InterruptedException {
		String[] opcionesAcceso = { "Registrar nuevo jugador", "Hacer login", "Salir" };
		String nombre;
		String passw;
		boolean jugadorNoConectado = true;
		while (jugadorNoConectado) {
			int opcionAcceso = servCall.menu("Menu de acceso", opcionesAcceso);
			switch (opcionAcceso) {
			case 1:
				jugador.imprimir("REGISTRO");
				jugador.imprimir("Introduzca nombre usuario");
				nombre = jugador.getDato();

				jugador.imprimir("Introduzca contraseña");
				passw = jugador.getDato();
				boolean registrado = autenticacion.registro(nombre, passw);
				if (registrado) {
					int id = autenticacion.getID(nombre, passw);
					jugador.imprimir("Usuario registrado con ID: " + id);
				} else
					jugador.imprimir("Nombre en uso, no ha sido posible el registro");
				break;
			case 2:
				// Menu de login, se podrá intentar todas las veces que se quiera
				jugador.imprimir("LOGIN");
				while (true) {
					jugador.imprimir("Introduzca nombre usuario");
					nombre = jugador.getDato();

					jugador.imprimir("Introduzca contraseña");
					passw = jugador.getDato();

					int id = autenticacion.acceso(nombre, passw);
					if (id == -2) {
						jugador.imprimir("Contraseña incorrecta");
					} else if (id == -1) {
						jugador.imprimir("Usuario no encontrado");
					} else {
						this.setID(id);
						jugador.imprimir("Bienvenido " + nombre + " " + id);

						jugadorNoConectado = false;
						menuPrincipal(jugador, id);
						break;
					}

					boolean intentarOtraVez = jugador.confirmar("¿Desea intentarlo otra vez?");
					if (!intentarOtraVez)
						break;
				}
				break;
			case 3:
				int idCallback = servCall.getIDCallback();
				autenticacion.desconexion(idCallback, idJugador);
				break;
			}
		}
	}

	private void setID(int id) {
		this.idJugador = id;
	}

	private void menuPrincipal(CallbackJugadorInterface jugador, int idJugador) throws RemoteException, NotBoundException, InterruptedException {

		boolean salir = false;
		String name = "Menu jugador " + idJugador;
		String[] ops = { "Informacion del jugador (puntuaciones historicas)", "Iniciar partida",
				"Partidas en espera", "Unirse a una partida ", "Salir" };
		while (salir == false) {
			int r = Utils.menu(name, ops);

			switch (r) {
			case 1:
				System.out.println("Historico de partidas");
				historicoPartidas(idJugador);
				break;
			case 2:
				System.out.println("Iniciar nueva partida");
				iniciarPartida(idJugador);
				break;
			case 3:
				System.out.println("Lista partida en espera");
				System.out.println(gestion.listaPartidasEspera());
				break;
			case 4:
				// El programa entra en un bucle en el que solo recibe 
				System.out.println("Unirse a partida");
				System.out.println("Introduzca id del jugador oponente");
				int idOponente = -1;
				try {
					String opcion = Utils.getDato();
					idOponente = Integer.parseInt(opcion);
				}catch (Exception e) {
					System.out.println("Valor no valido");
				}
				uniraPartida(idOponente);
				break;
			case 5:
				System.out.println("Saliendo del sistema");
				autenticacion.desconexion(jugador.getIDCallback(), idJugador);
			default:
				throw new IllegalArgumentException("Unexpected value: " + r);
			}
		}
	}

	private void historicoPartidas(int id) throws RemoteException {
		System.out.println(gestion.getPartidasTerminadasJugador(id));
	}

// Crea una nueva partida y la pone a la espera de empezar
	private void iniciarPartida(int id) throws RemoteException, NotBoundException, InterruptedException {
		gestion.nuevaPartida(id);
		esperarEventos(id);
	}
// Metodo para unirse a una partida empezada

	private void uniraPartida(int idOponente) throws RemoteException, NotBoundException, InterruptedException {
		// Sacamos por pantalla las listas empezadas
		gestion.empezarPartida(idOponente, idJugador);
		esperarEventos(idJugador);
	}

		// Metodo para colocar barco
	private void colocarBarco(int idJugador, int numPartida) throws RemoteException {
		System.out.println("Barco ");
		String coordenadas = Utils.getCoordenadas();
		System.out.println("Introduce direccion del barco (h o v)");
		String z = Utils.getDato();

		gestion.colocarBarco(coordenadas, z, numPartida, idJugador);
	}

	private void disparar(int idJugador) throws RemoteException {
		System.out.println("Disparo: ");
		String coordenadas = Utils.getCoordenadas();
		boolean disparo = gestion.disparo(coordenadas, idJugador);
		if (disparo) {
			System.out.println("Tocado");
		} else {
			System.out.println("Agua");
		}
		gestion.pasarTurno(idJugador);
	}
	
	
	public int getIdJugador() {
		return idJugador;
	}

	public void setIdJugador(int idJugador) {
		this.idJugador = idJugador;
	}
}