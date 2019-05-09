
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

class GeocodingException extends Exception {
}

public class Geocoding {
 private String prefix = "https://www.geocode.farm/v3/xml/reverse/?";
 private String url;
 private String filename;
 private boolean saved = false;
 private boolean parsed = false;
 private double latitude;
 private double longitude;
 
 //mie
 private String via;
 private String comune;
 private String provincia;
 private String regione;
 private String CAP;
 private String nazione;
 
 public Geocoding(String address, String filename) {
  URL server;
  HttpsURLConnection service;
  BufferedReader input;
  BufferedWriter output;
  String line;
  int status;
  
  this.filename = filename;
  try {
  // url = prefix + URLEncoder.encode(address, "UTF-8"); // costruzione dello URL di interrogazione del servizio
	  
	url = prefix +address;	 // costruzione dello URL di interrogazione del servizio
							//COSTRUIRLO SENZA ENCODING ALTRIMENTI NON ACCETTA IL SIMBOLO "=" CODIFICATO
   server = new URL(url);
   service = (HttpsURLConnection)server.openConnection();
   service.setRequestProperty("Host", "geocode.farm"); // impostazione header richiesta: host interrogato
   service.setRequestProperty("Accept", "application/xml"); // impostazione header richiesta: formato risposta (XML)
   service.setRequestProperty("Accept-Charset", "UTF-8"); // impostazione header richiesta: codifica risposta (UTF-8)
   service.setRequestMethod("GET"); // impostazione metodo di richiesta GET
   service.setDoInput(true); // attivazione ricezione
   service.connect(); // connessione al servizio
   status = service.getResponseCode(); // verifica stato risposta
   if (status != 200) {
    return; // errore
   }
   // apertura stream di ricezione da risorsa web 
   input = new BufferedReader(new InputStreamReader(service.getInputStream(), "UTF-8"));
   // apertura stream per scrittura su file
   output = new BufferedWriter(new FileWriter(filename));
   // ciclo di lettura da web e scrittura su file
   while ((line = input.readLine()) != null) {
    output.write(line); output.newLine();
   }
   input.close(); output.close();
   saved = true;
  }
  catch (IOException e) {}
 }
 
 public boolean isSaved() {
     return saved;
 }
 
 public boolean isParsed() {
     return parsed;
 }
 
  private void parseXML() throws GeocodingException {
        if (!saved) {
            throw new GeocodingException();
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filename);
            Element root = document.getDocumentElement();
            NodeList list = root.getElementsByTagName("status");
            if (list != null && list.getLength() > 0) {
                if (list.item(0).getFirstChild().getNodeValue().equalsIgnoreCase("SUCCESS")) {
                    list = root.getElementsByTagName("ADDRESS");
                    if (list != null && list.getLength() > 0) {
                        Element indirizzo = (Element)list.item(0);
                        NodeList street_name = indirizzo.getElementsByTagName("street_name");
                        via = street_name.item(0).getFirstChild().getNodeValue();
                        NodeList locality = indirizzo.getElementsByTagName("locality");
                        comune =locality.item(0).getFirstChild().getNodeValue();
                        NodeList admin_2 = indirizzo.getElementsByTagName("admin_2");
                        provincia =admin_2.item(0).getFirstChild().getNodeValue();
                        NodeList admin_1 = indirizzo.getElementsByTagName("admin_1");
                        regione =admin_1.item(0).getFirstChild().getNodeValue();
                        NodeList postal_code = indirizzo.getElementsByTagName("postal_code");
                        CAP =postal_code.item(0).getFirstChild().getNodeValue();
                        NodeList country = indirizzo.getElementsByTagName("country");
                        nazione =country.item(0).getFirstChild().getNodeValue();
                        parsed = true;
                    }
                }
            }
        }
        catch (IOException e) {
            throw new GeocodingException();
        }
        catch (ParserConfigurationException e) {
            throw new GeocodingException();
        }
        catch (SAXException e) {
            throw new GeocodingException();
        }
    }
  
  public String getComune() throws GeocodingException
  {
	  if (!saved) {
          throw new GeocodingException();
      }
      if (!parsed)
          parseXML();
      return comune;
  }
  
  public String getVia() throws GeocodingException
  {
	  if (!saved) {
          throw new GeocodingException();
      }
      if (!parsed)
          parseXML();
      return via;
  }
  
  public String getProvincia() throws GeocodingException
  {
	  if (!saved) {
          throw new GeocodingException();
      }
      if (!parsed)
          parseXML();
      return provincia;
  }
  
  public String getRegione() throws GeocodingException
  {
	  if (!saved) {
          throw new GeocodingException();
      }
      if (!parsed)
          parseXML();
      return regione;
  }
  
  public String getCAP() throws GeocodingException
  {
	  if (!saved) {
          throw new GeocodingException();
      }
      if (!parsed)
          parseXML();
      return CAP;
  }
  
  public String getNazione() throws GeocodingException
  {
	  if (!saved) {
          throw new GeocodingException();
      }
      if (!parsed)
          parseXML();
      return nazione;
  }
  
  
    
 
 /*
 public static void main(String[] args) {
  if (args.length < 2)
      System.err.println("Errore argomenti!");
  else {
    Geocoding geocoding = new Geocoding(args[0], args[1]);
    if (geocoding.isSaved())
        System.out.println("File XML salvato.");
    else
        System.err.println("Errore interrogazione servizio!");
  }
 }
 */
 
public static void main(String args[]) {
        Geocoding Bienno = new Geocoding("lat=45.9377526345932&lon=10.2921686375628", "geocoding.xml");
        try {
            System.out.println("(" + Bienno.getVia() + ";" + Bienno.getComune() +";"+Bienno.getProvincia()+
            		";"+ Bienno.getRegione()+";"+Bienno.getCAP()+";"+Bienno.getNazione()+")");
        }
        catch (GeocodingException e) {
            System.err.println("Errore interrogazione servizio!");
        }
    }
}
