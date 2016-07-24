@define('boolean -> bool')
@define('string -> std::string')
@define('Integer -> int')

#include <iostream>

@foreach('var <- _global_variables')
@echo('var.type') @echo('var.name') @if('?var.value') = @noIf('"${var.type}" == "string"') "@echo('var.value')" @else @echo('var.value')#end #end ;
#end

@foreach('?function <- _functions_')
@echo('function.code')
#end

int main(void) {
    @echo('?body')
    return 0;
}
