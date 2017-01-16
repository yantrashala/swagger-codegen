package io.swagger.codegen.languages;

import io.swagger.codegen.*;

public class PlayJavaCodegen extends AbstractJavaCodegen implements CodegenConfig {

	public PlayJavaCodegen() {
		super();
	}

	@Override
	public CodegenType getTag() {
		return CodegenType.SERVER;
	}

	@Override
	public String getName() {
		return "playjava";
	}

	@Override
	public String getHelp() {
		return "Generates a play2.5 server application with Java";
	}

}
