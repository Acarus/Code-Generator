@noIf('"${direction}" == "inc"')
for @echo('counter') := @echo('from') to @echo('to') do
begin
    @echo('?body')
end;
@else
for @echo('counter') := @echo('from') downto @echo('to') do
begin
    @echo('?body')
end;
#end
