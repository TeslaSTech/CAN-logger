# CAN-Opener

### Mode 01 OBD-II toolkit <br> by Val Revankar

## About
CAN-Opener is a small toolkit that makes interfacing with a car's On-Board Diagnostics port easy.<br>
It has three main features:
- Collecting data from an ELM327 device over a serial port and storing the hex response frames to CSV
- Converting a CSV table of hex data frames to human-readable data and storing it as CSV (for use in spreadsheets) 
- Converting single ELM327 or "OBD-II over CAN" response frames.

## Industry standards

CAN-Opener works with the following:
- OBD decoding keys are compiled from a document compliant with the Vector DBC File Format Specification
- ELM327 communication is compliant with the data sheet provided by Elm Electronics.

## Testing scope

- So far, this program has only been tested successfully on Windows using the `obdsimwindows` ELM327 simulator.

## Known bugs/issues

- Simulating communication with an ELM327 using the `elm-emulator` package, the program has a tendency to read all but the first and last frames more than once.
- Especially on Linux, the jSerialComm library sometimes does not list virtual terminals. You will have to know them and manually type them in.
  - This is why the program does not check that the chosen serial port exists/does not exist.
- You must enter a serial port that actually exists, due to the above. The program will not crash, it will just not collect data.

## Where to find documentation?

There is a `javadoc/` folder that contains documentation on every class in this suite.

## How to set up the obdsimwindows suite to beta test

You will need to set up com0com, which creates virtual serial port pairs. Without these, the obdsim tool has nothing to attach to.
It also helps to run it from PowerShell, or the command line, since it will log any issues to the console.
**<br>com0com WILL NOT WORK if Secure Boot is enabled. Proceed with caution, and know the risks of disabling Secure Boot.**

## Run configuration

This project contains the jSerialComm library, managed by the Maven build system. The JAR is in the `lib/` directory. 
Make sure to include that directory in the classpath when running the file!
I set my working directory to the project root (`.../CAN-logger/`) so that CSVs made in the program do not save in the `src/` folder.

## I want to add functionality/contribute to this project!

Create a pull request! There is lots to do. TODOs are scattered aplenty.

## I encountered a bug!

Submit an issue request!!! I'm not a perfect tester by any means, anything you catch is valuable!
