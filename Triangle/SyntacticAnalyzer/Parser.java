/*
 * @(#)Parser.java                        2.1 2003/10/07
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
 */

package Triangle.SyntacticAnalyzer;

import Triangle.AbstractSyntaxTrees.*;
import Triangle.AbstractSyntaxTrees.DoUntilCommand;
import Triangle.ErrorReporter;

import java.util.concurrent.Delayed;

public class Parser {

    private Scanner lexicalAnalyser;
    private ErrorReporter errorReporter;
    private Token currentToken;
    private SourcePosition previousTokenPosition;

    public Parser(Scanner lexer, ErrorReporter reporter) {
        lexicalAnalyser = lexer;
        errorReporter = reporter;
        previousTokenPosition = new SourcePosition();
    }

// accept checks whether the current token matches tokenExpected.
// If so, fetches the next token.
// If not, reports a syntactic error.

    void accept(int tokenExpected) throws SyntaxError {
        if (currentToken.kind == tokenExpected) {
            previousTokenPosition = currentToken.position;
            currentToken = lexicalAnalyser.scan();
        } else {
            syntacticError("\"%\" expected here", Token.spell(tokenExpected));
        }
    }

    void acceptIt() {
        previousTokenPosition = currentToken.position;
        currentToken = lexicalAnalyser.scan();
    }

// start records the position of the start of a phrase.
// This is defined to be the position of the first
// character of the first token of the phrase.

    void start(SourcePosition position) {
        position.start = currentToken.position.start;
    }

// finish records the position of the end of a phrase.
// This is defined to be the position of the last
// character of the last token of the phrase.

    void finish(SourcePosition position) {
        position.finish = previousTokenPosition.finish;
    }

    void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
        SourcePosition pos = currentToken.position;
        errorReporter.reportError(messageTemplate, tokenQuoted, pos);
        throw (new SyntaxError());
    }

///////////////////////////////////////////////////////////////////////////////
//
// PROGRAMS
//
///////////////////////////////////////////////////////////////////////////////

    public Program parseProgram() {

        Program programAST = null;

        previousTokenPosition.start = 0;
        previousTokenPosition.finish = 0;
        currentToken = lexicalAnalyser.scan();

        try {
            Command cAST = parseCommand();
            programAST = new Program(cAST, previousTokenPosition);
            if (currentToken.kind != Token.EOT) {
                syntacticError("\"%\" not expected after end of program",
                        currentToken.spelling);
            }
        } catch (SyntaxError s) {
            return null;
        }
        return programAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// LITERALS
//
///////////////////////////////////////////////////////////////////////////////

// parseIntegerLiteral parses an integer-literal, and constructs
// a leaf AST to represent it.

    IntegerLiteral parseIntegerLiteral() throws SyntaxError {
        IntegerLiteral IL = null;

        if (currentToken.kind == Token.INTLITERAL) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            IL = new IntegerLiteral(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            IL = null;
            syntacticError("integer literal expected here", "");
        }
        return IL;
    }

// parseCharacterLiteral parses a character-literal, and constructs a leaf
// AST to represent it.

    CharacterLiteral parseCharacterLiteral() throws SyntaxError {
        CharacterLiteral CL = null;

        if (currentToken.kind == Token.CHARLITERAL) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            CL = new CharacterLiteral(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            CL = null;
            syntacticError("character literal expected here", "");
        }
        return CL;
    }

// parseIdentifier parses an identifier, and constructs a leaf AST to
// represent it.

    Identifier parseIdentifier() throws SyntaxError {
        Identifier I = null;

        if (currentToken.kind == Token.IDENTIFIER) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            I = new Identifier(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            I = null;
            syntacticError("identifier expected here", "");
        }
        return I;
    }

// parseOperator parses an operator, and constructs a leaf AST to
// represent it.

    Operator parseOperator() throws SyntaxError {
        Operator O = null;

        if (currentToken.kind == Token.OPERATOR) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            O = new Operator(spelling, previousTokenPosition);
            currentToken = lexicalAnalyser.scan();
        } else {
            O = null;
            syntacticError("operator expected here", "");
        }
        return O;
    }

///////////////////////////////////////////////////////////////////////////////
//
// COMMANDS
//
///////////////////////////////////////////////////////////////////////////////

// parseCommand parses the command, and constructs an AST
// to represent its phrase structure.

    Command parseCommand() throws SyntaxError {
        Command commandAST = null; // in case there's a syntactic error

        SourcePosition commandPos = new SourcePosition();

        start(commandPos);
        commandAST = parseSingleCommand();
        while (currentToken.kind == Token.SEMICOLON) {
            acceptIt();
            Command c2AST = parseSingleCommand();
            finish(commandPos);
            commandAST = new SequentialCommand(commandAST, c2AST, commandPos);
        }
        return commandAST;
    }

    Command parseSingleCommand() throws SyntaxError {
        Command commandAST = null; // in case there's a syntactic error
        SourcePosition commandPos = new SourcePosition();
        start(commandPos);
        switch (currentToken.kind) {
            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                if (currentToken.kind == Token.LPAREN) { // Lista parametros funciones
                    acceptIt();
                    ActualParameterSequence apsAST = parseActualParameterSequence();
                    accept(Token.RPAREN);
                    finish(commandPos);
                    commandAST = new CallCommand(iAST, apsAST, commandPos);
                } else { // Parse Variable declaration
                    Vname vAST = parseRestOfVname(iAST);
                    accept(Token.BECOMES);
                    Expression eAST = parseExpression();
                    finish(commandPos);
                    commandAST = new AssignCommand(vAST, eAST, commandPos);
                }
            }
            break;

            case Token.LOOP:
                acceptIt();
                if(currentToken.kind == Token.WHILE){
                    acceptIt();
                    Expression eAST = parseExpression();
                    accept(Token.DO);
                    Command cAST = parseCommand();
                    accept(Token.END);
                    finish(commandPos);
                    commandAST = new WhileCommand(eAST, cAST, commandPos); //TODO Tecnicamente el AST de while que existe sirve REVISAR que sirva
                }
                else if(currentToken.kind == Token.UNTIL){
                    Expression eAST = parseExpression();
                    accept(Token.DO);
                    Command cAST = parseCommand();
                    accept(Token.END);
                    finish(commandPos);
                    commandAST = new UntilCommand(eAST,cAST,commandPos); // TODO REVISAR ARBOL DE SINTAXIS ABSTRACTA
                }
                else if(currentToken.kind == Token.DO){
                    acceptIt();
                    Command cAST = parseCommand();
                    Expression eAST = null;
                    if(currentToken.kind == Token.WHILE){
                        acceptIt();
                        eAST = parseExpression();
                        accept(Token.END);
                        finish(commandPos);
                        commandAST = new DoWhileCommand(cAST,eAST,commandPos); //TODO REVISAR ARBOL DE SINTAXIS ABSTRACTA
                    }
                    else if(currentToken.kind ==Token.UNTIL){
                        acceptIt();
                        eAST = parseExpression();
                        accept(Token.END);
                        finish(commandPos);
                        commandAST = new DoUntilCommand(cAST,eAST,commandPos); // TODO REVISAR ARBOL DE SINTAXIS ABSTRACTA
                    }

                    else
                        syntacticError("\"%\" Error, UNTIL OR WHILE expected got: ",
                                currentToken.spelling);
                        break;
                }
                else if(currentToken.kind == Token.FOR){
                    acceptIt();
                    Identifier iAST = parseIdentifier();
                    accept(Token.BECOMES);
                    Expression eAST = parseExpression();
                    accept(Token.TO);
                    Expression eAST2 = parseExpression();
                    accept(Token.DO);
                    Command cAST = parseCommand();
                    accept(Token.END);
                    finish(commandPos);
                    commandAST = new ForCommand(iAST,eAST,eAST2,cAST, commandPos); //TODO REVISAR ARBOL DE SINTAXIS ABSTRACTA

                }
                else{
                    syntacticError("\"%\" SyntaxError expected {while, until, do, for}",
                            currentToken.spelling);
                    break;
                }
                break;

            case Token.LET: {
                acceptIt();
                Declaration dAST = parseDeclaration();
                accept(Token.IN);
                Command cAST = parseCommand();
                accept(Token.END);
                finish(commandPos);
                commandAST = new LetCommand(dAST, cAST, commandPos);
            }
            break;


            case Token.IF: {
                //TODO revisar la recurson que hace el elsif para ver que el arbol quede bien la solucion esta bien cochina :V
                acceptIt();
                Expression e1AST = parseExpression();
                accept(Token.THEN);
                Command c1AST = parseCommand();
                Command c2AST = null;
                if(currentToken.kind == Token.ELSIF)
                    c2AST = parseElsif();
                else{
                    accept(Token.ELSE);
                    c2AST = parseCommand();
                }
                accept(Token.END);
                finish(commandPos);
                commandAST = new IfCommand(e1AST, c1AST, c2AST, commandPos);
            }
            break;

            case Token.NOTHING: {
                acceptIt();
                finish(commandPos);
                commandAST = new EmptyCommand(commandPos); //TODO Agregar NOTHING
                break;
            }

            case Token.SEMICOLON:
            case Token.END:
            case Token.ELSE:
            case Token.IN:
            case Token.EOT:
                finish(commandPos);
                commandAST = new EmptyCommand(commandPos); //TODO Agregar NOTHING
                break;

            default:
                syntacticError("\"%\" cannot start a command",
                        currentToken.spelling);
                break;

        }

        return commandAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// Auxiliar para parsear el elsif recursivamente
//
///////////////////////////////////////////////////////////////////////////////

    Command parseElsif() throws SyntaxError{
        SourcePosition commandPos = new SourcePosition();
        start(commandPos);
        Command cAST = null;
        accept(Token.ELSIF);
        Expression eAST = parseExpression();
        accept(Token.THEN);
        Command cAUX = parseCommand();
        finish(commandPos);
        if(currentToken.kind == Token.ELSIF)
            cAST = new ElsifCommand(eAST, cAUX, parseElsif(), commandPos);
        else{
            Command elseCommand = parseCommand();
            cAST = new ElsifCommand(eAST, cAUX, elseCommand, commandPos);
        }
        return cAST;
    }


///////////////////////////////////////////////////////////////////////////////
//
// EXPRESSIONS
//
///////////////////////////////////////////////////////////////////////////////

    Expression parseExpression() throws SyntaxError {
        Expression expressionAST = null; // in case there's a syntactic error

        SourcePosition expressionPos = new SourcePosition();

        start(expressionPos);

        switch (currentToken.kind) {

            case Token.LET: {
                acceptIt();
                Declaration dAST = parseDeclaration();
                accept(Token.IN);
                Expression eAST = parseExpression();
                finish(expressionPos);
                expressionAST = new LetExpression(dAST, eAST, expressionPos);
            }
            break;

            case Token.IF: {
                acceptIt();
                Expression e1AST = parseExpression();
                accept(Token.THEN);
                Expression e2AST = parseExpression();
                accept(Token.ELSE);
                Expression e3AST = parseExpression();
                finish(expressionPos);
                expressionAST = new IfExpression(e1AST, e2AST, e3AST, expressionPos);
            }
            break;

            default:
                expressionAST = parseSecondaryExpression();
                break;
        }
        return expressionAST;
    }

    Expression parseSecondaryExpression() throws SyntaxError {
        Expression expressionAST = null; // in case there's a syntactic error

        SourcePosition expressionPos = new SourcePosition();
        start(expressionPos);

        expressionAST = parsePrimaryExpression();
        while (currentToken.kind == Token.OPERATOR) {
            Operator opAST = parseOperator();
            Expression e2AST = parsePrimaryExpression();
            expressionAST = new BinaryExpression(expressionAST, opAST, e2AST,
                    expressionPos);
        }
        return expressionAST;
    }

    Expression parsePrimaryExpression() throws SyntaxError {
        Expression expressionAST = null; // in case there's a syntactic error

        SourcePosition expressionPos = new SourcePosition();
        start(expressionPos);

        switch (currentToken.kind) {

            case Token.INTLITERAL: {
                IntegerLiteral ilAST = parseIntegerLiteral();
                finish(expressionPos);
                expressionAST = new IntegerExpression(ilAST, expressionPos);
            }
            break;

            case Token.CHARLITERAL: {
                CharacterLiteral clAST = parseCharacterLiteral();
                finish(expressionPos);
                expressionAST = new CharacterExpression(clAST, expressionPos);
            }
            break;

            case Token.LBRACKET: {
                acceptIt();
                ArrayAggregate aaAST = parseArrayAggregate();
                accept(Token.RBRACKET);
                finish(expressionPos);
                expressionAST = new ArrayExpression(aaAST, expressionPos);
            }
            break;

            case Token.LCURLY: {
                acceptIt();
                RecordAggregate raAST = parseRecordAggregate();
                accept(Token.RCURLY);
                finish(expressionPos);
                expressionAST = new RecordExpression(raAST, expressionPos);
            }
            break;

            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                if (currentToken.kind == Token.LPAREN) {
                    acceptIt();
                    ActualParameterSequence apsAST = parseActualParameterSequence();
                    accept(Token.RPAREN);
                    finish(expressionPos);
                    expressionAST = new CallExpression(iAST, apsAST, expressionPos);

                } else {
                    Vname vAST = parseRestOfVname(iAST);
                    finish(expressionPos);
                    expressionAST = new VnameExpression(vAST, expressionPos);
                }
            }
            break;

            case Token.OPERATOR: {
                Operator opAST = parseOperator();
                Expression eAST = parsePrimaryExpression();
                finish(expressionPos);
                expressionAST = new UnaryExpression(opAST, eAST, expressionPos);
            }
            break;

            case Token.LPAREN:
                acceptIt();
                expressionAST = parseExpression();
                accept(Token.RPAREN);
                break;

            default:
                syntacticError("\"%\" cannot start an expression",
                        currentToken.spelling);
                break;

        }
        return expressionAST;
    }

    RecordAggregate parseRecordAggregate() throws SyntaxError {
        RecordAggregate aggregateAST = null; // in case there's a syntactic error

        SourcePosition aggregatePos = new SourcePosition();
        start(aggregatePos);

        Identifier iAST = parseIdentifier();
        accept(Token.IS);
        Expression eAST = parseExpression();

        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            RecordAggregate aAST = parseRecordAggregate();
            finish(aggregatePos);
            aggregateAST = new MultipleRecordAggregate(iAST, eAST, aAST, aggregatePos);
        } else {
            finish(aggregatePos);
            aggregateAST = new SingleRecordAggregate(iAST, eAST, aggregatePos);
        }
        return aggregateAST;
    }

    ArrayAggregate parseArrayAggregate() throws SyntaxError {
        ArrayAggregate aggregateAST = null; // in case there's a syntactic error

        SourcePosition aggregatePos = new SourcePosition();
        start(aggregatePos);

        Expression eAST = parseExpression();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            ArrayAggregate aAST = parseArrayAggregate();
            finish(aggregatePos);
            aggregateAST = new MultipleArrayAggregate(eAST, aAST, aggregatePos);
        } else {
            finish(aggregatePos);
            aggregateAST = new SingleArrayAggregate(eAST, aggregatePos);
        }
        return aggregateAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// VALUE-OR-VARIABLE NAMES
//
///////////////////////////////////////////////////////////////////////////////

    Vname parseVname() throws SyntaxError {
        Vname vnameAST = null; // in case there's a syntactic error
        Identifier iAST = parseIdentifier();
        vnameAST = parseRestOfVname(iAST);
        return vnameAST;
    }

    Vname parseRestOfVname(Identifier identifierAST) throws SyntaxError {
        SourcePosition vnamePos = new SourcePosition();
        vnamePos = identifierAST.position;
        Vname vAST = new SimpleVname(identifierAST, vnamePos);

        while (currentToken.kind == Token.DOT ||
                currentToken.kind == Token.LBRACKET) {

            if (currentToken.kind == Token.DOT) {
                acceptIt();
                Identifier iAST = parseIdentifier();
                vAST = new DotVname(vAST, iAST, vnamePos);
            } else {
                acceptIt();
                Expression eAST = parseExpression();
                accept(Token.RBRACKET);
                finish(vnamePos);
                vAST = new SubscriptVname(vAST, eAST, vnamePos);
            }
        }
        return vAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// DECLARATIONS
//
///////////////////////////////////////////////////////////////////////////////
    Declaration parseDeclaration() throws SyntaxError {
        Declaration declarationAST = null; // in case there's a syntactic error
        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);
        declarationAST = parseCompoundDeclaration(); //TODO DECLARATION ESPECIFICACION DE MODIFICAR
        while (currentToken.kind == Token.SEMICOLON) {
            acceptIt();
            Declaration d2AST = parseCompoundDeclaration();
            finish(declarationPos);
            declarationAST = new SequentialDeclaration(declarationAST, d2AST,
                    declarationPos);
        }
        return declarationAST;
    }


    /* Agregado PROYECTO 1 PARSE COMPOUND DECLARATION*/
    Declaration parseCompoundDeclaration() throws SyntaxError {
        Declaration declarationAST = null;
        SourcePosition compoundPos = new SourcePosition();
        start(compoundPos);
        switch (currentToken.kind){
            //Caso de un compound Declaration
            case Token.CONST:
            case Token.VAR:
            case Token.PROC:
            case Token.FUNC:
            case Token.TYPE:
                declarationAST = parseSingleDeclaration();
                break;
            case Token.REC: {
                acceptIt();
                //TODO parse ProcFuncs
                accept(Token.END);
            }
            case Token.PRIVATE: {
                acceptIt();
                Declaration d2AST = parseDeclaration();
                accept(Token.IN);
                Declaration d3AST = parseDeclaration();
                accept(Token.END);
                //TODO ver si va un finish
            }
        }
        return declarationAST;
    }

    Declaration parseProcFunc() throws SyntaxError{ //TODO crear AST para proc funcs
        //TODO falta juntar todo en un AST y devolverlo
        SourcePosition procFuncsPos = new SourcePosition();
        Declaration declarationAST = null;
        start(procFuncsPos);
        switch (currentToken.kind){
            case Token.PROC: {
                acceptIt();
                accept(Token.IDENTIFIER);
                Identifier identifier = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence formalParameter = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.IS);
                Command command = parseCommand();
                accept(Token.END);
                finish(procFuncsPos);
                declarationAST = new ProcDeclaration(identifier, formalParameter, command, procFuncsPos);
                break;
            }
            case Token.FUNC: {
                acceptIt();
                accept(Token.IDENTIFIER);
                Identifier identifier = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence formalParameterSequence = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter typeDenoter = parseTypeDenoter();
                accept(Token.IS);
                Expression expression = parseExpression();
                finish(procFuncsPos);
                declarationAST = new FuncDeclaration(identifier, formalParameterSequence, typeDenoter, expression, procFuncsPos);
                break;
            }
            default:
                syntacticError("\"%\" expected \'proc\' or \'func\' got ",
                        currentToken.spelling);
                break;
        }
        return declarationAST;
    }

    void parseProcFuncs() throws SyntaxError{
        parseProcFunc();
        if(currentToken.kind == Token.AND){ //TODO REVISAR Hacer obligatorio el primer AND
            while(currentToken.kind == Token.AND){
                acceptIt();
                parseProcFunc();
            }
        }
        else{
            syntacticError("\"%\" expected \'and\' got ",
                    currentToken.spelling);
        }

    }



    /*------------------------------------------------------------*/

    Declaration parseSingleDeclaration() throws SyntaxError {
        Declaration declarationAST = null; // in case there's a syntactic error

        SourcePosition declarationPos = new SourcePosition();
        start(declarationPos);

        switch (currentToken.kind) {

            case Token.CONST: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.IS);
                Expression eAST = parseExpression();
                finish(declarationPos);
                declarationAST = new ConstDeclaration(iAST, eAST, declarationPos);
            }
            break;

            case Token.VAR: {
                acceptIt();
                Identifier iAST = parseIdentifier();

                if(currentToken.kind ==Token.COLON){
                    accept(Token.COLON);
                    TypeDenoter tAST = parseTypeDenoter();
                    finish(declarationPos);
                    declarationAST = new VarDeclaration(iAST, tAST, declarationPos);
                }else if(currentToken.kind == Token.BECOMES ){
                    acceptIt();
                    Expression eAST = parseExpression();
                    finish(declarationPos);
                    declarationAST = new VarInitialized(iAST,eAST,declarationPos);// TODO IMPLEMENTAR ARBOL SINTAXIS, ESTE ES EL DE VARIABLE INICIALIZADA
                }
                else
                    syntacticError("\"%\" Error al inicializar o declarar una variable",
                            currentToken.spelling);
            }
            break;

            case Token.PROC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.IS);
                Command cAST = parseCommand();
                accept(Token.END);
                finish(declarationPos);
                declarationAST = new ProcDeclaration(iAST, fpsAST, cAST, declarationPos);
            }
            break;

            case Token.FUNC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                accept(Token.IS);
                Expression eAST = parseExpression();
                finish(declarationPos);
                declarationAST = new FuncDeclaration(iAST, fpsAST, tAST, eAST,
                        declarationPos);
            }
            break;

            case Token.TYPE: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.IS);
                TypeDenoter tAST = parseTypeDenoter();
                finish(declarationPos);
                declarationAST = new TypeDeclaration(iAST, tAST, declarationPos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start a declaration",
                        currentToken.spelling);
                break;

        }
        return declarationAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// PARAMETERS
//
///////////////////////////////////////////////////////////////////////////////

    FormalParameterSequence parseFormalParameterSequence() throws SyntaxError {
        FormalParameterSequence formalsAST;

        SourcePosition formalsPos = new SourcePosition();

        start(formalsPos);
        if (currentToken.kind == Token.RPAREN) {
            finish(formalsPos);
            formalsAST = new EmptyFormalParameterSequence(formalsPos);

        } else {
            formalsAST = parseProperFormalParameterSequence();
        }
        return formalsAST;
    }

    FormalParameterSequence parseProperFormalParameterSequence() throws SyntaxError {
        FormalParameterSequence formalsAST = null; // in case there's a syntactic error;

        SourcePosition formalsPos = new SourcePosition();
        start(formalsPos);
        FormalParameter fpAST = parseFormalParameter();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            FormalParameterSequence fpsAST = parseProperFormalParameterSequence();
            finish(formalsPos);
            formalsAST = new MultipleFormalParameterSequence(fpAST, fpsAST,
                    formalsPos);

        } else {
            finish(formalsPos);
            formalsAST = new SingleFormalParameterSequence(fpAST, formalsPos);
        }
        return formalsAST;
    }

    FormalParameter parseFormalParameter() throws SyntaxError {
        FormalParameter formalAST = null; // in case there's a syntactic error;

        SourcePosition formalPos = new SourcePosition();
        start(formalPos);

        switch (currentToken.kind) {

            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                finish(formalPos);
                formalAST = new ConstFormalParameter(iAST, tAST, formalPos);
            }
            break;

            case Token.VAR: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                finish(formalPos);
                formalAST = new VarFormalParameter(iAST, tAST, formalPos);
            }
            break;

            case Token.PROC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                finish(formalPos);
                formalAST = new ProcFormalParameter(iAST, fpsAST, formalPos);
            }
            break;

            case Token.FUNC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                accept(Token.LPAREN);
                FormalParameterSequence fpsAST = parseFormalParameterSequence();
                accept(Token.RPAREN);
                accept(Token.COLON);
                TypeDenoter tAST = parseTypeDenoter();
                finish(formalPos);
                formalAST = new FuncFormalParameter(iAST, fpsAST, tAST, formalPos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start a formal parameter",
                        currentToken.spelling);
                break;

        }
        return formalAST;
    }


    ActualParameterSequence parseActualParameterSequence() throws SyntaxError {
        ActualParameterSequence actualsAST;

        SourcePosition actualsPos = new SourcePosition();

        start(actualsPos);
        if (currentToken.kind == Token.RPAREN) {
            finish(actualsPos);
            actualsAST = new EmptyActualParameterSequence(actualsPos);

        } else {
            actualsAST = parseProperActualParameterSequence();
        }
        return actualsAST;
    }

    ActualParameterSequence parseProperActualParameterSequence() throws SyntaxError {
        ActualParameterSequence actualsAST = null; // in case there's a syntactic error

        SourcePosition actualsPos = new SourcePosition();

        start(actualsPos);
        ActualParameter apAST = parseActualParameter();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            ActualParameterSequence apsAST = parseProperActualParameterSequence();
            finish(actualsPos);
            actualsAST = new MultipleActualParameterSequence(apAST, apsAST,
                    actualsPos);
        } else {
            finish(actualsPos);
            actualsAST = new SingleActualParameterSequence(apAST, actualsPos);
        }
        return actualsAST;
    }

    ActualParameter parseActualParameter() throws SyntaxError {
        ActualParameter actualAST = null; // in case there's a syntactic error

        SourcePosition actualPos = new SourcePosition();

        start(actualPos);

        switch (currentToken.kind) {

            case Token.IDENTIFIER:
            case Token.INTLITERAL:
            case Token.CHARLITERAL:
            case Token.OPERATOR:
            case Token.LET:
            case Token.IF:
            case Token.LPAREN:
            case Token.LBRACKET:
            case Token.LCURLY: {
                Expression eAST = parseExpression();
                finish(actualPos);
                actualAST = new ConstActualParameter(eAST, actualPos);
            }
            break;

            case Token.VAR: {
                acceptIt();
                Vname vAST = parseVname();
                finish(actualPos);
                actualAST = new VarActualParameter(vAST, actualPos);
            }
            break;

            case Token.PROC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                finish(actualPos);
                actualAST = new ProcActualParameter(iAST, actualPos);
            }
            break;

            case Token.FUNC: {
                acceptIt();
                Identifier iAST = parseIdentifier();
                finish(actualPos);
                actualAST = new FuncActualParameter(iAST, actualPos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start an actual parameter",
                        currentToken.spelling);
                break;

        }
        return actualAST;
    }

///////////////////////////////////////////////////////////////////////////////
//
// TYPE-DENOTERS
//
///////////////////////////////////////////////////////////////////////////////

    TypeDenoter parseTypeDenoter() throws SyntaxError {
        TypeDenoter typeAST = null; // in case there's a syntactic error
        SourcePosition typePos = new SourcePosition();

        start(typePos);

        switch (currentToken.kind) {

            case Token.IDENTIFIER: {
                Identifier iAST = parseIdentifier();
                finish(typePos);
                typeAST = new SimpleTypeDenoter(iAST, typePos);
            }
            break;

            case Token.ARRAY: {
                acceptIt();
                IntegerLiteral il1AST = parseIntegerLiteral();
                if(currentToken.kind == Token.OF){
                    acceptIt();
                    TypeDenoter tAST = parseTypeDenoter();
                    finish(typePos);
                    typeAST = new ArrayTypeDenoter(il1AST, tAST, typePos); //TODO cambiar AST
                } else if(currentToken.kind == Token.DOUBLE_DOTS){
                    accept(Token.DOUBLE_DOTS);
                    IntegerLiteral il2AST = parseIntegerLiteral();
                    accept(Token.OF);
                    TypeDenoter tAST = parseTypeDenoter();
                    finish(typePos);
                    typeAST = new ArrayTypeDenoterStatic(il1AST, il2AST, tAST, typePos); //TODO agregar AST para Array con ..
                }
                /*accept(Token.OF);
                TypeDenoter tAST = parseTypeDenoter();
                finish(typePos);
                typeAST = new ArrayTypeDenoter(ilAST, tAST, typePos);
                */
                /*
                else if(currentToken.kind == Token.DO){
                    acceptIt();
                    Command cAST = parseCommand();
                    Expression eAST = null;
                    if(currentToken.kind == Token.WHILE){
                        acceptIt();
                        eAST = parseExpression();
                        accept(Token.END);
                        finish(commandPos);
                        commandAST = new loopWhileCommand(cAST,eAST,commandPos); //TODO HACER ARBOL DE SINTAXIS ABSTRACTA
                    }
                    else if(currentToken.kind ==Token.UNTIL){
                        acceptIt();
                        eAST = parseExpression();
                        accept(Token.END);
                        finish(commandPos);
                        commandAST = new loopUntilCommand(cAST,eAST,commandPos); // TODO HACER ARBOL DE SINTAXIS ABSTRACTA
                    }

                    else
                        syntacticError("\"%\" Error, se esperaba UNTIL O WHILE",
                                currentToken.spelling);
                 */
            }
            break;

            case Token.RECORD: {
                acceptIt();
                FieldTypeDenoter fAST = parseFieldTypeDenoter();
                accept(Token.END);
                finish(typePos);
                typeAST = new RecordTypeDenoter(fAST, typePos);
            }
            break;

            default:
                syntacticError("\"%\" cannot start a type denoter",
                        currentToken.spelling);
                break;

        }
        return typeAST;
    }

    FieldTypeDenoter parseFieldTypeDenoter() throws SyntaxError {
        FieldTypeDenoter fieldAST = null; // in case there's a syntactic error

        SourcePosition fieldPos = new SourcePosition();

        start(fieldPos);
        Identifier iAST = parseIdentifier();
        accept(Token.COLON);
        TypeDenoter tAST = parseTypeDenoter();
        if (currentToken.kind == Token.COMMA) {
            acceptIt();
            FieldTypeDenoter fAST = parseFieldTypeDenoter();
            finish(fieldPos);
            fieldAST = new MultipleFieldTypeDenoter(iAST, tAST, fAST, fieldPos);
        } else {
            finish(fieldPos);
            fieldAST = new SingleFieldTypeDenoter(iAST, tAST, fieldPos);
        }
        return fieldAST;
    }
}
