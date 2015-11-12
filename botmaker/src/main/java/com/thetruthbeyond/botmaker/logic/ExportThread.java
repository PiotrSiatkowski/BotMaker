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
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import  com.thetruthbeyond.chatterbean.utility.logging.Logger;
import  com.thetruthbeyond.chatterbean.parser.api.AliceBotExplorerException;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.thetruthbeyond.bot.CurrentBot;
import com.thetruthbeyond.botmaker.assets.AssetsLoader;
import com.thetruthbeyond.botmaker.files.IconWriter;
import com.thetruthbeyond.gui.interfaces.FileManager;
import com.thetruthbeyond.gui.utility.security.HashGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

public class ExportThread extends Thread {

	private static final String EXPORT_DIRECTORY = "Export";
	private static final String BOT_DIRECTORY = "Bots";

	private String errorMessage;
	private boolean errorOccured = false;

	private FileHandle portraitFile;

	private final String name;
	private final String password;
	private final String output;

	private final FileManager loader;
	private final BotExplorer explorer;

	public ExportThread(String name, String output, FileManager loader) {
		this.name = name;
		this.output = output;

		this.loader = loader;

		explorer = new BotExplorer(name);
		if(CurrentBot.instance != null)
			password = CurrentBot.instance.getPassword();
		else
			password = "";
	}
	
	@Override
	public void run() {
		errorOccured = !createIconFile();
		if(errorOccured)
			return;

		errorOccured = !createBotJar();
		if(errorOccured)
			return;

		errorOccured = !wrapJarToExe();
	}

	private boolean createIconFile() {
		try {
			FileFilter imageFilter = new FileNameExtensionFilter("Image files", "jpg", "png", "tif", "bmp");
			FileHandle botDirectory = Gdx.files.internal(BOT_DIRECTORY + "/" + name);

			portraitFile = loader.getAssetHandle("Portrait");
			for(FileHandle file : botDirectory.list()) {
				if(file.nameWithoutExtension().equals("Portrait") && imageFilter.accept(file.file())) {
					portraitFile = file;
					break;
				}
			}

			IconWriter writer = new IconWriter();
			writer.createIcon(portraitFile.file(), new File(EXPORT_DIRECTORY + File.separator + "Icon.ico"));

			// Creating 64x64 png icon.
			File icon = new File(EXPORT_DIRECTORY + File.separator + "Icon64.png");
			writer.createIcon64png(portraitFile.file(), icon);

			return true;
		} catch(FileNotFoundException exception) {
			new Logger().writeMessage("Error: portrait file couldn;t be found.", exception.getMessage());

			errorMessage = "CurrentBot icon cannot be created.";
			return false;
		} catch(IOException exception) {
			new Logger().writeMessage("Error: undefined IOException while generating an icon.", exception.getMessage());

			errorMessage = "CurrentBot icon cannot be created.";
			return false;
		}
	}

	private boolean createBotJar() {
		// Create temporary file for new bot.
		Path temporaryFilePath = Paths.get(EXPORT_DIRECTORY + File.separator + "TemporaryBot.jar");

		// Adding encrypted password before the content will be copied.
		Properties configuration = explorer.getConfigurationMap();

		try {
			HashGenerator generator = new HashGenerator();
			String encrypted = generator.generateHash(password);
			configuration.setProperty("password", encrypted);
			explorer.setConfigurationMap(configuration);
		} catch(NoSuchAlgorithmException ignored) {
			errorMessage = "SHA-256 algorithm cannot be found. Password cannot be generated.";
			return false;
		} catch(UnsupportedEncodingException ignored) {
			errorMessage = "UTF-16 coding cannot be established on this computer. Password cannot be generated.";
			return false;
		}

		// Copying bot files to temporary jar file.
		try(JarOutputStream temporaryStream = new JarOutputStream(new FileOutputStream(temporaryFilePath.toFile()))) {

			// Getting all needed files.
			List<InputStream> streams = new LinkedList<>();
			streams.add(explorer.getConfiguration());
			streams.add(explorer.getProperties());
			streams.add(explorer.getPredicates());
			streams.add(explorer.getSplitters());
			streams.add(explorer.getSubstitutions());

			InputStream[] aimlStreams = explorer.getAIMLS();
			streams.addAll(Arrays.asList(aimlStreams));

			List<File> files = new LinkedList<>();
			files.add(explorer.getConfigurationFile());
			files.add(explorer.getPropertiesFile());
			files.add(explorer.getPredicatesFile());
			files.add(explorer.getSplittersFile());
			files.add(explorer.getSubstitutionsFile());

			List<File> aimls = explorer.getAIMLFiles();
			files.addAll(aimls);

			// Added the new files to the jar.
			for(int i = 0, n = files.size(); i != n; ++i) {
				byte[] buffer = new byte[1024];
				try(InputStream stream = streams.get(i)) {
					int bytesRead;

					// Elimination of Bots/ prefix.
					String path = files.get(i).getPath();
					path = path.substring(path.indexOf('\\') + 1, path.length());

					// In jar files only "/" separators work properly.
					JarEntry entry = new JarEntry(path.replace("\\", "/"));
					temporaryStream.putNextEntry(entry);

					while((bytesRead = stream.read(buffer)) != -1)
						temporaryStream.write(buffer, 0, bytesRead);
				}
			}

			// Create list of aiml files to load then from client jar.
			JarEntry aimlsEntry = new JarEntry("Configuration/aimls.txt");
			temporaryStream.putNextEntry(aimlsEntry);

			byte[] bufferForAiml;
			for(int i = 0, size = aimls.size(); i != size; i++) {
				File file = aimls.get(i);
				if(i == size - 1)
					bufferForAiml = (file.getName()).getBytes(StandardCharsets.UTF_8);
				else
					bufferForAiml = (file.getName() + "\r\n").getBytes(StandardCharsets.UTF_8);
				temporaryStream.write(bufferForAiml, 0, bufferForAiml.length);
			}

			// Adding name.
			JarEntry nameEntry = new JarEntry("Configuration/botname.txt");
			temporaryStream.putNextEntry(nameEntry);
			byte[] bufferForName = name.getBytes(StandardCharsets.UTF_8);
			temporaryStream.write(bufferForName, 0, bufferForName.length);

			// Add portrait.
			try(FileInputStream stream = new FileInputStream(portraitFile.file())) {

				byte[] buffer = new byte[1024];
				int bytesRead;

				JarEntry entry = new JarEntry(AssetsLoader.ASSETS_FOLDER + "/" + portraitFile.name());
				temporaryStream.putNextEntry(entry);

				while((bytesRead = stream.read(buffer)) != -1)
					temporaryStream.write(buffer, 0, bytesRead);
			}

			// Add icon.
			File icon = new File(EXPORT_DIRECTORY + File.separator + "Icon64.png");
			try(FileInputStream stream = new FileInputStream(icon)) {

				byte[] buffer = new byte[1024];
				int bytesRead;

				JarEntry entry = new JarEntry(AssetsLoader.ASSETS_FOLDER + "/Icon64.png");
				temporaryStream.putNextEntry(entry);

				while((bytesRead = stream.read(buffer)) != -1)
					temporaryStream.write(buffer, 0, bytesRead);
			}

			try {
				JarFile botChat = new JarFile(EXPORT_DIRECTORY + File.separator + "BotChat.jar");

				// Copy original jar file to the temporary one.
				Enumeration<JarEntry> jarEntries = botChat.entries();
				while(jarEntries.hasMoreElements()) {
					JarEntry entry = jarEntries.nextElement();

					// Recalculate entry's compressed size in case it was modified.
					entry.setCompressedSize(-1);
					try(InputStream entryInputStream = botChat.getInputStream(entry)) {
						temporaryStream.putNextEntry(entry);

						byte[] buffer = new byte[1024];

						int bytesRead;
						while((bytesRead = entryInputStream.read(buffer)) != -1)
							temporaryStream.write(buffer, 0, bytesRead);
					}
				}
			} catch(IOException ignored) {
				errorMessage = "File BotChat.jar is missing or cannot be opened properly.";
				return false;
			}
		 } catch(FileNotFoundException ignored) {
			errorMessage = "Temporary bot jar couldn't be found or created.";
			return false;
		} catch(UnsupportedEncodingException ignored) {
			errorMessage = "UTF-16 coding cannot be established on this computer. CurrentBot name cannot be copied.";
			return false;
		} catch(IOException ignored) {
			errorMessage = "CurrentBot files cannot be found. They were propably delteted by mistake.";
			return false;
		} catch(AliceBotExplorerException ignored) {
			errorMessage = "CurrentBot couldn't be parsed. Configuration files cannot be read.";
			return false;
		} finally {
			// Writing configuration file with real password (not encrypted) back.
			configuration.setProperty("password", password);
			explorer.setConfigurationMap(configuration);
		 }
		
		return true;
	}
	
	@SuppressWarnings({"UseOfProcessBuilder", "OverlyBroadThrowsClause", "ProhibitedExceptionDeclared"})
	private boolean wrapJarToExe() {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			File configuration = new File("Wrapper/BotLaunch4j.xml");
			Document document = builder.parse(configuration);

			Node outfile = document.getElementsByTagName("outfile").item(0);
			outfile.setTextContent(output + "\\" + name + ".exe");

			Node jar = document.getElementsByTagName("jar").item(0);
			jar.setTextContent(Paths.get("").toAbsolutePath() + File.separator + EXPORT_DIRECTORY + File.separator + "TemporaryBot.jar");

			Node icon = document.getElementsByTagName("icon").item(0);
			icon.setTextContent(Paths.get("").toAbsolutePath() + File.separator + EXPORT_DIRECTORY + File.separator + "Icon.ico");

			Node originalName = document.getElementsByTagName("originalFilename").item(0);
			originalName.setTextContent(name + ".exe");

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			DOMSource source = new DOMSource(document);

			StreamResult result = new StreamResult(configuration);
			transformer.transform(source, result);

			new ProcessBuilder(new File("Wrapper/launch4jc").getPath(), "Wrapper/BotLaunch4j.xml").start();
		} catch(ParserConfigurationException ignored) {
			errorMessage = "Exe generator configuration file cannot be parsed.";
			return false;
		} catch(SAXException ignored) {
			errorMessage = "Exe generator configuration file cannot be parsed.";
			return false;
		} catch(TransformerConfigurationException ignored) {
			errorMessage = "Exe generator configuration file cannot be updated";
			return false;
		} catch(TransformerException ignored) {
			errorMessage = "Exe generator configuration file cannot be updated";
			return false;
		} catch(IOException ignored) {
			errorMessage = "Exe file cannot be created by Launch4j proccess.";
			return false;
		}

		return true;
	}

	public boolean hasErrorOccured() {
		return errorOccured;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
