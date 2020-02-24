package es.uned.gberral2.comun;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;


public interface ServicioDatosInterface extends Remote {

	// Metodo para conseguir identificador

	void setCallbackMap(Map<Integer, CallbackJugadorInterface> callbackMap) throws RemoteException;
	Map<Integer, CallbackJugadorInterface> getCallbackMap() throws RemoteException;
	void setJugadorCallback(Map<Integer, CallbackJugadorInterface> jugadorCallback) throws RemoteException;
	Map<Integer, CallbackJugadorInterface> getJugadorCallback() throws RemoteException;
	void setCallbacks(ArrayList<CallbackJugadorInterface> callbacks) throws RemoteException;
	ArrayList<CallbackJugadorInterface> getCallbacks() throws RemoteException;
	
	public ArrayList<String> getJugadoresRegistrados() throws RemoteException;
	public void setJugadoresRegistrados(ArrayList<String> jugadoresRegistrados) throws RemoteException;
	public ArrayList<String> getJugadoresConectados() throws RemoteException;
	public void setJugadoresConectados(ArrayList<String> jugadoresConectados) throws RemoteException;
	public Map<String, String> getUsuarios() throws RemoteException;
	public void setUsuarios(Map<String, String> usuarios) throws RemoteException;
	public ArrayList<Partida> getPartidasEspera() throws RemoteException;
	public void setPartidasEspera(ArrayList<Partida> partidasEspera) throws RemoteException;
	public ArrayList<Partida> getPartidasEmpezadas() throws RemoteException;
	public void setPartidasEmpezadas(ArrayList<Partida> partidasEmpezadas) throws RemoteException;
	public ArrayList<String> getPartidasTerminadas() throws RemoteException;
	public void setPartidasTerminadas(ArrayList<Partida> partidasTerminadas) throws RemoteException;
	
	ArrayList<String> getPartidasTerminadasJugador(int id) throws RemoteException;
	ArrayList<Partida> getPartidasEsperaJugador(int id) throws RemoteException;
	ArrayList<Partida> getPartidasEmpezadasJugador(int id) throws RemoteException;

	public int añadirPartidaEspera(int id) throws RemoteException;
	
	
	//public int getID(String nombre, String passw) throws RemoteException; 
	public String listaJugadoresRegistrados()throws RemoteException;
	public String listaJugadoresConectados() throws RemoteException;
	public String[] listaPartidasEspera() throws RemoteException;
	public String[] listaPartidasEmpezadas() throws RemoteException;
	public String[] listaPartidasTerminadas() throws RemoteException;
	
	public Partida unirsePartida(int i, int id) throws RemoteException;
	public String[] listaPartidasEmpezadasJugador(int id) throws RemoteException;
	public void ganarPartida(int i, int id) throws RemoteException;
	public void colocarBarco(String coordenadas, String z, int numPartida, int id) throws RemoteException;
	String getEstadoPartida(int numPartida, int id) throws RemoteException;
	Partida seleccionPartida(int numPartida, int id) throws RemoteException;
	public boolean estanBarcosColocados(int numPartida, int id) throws RemoteException;
	public boolean disparo(String coordenadas, int numPartida, int id)throws RemoteException;
	public boolean empezar(int numPartida, int id)throws RemoteException;
	public boolean turno(int numPartida, int id)throws RemoteException;
	public int getGanador(int numPartida, int id)throws RemoteException;
	boolean isTerminada(int numPartida, int id)throws RemoteException;
	String tableroJugador(int numPartida, int id)throws RemoteException;
	public void pasarTurno(int numPartida, int id) throws RemoteException;
}
