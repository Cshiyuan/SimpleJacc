@echo off

javac -classpath lib/ -d bin/ -sourcepath src @sourcelist.txt

java -cp bin/ simplejacc.Main




@echo HELLO! CHENSHIYUAN!
pause