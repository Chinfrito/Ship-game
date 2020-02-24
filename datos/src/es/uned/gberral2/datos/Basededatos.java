/* 
 * Guillermo Berral Candel
 * gberral2@alumno.unes.es
 */

package es.uned.gberral2.datos;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import es.uned.gberral2.datos.ServicioDatosImpl;
import es.uned.gberral2.comun.ServicioDatosInterface;
import es.uned.gberral2.comun.Utils;

public class Basededatos {

	public static void main(String[] args) throws RemoteException {
		try {
			Basededatos datos = new Basededatos();

			Registry registry = LocateRegistry.createRegistry(Utils.PUERTO);

			ServicioDatosImpl servDatos = new ServicioDatosImpl();

			ServicioDatosInterface stub = (ServicioDatosInterface) UnicastRemoteObject.exportObject(servDatos, Utils.PUERTO);
			
			String ip = Utils.ip;
			int rmiport = Utils.PUERTO;
			String URL_nombreServDatos = 
					"rmi://"+ ip + ":" + rmiport + "/" + "ServicioDatos";
			
			registry.rebind(URL_nombreServDatos, stub);

			System.out.println("Base de datos y servicio de datos listos");
			
			datos.menuPrincipal(servDatos);
			
			//  Cerrar base de datos
			registry.unbind(URL_nombreServDatos);
			UnicastRemoteObject.unexportObject(servDatos, true);
			
		} catch (Exception e) {
			System.err.println("Base de datos exception: " + e.toString());
			e.printStackTrace();
		}
	}
	
	/* Interfaz de la base de datos:
	 * - Informacion
	 * - Lista jugadores: lista de jugadores registrados
	 * - Salir: cierra la base de datos 
	 */
	private void menuPrincipal (ServicioDatosImpl servDatos) throws RemoteException{
		boolean salir = false;
		String[] ops = {"Informacion","Lista de jugadores","Salir", "Lista partidas"};
		while (salir == false) {
			int r = Utils.menu("Base de Datos", ops);
			switch (r) {
			case 1:
				informacion();
				break;
			case 2:
				listaJugadoresReg(servDatos);
				break;
			case 3:
				salir();
				salir = true;
				break;
			case 4:
				System.out.println("Partidas en espera");
				Utils.listar(servDatos.listaPartidasEspera());
				System.out.println();
				System.out.println("Partidas listas");
				Utils.listar(servDatos.listaPartidasEmpezadas());
				System.out.println();
				System.out.println("Partidas terminadas");
				Utils.listar(servDatos.listaPartidasTerminadas());
				break;
			default:
				System.out.println("Opcion fuera de limites");;
			}
			System.out.println();
			System.out.println();
		
		}
	}
	// Imprime por pantalla la informacion de esta base de datos
	public void informacion() throws RemoteException {
		try {
			System.out.println("Informacion Base de datos");
		} catch (Exception e) {
			System.out.println("Base de datos metodo informacion() exception: " + e.toString());
			e.printStackTrace();
		}
	}
	// Imprime por pantalla la lista de jugadores registrados
	public void listaJugadoresReg(ServicioDatosInterface servDatos) throws RemoteException {
		System.out.println(servDatos.listaJugadoresRegistrados());

	}
	
	// Enseña un mensaje y continua el codigo del main para que se cierre la base de datos
	public void salir() throws RemoteException {
		System.out.println("Cerrando base de datos");
	}

}
