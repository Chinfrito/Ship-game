package es.uned.gberral2.comun;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioAutenticacionInterface extends Remote {
	
	public int getIdToConect() throws RemoteException;
	// Registrar el callback sin usuario
	public void conexion(int id)  throws RemoteException, NotBoundException;
	// Registrar al usuario
	public boolean registro(String nombre, String password) throws RemoteException;
	public int getID(String nombre, String password) throws RemoteException;
	public int acceso(String nombre, String passw) throws RemoteException;
	public void desconexion(int identificadorCallback, int idJugador) throws RemoteException;
	public void autenticar(int idCallback, int idJugador) throws RemoteException;
}
