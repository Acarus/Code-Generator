@noIf('"${direction}" == "inc"')
for (@echo('counter') = @echo('from'); @echo('counter') <= @echo('to'); @echo('counter')++) {
    @echo('?body')
}
@else
for (@echo('counter') = @echo('from'); @echo('counter') >= @echo('to'); @echo('counter')--) {
    @echo('?body')
}
#end
