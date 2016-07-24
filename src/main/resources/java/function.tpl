@define('string -> String')

public static @echo('returnType') @echo('name')(@foreach('arg <- args') @echo('arg.type') @echo('arg.name') @if('${_isLastElement_} != true') , #end #end) {
    @foreach('var <- vars')
    @echo('var.type') @echo('var.name') @if('?var.value') = @noIf('"${var.type}" == "string"') "@echo('var.value')" @else @echo('var.value')#end #end ;
    #end
    @echo('body')
}