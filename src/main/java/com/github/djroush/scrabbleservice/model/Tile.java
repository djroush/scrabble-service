package com.github.djroush.scrabbleservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFormat(shape = Shape.STRING)
public enum Tile {
	A(1), B(3), C(3), D(2), E(1), F(4), G(2),
	H(4), I(1), J(8), K(5), L(1), M(3), N(1),
	O(4), P(3), Q(10),R(1), S(1), T(1), U(1),
	V(4), W(4), X(8), Y(4), Z(10), BLANK(0);
	
	private int value;
	
	private Tile(int value) {
		this.value = value; 
	}
	@JsonIgnore
	public int getValue() {
		return value;
	}
	public String getName() {
		return name();
	};
	
	public static Tile from(char letter) {
		switch (letter) {
		case 'A': return Tile.A;
		case 'B': return Tile.B;
		case 'C': return Tile.C;
		case 'D': return Tile.D;
		case 'E': return Tile.E;
		case 'F': return Tile.F;
		case 'G': return Tile.G;
		case 'H': return Tile.H;
		case 'I': return Tile.I;
		case 'J': return Tile.J;
		case 'K': return Tile.K;
		case 'L': return Tile.L;
		case 'M': return Tile.M;
		case 'N': return Tile.N;
		case 'O': return Tile.O;
		case 'P': return Tile.P;
		case 'Q': return Tile.Q;
		case 'R': return Tile.R;
		case 'S': return Tile.S;
		case 'T': return Tile.T;
		case 'U': return Tile.U;
		case 'V': return Tile.V;
		case 'W': return Tile.W;
		case 'X': return Tile.X;
		case 'Y': return Tile.Y;
		case 'Z': return Tile.Z;
		default: return Tile.BLANK;
		}
	}
}
