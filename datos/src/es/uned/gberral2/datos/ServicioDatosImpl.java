package es.uned.gberral2.datos;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.uned.gberral2.comun.CallbackJugadorInterface;
import es.uned.gberral2.comun.Partida;
import es.uned.gberral2.comun.ServicioDatosInterface;

/*
 * Clase encargada de realionar jugadores-partidas-tableros
 * Mantendrá:
 * - lista de jugadores registrados y conectados al sistema
 * - lista de jugadores que estan esperando a jugar
 * - lista de partidas con informacion
 * 		- tablero
 * 		- posicion de los barcos
 * 		- disparos efectuados
 */

public class ServicioDatosImpl implements ServicioDatosInterface {

	private ArrayList<CallbackJugadorInterface> callbacks;
	private Map<Integer, CallbackJugadorInterface> jugadorCallback;
	private Map<Integer, CallbackJugadorInterface> callbackMap;
	private ArrayList<String> jugadoresRegistrados;
	private ArrayList<String> jugadoresConectados;
	private Map<String, String> usuarios;
	private ArrayList<Partida> partidasEspera;
	private ArrayList<Partida> partidasEmpezadas;
	private ArrayList<Partida> partidasTerminadas;

	public ServicioDatosImpl() throws RemoteException {
		callbacks = new ArrayList<CallbackJugadorInterface>();
		jugadorCallback = new HashMap<Integer, CallbackJugadorInterface>();
		callbackMap = new HashMap<Integer, CallbackJugadorInterface>();
		jugadoresConectados = new ArrayList<String>();
		jugadoresRegistrados = new ArrayList<String>();
		usuarios = new HashMap<String, String>();
		partidasEmpezadas = new ArrayList<Partida>();
		partidasEspera = new ArrayList<Partida>();
		partidasTerminadas = new ArrayList<Partida>();
		
		rellenarDatos();
	}

	
/*
 * 1. METODOS PARA LA AUTENTICACION
 */
	// Metodo para enseñar la lista de jugadores registrados
	@Override
	public String listaJugadoresRegistrados() throws RemoteException {
		System.out.println("Jugadores Registrados: ");
		String lista = "";
		for (int i = 0; i < jugadoresRegistrados.size(); i++) {
			lista = lista + (i+1) + " " + jugadoresRegistrados.get(i) + "\n";
		}
		return lista;
	}
	
	// Metodo para enseñar la lista de jugadores registrados
	@Override
	public String listaJugadoresConectados() throws RemoteException {
		System.out.println("Jugadores Conectados: ");
		String lista = "";
		for (int i = 0; i < jugadoresConectados.size(); i++) {
			int id = jugadoresRegistrados.indexOf(jugadoresConectados.get(i));
			lista = lista + id + " " + jugadoresRegistrados.get(id - 1) + "\n";
		}
		return lista;
	}
	
	private void rellenarDatos() {
		jugadoresRegistrados.add("guille");
		usuarios.put("guille", "1");
		
		jugadoresRegistrados.add("maria");
		usuarios.put("maria", "1");
	}
	
	/*
	 * 2. METODOS PARA LA GESTION
	 */
	
	// Devuelve la lista de partidas terminadas
	@Override
	public String[] listaPartidasTerminadas() throws RemoteException {
		String[] partidas = new String[partidasTerminadas.size()];
		if (partidasTerminadas.size() != 0) {
			for (int i = 0; i < partidasTerminadas.size(); i++) {
				// Selecionamos la partida que estamos apuntando
				Partida partida = partidasTerminadas.get(i);
				partidas[i] = partida.toString();
			}
		}
		return partidas;
	}

	// Metodo para añadir una nueva partida a la lista de partidasEspera
	@Override
	public int añadirPartidaEspera(int id) throws RemoteException {
		Partida partida = new Partida(id);
		partidasEspera.add(partida);
		return id;
	}

	// Añade jugador 2 a la partida,
	// añade la partida a la lista de empezadas
	// quita la de la lista de partidas en espera
	@Override
	public Partida unirsePartida(int i, int id) throws RemoteException {
		Partida partida = partidasEspera.get(i - 1);
		partida.setPlayer2(id);
		partida.setLista();
		partidasEmpezadas.add(partida);
		partidasEspera.remove(i-1);
		return partida;
	}

	// Muestra la lista de partidas en espera
	// Devuelve una lista de arrays con el id de cada jugador
	// seguido de su nombre
	@Override
	public String[] listaPartidasEspera() throws RemoteException {
		String[] partidas = new String[partidasEspera.size() ];
		if (partidasEspera.size() != 0) {
			for (int i = 0; i < partidasEspera.size(); i++) {
				// Selecionamos la partida que estamos apuntando
				Partida partida = partidasEspera.get(i);
				partidas[i] = partida.toString();
			}
		}
		return partidas;
	}
	
	// Devuelve la lista de partidas empezadas en un vector de String
	@Override
	public String[] listaPartidasEmpezadas() throws RemoteException {
		String[] partidas = new String[partidasEmpezadas.size()];
		if (partidasEmpezadas.size() != 0) {
			for (int i = 0; i < partidasEmpezadas.size(); i++) {
				// Selecionamos la partida que estamos apuntando
				Partida partida = partidasEmpezadas.get(i);
				partidas[i] = partida.toString();
			}
		}
		return partidas;
	}
	
	// Devuelve la lista de partidas empezadas con un jugador en concreto
	@Override
	public String[] listaPartidasEmpezadasJugador(int id) {
		String[] partidas = new String[partidasEmpezadas.size()];
		int e = 0; // Variable para almacenar en el array
		for (int i = 0; i < partidasEmpezadas.size(); i++) {
			Partida partida = partidasEmpezadas.get(i);
			if (partida.getPlayer1() == id || partida.getPlayer2() == id) {
				// String nombre = jugadoresRegistrados.get(id);
				partidas[e] = partida.toString();
				e++;
			}
		}
		return partidas;
	}
	
	// Proceso para obtener un lista de las partidas empezadas por el jugador
	// Se utiliza dentro del servicio cuando el jugador seleciona una partida a la que unirse
	private Partida[] partidasEmpezadasJugador(int id) {
		Partida[] partidas = new Partida[partidasEmpezadas.size()];
		int e = 0; // Variable para almacenar en el array
		for (int i = 0; i < partidasEmpezadas.size(); i++) {
			Partida partida = partidasEmpezadas.get(i);
			if (partida.getPlayer1() == id || partida.getPlayer2() == id) {
				// String nombre = jugadoresRegistrados.get(id);
				partidas[e] = partida;
				e++;
			}
		}
		return partidas;
	}
	
	
	// Selecciona una partida de la lista de partidas empezadas y 
	// la pasa a la lista de partidas terminadas
	// pone como ganador al usuario que se le pasa
	@Override
	public void ganarPartida(int i, int id) throws RemoteException {
		int ganador = id;
		Partida partida = partidasEmpezadas.get(i - 1);
		partida.setGanador(ganador);
		partida.terminar();
		partidasEmpezadas.remove(i - 1);
		partidasTerminadas.add(partida);
		
	}
	
	// Devuelve la partida con indice numPartida de la lista de partidas
	// del que se corresponde con el id
	@Override
	public Partida seleccionPartida(int numPartida, int id)throws RemoteException {
		// Buscamos la partida a la que se refiere:
		//	el numero de partida es la referencia a
		//	la lista de partidas empezadas por le jugador con el id
		Partida[] partidas = partidasEmpezadasJugador(id);
		Partida partida = partidas[numPartida];
		return partida;	
	}
	
	// Devuelve el estado de la partida en forma de string
	@Override
	public String getEstadoPartida(int numPartida, int id) throws RemoteException{
		Partida partida = seleccionPartida(numPartida, id);
		return partida.getEstado();
	}
	
	// Coloca un barco en la partida correspondiente y en el tablero correspondiente
	@Override
	public void colocarBarco(String coordenadas, String z, int numPartida, int id) throws RemoteException{
		Partida partida = partidasEmpezadasJugador(id)[numPartida];
		partida.colocarBarco(coordenadas, z, id);
	}
	// Dispara al tablero del otro jugador en la partida especificada
	@Override
	public boolean disparo(String coordenadas, int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		return partida.disparo(id, coordenadas);
	}
	// Devuelve true si todos los barcos estan colocados
	@Override
	public boolean estanBarcosColocados(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		return partida.estanBarcosColocados(id);
	}
	// Llama al metodo empezar de la partida seleccionada 
	// y la incluye en la lista de partidas empezadas
	@Override
	public boolean empezar(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		if (partida.empezar())	{
			return true;
		}
		return false;
	}
	@Override 
	public void pasarTurno(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		partida.pasarTurno(id);
	}
	@Override
	public String tableroJugador(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		return partida.tableroJugador(id);
	}
	// Devuelve true si es el turno del jugador con id especificada
	@Override
	public boolean turno(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		return partida.turno(id);
	}
	// Devuelve true si la partida esta terminada
	@Override
	public boolean isTerminada(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		return partida.isTerminada();
	}
	// Devuelve el id del ganador de la partida
	@Override
	public int getGanador(int numPartida, int id) throws RemoteException {
		Partida partida = seleccionPartida(numPartida, id);
		return partida.getGanador();
	}

	@Override
	public ArrayList<String> getJugadoresRegistrados() {
		return jugadoresRegistrados;
	}
	@Override
	public void setJugadoresRegistrados(ArrayList<String> jugadoresRegistrados) {
		this.jugadoresRegistrados = jugadoresRegistrados;
	}
	@Override
	public ArrayList<String> getJugadoresConectados() {
		return jugadoresConectados;
	}
	@Override
	public void setJugadoresConectados(ArrayList<String> jugadoresConectados) {
		this.jugadoresConectados = jugadoresConectados;
	}
	@Override
	public Map<String, String> getUsuarios() {
		return usuarios;
	}
	@Override
	public void setUsuarios(Map<String, String> usuarios) {
		this.usuarios = usuarios;
	}
	
	
	@Override
	public ArrayList<Partida> getPartidasEspera() {
		ArrayList<Partida> partidasEsp = new ArrayList<Partida>();
		for (int i = 0; i < partidasEspera.size(); i++) {
			Partida partida = partidasEspera.get(i);
			partidasEsp.add(partida);
		}
		return partidasEsp;
	}
	@Override
	public ArrayList<Partida> getPartidasEsperaJugador(int id){
		ArrayList<Partida> partidas = new ArrayList<Partida>();
		
		for (int i = 0; i < partidasEspera.size(); i++) {
			Partida partida = partidasEspera.get(i);
			if (partida.getPlayer1() == id || partida.getPlayer2() == id) {
				// String nombre = jugadoresRegistrados.get(id);
				partidas.add(partida);
			}
		}
		return partidas;
	}
	@Override
	public void setPartidasEspera(ArrayList<Partida> partidasEspera) {
		this.partidasEspera = partidasEspera;
	}
	@Override
	public ArrayList<Partida> getPartidasEmpezadas() {
		ArrayList<Partida> partidas = new ArrayList<Partida>();
		for (int i = 0; i < partidasEmpezadas.size(); i++) {
			Partida partida = partidasEspera.get(i);
			partidas.add(partida);
		}
		return partidas;
	}
	@Override
	public ArrayList<Partida> getPartidasEmpezadasJugador(int id){
		ArrayList<Partida> partidas = new ArrayList<Partida>();
		
		for (int i = 0; i < partidasEmpezadas.size(); i++) {
			Partida partida = partidasEmpezadas.get(i);
			if (partida.getPlayer1() == id || partida.getPlayer2() == id) {
				// String nombre = jugadoresRegistrados.get(id);
				partidas.add(partida);
			}
		}
		return partidas;
	}
	@Override
	public void setPartidasEmpezadas(ArrayList<Partida> partidasEmpezadas) {
		this.partidasEmpezadas = partidasEmpezadas;
	}
	
	@Override
	public ArrayList<String> getPartidasTerminadasJugador(int id){
		ArrayList<String> partidas = new ArrayList<String>();
		
		for (int i = 0; i < partidasTerminadas.size(); i++) {
			Partida partida = partidasTerminadas.get(i);
			if (partida.getPlayer1() == id || partida.getPlayer2() == id) {
				// String nombre = jugadoresRegistrados.get(id);
				partidas.add(partida.toString());
			}
		}
		return partidas;
	}
	@Override
	public ArrayList<String> getPartidasTerminadas() {
		ArrayList<String> partidas = new ArrayList<String>();
		for (int i = 0; i < partidasTerminadas.size(); i++) {
			Partida partida = partidasTerminadas.get(i);
			partidas.add(partida.toString());
		}
		return partidas;
	}
	@Override
	public void setPartidasTerminadas(ArrayList<Partida> partidasTerminadas) {
		this.partidasTerminadas = partidasTerminadas;
	}
	@Override
	public ArrayList<CallbackJugadorInterface> getCallbacks() throws RemoteException {
		return callbacks;
	}
	@Override
	public void setCallbacks(ArrayList<CallbackJugadorInterface> callbacks) throws RemoteException {
		this.callbacks = callbacks;
	}
	@Override
	public Map<Integer, CallbackJugadorInterface> getJugadorCallback() throws RemoteException {
		return jugadorCallback;
	}
	@Override
	public void setJugadorCallback(Map<Integer, CallbackJugadorInterface> jugadorCallback) throws RemoteException {
		this.jugadorCallback = jugadorCallback;
	}
	@Override
	public Map<Integer, CallbackJugadorInterface> getCallbackMap() throws RemoteException{
		return callbackMap;
	}
	@Override
	public void setCallbackMap(Map<Integer, CallbackJugadorInterface> callbackMap) throws RemoteException {
		this.callbackMap = callbackMap;
	}
}