package es.uned.gberral2.comun;

import java.util.ArrayList;

public class ColaEventos{
	
	public ArrayList<String> cola;

	public ColaEventos(ArrayList<String> cola) {
		this.cola = cola;
	}
	public ColaEventos() {
		cola = new ArrayList<String>();
	}
	public int size() {
		return cola.size();
	}
	public void add(String s) {
		cola.add(s);
	}
	public String getEvento() {
		if (!cola.isEmpty()) {
			String s = cola.get(0);
			cola.remove(0);
			return s;
		}else
			return "";
	}
	public String toString() {
		return cola.toString();
	}
}
