@define('int -> Integer')
@define('double -> Real')
@define('boolean -> Boolean')
@define('string -> String')

program @echo('programName');
var @foreach('var <- _global_variables') @echo('var.name'): @echo('var.type')@noIf('${_isLastElement_} == false'), @else;
#end #end
@foreach('?function <- _functions_')
@echo('function.code')
#end

begin
@foreach('var <- _global_variables') @if('?var.value') @noIf('"${var.type}" == "string"') @echo('var.name') := "@echo('var.value')"; @else @echo('var.name') := @echo('var.value'); #end #end #end
@echo('?body')
end.