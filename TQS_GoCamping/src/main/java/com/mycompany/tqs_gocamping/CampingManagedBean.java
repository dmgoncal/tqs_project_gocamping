package com.mycompany.tqs_gocamping;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Named(value = "CampingManagedBean")
@RequestScoped
public class CampingManagedBean {
    private final List<String> types = new ArrayList<>();
    List<Place> results = new ArrayList<>();
   
    private String destination=null;
    private String arrivalDate=null;
    private String leavingDate=null;
    private String type=null;
    private int people=0;
    
    private String user;
    private String password;
    private String email;
    
    private String selectedPlace;

    public CampingManagedBean() {
        types.add("Tenda");
        types.add("Tenda Média");
        types.add("Tenda Família");
        types.add("Casa");
        types.add("Casa Média");
        types.add("Casa Família");

    } 

    public void setDestination(String destination){
        this.destination=destination;
    }
    public void setArrivalDate(String arrivalDate){
        this.arrivalDate=arrivalDate;
    }
    public void setLeavingDate(String leavingDate){
        this.leavingDate=leavingDate;
    }
    public void setType(String type){
        this.type=type;
    }
    public void setPeople(int people){
        this.people=people;
    }
    public String getDestination(){
        return destination;
    }
    public String getArrivalDate(){
        return arrivalDate;
    }
    public String getLeavingDate(){
        return leavingDate;
    }
    public String getType(){
        return type;
    }
    public int getPeople(){
        return people;
    } 
    public List getTypes(){
        return types;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSelectedPlace() {
        return selectedPlace;
    }
    public void setSelectedPlace(String selectedPlace) {
        this.selectedPlace = selectedPlace;
    }
    public List<Place> getResults() {
        return results;
    }
    public void setResults(List<Place> results) {
        this.results = results;
    }
    
    public String moveToSearchPage() throws ParserConfigurationException, SAXException, IOException{
        setResults(getPlaces());
        return "searchPage";
    }
    
    public List<Place> getPlaces() throws ParserConfigurationException, SAXException, IOException {
        Client c = Client.create();
        String xmlRecords = "";
        if(destination!=null && type!=null){
            WebResource wr = c.resource("http://deti-tqs-08.ua.pt:8080/TQS_GoCamping/webresources/camping/full/"+destination+"/"+arrivalDate+"/"+leavingDate+"/"+type+"/"+people);
            xmlRecords = wr.get(String.class);
        }
        else if(destination!=null && type==null){
            WebResource wr = c.resource("http://deti-tqs-08.ua.pt:8080/TQS_GoCamping/webresources/camping/noType/"+destination+"/"+arrivalDate+"/"+leavingDate+"/"+people);
            xmlRecords = wr.get(String.class);
        }
        else if(destination==null && type!=null){
            WebResource wr = c.resource("http://deti-tqs-08.ua.pt:8080/TQS_GoCamping/webresources/camping/noDestination/"+arrivalDate+"/"+leavingDate+"/"+type+"/"+people);
            xmlRecords = wr.get(String.class);
        }
        else if(destination==null && type==null){
            WebResource wr = c.resource("http://deti-tqs-08.ua.pt:8080/TQS_GoCamping/webresources/camping/nothing/"+arrivalDate+"/"+leavingDate+"/"+people);
            xmlRecords = wr.get(String.class);
        }
        destination=null;
        type=null;
        arrivalDate=null;
        leavingDate=null;
        people=0;
        return getResults(xmlRecords);
    }
    
    public List<Place> getResults(String xmlRecords){
        List<Place> places = new ArrayList<>();
        try{
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);
            NodeList nList = doc.getElementsByTagName("place");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    Place p = new Place();
                    p.setId(Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent()));
                    p.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
                    
                    p.setCapacity(Integer.parseInt(eElement.getElementsByTagName("capacity").item(0).getTextContent()));
                    p.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());

                    NodeList insideList = nNode.getChildNodes();
                    Park pk = new Park();
                    for (int temp1 = 0; temp1 < insideList.getLength(); temp1++) {
                        Node childNode = insideList.item(temp1);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement1 = (Element) nNode;
                            pk.setAddress(eElement1.getElementsByTagName("address").item(0).getTextContent());
                            pk.setId(Integer.parseInt(eElement1.getElementsByTagName("id").item(0).getTextContent()));
                            pk.setName(eElement1.getElementsByTagName("name").item(0).getTextContent());
                            pk.setPic(eElement1.getElementsByTagName("pic").item(0).getTextContent());
                        }
                    }
                    p.setParkId(pk);
                    p.setPic(eElement.getElementsByTagName("pic").item(0).getTextContent());
                    p.setPrice(Float.parseFloat(eElement.getElementsByTagName("price").item(0).getTextContent()));
                    places.add(p);
                }
            }
        }catch(IOException | NumberFormatException | ParserConfigurationException | DOMException | SAXException e){
        }
        return places;
    } 
}

