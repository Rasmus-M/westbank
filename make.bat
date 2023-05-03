xas99.py src/main-memory.a99 -i -q -R -L westbank.lst -E symbols.txt -o bin/main
xas99.py src/rom-banks.a99 -B -q -R -o bin/banks.bin

java -jar tools/ea5tocart.jar bin\main "WEST BANK"

copy /b bin\main8.bin ^
    + bin\banks.bin ^
    westbank8.bin

java -jar tools/CopyHeader.jar westbank8.bin 60

jar -cvf westbank.rpk westbank8.bin layout.xml > make.log

