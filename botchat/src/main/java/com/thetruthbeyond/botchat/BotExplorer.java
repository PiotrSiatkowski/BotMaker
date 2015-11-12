package com.thetruthbeyond.botchat;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import  com.thetruthbeyond.chatterbean.AliceBot;
import  com.thetruthbeyond.chatterbean.Context;
import  com.thetruthbeyond.chatterbean.parser.api.AliceBotExplorerException;
import  com.thetruthbeyond.chatterbean.parser.api.Explorer;
import  com.thetruthbeyond.chatterbean.text.structures.Sentence;
import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.thetruthbeyond.botchat.assets.AssetsLoader;
import com.thetruthbeyond.debug.CheckpointCounter;
import com.thetruthbeyond.gui.configuration.Consts;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class BotExplorer implements Explorer {

	private Properties configuration = new Properties();
	private String name;
	
	private Logger logger = new Logger();
	
	private JarFile me;
	
	public BotExplorer(String name) {
		this.name = name;
		
		try {
			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Executed initiation method of BotExplorer");

			String path = BotChat.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");

			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Path of this file was decoded as " + decodedPath);
			me = new JarFile(decodedPath);

			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Jar file has been properly read");

			configuration.loadFromXML(getConfiguration());

			if(Consts.DEV_MODE) {
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Bot configuration was read");
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Categories from:----" + configuration.getProperty("categories"));
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Predicates from:----" + configuration.getProperty("predicates"));
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Password from:------" + configuration.getProperty("password"));
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Properties from:----" + configuration.getProperty("properties"));
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Splitters from:-----" + configuration.getProperty("splitters"));
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Substitutions from:-" + configuration.getProperty("substitutions"));
			}
		} catch(UnsupportedEncodingException e) {
			logger.writeMessage("Error", "Program cannot create protection domain.");
		} catch(IOException e) {
			logger.writeMessage("Error", "Program cannot read jar entries.");
		} catch(AliceBotExplorerException e) {
			logger.writeMessage("Error", "Program cannot read configuration file.");
		}
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

			// Create necessary directories.
			File directory = new File(System.getenv("AppData") + File.separator + name + File.separator +
									  configuration.getProperty("predicates"));

			if(user.isEmpty() || user.equals(Sentence.ASTERISK)) {

				boolean success = false;
				if(!directory.exists())
					success = directory.mkdirs();

				if(success)
					result = new StreamResult();
			}
			else {
				String userName = user.getOriginal();
				result = new StreamResult(getPredicatesFile(userName));
			}

			transformer.transform(source, result);
		} catch (Exception exception) {
			logger.writeMessage("Error", "The application is not able to save predicates.");
			logger.writeError(exception);
		}
	}

	public File getPredicatesFile(String userName) {
		if(configuration == null)
			return null;

		return new File(System.getenv("AppData") + File.separator + name +
						File.separator + configuration.getProperty("predicates") + userName + ".xml");
	}

	public InputStream getConfiguration() throws AliceBotExplorerException {
		try {
			FileHandle handle = Gdx.files.classpath(name + "/configuration.xml");

			if(Consts.DEV_MODE)
				logger.writeMessage(CheckpointCounter.nextCheckpoint(), "Configuration path: " + handle.path());

			return me.getInputStream( new JarEntry(handle.path()) );
		} catch(FileNotFoundException exception) {
			throw new AliceBotExplorerException("Error while retrieving configuration. The file was not found.", exception);
		} catch(IOException exception) {
			throw new AliceBotExplorerException("Error while retrieving configuration file.", exception);
		}
	}

	public InputStream getProperties() throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		
		try {
			FileHandle handle = Gdx.files.classpath(name + "/" + configuration.getProperty("properties"));
			JarEntry entry = new JarEntry(handle.path());
			return me.getInputStream(entry);
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "properties" + ": " + configuration.getProperty("properties"), e);
		}
	}
	
	public InputStream getPredicates() throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		
		try {
			FileHandle handle = Gdx.files.classpath(name + File.separator + configuration.getProperty("predicates") + "Temporary_predicates.xml");
			if(!handle.exists())
				handle = Gdx.files.classpath(name + File.separator + configuration.getProperty("predicates") + "Default_predicates.xml");
			
			JarEntry entry = new JarEntry(handle.path());
			return me.getInputStream(entry);
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "predicates" + ": " + configuration.getProperty("predicates") + "Default.xml", e);
		}
	}
	
	public InputStream getPredicates(String userName) throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		
		try {
			FileHandle handle = Gdx.files.classpath(name + "/" + configuration.getProperty("predicates") + userName + ".xml");

			JarEntry entry = new JarEntry(handle.path());
			return me.getInputStream(entry);
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "predicates" + ": " + configuration.getProperty("predicates") + userName + ".xml", e);
		}
	}
	
	public InputStream getSplitters() throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		
		try {
			FileHandle handle = Gdx.files.classpath(name + "/" + configuration.getProperty("splitters"));
			JarEntry entry = new JarEntry(handle.path());
			return me.getInputStream(entry);
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "splitters" + ": " + configuration.getProperty("splitters"), e);
		}
	}
	
	public InputStream getSubstitutions() throws AliceBotExplorerException {
		if(configuration == null)
			return null;
		
		try {
			FileHandle handle = Gdx.files.classpath(name + "/" + configuration.getProperty("substitutions"));
			JarEntry entry = new JarEntry(handle.path());
			return me.getInputStream(entry);
		} catch (Exception e) {
			throw new AliceBotExplorerException("Error while retrieving " + "substitutions" + ": " + configuration.getProperty("substitutions"), e);
		}
	}

	public InputStream getAIML(String topic) throws AliceBotExplorerException {
		if(configuration == null)
			return null;

		try {
			FileHandle handle = Gdx.files.classpath(name + "/" + configuration.getProperty("categories") + File.separator + topic + ".aiml");
			JarEntry entry = new JarEntry(handle.path());
			return me.getInputStream(entry);
		} catch(Exception exception) {
			throw new AliceBotExplorerException("Error while retrieving AIML: " + topic, exception);
		}
	}
	
	public InputStream[] getAIMLS() throws AliceBotExplorerException {
		if(configuration == null)
			return new InputStream[0];

		ArrayList<InputStream> streams = new ArrayList<>();
		String root = name + "/" + configuration.getProperty("categories");
		
		BufferedReader reader;

		FileHandle aimlList = Gdx.files.classpath(AssetsLoader.CONFIG_DIRECTORY + "/aimls.txt");
		reader = new BufferedReader(aimlList.reader());

		String fileName = "";
		try {
			while((fileName = reader.readLine()) != null) {
				if(Consts.DEV_MODE)
					logger.writeMessage(CheckpointCounter.nextCheckpoint(), "AIML file: " + fileName);
				FileHandle handle = Gdx.files.classpath(root + "/" + fileName);
				streams.add(me.getInputStream(new JarEntry(handle.path())));
			}
		} catch(Exception exception) {
			throw new AliceBotExplorerException("Error while retrieving AIML: " + fileName, exception);
		}
		
		return streams.toArray( new InputStream[streams.size()] );
	}
	
	public void close() {
		try {
			if(me != null)
				me.close();
		} catch(IOException e) {
			logger.writeMessage("Warning", "Cannot close archive file.");
		}
	}
}
