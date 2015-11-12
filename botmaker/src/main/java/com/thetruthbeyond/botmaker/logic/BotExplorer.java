/*
 * Copyleft (C) 2015 Piotr Siatkowski find me on Facebook;
 * This file is part of BotMaker. BotMaker is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. BotMaker is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with BotMaker (look at the
 * Documents directory); if not, either write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, or visit
 * (http://www.gnu.org/licenses/gpl.txt).
 */

package com.thetruthbeyond.botmaker.logic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import  com.thetruthbeyond.chatterbean.parser.api.AliceBotExplorerException;
import  com.thetruthbeyond.chatterbean.parser.api.Explorer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.Context;
import  com.thetruthbeyond.chatterbean.aiml.AIMLXmlException;
import  com.thetruthbeyond.chatterbean.aiml.Aiml;
import  com.thetruthbeyond.chatterbean.aiml.Category;
import  com.thetruthbeyond.chatterbean.aiml.Topic;
import  com.thetruthbeyond.chatterbean.text.structures.Sentence;
import static java.nio.file.StandardCopyOption.*;

@SuppressWarnings("OverlyBroadCatchBlock")
public class BotExplorer implements Explorer {

	private static final String BOTS_DIRECTORY = "Bots";
	private static final String EXAMPLE_DIRECTORY = "Structure";
		
	private final Properties configuration = new Properties();
	private String name;

	public BotExplorer(String name) {
		try {
			this.name = name;

			if(Gdx.files.internal(BOTS_DIRECTORY + "/" + name).exists())
				configuration.loadFromXML(getConfiguration());
		} catch(InvalidPropertiesFormatException exception) {
			new Logger().writeMessage("Error: Program cannot read configuration file. Wrong format.", exception.getMessage());
		} catch(IOException exception) {
			new Logger().writeMessage("Error: Program cannot read jar entries.", exception.getMessage());
		} catch(AliceBotExplorerException exception) {
			new Logger().writeMessage("Error: Program cannot read configuration file.", exception.getMessage());
		}
	}

	@SuppressWarnings("ReturnOfCollectionOrArrayField")
	public Properties getConfigurationMap() {
		return configuration;
	}

	@Override
	public InputStream getConfiguration() throws AliceBotExplorerException {
		try {
			File directory = new File("." + File.separator + BOTS_DIRECTORY + File.separator + name);
			if(!directory.exists())
				return null;

			return new FileInputStream(directory.getPath() + File.separator + "configuration.xml");
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving configuration. The file was not found.", exception);
		}
	}

	public void setConfigurationMap(Properties properties) {
		// Checking directory.
		File directory = new File("." + File.separator + BOTS_DIRECTORY + File.separator + name);
		if(!directory.exists())
			return;
						
		File file = new File(directory.getPath() + File.separator + "configuration.xml");
			
		try {
			properties.storeToXML(new FileOutputStream(file.getPath()), "Chatterbot configuration file");
		} catch(InvalidPropertiesFormatException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Properties wrong format has been detected.");
			logger.writeError(exception);
		} catch(FileNotFoundException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Properties file could not be found.");
			logger.writeError(exception);
		} catch(IOException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined IOException occured.");
			logger.writeError(exception);
		}
	}

	public void createBotStructure() {
		try {
			Files.createDirectory(Paths.get(Gdx.files.internal(BOTS_DIRECTORY + "/" + name).path()));
			createNecessaryFiles();
			configuration.loadFromXML(getConfiguration());
		} catch(IOException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined IOException occured.");
			logger.writeError(exception);
		} catch(AliceBotExplorerException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Parser exception occured when trying to create bot structure.");
			logger.writeError(exception);
		}
	}
	
	public void deleteBotStructure() {
		try {
			FileHandle root = Gdx.files.internal(BOTS_DIRECTORY + "/" + name);

			// Recursive delete.
			deleteFileRecursive(root);
		} catch(IOException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined IOException occured when deleting file structure.");
			logger.writeError(exception);
		}
	}

	private void deleteFileRecursive(FileHandle handle) throws IOException {
		if(handle.isDirectory())
			for(FileHandle subhandle : handle.list())
				deleteFileRecursive(subhandle);
		Files.delete(Paths.get(handle.path()));
	}

	private void createNecessaryFiles() {
		try {
			FileHandle directory = Gdx.files.internal(EXAMPLE_DIRECTORY);			
			for(FileHandle handle : directory.list()) {
				Files.copy(Paths.get(handle.path()), Paths.get(BOTS_DIRECTORY + "/" + name + "/" + handle.name()), REPLACE_EXISTING);
				// Recursive copy.
				if(handle.isDirectory())
					for(FileHandle subhandle : handle.list())
						Files.copy(Paths.get(subhandle.path()), Paths.get(BOTS_DIRECTORY + "/" + name + "/" + handle.name() + "/" + subhandle.name()), REPLACE_EXISTING);
			}
		} catch(IOException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined IOException occured when creating structure's files.");
			logger.writeError(exception);
		}
	}

	public void updatePropertiesFile(Context context) {
		try {
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element root = document.createElement("context");
			
			String indent = "  ";
			Set<String> properties = context.getPropertiesNames();
			
			// Setting the rest of remaining properties and comments.
			root.appendChild(document.createTextNode("\n" + indent));
			root.appendChild(document.createComment("CurrentBot predicates are set at load time, and cannot be changed at runtime."));
			for(String property : properties) {
				Element element = document.createElement("bot");
				element.setAttribute("name", property);
				element.setAttribute("value", context.getProperty(property));
				
				root.appendChild(document.createTextNode("\n" + indent));
				root.appendChild(element);				
			}
			
			root.appendChild(document.createTextNode("\n"));
			document.appendChild(root);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			
			StreamResult result = new StreamResult(getPropertiesFile());
			transformer.transform(source, result);
			
			if(properties.contains("name"))
				updateDirectoryName(context.getProperty("name"));
		} catch (Exception exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined exception occured when updating property file.");
			logger.writeError(exception);
		}
	}
	
	private void updateDirectoryName(String newName) {
		FileHandle directory = Gdx.files.internal(BOTS_DIRECTORY + "/" + name);

		boolean success = false;
		if(directory.isDirectory())
			success = directory.file().renameTo( Gdx.files.internal(BOTS_DIRECTORY + "/" + newName).file() );

		if(success)
			name = newName;
	}

	@Override
	public void updatePredicatesFile(Context context) {
		try {
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element root = document.createElement("context");
			
			Set<String> predicates = context.getPredicatesNames();
			
			String indent = "  ";
			
			root.appendChild(document.createTextNode("\n" + indent));
			root.appendChild(document.createComment("Default values for predicates, can be changed later at runtime."));
			
			for(String predicate : predicates) {
				Element element = document.createElement("set");
				element.setAttribute("name", predicate);
				element.setAttribute("value", context.getPredicate(predicate));
				
				root.appendChild(document.createTextNode("\n" + indent));
				root.appendChild(element);
			}
			
			root.appendChild(document.createTextNode("\n"));
			document.appendChild(root);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			
			// Write predicates to file connected with the current user.
			Sentence user = context.getSetting(AliceBot.USER);
			
			StreamResult result;
			if(user.isEmpty() || user.equals(Sentence.ASTERISK))
				result = new StreamResult(new File(BOTS_DIRECTORY + File.separator + name + File.separator + 
												   configuration.getProperty("predicates") + "Default_predicates.xml"));
			else {
				String userName = user.getOriginal();
				result = new StreamResult(getPredicatesFile(userName));
			}
			
			transformer.transform(source, result);
		} catch(Exception exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined exception occured whe updating predicates file.");
			logger.writeError(exception);
		}
	}
	
	public void addToAIML(String topic, Category category) {
		if(configuration == null)
			return;
		try {
			File aiml = topic.equals("") ? getAIMLFile("Default") : getAIMLFile(topic);
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document;
	    	
			if(aiml.exists()) {	
				document = builder.parse(aiml);
				addToExisitingAIML(document, category, topic);
			} else {
				if(!aiml.createNewFile())
					throw new Exception("Cannot create new aiml file named: " + topic + ".aiml");
				
				document = builder.newDocument();
				
				addToNewAIML(document, category, topic);
			}
				
			printAIML(document, aiml);
		} catch(AIMLXmlException exception) {
			System.out.print(exception.getMessage());
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void addToExisitingAIML(Document document, Category category, String topic) {
		int indentLevel = 1;
		
		Node rootElement;
		if(topic.equals(""))
			rootElement = document.getElementsByTagName("aiml").item(0);
		else {
			rootElement = document.getElementsByTagName("topic").item(0);
			indentLevel = 2;
		}
	
		// Eliminating line break at the end of document.
		rootElement.removeChild(rootElement.getLastChild());
		
		// Creating category element.		
		Node categoryElement = category.getNode(document, rootElement, indentLevel);
									
		// Appending category.
		rootElement.appendChild(categoryElement);
		rootElement.appendChild(document.createTextNode("\n"));
	}
	
	public void addToNewAIML(Document document, Category category, String topic) {
		// Creating new topic tag.
		Topic topicTag = new Topic(topic);
		topicTag.appendChild(category);
		
		// Creating root tag with new category and topic.
		Aiml aimlTag = new Aiml(topicTag);
		
		document.appendChild(aimlTag.getNode(document, null, 0));
	}
	
	public void updateAIML(String topic, Category category) {
		if(configuration == null)
			return;
		try {
			File aiml;
			if(topic.equals(""))
				aiml = getAIMLFile("Default");
			else
				aiml = getAIMLFile(topic);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(aiml);
			
	    	int indentLevel = 1;
	    	
	    	Node rootElement;
			if(topic.equals(""))
				rootElement = document.getElementsByTagName("aiml").item(0);
			else {
				rootElement = document.getElementsByTagName("topic").item(0);
				indentLevel = 2;
			}
			
			NodeList patterns = document.getElementsByTagName("pattern");
			
			int size = patterns.getLength();
			String currentPattern = category.getPattern().toString();
			
			
			for(int i = 0; i != size; i++) {
				Node node = patterns.item(i);
				if(node.getTextContent().equals(currentPattern)) {
					
					// Replacing old category.
					rootElement.replaceChild(category.getNode(document, rootElement, indentLevel), node.getParentNode());
					
					// Eliminating line break at the end of document.
					rootElement.removeChild(rootElement.getLastChild());
					break;
				}
			}
			
			printAIML(document, aiml);
		} catch(Exception exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined exception occured when updating one of AIML files.");
			logger.writeError(exception);
		}
	}
	
	public void printAIML(Document document, File aiml) {
		// Writing out to file.
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
						
			StreamResult result = new StreamResult(aiml);
			transformer.transform(source, result);
		} catch(TransformerConfigurationException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined TransformerConfigurationException occured when creating AIML file.");
			logger.writeError(exception);
		} catch(TransformerException exception) {
			Logger logger = new Logger();
			logger.writeMessage("Error", "Undefined TransformerException occured when creating AIML file.");
			logger.writeError(exception);
		}
	}
	
	public File getConfigurationFile() throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		try {
			File directory = new File(BOTS_DIRECTORY + File.separator + name);
			if(!directory.exists())
				return null;
							
			return new File(directory.getPath() + File.separator + "configuration.xml");
		
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "properties" + ": " + configuration.getProperty("properties"), e);
		}
	}

	@Override
	public InputStream getProperties() throws AliceBotExplorerException {
		if(configuration == null)
			return null;

		try {
			return new FileInputStream(getPropertiesFile());
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "properties" + ": " + configuration.getProperty("properties"), e);
		}
	}

	public File getPropertiesFile() {
		if(configuration == null)
			return null;

		return new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("properties"));
	}

	@Override
	public InputStream getPredicates() throws AliceBotExplorerException {
		if(configuration == null)
			return null;

		try {
			return new FileInputStream(getPredicatesFile());
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving " + "predicates" + ": " + configuration.getProperty("predicates") + "Temporary_predicates.xml or Default_predicates.xml", exception);
		}
	}

	public File getPredicatesFile() {
		if(configuration == null)
			return null;
		
		File file = new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("predicates") + "Temporary_predicates.xml");
		if(file.exists())
			return file;
		else
			return new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("predicates") + "Default_predicates.xml");
	}

	@Override
	public InputStream getPredicates(String userName) throws AliceBotExplorerException {
		if(configuration == null)
			return null;

		try {
			return new FileInputStream(getPredicatesFile(userName));
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving " + "predicates" + ": " + configuration.getProperty("predicates") + userName + ".xml", exception);
		}
	}

	public File getPredicatesFile(String userName) {
		if(configuration == null)
			return null;

		return new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("predicates") + userName + ".xml");
	}

	@Override
	public InputStream getSplitters() throws AliceBotExplorerException {
		if(configuration == null)
			return null;

		try {
			return new FileInputStream(getSplittersFile());
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving " + "splitters" + ": " + configuration.getProperty("splitters"), exception);
		}
	}

	public File getSplittersFile() {
		if(configuration == null)
			return null;

		return new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("splitters"));
	}

	@Override
	public InputStream getSubstitutions() throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		try {
			return new FileInputStream(getSubstitutionsFile());
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving " + "substitutions" + ": " + configuration.getProperty("substitutions"), exception);
		}
	}

	public File getSubstitutionsFile() {
		if(configuration == null)
			return null;

		return new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("substitutions"));
	}

	@Override
	public InputStream getAIML(String topic) throws AliceBotExplorerException {
		if(configuration == null)
			return null;

		try {
			return new FileInputStream(getAIMLFile(topic));
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving " + "categories" + ": " + configuration.getProperty("categories"), exception);
		}
	}

	public File getAIMLFile(String topic) throws AliceBotExplorerException {
		File root = new File(BOTS_DIRECTORY + File.separator + name + File.separator + configuration.getProperty("categories"));
		if(root.isDirectory()) {
			for(String name : root.list()) {
				if(name.equals(topic + ".aiml"))
					return  new File(root + File.separator + name);
			}
			
			return new File(root + File.separator + topic + ".aiml");
		} else {
			throw new AliceBotExplorerException("Error while retrieving " + "categories" + ": " + configuration.getProperty("categories"));
		}
	}

	@Override
	public InputStream[] getAIMLS() throws AliceBotExplorerException {
		List<File> aimls = getAIMLFiles();

		try {
			InputStream[] streams = new InputStream[aimls.size()];
			for(int i = 0, n = aimls.size(); i != n; ++i)
				streams[i] = new FileInputStream(aimls.get(i));
			return streams;
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving " + "categories" + ": " + configuration.getProperty("categories"), exception);
		}
	}

	public List<File> getAIMLFiles() throws AliceBotExplorerException {
		if(configuration == null)
			return new LinkedList<>();

		File root = new File(BOTS_DIRECTORY + File.separator + name + File.separator
				+ configuration.getProperty("categories"));
		if(root.isDirectory()) {
			List<File> files = new LinkedList<>();
			for(String name : root.list())
				files.add(new File(root + File.separator + name));
			return files;
		} else {
			throw new AliceBotExplorerException("Error while retrieving " + "categories" + ": " + configuration.getProperty("categories"));
		}
	}
}
