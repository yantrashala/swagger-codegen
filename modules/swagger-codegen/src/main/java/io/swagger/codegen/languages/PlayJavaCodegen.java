package io.swagger.codegen.languages;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.codegen.*;

public class PlayJavaCodegen extends AbstractJavaCodegen {
	

    public static final String TITLE = "title";
    public static final String PLAY_JAVA_LIBRARY = "play-java";
	public static final String DEFAULT_LIBRARY = PLAY_JAVA_LIBRARY;
    public static final String PLAY_SCALA_LIBRARY = "play-scala";

	public PlayJavaCodegen() {
		super();
		
		outputFolder = "generated-code/playjava";
        apiTestTemplateFiles.clear();
        embeddedTemplateDir = templateDir = "Play";
        apiPackage = "io.swagger.controllers";
        modelPackage = "io.swagger.models";
        invokerPackage = "io.swagger.api";
        artifactId = "swagger-playjava";
        
        projectFolder = "app";
        sourceFolder = projectFolder;
        
        
        cliOptions.add(new CliOption(TITLE, "server title name"));
        
        supportedLibraries.put(DEFAULT_LIBRARY, "Play Server application with controllers/models in Java");
        supportedLibraries.put(PLAY_SCALA_LIBRARY, "Play Server application with controllers/models in Scala");     
        setLibrary(DEFAULT_LIBRARY);

        CliOption library = new CliOption(CodegenConstants.LIBRARY, "library template (sub-template) to use");
        library.setDefault(DEFAULT_LIBRARY);
        library.setEnum(supportedLibraries);
        cliOptions.add(library);
        
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        supportingFiles.add(new SupportingFile("build.sbt", "", "build.sbt"));
        supportingFiles.add(new SupportingFile("project/build.properties", "project", "build.properties"));
        supportingFiles.add(new SupportingFile("project/plugins.sbt", "project", "plugins.sbt"));
        supportingFiles.add(new SupportingFile("project/eclipse.sbt", "project", "eclipse.sbt"));
        supportingFiles.add(new SupportingFile("sbt", "", "sbt"));
        supportingFiles.add(new SupportingFile("conf/routes", "conf", "routes"));
        supportingFiles.add(new SupportingFile("conf/application.conf", "conf", "application.conf"));
        supportingFiles.add(new SupportingFile("conf/swagger.routes.mustache", "conf", "swagger.routes"));
        
        apiTemplateFiles.put("api.mustache", "Controller.java");
        // Will use swagger UI for docs in place of Markdown
        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");
        
	}

	@Override
	public CodegenType getTag() {
		return CodegenType.SERVER;
	}

	@Override
	public String getName() {
		return "play-java";
	}

	@Override
	public String getHelp() {
		return "Generates a play2.5 server application with Java";
	}
	
    @Override
    public void processOpts() {
        super.processOpts();              
    }
    
    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);

        if ("null".equals(property.example)) {
            property.example = null;
        }

        //Add imports for Jackson
        if (!Boolean.TRUE.equals(model.isEnum)) {
            model.imports.add("JsonProperty");

            if (Boolean.TRUE.equals(model.hasEnums)) {
                model.imports.add("JsonValue");
            }
        } else { // enum class
            //Needed imports for Jackson's JsonCreator
            if (additionalProperties.containsKey("jackson")) {
                model.imports.add("JsonCreator");
            }
        }
    }

    @Override
    public Map<String, Object> postProcessModelsEnum(Map<String, Object> objs) {
        objs = super.postProcessModelsEnum(objs);

        //Add imports for Jackson
        List<Map<String, String>> imports = (List<Map<String, String>>)objs.get("imports");
        List<Object> models = (List<Object>) objs.get("models");
        for (Object _mo : models) {
            Map<String, Object> mo = (Map<String, Object>) _mo;
            CodegenModel cm = (CodegenModel) mo.get("model");
            // for enum model
            if (Boolean.TRUE.equals(cm.isEnum) && cm.allowableValues != null) {
                cm.imports.add(importMapping.get("JsonValue"));
                Map<String, String> item = new HashMap<String, String>();
                item.put("import", importMapping.get("JsonValue"));
                imports.add(item);
            }
        }

        return objs;
    }
    
    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
    	
    	Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        if (operations != null) {
            List<CodegenOperation> ops = (List<CodegenOperation>) operations.get("operation");
            for (CodegenOperation operation : ops) {
                List<CodegenResponse> responses = operation.responses;
                if (responses != null) {
                    for (CodegenResponse resp : responses) {
                        if ("0".equals(resp.code)) {
                            resp.code = "200";
                        }
                    }
                }

                if (operation.returnType == null) {
                    operation.returnType = "Void";
                } else if (operation.returnType.startsWith("List")) {
                    String rt = operation.returnType;
                    int end = rt.lastIndexOf(">");
                    if (end > 0) {
                        operation.returnType = rt.substring("List<".length(), end).trim();
                        operation.returnContainer = "List";
                    }
                } else if (operation.returnType.startsWith("Map")) {
                    String rt = operation.returnType;
                    int end = rt.lastIndexOf(">");
                    if (end > 0) {
                        operation.returnType = rt.substring("Map<".length(), end).split(",")[1].trim();
                        operation.returnContainer = "Map";
                    }
                } else if (operation.returnType.startsWith("Set")) {
                    String rt = operation.returnType;
                    int end = rt.lastIndexOf(">");
                    if (end > 0) {
                        operation.returnType = rt.substring("Set<".length(), end).trim();
                        operation.returnContainer = "Set";
                    }
                }
            }
        }
    	
    	
    	return objs;
    }

}
