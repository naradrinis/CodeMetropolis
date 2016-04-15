package codemetropolis.toolchain.commons.cdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import codemetropolis.toolchain.commons.util.PrintableXmlElement;

public class CdfElement extends PrintableXmlElement {

	private static final String SOURCE_ID_KEY = "source_id";
	
	private String name;
	private String type;
	private List<CdfProperty> properties;
	
	public CdfElement() {
		this(null, null);
	}
	
	public CdfElement(String name, String type) {
		this.name = name;
		this.type = type;
		properties = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<CdfProperty> getProperties() {
		return new ArrayList<>(properties);
	}
	
	public String getPropertyValue(String name){
		CdfProperty property = getProperty(name);
		if(property == null) return null;
		return property.getValue();
	}
	
	public CdfProperty getProperty(String name){
		for(CdfProperty property : properties) {
			if(name.equals(property.getName()))
			{
				return property;
			}
		}
		return null;
	}
	
	public void addProperty(String name, String value, CdfProperty.Type type) {
		properties.add(new CdfProperty(name, value, type));
	}
	
	public String getSourceId() {
		return getPropertyValue(SOURCE_ID_KEY);
	}
	
	public void setSourceId(String id) {
		addProperty(SOURCE_ID_KEY, id, CdfProperty.Type.STRING);
	}
	
	@SuppressWarnings("unchecked")
	public List<CdfElement> getChildElements() {
		return Collections.unmodifiableList((List<CdfElement>)(List<?>)childElements);
	}
	
	public void addChildElement(CdfElement child) {
		childElements.add(child);
	}
	
	public void removeChildElement(CdfElement child) {
		childElements.remove(child);
	}

	public int getNumberOfChildren() {
		return childElements.size();
	}
	
	public List<CdfElement> getDescendants() {

		List<CdfElement> result = new ArrayList<CdfElement>();
		Stack<CdfElement> temp = new Stack<CdfElement>();
		temp.push(this);
		while(!temp.isEmpty()) {
			CdfElement current = temp.pop();
			if(current.getNumberOfChildren() > 0) {
				for(CdfElement child : current.getChildElements()) {
					result.add(child);
					temp.push(child);
				}
			}
		}
		return result;
	}
	
	public Element toXmlElement(Document doc) {
		Element element = doc.createElement("element");
		element.setAttribute("name", name);
		element.setAttribute("type", type.toString().toLowerCase());		
		Element children = doc.createElement("children");
		element.appendChild(children);
		for(CdfElement c : getChildElements()) {
			children.appendChild(c.toXmlElement(doc));
		}
		
		Element propertiesElement = doc.createElement("properties");
		element.appendChild(propertiesElement);		
		for(CdfProperty prop : this.properties) {
			Element attr = doc.createElement("property");
			attr.setAttribute("type", prop.getType().name().toLowerCase());
			attr.setAttribute("name", prop.getName());
			attr.setAttribute("value", prop.getValue());
			propertiesElement.appendChild(attr);
		}
		
		return element;
	}
	
	public void toXml(XMLStreamWriter writer) {
		try {
			writer.writeStartElement("element");
			writer.writeAttribute("name", name);
			writer.writeAttribute("type", type.toString().toLowerCase());
			
			writer.writeStartElement("children");
			for(CdfElement child : getChildElements()) {
				child.toXml(writer);
			}
			writer.writeCharacters(lightWeightContent);
			writer.writeEndElement();
			
			writer.writeStartElement("properties");
			for(CdfProperty prop : this.properties) {
				writer.writeStartElement("property");
				writer.writeAttribute("name", prop.getName());
				writer.writeAttribute("value", prop.getValue());
				writer.writeAttribute("type", prop.getType().name().toLowerCase());
				writer.writeEndElement();
			}
			writer.writeEndElement();
			
			writer.writeEndElement();
			writer.flush();
		} catch (XMLStreamException e) {
			return;
		}
	}
	
}
