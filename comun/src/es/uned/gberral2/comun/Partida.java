package es.uned.gberral2.comun;

import java.io.Serializable;

public class Partida implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Añadido automaticamente por eclipse
	 */
	private static final int NUMERO_BARCOS = 1; // Cambiar para el test final
	private static final int TAMAÑO_BARCO = 3;
	private static final int TAMAÑO_TABLERO = 10;
	int[][] tablero1;
	int[][] tablero2;
	int player1;
	int player2;
	int ganador;
	/* Estado de la partida:
	 * - Esperando si falta contrincante
	 * - Lista si faltan colocar los barcos
	 * - Empezada si estan disparandose por turnos
	 * - Terminada si alguien ha ganado
	 */
	
	enum Estado {ESPERANDO, LISTA, TURNO_PLAYER1, TURNO_PLAYER2, TERMINADA};
	Estado estado;
	
	public Partida(int player1) {
		System.out.println("Creando partida");
		tablero1 = new int[TAMAÑO_TABLERO][TAMAÑO_TABLERO];
		tablero2 = new int[TAMAÑO_TABLERO][TAMAÑO_TABLERO];
		this.player1 = player1;
		player2 = -1;
		ganador = -1;
		estado = Estado.ESPERANDO;
	}
	
   /* Para colocar los barcos se pediran 3 datos
	* x: 1-10 ordenada 
	* y: a-j abcisa
	* z: h, v direccion
	*/
	public void colocarBarco(String coordenadas, String z, int id) {
		for (int i = 0; i <= NUMERO_BARCOS; i++) {
			if (isPlayer1(id)) {
				colocarBarco(coordenadas, z, tablero1);
			} else if (isPlayer2(id)) {
				colocarBarco(coordenadas, z, tablero2);
			} else {
				System.out.println("Jugador no reconocido");
			}
		}
	}
	public boolean colocarBarco(String coordenadas, String z, int[][] tablero) {
		// Comprobamos que los numeros esten dentro de los limites
		int x = Utils.getX(coordenadas);
		int y = Utils.getY(coordenadas);
		if (x < tablero.length && y < tablero[0].length && tablero[x][y] == 0) {
			
			// Si la z es horizontal el barco irá hacia la izquierda
			if (z.equals("h")) {
				int limite = y + (TAMAÑO_BARCO - 1);
				// Comprobamos que el barco no se sale del tablero o si coincide con algun otro barco
				if (limite < tablero[0].length && tablero[x][limite] == 0 && tablero[x][limite - 1] == 0)
				{
					for (int i = y; i <= limite; i++) {
						tablero[x][i] = 1;
					}
					//System.out.println("Barco colocado desde: " + traducirCoordenadas(x,y) + 
						//			" hasta " + traducirCoordenadas(x,y+2));
					return true;
				}
				
			// Si la z es v el barco irá hacia abajo
			} else if (z.equals("v")) {
				int limite = x + (TAMAÑO_BARCO - 1);
				if (limite < tablero.length && tablero[limite][y] == 0 && tablero[limite - 1][y] == 0) {
					for (int i = x; i <= limite; i++) {
						tablero[i][y] = 1;
					}
					//System.out.println("Barco colocado desde: " + traducirCoordenadas(x,y) + 
						//	" hasta " + traducirCoordenadas(x+2,y));
					return true;
				}
			} 
		}
		// Si el programa llega hasta aqui quiere decir que alguna de las variables es erronea
		return false;
	}
	
	public boolean disparo(int id, String coordenadas) {
		if(isPlayer1(id))
			return disparo(coordenadas, tablero2);
		if(isPlayer2(id))
			return disparo(coordenadas, tablero1);
		return false;
	}
	public boolean disparo(String coordenadas, int[][] tablero) {
		// Primero traducimos las coordenadas
		int x = Utils.getX(coordenadas);
		int y = Utils.getY(coordenadas);
		// Segundo comprobamos si han impactado ( es decir si hay un 1)
		if (tablero[x][y] == 1) {
			tablero[x][y] = 0;
			return true;
		}
		return false;
	}
	
	public boolean isPlayer1(int id) {
		if ( this.player1 == id ){
			return true;
		}else {
			return false;
		}
	}
	public boolean isPlayer2(int id) {
		if ( this.player2 == id ){
			return true;
		}else {
			return false;
		}
	}
	
	//Devuelve true si el tablero esta vacio
	public boolean isEmpty(int[][] tablero) {
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero.length; j++) {
				if (tablero[i][j]== 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Si alguno de los tableros esta vacio
	// rellena el campo ganador y devuelve true
	public boolean isTerminada() {
		if (isEmpty(tablero1)) {
			ganador = player2;
			estado = Estado.TERMINADA;
			return true;
		}else if (isEmpty(tablero2)) {
			ganador = player1;
			estado = Estado.TERMINADA;
			return true;
		}else {
			return false;
		}
	}
	
	// Comprueba que estan los barcos colocados
	// Si esta todo listo 
	// Cambia el estado a Turno del jugador 1
	// y devuelve true
	public boolean empezar() {
		if (estanBarcosColocados(player1) && estanBarcosColocados(player2)) {
			estado = Estado.TURNO_PLAYER1;
			return true;
		}
		return false;
	}
	
	// Comprueba que estan puestos todos los barcos 
	// Para ello comprueba que hay el numero de unos exacto en el tablero
	public boolean estanBarcosColocados(int id) {
		int total = NUMERO_BARCOS * TAMAÑO_BARCO;
		int contador = 0;
		if ( isPlayer1(id) ){
			for (int i = 0; i < tablero1.length; i++) {
				for (int j = 0; j < tablero1[i].length; j++) {
					if(tablero1[i][j] == 1)
						contador ++;
				}
			}
			return (contador == total);
		}else if (isPlayer2(id)) {
			for (int i = 0; i < tablero1.length; i++) {
				for (int j = 0; j < tablero1[i].length; j++) {
					if(tablero1[i][j] == 1)
						contador ++;
				}
			}
			return (contador == total);
		}
		return false;
	}
	
	public int getPlayer1() {
		return player1;
	}
	public void setPlayer1(int player1) {
		this.player1 = player1;
	}
	public int getPlayer2() {
		return player2;
	}
	public void setPlayer2(int player2) {
		this.player2 = player2;
	}
	
	public int getGanador() {
		return this.ganador;
	}
	public void setGanador(int ganador) {
		this.ganador = ganador;
	}
	public String getEstado() {
		switch (this.estado) {
		case LISTA:
			return "lista";
		case TURNO_PLAYER1:
			return "turno_player1";
		case TURNO_PLAYER2:
			return "turno_player2";
		case TERMINADA:
			return "terminada";
		default:
			return "esperando";
		}
	}
	public boolean turno(int id) {
		if(isPlayer1(id) && estado == Estado.TURNO_PLAYER1)
			return true;
		if(isPlayer2(id) && estado == Estado.TURNO_PLAYER2)
			return true;
		
		return false;
	}
	public void terminar() {
		this.estado = Estado.TERMINADA;		
	}

	public void setLista() {
		this.estado = Estado.LISTA;
	}

	public String toString() {
		String s = "Esperando " + player1;
		if (player2 != -1) {
			s = player1 + " VS " + player2;
		}
		if (ganador != -1) {
			s = s + " -- Ganador: " + ganador;
		}
		return s;
	}
	public String tableroJugador(int id) {
		if(isPlayer1(id)) 
			return tableroToString(tablero1);
		if(isPlayer2(id))
			return tableroToString(tablero2);
		return null;
	}
	public String tableroToString(int[][] tablero) {
		String tabla = "";
		for (int i = 0; i < tablero.length; i++) {
			for (int j = 0; j < tablero[i].length; j++) {
				tabla = tabla + "|" + tablero[i][j];
			}
			tabla = tabla + "\n";
		}
		return tabla;
	}

	public void pasarTurno(int id) {
		if(isPlayer1(id))
			estado = Estado.TURNO_PLAYER2;
		if(isPlayer2(id))
			estado = Estado.TURNO_PLAYER1;
	}
}
