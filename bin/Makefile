# .PHONEY: build, run

# build: Program.class

# run: Program.class
# 	java Program

# Program.class: Program.java
# 	javac Program.java

.PHONY: build, run, clean

# <make clean> Win11 and Linux compatibility
ifeq ($(OS),Windows_NT)
    CLEAN = del /Q
else
    CLEAN = rm -f
endif

build: Program.class

run: Program.class
	java Program $(file)

clean:
	$(CLEAN) *.class

Program.class: Program.java
	javac Program.java