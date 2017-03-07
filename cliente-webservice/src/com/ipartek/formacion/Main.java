package com.ipartek.formacion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.correos.oficinasbuzones.OficinasBuzones;
import es.correos.oficinasbuzones.OficinasBuzonesSoap;
import net.webservicex.CCChecker;
import net.webservicex.CCCheckerSoap;
import net.webservicex.BibleWebservice.BibleWebservice;
import net.webservicex.BibleWebservice.BibleWebserviceSoap;
import net.webservicex.LoanApr.FinanceService;
import net.webservicex.LoanApr.FinanceServiceSoap;
import net.webservicex.geoip.GeoIP;
import net.webservicex.geoip.GeoIPService;
import net.webservicex.geoip.GeoIPServiceSoap;

public class Main {

	public static void main(String[] args) {

		if(validarTarjetaCredito()){
			System.out.println("es valido");
		} else {
			System.out.println("No es valido");
		}
		GeoIP geoip = obtenerIp();
		System.out.println("La ip es : " +geoip.getIP());
		geoip = obtenerPais(geoip.getIP());
		System.out.println("El país es: " +geoip.getCountryName());
		
		List<Biblia> libros = getBiblia();
		for(int i = 0 ; i < libros.size(); i++){
			System.out.println("codigo " + libros.toString());
		}
		
		/*
		double resultadoARP = calcularAPR(500, 50, 2, 28);
		System.out.println("La cuota de ARP es :" + resultadoARP+"%");
		
		double resultado = calcularLMP (500, 50, 2, 28);
		System.out.println("La cuota LMP es :" + resultado);
		
		double resultadoOMP = calcularOMP (5000, 8, 12);
		System.out.println("La cuota OMP es :" + resultadoOMP+"€");
		
		double resultadoLNOP = calcularLNOP (500, 2, 28);
		System.out.println("La cuota LNOP es :" + resultadoLNOP);
		
		Map<Integer, Provincia> provincias = getProvincias();
		for(Map.Entry<Integer, Provincia> entry : provincias.entrySet()){
			Provincia provincia = entry.getValue();
			//Integer code = entry.getKey();
			System.out.println(provincia.toString());
		}
		*/
	}

	private static List<Biblia> getBiblia() {
		List<Biblia> libros = null;
		BibleWebservice libro = new BibleWebservice();
		BibleWebserviceSoap librosoap = libro.getBibleWebserviceSoap();
		String texto = librosoap.getBookTitles();
		
		libros = parseToListBiblia(texto);
		
		return libros;
	}

	private static List<Biblia> parseToListBiblia(String texto) {
		List<Biblia> libros = null;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try{
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(texto.getBytes())));
			int len = doc.getElementsByTagName("BookTitle").getLength();
			Biblia libro = new Biblia();
			libros = new ArrayList<Biblia>();
			for(int i = 0 ; i < len; i++){
				libro = new Biblia();
				Node lib = doc.getElementsByTagName("BookTitle").item(i);
				libro.setTitulo(lib.getFirstChild().getNodeValue());
				Node code = doc.getPreviousSibling().getPreviousSibling();
				libro.setCodigo(Integer.parseInt(code.getNodeValue()));
				libros.add(libro.getCodigo(), libro);
			}
			
		}catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return libros;
	}

	private static boolean validarTarjetaCredito() {
		CCChecker checker =  new CCChecker();
		CCCheckerSoap soapclient = checker.getCCCheckerSoap();
		String resultado = soapclient.validateCardNumber("VISA", "4145894219289521");
		boolean valid = false;
		if(resultado.contains("is valid")){
			valid = true;
		}
		return valid;
	}

	private static GeoIP obtenerIp(){
		GeoIP geoip = null;
		try{
		GeoIPService cliente = new GeoIPService();
		GeoIPServiceSoap clientesoap = cliente.getGeoIPServiceSoap();
		 geoip = clientesoap.getGeoIPContext();
		}catch (Exception e){
		
		}
		return geoip;
	}
	private static GeoIP obtenerPais(String ip){
		GeoIP geoip = null;
		try{
			GeoIPService cliente = new GeoIPService();
			GeoIPServiceSoap clientesoap = cliente.getGeoIPServiceSoap();
			geoip = clientesoap.getGeoIP(ip);
		}catch(Exception e){
			
		}
		return geoip;
	}
	
	//FinanceService////
	private static double calcularAPR(double loanAmount, double extraCost, double interestRate, double months){
		double resultado = 0;
		FinanceService calcular = new FinanceService();
		FinanceServiceSoap calcularsoap= calcular.getFinanceServiceSoap();
		resultado= calcularsoap.apr(loanAmount, extraCost, interestRate, months);
		return resultado;
	}
	
	private static double calcularLMP(double loanAmount, double residualValue, double interestRate, double months){
		double resultado = 0;
		FinanceService calcular = new FinanceService();
		FinanceServiceSoap calcularsoap= calcular.getFinanceServiceSoap();
		resultado= calcularsoap.leaseMonthlyPayment(loanAmount, residualValue, interestRate, months);
		return resultado;
	}
	
	private static double calcularOMP(double loanAmount, double interestRate, double months){
		double resultado = 0;
		FinanceService calcular = new FinanceService();
		FinanceServiceSoap calcularsoap= calcular.getFinanceServiceSoap();
		resultado= calcularsoap.loanMonthlyPayment(loanAmount, interestRate, months);
		return resultado;
	}
	
	private static double calcularLNOP(double loanAmount, double interestRate, double monthlyPayment){
		double resultado = 0;
		FinanceService calcular = new FinanceService();
		FinanceServiceSoap calcularsoap= calcular.getFinanceServiceSoap();
		resultado= calcularsoap.loanNumberOfPayment(loanAmount, interestRate, monthlyPayment);
		return resultado;
	}
	
	//correos////
	
	private static Map<Integer, Provincia> getProvincias(){
		Map<Integer, Provincia> provincias = null;
		OficinasBuzones cliente = new OficinasBuzones();
		OficinasBuzonesSoap clientesoap = cliente.getOficinasBuzonesSoap();
		String texto = clientesoap.consultaProvincias("");
		
		provincias = parseToMapProvincias(texto);
		
		return provincias;
	}

	private static Map<Integer, Provincia> parseToMapProvincias(String texto) {
		Map<Integer, Provincia> provincias = null;
		//esta clase es la que me permite trabajar con tipos de datos XML
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		Document doc = null;
		try{
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(texto.getBytes("Windows-1252"))));
			int len = doc.getElementsByTagName("provincia").getLength();
			Provincia provincia = null;
			provincias = new HashMap<Integer, Provincia>();
			for(int i = 0 ; i < len; i++){
				provincia = new Provincia();
				Node prov = doc.getElementsByTagName("provincia").item(i);
				provincia.setNombre(prov.getFirstChild().getNodeValue());
				NamedNodeMap attrs = prov.getAttributes();
				int codigo = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
				
				provincia.setCodigo(codigo);
				
				provincias.put(provincia.getCodigo(), provincia);
				
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return provincias;
	}
	
	
}
