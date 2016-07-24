@if('"${var.type}" == "int"')
@echo('var.name') = consoleReader.nextInt();
#end

@if('"${var.type}" == "string"')
@echo('var.name') = consoleReader.nextLine();
#end

@if('"${var.type}" == "double"')
@echo('var.name') = consoleReader.nextDouble();
#end
