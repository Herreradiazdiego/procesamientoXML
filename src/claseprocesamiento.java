import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class claseprocesamiento extends DefaultHandler {
    private static final String CLASS_NAME = claseprocesamiento.class.getName();
    private final static Logger LOG = Logger.getLogger(CLASS_NAME);

    private SAXParser parser = null;
    private SAXParserFactory spf;

    private double totalSales;
    private boolean inSales;


    private String currentElement;
    private String id;
    private String name;
    private String lastName;
    private String sales;
    private String state;
    private String dept;

    private String keyword;

    private HashMap<String, Double> estado1;
    private HashMap<String, Double> depa1;

    public claseprocesamiento() {
        super();
        spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);

        estado1 = new HashMap<>();
        depa1 = new HashMap<>();
    }

    private void process(File file) {
        try {
            parser = spf.newSAXParser();

        } catch (SAXException | ParserConfigurationException e) {
            LOG.severe(e.getMessage());
            System.exit(1);
        }
        System.out.println("\nStarting parsing of " + file + "\n");
        try {
            keyword = state;
            parser.parse(file, this);
        } catch (IOException | SAXException e) {
            LOG.severe(e.getMessage());
        }
    }

    @Override
    public void startDocument() throws SAXException {

        totalSales = 0.0;
    }

    @Override
    public void endDocument() throws SAXException {

        Set<Map.Entry<String, Double>> Estado = estado1.entrySet();
        System.out.println("--Ventas por Estado--");
        for (Map.Entry<String, Double> entry : Estado) {
            System.out.printf("%-15.15s $%,9.2f\n", entry.getKey(), entry.getValue());
        }
        Set<Map.Entry<String, Double>> Deps = depa1.entrySet();
        System.out.println("--Ventas por Departamento--");
        for(Map.Entry<String, Double> entry : Deps){
            System.out.printf("%-15.15s $%,9.2f\n", entry.getKey(), entry.getValue());
        }
        System.out.printf("total de ventas: $%,9.2f\n", totalSales);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (localName.equals("sale_record")) {
            inSales = true;
        }
        currentElement = localName;
    }

    @Override
    public void characters(char[] bytes, int start, int length) throws SAXException {

        switch (currentElement) {
            case "id":
                this.id = new String(bytes, start, length);
                break;
            case "first_name":
                this.name = new String(bytes, start, length);
                break;
            case "last_name":
                this.lastName = new String(bytes, start, length);
                break;
            case "sales":
                this.sales = new String(bytes, start, length);
                break;
            case "state":
                this.state = new String(bytes, start, length);
                break;
            case "department":
                this.dept = new String(bytes, start, length);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("sale_record")) {
            double vs = 0.0;
            try {
                vs = Double.parseDouble(this.sales);
            } catch (NumberFormatException e) {
                LOG.severe(e.getMessage());
            }
            //TODO separar todsas las aps para el estado y el departamento
            if (estado1.containsKey(this.state)) {
                double sum = estado1.get(this.state);
                estado1.put(this.state, sum + vs);
            } else {
                estado1.put(this.state, vs);
            }
            if (depa1.containsKey(this.dept)) {
                double sum = depa1.get(this.dept);
                depa1.put(this.dept, sum + vs);
            } else {
                depa1.put(this.dept, vs);
            }
            totalSales = totalSales + vs;
            inSales = false;
        }
    }

    private void printRecord() {
        System.out.printf("%4.4s %-10.10s %-10.10s %9.9s %-10.10s %-15.15s\n",
                id, name, lastName, sales, state, dept);
    }


    public static void main(String args[]) {
        if (args.length == 0) {
            LOG.severe("No file to process. Usage is:" + "\njava DeptSalesReport <keyword>");
            return;
        }
        File xmlFile = new File(args[0]);
        claseprocesamiento handler = new claseprocesamiento();
        handler.process(xmlFile);
    }
}