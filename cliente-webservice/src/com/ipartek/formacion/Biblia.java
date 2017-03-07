package com.ipartek.formacion;

import java.io.Serializable;

public class Biblia implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int codigo;
	private String titulo;
	
	public Biblia(){
		super();
		this.codigo=00;
		this.titulo="";
	}

	@Override
	public String toString() {
		return "Biblia [codigo=" + codigo + ", titulo=" + titulo + "]";
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	
	
	
}
