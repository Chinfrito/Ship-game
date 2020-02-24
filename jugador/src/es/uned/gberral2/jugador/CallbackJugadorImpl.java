package es.uned.gberral2.jugador;

import java.rmi.RemoteException;

import es.uned.gberral2.comun.CallbackJugadorInterface;
import es.uned.gberral2.comun.ColaEventos;
import es.uned.gberral2.comun.Utils;

/*
 * Tiene los metodos necesarios para recibir los 
 * mensajes (eventos) descritos e las otras clases 
 */
public class CallbackJugadorImpl implements CallbackJugadorInterface {

	/**
	 * 
	 */
	ColaEventos colaEventos;
	
	int idJugador;
	int idCallback;

	
	public CallbackJugadorImpl() {
		colaEventos = new ColaEventos();
	}
	@Override
	public synchronized void recibirEvento(String evento) {
		colaEventos.add(evento);
	}
	@Override
	public synchronized String getEvento() {
		return colaEventos.getEvento();
	}
	@Override
	public synchronized boolean hayEventos() {
		return (colaEventos.size() != 0);
	}
	
	@Override
	public int getIDJugador() throws RemoteException {
		return idJugador;
	}
	@Override
	public int getIDCallback() throws RemoteException{
		return idCallback;
	}
	
	@Override
	public void setIDJugador(int id) throws RemoteException {
		this.idJugador = id;
	}
	@Override
	public void setIDCallback(int id) throws RemoteException {
		this.idCallback =  id;
	}
	@Override
	public String getDato() throws RemoteException {
		return Utils.getDato();
	}
	@Override
	public void imprimir(String objeto) throws RemoteException {
		System.out.println(objeto);
	}
	@Override
	public int menu(String nombre, String[] opciones) throws RemoteException {
		return Utils.menu(nombre, opciones);
	}
	@Override
	public boolean confirmar(String pregunta) throws RemoteException {
		return Utils.confirmar(pregunta);
	}
	
}
