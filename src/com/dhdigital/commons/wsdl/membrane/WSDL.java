package com.dhdigital.commons.wsdl.membrane;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.predic8.schema.Import;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;

/**
 * 
 * All methods pertaining to WSDL are to be mentioned here.
 * 
 * @author joshi
 *
 */
public class WSDL {

	private static Logger logger = LoggerFactory.getLogger(WSDL.class);

	/**
	 * 
	 * This method would basically create new Definitions for provided schemaMap and
	 * also operations provided in Config object. This method would not check the
	 * existence of WSDL and simply creates a new WSDL.
	 * 
	 * A WSDL creation would be started by creating Schema and importing all defined
	 * Schemas into this schema. Once they are imported, it would then go ahead with
	 * part creation and associated operations.
	 * 
	 * @param schemaMap
	 * @param config
	 * @return
	 */
	private Definitions createNewWSDL(Map<String, Schema> schemaMap, WSDLConfiguration config) {
		Definitions wsdl = null;
		List<Import> imports = null;
		Import schemaImport = null;
		Schema localSchema = null;
		PortType portType = null;
		com.dhdigital.commons.wsdl.membrane.Operation op = null;
		try {
			logger.debug("Creating WSDL with wsdlName {} and namespace as {}", config.getWsdlName(),
					config.getTargetNamespace());
			wsdl = new Definitions(config.getTargetNamespace(), config.getWsdlName());

			{
				logger.debug("Creating Schema for current WSDL first ");
				localSchema = new Schema();
				localSchema.setTargetNamespace(config.getTargetNamespace());
				imports = new ArrayList<Import>();
				Set<String> schemas = schemaMap.keySet();
				for (String schema : schemas) {
					// Importing the schemas present
					logger.debug("Importing schema to the wsdl created {}", schema);
					schemaImport = new Import();
					schemaImport.setSchemaLocation(config.getXsds().get(schema));
					schemaImport.setNamespace(schemaMap.get(schema).getTargetNamespace());
					imports.add(schemaImport);
					localSchema.setImports(imports);
				}
				logger.debug("Schemas added for current WSDL");
			}
			wsdl.addSchema(localSchema);

			{
				logger.debug("Creating a Port type and creating a new operation");
				portType = wsdl.newPortType(config.getWsdlName());
				logger.debug("Creating operations for WSDL");
				op = new com.dhdigital.commons.wsdl.membrane.Operation();
				portType = op.getOperations(portType, config, schemaMap);
			}
		} catch (Exception e) {
			logger.error("Exception while generating WSDL using Membrane API with details {}", e);
			throw e;
		}
		return wsdl;
	}

	/**
	 * 
	 * This method's basic responsibility is to identify if WSDL has to be created
	 * or it is already provided. If the WSDL is not available (folderName +
	 * wsdlName) then it would be simple creation of new WSDL which would call above
	 * method there by performing the operation of creating a new WSDL. The
	 * complication would come if the WSDL is already existing, one should identify
	 * if part has to be modified or created or operation has to be modified or
	 * created. This method would then interact with Operations, Part, Schema part
	 * of the package and then provide updated definitions along with operations
	 * added if any.
	 * 
	 * @param schemaMap
	 * @param config
	 * @return
	 */
	public Definitions generateWSDLUsingMembrane(Map<String, Schema> schemaMap, WSDLConfiguration config) {
		Definitions wsdl = null;
		List<Import> imports = null;
		List<Schema> wsdlSchemas = null;
		Import schemaImport = null;
		PortType portType = null;
		com.dhdigital.commons.wsdl.membrane.Operation op = null;
		try {
			logger.debug("Creating/Modifying WSDL with wsdlName {} and namespace as {}", config.getWsdlName(),
					config.getTargetNamespace());
			wsdl = getWsdlFromConfig(config);
			if (wsdl == null) {
				wsdl = createNewWSDL(schemaMap, config);
			} else {
				{
					logger.debug("Trying to modify Schema information in the WSDL");

					wsdlSchemas = wsdl.getSchemas();
					imports = wsdlSchemas.get(0).getImports();
					Set<String> schemas = config.getXsds().keySet();
					for (String schema : schemas) {
						// Importing the schemas present
						logger.debug("Trying to check if schema is already available {}", schema);
						List<Schema> availableSchemas = wsdlSchemas.stream()
								.filter(sc -> config.getXsds().get(schema).equals(sc.getSchemaLocation()))
								.collect(Collectors.toList());
						if (null == availableSchemas || availableSchemas.size() == 0) {
							schemaImport = new Import();
							schemaImport.setSchemaLocation(config.getXsds().get(schema));
							schemaImport.setNamespace(schemaMap.get(schema).getTargetNamespace());
							imports.add(schemaImport);
						}
					}
					logger.debug("Schemas added for current WSDL");
				}

				logger.debug("Getting the port type from the existing WSDL");
				portType = wsdl.getPortType(config.getWsdlName());
				op = new com.dhdigital.commons.wsdl.membrane.Operation();
				portType = op.getOperations(portType, config, schemaMap);
			}

		} catch (Exception e) {
			logger.error("Exception while generating WSDL using Membrane API with details {}", e);
			throw e;
		}
		return wsdl;
	}

	/**
	 * It would check if the WSDL is already available using the details provided in
	 * config. If a WSDL is present, the WSDL would then be parsed accordingly, on
	 * successful parsing it would then be capable of being updated. If WSDL parsing
	 * is failed because of invalidity of the WSDL, then it would be treated as a
	 * new creation and the existing wSDL would be overwritten. Hence once must be
	 * very careful which creating configuration of this WSDL.
	 * 
	 * @param config
	 * @return
	 */
	public Definitions getWsdlFromConfig(WSDLConfiguration config) {
		Definitions wsdl = null;
		String fullWsdlPath = config.getDefaultFolderLocationForWSDLs() + File.separatorChar + config.getWsdlName()
				+ ".wsdl";
		File wsdlFile = new File(fullWsdlPath);
		try {
			if (wsdlFile.exists()) {
				WSDLParser parser = new WSDLParser();
				wsdl = parser.parse(fullWsdlPath);
				logger.debug("Validating the WSDL and the WSDL provided is valid.");
			}
		} catch (Exception e) {
			logger.error("Exception while parsing the WSDL. Hence application will proceed with overwriting the WSDL");
		}
		return wsdl;
	}

	/**
	 * This method would write WSDL to a particular folder location provided in the
	 * config. All XSDs, WSDL must definitely be available in the same folder
	 * location. Division of folder location currently is not possible and may lead
	 * to invalidity of WSDL and XSDs.
	 * 
	 * @param wsdl
	 * @param config
	 * @return
	 * @throws IOException
	 */
	public String writeWSDLtoFile(Definitions wsdl, WSDLConfiguration config) throws IOException {
		String wsdlPath = null;
		logger.debug("Trying to write file to a file system");
		BufferedWriter writer = null;
		try {
			wsdlPath = config.defaultFolderLocationForWSDLs + File.separatorChar + config.getWsdlName() + ".wsdl";
			File file = new File(wsdlPath);
			if (!file.exists())
				file.createNewFile();
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(wsdl.getAsString());

		} catch (IOException e) {
			logger.error("IO Exception while writing WSDL to file with details {}", e);
			throw e;
		} catch (Exception e) {
			logger.error("Exception while writing WSDL to a file with details {}", e);
			throw e;
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
		return wsdlPath;
	}

}
