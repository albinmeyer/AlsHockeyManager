/*
 * This file is part of Al's Hockey Manager
 * Copyright (C) 1998-2012 Albin Meyer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.hockman.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ch.hockman.util.IndentingXMLStreamWriter;

/**
 * A file where a game or league is persisted into.
 *
 * @author Albin
 *
 */
public class GameLeagueFile {
	private String currFileName;
	private XMLStreamWriter xmlWriter;
	private XMLStreamReader xmlReader;

	GameLeagueFile() {
		xmlReader = null;
		xmlWriter = null;
	}

	String getCurrFileName() {
		return currFileName;
	}

	void setCurrFileName(String fileName) {
		currFileName = fileName;
	}

	boolean openForXMLWrite() {
		assert (xmlReader == null && xmlWriter == null);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			xmlWriter = factory.createXMLStreamWriter(new FileOutputStream(
					currFileName), "UTF-8");
			xmlWriter = new IndentingXMLStreamWriter(xmlWriter);
			xmlWriter.writeStartDocument();
		} catch (FileNotFoundException e) {
			return false;
		} catch (XMLStreamException e) {
			return false;
		}
		return xmlWriter != null;
	}

	boolean openForXMLRead() {
		assert (xmlReader == null && xmlWriter == null);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		try {
			xmlReader = factory.createXMLStreamReader(new FileInputStream(
					currFileName), "UTF-8");
			if (xmlReader.hasNext()) {
				if (xmlReader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
					xmlReader.next();
				}
			}
		} catch (XMLStreamException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		}
		return xmlReader != null;
	}

	boolean opened() {
		return xmlWriter != null || xmlReader != null;
	}

	void close() {
		if (xmlWriter != null) {
			assert (xmlReader == null);
			try {
				xmlWriter.writeEndDocument();
				xmlWriter.close();
			} catch (XMLStreamException e) {
				throw new IllegalStateException(e);
			}
			xmlWriter = null;
		} else {
			assert (xmlReader != null);
			try {
				if (xmlReader.hasNext()) {
					if (xmlReader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
						xmlReader.close();
					}
				}
			} catch (XMLStreamException e) {
				throw new IllegalStateException(e);
			}
			xmlReader = null;
		}
	}

	public void parseSurroundingStartElement(String elementName) {
		try {
			while(xmlReader.hasNext() && xmlReader.getEventType() == XMLStreamConstants.COMMENT) {
				xmlReader.next();
			}
			if (xmlReader.hasNext()
					&& xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
				String locName = xmlReader.getLocalName();
				if (locName.equals(elementName)) {
					xmlReader.next();
					while(xmlReader.hasNext()
							&& xmlReader.getEventType() == XMLStreamConstants.COMMENT) {
						xmlReader.next();
					}
					while (xmlReader.hasNext()
							&& xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
						if (!xmlReader.isWhiteSpace()) {
							throw new IllegalStateException(
									"illegal xml data in element "
											+ elementName); // illegal xml file
						}
						xmlReader.next();
					}
					return;
				} else {
					throw new IllegalStateException(
							"Following xml element not found: " + elementName
									+ "\nBut found " + locName + "\n");
				}
			}
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

	public void parseSurroundingEndElement() {
		try {
			if (xmlReader.hasNext()
					&& xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
				xmlReader.next();
				while (xmlReader.hasNext()
						&& xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
					if (!xmlReader.isWhiteSpace()) {
						throw new IllegalStateException(
								"illegal xml end element"); // illegal xml file
					}
					xmlReader.next();
				}
				return;
			}
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);			
		}
	}

	boolean getBool(String elementName) {
		return getInt(elementName) > 0;
	}

	public int getInt(String elementName) {
		return Integer.parseInt(getString(elementName));
	}

	public String getString(String elementName) {
		String s = "";
		try {
			if (xmlReader.hasNext()
					&& xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
				String locName = xmlReader.getLocalName();
				if (locName.equals(elementName)) {
					xmlReader.next();
					if (xmlReader.hasNext()
							&& xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
						if (!xmlReader.isWhiteSpace()) {
							s = xmlReader.getText();
							xmlReader.next();
							while (xmlReader.hasNext()
									&& xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
								s += xmlReader.getText();
								xmlReader.next();
							}
							if (xmlReader.hasNext()
									&& xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
								xmlReader.next();
								while(xmlReader.hasNext()
										&& xmlReader.getEventType() == XMLStreamConstants.COMMENT) {
									xmlReader.next();
								}
								while (xmlReader.hasNext()
										&& xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
									if (!xmlReader.isWhiteSpace()) {
										throw new IllegalStateException(
												"Illegal XML child");
									}
									xmlReader.next();
								}
							}
						} else {
							throw new IllegalStateException(
									"whitespace found inside child element");
						}
					} else {
						if (xmlReader.hasNext()
								&& xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT) {
							xmlReader.next();
							while (xmlReader.hasNext()
									&& xmlReader.getEventType() == XMLStreamConstants.CHARACTERS) {
								if (!xmlReader.isWhiteSpace()) {
									throw new IllegalStateException("Illegal XML child");
								}
								xmlReader.next();
							}
						}
					}
				} else {
					throw new IllegalStateException(
							"Following xml element not found: " + elementName
									+ "\nBut found " + locName + "\n");
				}
			} else {
				throw new IllegalStateException("No start element found for: "
						+ elementName + "\nBut found: " + xmlReader.getText());
			}
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
		return s;
	}

	public void writeSurroundingStartElement(String elementName) {
		try {
			xmlWriter.writeStartElement(elementName);
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}

	}

	public void writeSurroundingEndElement() {
		try {
			xmlWriter.writeEndElement();
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

	public void write(String elementName, String s) {
		write(elementName, s, null);
	}
	
	public void write(String elementName, String s, String comment) {
		try {
			xmlWriter.writeStartElement(elementName);
			xmlWriter.writeCharacters(s);
			xmlWriter.writeEndElement();
			if(comment != null) {
				xmlWriter.writeComment(comment);
			}
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

	public void write(String elementName, int i) {
		write(elementName, i, null);
	}
	
	public void write(String elementName, int i, String comment) {
		write(elementName, Integer.toString(i), comment);
	}

	public void write(String elementName, boolean b) {
		write(elementName, b, null);
	}
	
	public void write(String elementName, boolean b, String comment) {
		write(elementName, b ? 1 : 0, comment);
	}

}
