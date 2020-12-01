/*
  Copyright (c) 2020 Tsiridis Nikolaos

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;

@BuildParseTree
public class GRPsParser extends BaseParser<Object> {

    public static final String COLUMN_SEPARATOR = "||";
    public static final String ROW_SEPARATOR = "\n";


    Rule GrpData() {
        return Sequence(HeaderPart(), OneOrMore(DataLine()), EOI);
    }

    Rule DataLine() {
        return FirstOf(
                EmptyRow(),
                DataRow()
        );
    }

    Rule EmptyRow() {
        return EOL();
    }

    Rule DataRow() {
        return Sequence(OneOrMore(Sequence(TestNot(EOL()), Cell())), EOL());
    }

    Rule HeaderPart() {
        return Sequence(
                ZeroOrMore(EmptyRow()),
                Sequence(
                        ZeroOrMore(EmptyCell()),
                        FixedValueCell("SOFTX:"),
                        AlphanumericCell(),
                        push( new GRPData().setReportName((String) pop())),
                        EOL())
        );
    }

    Rule Cell() {
        return FirstOf(
                EmptyCell(),
                NumericCell(),
                AlphanumericCell()
        );
    }

    Rule EmptyCell() {
        return EOC();
    }


    Rule AlphanumericCell() {
        return Sequence(
                OneOrMore(
                        Sequence(
                                TestNot(EOC()),
                                ANY)
                ).suppressSubnodes(),
                EOC(),
                push(match())
        );
    }

    Rule FixedValueCell(String expectedValue) {
        return Sequence(String(expectedValue), Spacing(), EOC()).label('\'' + expectedValue + '\'');
    }

    Rule NumericCell() {
        return Sequence(FirstOf(DecimalNumeral(), DecimalFloat()), EOC());
    }

    @SuppressSubnodes
    Rule DecimalNumeral() {
        return FirstOf('0', Sequence(CharRange('1', '9'), ZeroOrMore(Digit())));
    }

    @SuppressSubnodes
    Rule DecimalFloat() {
        return FirstOf(
                Sequence(OneOrMore(Digit()), '.', ZeroOrMore(Digit())),
                Sequence('.', OneOrMore(Digit())),
                OneOrMore(Digit())
        );
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressNode
    Rule Spacing() {
        return  ZeroOrMore(AnyOf(" \t\r\n").label("Whitespace"));
    }

    Rule EOC () {
        return String(COLUMN_SEPARATOR);
    }

    Rule EOL() {
        return String(ROW_SEPARATOR);
    }

    public String removeTrailingColumnSeparator(String cellData) {
        if (cellData == null) {
            return "";
        }
        return cellData.substring(0, cellData.lastIndexOf(COLUMN_SEPARATOR));
    }
}
