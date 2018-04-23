/*
 * @(#)Token.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 *
 * Modificaciones Proyecto 1 20018/04/23
 * Realizadas por
 * Javier Contreras Muñoz
 * Bryan Mena Villalobos
 * David Valverde Garro
 *
 * Se añaden nuevos token para soportar la nueva sintaxis de Triangulo Extendido
 * Se marcan con el comentario '//PROYECTO 1' los nuevos Token
 * Se elimina el token 'begin'
 */

package Triangle.SyntacticAnalyzer;


final class Token extends Object {
    protected int kind;
    protected String spelling;
    protected SourcePosition position;

    public static final int

    // literals, identifiers, operators...
    INTLITERAL = 0,
    CHARLITERAL = 1,
    IDENTIFIER = 2,
    OPERATOR = 3,

    // reserved words - must be in alphabetical order...
    AND = 4, //PROYECTO 1
    ARRAY = 5,
    //BEGIN		= 5, SE ELIMINA PROYECTO 1
    CONST = 6,
    DO = 7,
    ELSE = 8,
    ELSIF = 9,  //PROYECTO 1
    END = 10,
    FOR = 11,
    FUNC = 12, //PROYECTO 1
    IF = 13,
    IN = 14,
    LET = 15,
    LOOP = 16, //PROYECTO 1
    NOTHING = 17, //PROYECTO 1
    OF = 18,
    PRIVATE = 19, //PROYECTO 1
    PROC = 20,
    REC = 21, //PROYECTO 1
    RECORD = 22,
    THEN = 23,
    TO = 24, //PROYECTO 1
    TYPE = 25,
    UNTIL = 26, //PROYECTO 1
    VAR = 27,
    WHILE = 28,

    // punctuation...
    DOT = 29,
    COLON = 30,
    SEMICOLON = 31,
    COMMA = 32,
    BECOMES = 33,
    IS = 34,
    DOUBLE_DOTS = 35, // PROYECTO 1

    // brackets...
    LPAREN = 36,
    RPAREN = 37,
    LBRACKET = 38,
    RBRACKET = 39,
    LCURLY = 40,
    RCURLY = 41,

    // special tokens...
    EOT = 42,
    ERROR = 43;

    private final static int
    firstReservedWord = Token.AND, //Token.ARRAY, Cambia por proyecto 1, se agrega token AND antes de ARRAY
    lastReservedWord  =Token.WHILE;

    private static String[] tokenTable = new String[]{
            "<int>",
            "<char>",
            "<identifier>",
            "<operator>",
            "and", //PROYECTO 1
            "array",
            //"begin", SE ELIMINA PROYECTO 1
            "const",
            "do",
            "else",
            "elsif", //PROYECTO 1
            "end",
            "for", //PROYECTO 1
            "func",
            "if",
            "in",
            "let",
            "loop", //PROYECTO 1
            "nothing", //PROYECTO 1
            "of",
            "private", //PROYECTO 1
            "proc",
            "rec", //PROYECTO 1
            "record",
            "then",
            "to", //PROYECTO 1
            "type",
            "until", //PROYECTO 1
            "var",
            "while",
            ".",
            ":",
            ";",
            ",",
            ":=",
            "~",
            "..", //PROYECTO 1
            "(",
            ")",
            "[",
            "]",
            "{",
            "}",
            "",
            "<error>"
    };

// Token classes...

    public Token(int kind, String spelling, SourcePosition position) {
        if (kind == Token.IDENTIFIER) {
            int currentKind = firstReservedWord;
            boolean searching = true;
            while (searching) {
                int comparison = tokenTable[currentKind].compareTo(spelling);
                if (comparison == 0) {
                    this.kind = currentKind;
                    searching = false;
                } else if (comparison > 0 || currentKind == lastReservedWord) {
                    this.kind = Token.IDENTIFIER;
                    searching = false;
                } else {
                    currentKind++;
                }
            }
        } else
            this.kind = kind;
        this.spelling = spelling;
        this.position = position;
    }

    public static String spell(int kind) {
        return tokenTable[kind];
    }

    public String toString() {
        return "Kind=" + kind + ", spelling=" + spelling + ", position=" + position;
    }
}
