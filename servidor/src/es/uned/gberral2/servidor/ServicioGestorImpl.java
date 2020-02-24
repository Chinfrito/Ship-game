package es.uned.gberral2.servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import es.uned.gberral2.comun.CallbackJugadorInterface;
import es.uned.gberral2.comun.Partida;
import es.uned.gberral2.comun.ServicioDatosInterface;
import es.uned.gberral2.comun.ServicioGestorInterface;
import es.uned.gberral2.comun.Utils;
/*
 * Gestiona todas las operaciones de los jugadores 
 * en relacion al juego y la emision de eventos
 * - Crea las estructuras para jugar la partida
 * - Gestiona la logica de juego
 * - Informa a los jugadores del inicio de la partida,
 * las puntuaciones, y del final
 */
public class ServicioGestorImpl implements ServicioGestorInterface {
	
	private ServicioDatosInterface datos;
	
	public ServicioGestorImpl() throws RemoteException, NotBoundException {
		// 1. Seleccionamos el servicio base de datos
		Registry registry = LocateRegistry.getRegistry(Utils.PUERTO);
		String ip = Utils.ip;
		int rmiport = Utils.PUERTO;
		datos = (ServicioDatosInterface) registry.lookup( 
				"rmi://"+ ip + ":" + rmiport + "/" + "ServicioDatos");
	}

	// Devuelve un String con todas las partidas terminadel jugador
	@Override
	public String getPartidasTerminadasJugador(int id) throws RemoteException {
		ArrayList<String> partidas = datos.getPartidasTerminadasJugador(id);
		String lista = "";
		for (int i = 0; i < partidas.size(); i++) {
			lista = "\n" + partidas.get(i);
		}
		return lista;
	}
	// Inicia una nueva partida y la pone a la espera de que otro jugador se una
	@Override
	public int nuevaPartida(int id) throws RemoteException {
		System.out.println("Añadida partida del jugador" + id);
		return datos.añadirPartidaEspera(id);
	}

	@Override
	public String listaPartidasEspera() throws RemoteException {
		String partidas = "";
		for (int i = 0; i < datos.getPartidasEspera().size(); i++) {
			int id = datos.getPartidasEspera().get(i).getPlayer1();
			String nombre = datos.getJugadoresRegistrados().get(id);
			partidas = partidas + "\n" + nombre + " " + id;
		}
		return partidas;
	}
	
	// Se usa
	@Override
	public void empezarPartida(int idOponente, int idJugador) throws RemoteException {
		Partida partida = getPartidaEspera(idOponente);
		partida.setPlayer2(idJugador);
		partida.setLista();
		System.out.println("El jugador " + idJugador + " se ha unido a la partida " + idOponente);
		
		ArrayList<Partida> partidasEmpezadas = datos.getPartidasEmpezadas();
		partidasEmpezadas.add(partida);
		datos.setPartidasEmpezadas(partidasEmpezadas);
		System.out.println("Partida añadida a lista de empezadas");
		
		ArrayList<Partida> partidasEspera = datos.getPartidasEspera();
		partidasEspera.remove(partida);
		datos.setPartidasEspera(partidasEspera);
		System.out.println("Partida eliminada de lista espera");
		
		System.out.println("Partida añadida a la base de datos");
		
		// Seleccionamos los callbacks
		int id1 = partida.getPlayer1();
		int id2 = partida.getPlayer2();
		CallbackJugadorInterface jugador1 = datos.getJugadorCallback().get(id1);
		CallbackJugadorInterface jugador2 = datos.getJugadorCallback().get(id2);
		
		// Enviamos evento a los callbacks
		jugador1.recibirEvento("jugar");
		jugador2.recibirEvento("jugar");
		
	}
	private Partida getPartidaEmpezada(int id) throws RemoteException {
		ArrayList<Partida> partidasEmp = datos.getPartidasEmpezadas();
		Partida partida = null;
		for (int j = 0; j < partidasEmp.size(); j++) {
			Partida p = partidasEmp.get(j);
			if(p.isPlayer1(id) || p.isPlayer2(id)) {
				partida = p;
				return partida;
			}
		}
		return partida;
	}
	private Partida getPartidaEspera(int id) throws RemoteException {
		ArrayList<Partida> partidasEmp = datos.getPartidasEspera();
		Partida partida = null;
		for (int j = 0; j < partidasEmp.size(); j++) {
			Partida p = partidasEmp.get(j);
			if(p.isPlayer1(id) || p.isPlayer2(id)) {
				partida = p;
				return partida;
			}
		}
		return partida;
	}
	@Override
	public void jugar(int idJugador) throws RemoteException {
		Partida partida = getPartidaEmpezada(idJugador);
		CallbackJugadorInterface jugador = datos.getJugadorCallback().get(idJugador);
		switch (partida.getEstado()) {
		case "lista":
			if (!partida.estanBarcosColocados(idJugador)) {
				jugador.recibirEvento("colocar barco");
				;
				System.out.println("tablero jugador " + idJugador);
				System.out.println(partida.tableroJugador(idJugador));
			}
			if (!partida.empezar())
				System.out.println("Esperando al otro jugador");
			break;
		case "terminada":
			System.out.println("Partida Terminada");
			int ganador = partida.getGanador();
			if (ganador == idJugador) {
				jugador.recibirEvento("has ganado");
				jugador.recibirEvento("salir juego");
			} else {
				jugador.recibirEvento("has perdido");
				jugador.recibirEvento("salir juego");
			}
			break;
		default:
			if (partida.turno(idJugador)) {
				jugador.recibirEvento("disparar");
			} else {
				jugador.recibirEvento("esperar");
			}
		}
	}

	@Override
	public String getEstadoPartida(int numPartida, int id) throws RemoteException {
		return datos.getEstadoPartida(numPartida, id);
	}
	@Override
	public void pasarTurno(int id) throws RemoteException {
		Partida partida = getPartidaEmpezada(id);
		partida.pasarTurno(id);
	}
	@Override
	public void colocarBarco(String coordenadas, String z, int numPartida, int id) throws RemoteException {
		datos.colocarBarco(coordenadas, z, numPartida, id);
	}
	@Override
	public boolean disparo(String coordenadas, int idJugador) throws RemoteException{
		Partida partida = getPartidaEmpezada(idJugador);
		return partida.disparo(idJugador, coordenadas);
	}
	
	
	
	
	
	
	@Override
	public String[] listaPartidasEmpezadas() throws RemoteException {
		return datos.listaPartidasEmpezadas();
	}
	
	@Override
	public String[] listaPartidasEmpezadasJugador(int id) throws RemoteException {
		return datos.listaPartidasEmpezadasJugador(id);
	}
	
	@Override
	public String[] listaPartidasTerminadas(int id) throws RemoteException {
		return datos.listaPartidasEmpezadasJugador(id);
	}

	@Override
	public boolean empezar(int numPartida, int id) throws RemoteException{
		return datos.empezar(numPartida, id);
	}
	@Override
	public String tableroJugador(int numPartida, int id) throws RemoteException{
		return datos.tableroJugador(numPartida, id);
	}
	
	
	
	
	@Override
	public boolean estanBarcosColocados(int numPartida, int id) throws RemoteException{
		return datos.estanBarcosColocados(numPartida, id);
	}

	@Override
	public boolean turno(int numPartida, int id) throws RemoteException{
		return datos.turno(numPartida, id);
	}
	
	@Override
	public boolean isTerminada(int numPartida, int id) throws RemoteException{
		return datos.isTerminada(numPartida, id);
	}
	@Override
	public int getGanador(int numPartida, int id) throws RemoteException{
		return datos.getGanador(numPartida, id);
	}
}
