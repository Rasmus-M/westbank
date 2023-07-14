xas99.py src/main-memory.a99 -i -q -R -L westbank.lst -E symbols.txt -o bin/main
xas99.py src/rom-banks.a99 -B -q -R -o bin/banks.bin

java -jar tools/ea5tocart.jar bin\main "WEST BANK V2"

copy /b bin\main8.bin ^
    + bin\banks.bin ^
    westbank-v2-8.bin

java -jar tools/CopyHeader.jar westbank-v2-8.bin 60 17

java -jar tools/Background.jar

jar -cvf westbank-v2.rpk westbank-v2-8.bin layout.xml > make.log

