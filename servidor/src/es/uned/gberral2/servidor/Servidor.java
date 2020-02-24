/* 
 * Guillermo Berral Candel
 * gberral2@alumno.unes.es
 */
package es.uned.gberral2.servidor;

import es.uned.gberral2.comun.ServicioGestorInterface;
import es.uned.gberral2.comun.ServicioAutenticacionInterface;
import es.uned.gberral2.servidor.ServicioAutenticacionImpl;
import es.uned.gberral2.servidor.ServicioGestorImpl;
import es.uned.gberral2.comun.Utils;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Servidor implements Remote  {

	public static void main(String[] args) throws RemoteException{		
		try {
			// 1. Localizamos el registro
			Registry registry = LocateRegistry.getRegistry(Utils.PUERTO);
			// 2. Asignamos las variables de los nombres
			String ip = Utils.ip;
			int rmiport = Utils.PUERTO;
			// Servicio Autenticacion (servAu)
			ServicioAutenticacionImpl servAu = new ServicioAutenticacionImpl();
			ServicioAutenticacionInterface stub = (ServicioAutenticacionInterface) UnicastRemoteObject.
					exportObject(servAu, 0);
			String URL_nombreServAu = 
					"rmi://"+ ip + ":" + rmiport + "/" + "ServicioAutenticacion";
			registry.rebind(URL_nombreServAu, stub);
			System.out.println("Servicio Autenticacion listo");
			
			// Servicio Gestor (servGe)
			ServicioGestorImpl servGe = new ServicioGestorImpl();
			ServicioGestorInterface stub1 = (ServicioGestorInterface) UnicastRemoteObject.
					exportObject(servGe, 0);
			String URL_nombreServGe = 
					"rmi://"+ ip + ":" + rmiport + "/" + "ServicioGestor";
			
			registry.rebind(URL_nombreServGe, stub1);
			System.out.println("Servicio Gestor listo");
			
			System.out.println("Servidor listo");
			
			// Menu principal
			Servidor servidor = new Servidor();
			servidor.menuPrincipal();
			
			// Cerramos el servidor
			System.out.println("Cerrando Servidor");
			registry.unbind(URL_nombreServAu);
			UnicastRemoteObject.unexportObject(servAu, true);
			
			registry.unbind(URL_nombreServGe);
			UnicastRemoteObject.unexportObject(servGe, true);
	
			
		} catch (Exception e) {
			System.err.println("Servidor exception: " + e.toString());
			e.printStackTrace();
		}
		
		
	}

	public void menuPrincipal() {
		String[] opciones = {"Informacion", "Lista de Partidas", "Salir"}; 
		boolean salir = false;
		while (salir == false) {
			int r = Utils.menu("Servidor", opciones);
			switch (r) {
			case 1:
				informacion();
				break;
			case 2:
				// Completar con la lista de partidas
				break;
			case 3:
				salir = true;
				break;
			default:
				System.out.println("Opcion invalida");
			}
		}
		
	}
	
	private void informacion() {
		System.out.println("Informacion del servidor");
		
	}
}
