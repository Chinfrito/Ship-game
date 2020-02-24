package es.uned.gberral2.comun;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioGestorInterface extends Remote {

	
	String getPartidasTerminadasJugador(int id) throws RemoteException;
	int nuevaPartida (int id) throws RemoteException;
	String listaPartidasEspera() throws RemoteException;
	void jugar(int idJugador) throws RemoteException;
	String[] listaPartidasEmpezadas() throws RemoteException;
	String[] listaPartidasEmpezadasJugador(int id) throws RemoteException;
	String[] listaPartidasTerminadas(int id) throws RemoteException;
	String getEstadoPartida(int numPartida, int id) throws RemoteException;
	void pasarTurno(int idJugador) throws RemoteException;
	boolean disparo(String coordenadas, int id) throws RemoteException;
	
	void colocarBarco(String coordenadas, String z, int numPartida, int id) throws RemoteException;
	
	boolean estanBarcosColocados(int numPartida, int id) throws RemoteException;
	boolean empezar(int numPartida, int id)throws RemoteException;
	boolean turno(int numPartida, int id)throws RemoteException;
	int getGanador(int numPartida, int id)throws RemoteException;
	boolean isTerminada(int numPartida, int id)throws RemoteException;
	String tableroJugador(int numPartida, int id) throws RemoteException;
	void empezarPartida(int idOponente, int idJugador) throws RemoteException;
	
	
}
