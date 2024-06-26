package com.teoneag;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    static final GdbDriver gdbDriver = new GdbDriver();
    static final Scanner userInput = new Scanner(System.in);
    static private String lastLine;

    /**
     * Main method - CLI application entry point for the JVM-GDB-Wrapper class
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the JVM-GDB-Wrapper!");
        help();

        String option = "";
        while (!option.equals("quit") && !option.equals("10")) {
            System.out.println();
            lastLine = userInput.nextLine();
            option = lastLine.split(" ")[0];
            lastLine = lastLine.replace(option, "").trim();
            switch (option) {
                case "0", "help" -> help();
                case "1", "init" -> init();
                case "1.1", "gdb" -> gdb();
                case "1.2", "gcc" -> gcc();
                case "2", "test" -> test();
                case "3", "comp" -> comp();
                case "3.1", "compile" -> compile();
                case "4", "load" -> load();
                case "5", "start" -> start();
                case "6", "break" -> setBreakpoint();
                case "7", "handle" -> handle();
                case "8", "output" -> output();
                case "9", "reset" -> reset();
                case "10", "quit" -> {
                }
                default -> System.out.println("Invalid option. Please try again or type 'help'.");
            }
        }

    }

    private static void help() {
        System.out.println("""
                To chose an option, type the number or the name of the option, optionally followed by the arguments
                    0 - help = show this message
                    1 - init <folderPath> = set the folder path where the gdb.exe, gcc.exe are located
                        1.1 - gdb <gdbPath> = set the GDB path (include the name of the executable)
                        1.2 - gcc <gccPath> = set the GCC path (include the name of the executable)
                    2 - test = checks for the GDB, GCC versions
                    3 - comp <filePath.c> = compile a C file, the executable will be filePath.exe
                        3.1 - compile <source.c> <destination> = compile a C file
                    4 - load <filePath> = load a file to debug
                    5 - start = start the debugger
                    6 - break <fileName> <lineNumber> = set a breakpoint
                    7 - handle auto|manual = set the breakpoint handler
                            auto = print backtrace and resume
                            manual = wait for user input
                    8 - output on|off = show the debugger output
                    9 - reset = reset the debugger
                    10 - quit = exit the application""");
    }

    private static void init() {
        if (lastLine.isEmpty()) {
            System.out.println("The path cannot be empty. Please try again.");
            return;
        }
        gdbDriver.setGdbGccDir(lastLine);
        System.out.println("Folder path set to " + lastLine);
    }

    private static void gdb() {
        if (lastLine.isEmpty()) {
            System.out.println("The path cannot be empty. Please try again.");
            return;
        }
        gdbDriver.setGdbPath(lastLine);
        System.out.println("GDB path set to " + lastLine);
    }

    private static void gcc() {
        if (lastLine.isEmpty()) {
            System.out.println("The path cannot be empty. Please try again.");
            return;
        }
        gdbDriver.setGccPath(lastLine);
        System.out.println("GCC path set to " + lastLine);
    }

    private static void test() {
        System.out.println("Testing GDB and GCC versions...");
        gdbDriver.test();
        System.out.println("Test complete.");
    }

    private static void comp() {
        if (lastLine.isEmpty()) {
            System.out.println("The path cannot be empty. Please try again.");
            return;
        }
        System.out.println("Compiling " + lastLine + "...");
        gdbDriver.compile(lastLine);
    }

    private static void compile() {
        List<String> args = Arrays.asList(lastLine.split(" "));
        if (args.size() != 2) {
            System.out.println("Please provide the source and destination file paths.");
            return;
        }
        System.out.println("Compiling " + args.get(0) + " to " + args.get(1) + "...");
        gdbDriver.compile(args.get(0), args.get(1));
    }

    private static void load() {
        if (lastLine.isEmpty()) {
            System.out.println("The path cannot be empty. Please try again.");
            return;
        }
        gdbDriver.loadFile(lastLine);
        System.out.println("Loaded file " + lastLine);
    }

    private static void start() {
        System.out.println("Debugging started...");
        gdbDriver.run();
        System.out.println("Debugging finished.");
    }

    private static void setBreakpoint() {
        try {
            List<String> args = Arrays.asList(lastLine.split(" "));
            if (args.size() != 2) {
                System.out.println("Please provide the file name and line number.");
                return;
            }
            String fileName = args.get(0);
            int lineNumber = Integer.parseInt(args.get(1));
            gdbDriver.setBreakpoint(fileName, lineNumber);
            System.out.println("Breakpoint set at " + fileName + ":" + lineNumber);
        } catch (NumberFormatException e) {
            System.out.println("The line number must be an integer.");
        }
    }

    private static void handle() {
        if (lastLine.equals("auto")) {
            gdbDriver.setBreakHandler(() -> {
                System.out.println("Breakpoint hit with backtrace: " + gdbDriver.getBacktrace());
                gdbDriver.resume();
            });
            System.out.println("Auto handler set.");
            return;
        }
        if (lastLine.equals("manual")) {
            gdbDriver.setBreakHandler(() -> {
                System.out.println("Breakpoint hit. Press bt to print the backtrace or resume to continue.");
                String line;
                while (true) {
                    System.out.println();
                    line = userInput.nextLine();
                    switch (line) {
                        case "bt" -> backtrace();
                        case "resume" -> {
                            gdbDriver.resume();
                            return;
                        }
                        default -> System.out.println("Invalid command. Please use 'bt' or 'resume'.");
                    }
                }
            });
            System.out.println("Manual handler set.");
            return;
        }
        System.out.println("Invalid handler. Please use 'auto' or 'manual'.");
    }

    private static void backtrace() {
        System.out.println(gdbDriver.getBacktrace());
    }

    private static void reset() {
        gdbDriver.reset();
        System.out.println("Debugger reset.");
    }

    private static void output() {
        if (lastLine.equals("on")) {
            gdbDriver.setShowOutput(true);
            System.out.println("Output enabled.");
            return;
        }
        if (lastLine.equals("off")) {
            gdbDriver.setShowOutput(false);
            System.out.println("Output disabled.");
            return;
        }
        System.out.println("Invalid option. Please use 'on' or 'off'.");
    }
}