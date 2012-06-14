package org.eclipse.tapiji.rap.translator.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name = "Translation")
public class Translation implements Serializable {

	private static final long serialVersionUID = 2033276999496196690L;

	public String id;
	
	public String value;
	
	public Translation () {
		id = "";
		value = "";
	}
	
}
