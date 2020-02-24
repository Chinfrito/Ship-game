package es.uned.gberral2.comun;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallbackJugadorInterface extends Remote {
	
	public String getDato() throws RemoteException;

	public void imprimir(String objeto) throws RemoteException;
	
	public int menu(String nombre, String[] opciones) throws RemoteException;
	
	public boolean confirmar(String pregunta) throws RemoteException;
	
	public String getEvento() throws RemoteException;

	void recibirEvento(String evento) throws RemoteException;

	boolean hayEventos() throws RemoteException;

	public int getIDJugador() throws RemoteException;
	public int getIDCallback() throws RemoteException;

	void setIDJugador(int id) throws RemoteException;
	void setIDCallback(int id) throws RemoteException;

	
}
