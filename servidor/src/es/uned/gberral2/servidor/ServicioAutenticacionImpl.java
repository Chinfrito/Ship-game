package es.uned.gberral2.servidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;

import es.uned.gberral2.comun.CallbackJugadorInterface;
import es.uned.gberral2.comun.ServicioAutenticacionInterface;
import es.uned.gberral2.comun.ServicioDatosInterface;
import es.uned.gberral2.comun.Utils;

public class ServicioAutenticacionImpl implements ServicioAutenticacionInterface {
	Registry registry;
	private ServicioDatosInterface datos;
	
	

	/*
	 * Metodo acceso: recibe un nombre y una password y comprueba con la base de
	 * datos se existe el usuario - si existe llama al metodo login - si no existe
	 * llama al metodo registro
	 */
	public ServicioAutenticacionImpl() throws RemoteException, NotBoundException {
		// 1. Seleccionamos el servicio base de datos
		String ip = Utils.ip;
		int rmiport = Utils.PUERTO;
		registry = LocateRegistry.getRegistry(Utils.PUERTO);
		datos = (ServicioDatosInterface) registry.lookup(
				"rmi://"+ ip + ":" + rmiport + "/" + "ServicioDatos");

	}
	
	//Primer metodo para conectar 
	@Override
	public void conexion(int identificadorCallback) throws RemoteException, NotBoundException {

		// Selccionamos el objeto y lo guardamos en la lista de callbacks
		String ip = Utils.ip;
		int rmiport = Utils.PUERTO;
		CallbackJugadorInterface jugador = (CallbackJugadorInterface) registry
				.lookup("rmi://" + ip + ":" + rmiport + "/" + "CallbackJugador" + "/" + identificadorCallback);
		
		// Actualizamos la lista de datos
		ArrayList<CallbackJugadorInterface> callbacks = datos.getCallbacks();
		callbacks.add(jugador);
		datos.setCallbacks(callbacks);
		
		Map<Integer, CallbackJugadorInterface> callbackMap = datos.getCallbackMap();
		callbackMap.put(identificadorCallback, jugador);
		datos.setCallbackMap(callbackMap);
		
		jugador.recibirEvento("autenticar");
	}
	@Override 
	public void desconexion(int identificadorCallback, int idJugador) throws RemoteException {
		
		CallbackJugadorInterface jugador = datos.getCallbackMap().get(identificadorCallback);
		
		if(idJugador > 0) { // El jugador se ha registrado y esta conectado
			String nombre = datos.getJugadoresRegistrados().get(idJugador);
			
			ArrayList<String> jugadoresConectados = datos.getJugadoresConectados();
			jugadoresConectados.remove(nombre);
			datos.setJugadoresConectados(jugadoresConectados);			
			
			Map<Integer, CallbackJugadorInterface> jugadorCallback = datos.getJugadorCallback();
			jugadorCallback.remove(idJugador);
			datos.setJugadorCallback(jugadorCallback);
			
			System.out.println(nombre + " se ha desconectado");
		}
		ArrayList<CallbackJugadorInterface> callbacks = datos.getCallbacks();
		callbacks.remove(jugador);
		datos.setCallbacks(callbacks);
		
		jugador.recibirEvento("salir");
		
		Map<Integer, CallbackJugadorInterface> callbackMap = datos.getCallbackMap();
		callbackMap.remove(identificadorCallback, jugador);
		datos.setCallbackMap(callbackMap);
	}
	// Envia un evento al callback
	@Override
	public void autenticar(int idCallback, int idJugador) throws RemoteException {
		CallbackJugadorInterface jugador = datos.getCallbackMap().get(idCallback);
		// Se lanza el menu de autenticacion
		if (idJugador < 0) {
			jugador.recibirEvento("autenticar");
		}
		if (idJugador >= 0) // El usuario se ha logeado
			jugador.recibirEvento("menuPrincipal");
	}
	

	// Otorgar un identificador al callback
	@Override
	public int getIdToConect() throws RemoteException {
		return datos.getCallbacks().size();
	}
	
	// Registrar un usuario en la base de datos
	public boolean registro(String nombre, String passw) throws RemoteException {
		// Comprobamos si existe el nombre
		int existe = datos.getJugadoresRegistrados().indexOf(nombre);
		if (existe == -1) { // No existe
			// Se actaliza la lista de registrados
			ArrayList<String> jugadoresRegistrados = datos.getJugadoresRegistrados();
			jugadoresRegistrados.add(nombre);
			datos.setJugadoresRegistrados(jugadoresRegistrados);
			
			// Se actualiza el mapa de usuarios (nombre y contraseña)
			Map<String, String> usuarios = datos.getUsuarios();
			usuarios.put(nombre, passw);
			datos.setUsuarios(usuarios);

			return true; // El resgitro ha sido exitoso
		}
		return false; // El nombre esta cogido
	}

	// Meter usuario en la lista de jugadores conectados
	// devuelve el id del jugador
	// si no esta registrado se devuelve un -1
	public int acceso(String nombre, String password) throws RemoteException {
		int ident = getID(nombre, password);
		int identCallback = getIdToConect()-1;
		if (ident >= 0) { // El usuario esta registrado
			
			// Actualiza los jugadores conectados de la base de datos
			ArrayList<String> jugadoresConectados = datos.getJugadoresConectados();
			jugadoresConectados.add(nombre);
			datos.setJugadoresConectados(jugadoresConectados);
			// Actualiza el mapa del nombre del jugador con su objeto callback
			CallbackJugadorInterface jugador = datos.getCallbacks().get(identCallback);
			
			// Actualizamos el mapa de callbacks de la base de datos
			Map<Integer, CallbackJugadorInterface> jugadorCallback = datos.getJugadorCallback();
			jugadorCallback.put(ident, jugador);
			datos.setJugadorCallback(jugadorCallback);

			System.out.println(nombre + " se ha conectado" + ", su id es: " + ident);
		}
		return ident;
	}

	/*
	 * Para desconectar: - Quitamos el nombre de la lista de jugadores conectados de
	 * la base de datos - Quitamos el jugador de la lista de clientes - Quitamos la
	 * conexion en jugadorCallback
	 */
	

	/*
	 * Metodo para encontrar el ID de un nombre de usuario Devuelve un ID del 0 en
	 * adelante Si no encuentra el nombre devuelve -1 Si la contraseña no coincide
	 * devuelve -2
	 */
	@Override
	public int getID(String nombre, String passw) throws RemoteException {
		ArrayList<String> jugadoresRegistrados = datos.getJugadoresRegistrados();
		Map<String, String> usuarios = datos.getUsuarios();
		// Si la lista es vacia devolvemos un 0
		if (jugadoresRegistrados.isEmpty())
			return -1;
		// Buscamos en la lista jugadores registrados el nombre
		for (int i = 0; i < jugadoresRegistrados.size(); i++) {
			String actual = jugadoresRegistrados.get(i);
			// Si coincide el nombre comprobamos la passw
			if (actual.equals(nombre)) {
				if (usuarios.get(nombre).equals(passw))
					return i;
				else // La password no coincide
					return -2;
			}
		}
		return -1; // No ha coincidido ningun nombre
	}
}
