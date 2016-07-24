@define('int -> Integer')
@define('double -> Real')
@define('boolean -> Boolean')
@define('string -> String')

function @echo('name')(@foreach('arg <- args')@echo('arg.name'): @echo('arg.type')@if('${_isLastElement_} == false'); #end#end) : @echo('returnType');
var @foreach('var <- vars') @echo('var.name'): @echo('var.type')@noIf('${_isLastElement_} == false'),@else; #end#end
begin
    @foreach('var <- vars')
    @if('?var.value')
    @noIf('"${var.type}" == "string"')
    @echo('var.name') := "@echo('var.value')";
    @else
    @echo('var.name') := @echo('var.value');
    #end
    #end
    #end
    @echo('body')
end;