@define('string -> String')
import java.util.Scanner;

public class @echo('programName') {

    @foreach('var <- _global_variables')
    public static @echo('var.type') @echo('var.name') @if('?var.value') = @noIf('"${var.type}" == "string"') "@echo('var.value')" @else @echo('var.value')#end #end;
    #end

    @foreach('?function <- _functions_')
    @echo('function.code')
    #end

    public static void main(String[] args) {
        Scanner consoleReader = new Scanner(System.in);
        @echo('?body')
    }
}
