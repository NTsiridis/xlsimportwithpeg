import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;


/**
 * A PEG Parser , used to parse an XLS files
 * with GRP data
 */
@BuildParseTree
public class ParboiledGRPParser extends BaseParser<Object> {

    public static final char COLUMN_SEPARATOR = '|';
    public static final char ROW_SEPARATOR = '\n';

    Rule GRPData() {
        return Sequence(
                ZeroOrMore(EmptyLine()),
                ReportHeader(),
                OneOrMore(Segments()),
                EOI
        );
    }


    Rule ReportHeader() {
        return Sequence(
                ReportTitle(),
                DatabaseUsed(),
                RunReference(),
                ReportDate(),
                ReportTime(),
                ClientName(),
                OneOrMore(EmptyLine())
        );
    }

    Rule ReportTitle() {
        return Sequence(
                RowStartingWith("SOFTX:"),
                AlphaNumericCellValue(),
                EOL()
        );
    }

    Rule DatabaseUsed() {
        return Sequence(
                RowStartingWith("Database:"),
                AlphaNumericCellValue(),
                EOL()
        );
    }

    Rule ReportDate() {
        return Sequence(
                RowStartingWith("Date:"),
                DateCellValue(),
                EOL()
        );
    }

    Rule ReportTime() {
        return Sequence(
                RowStartingWith("Time:"),
                TimeCellValue(),
                EOL()
        );
    }

    Rule ClientName() {
        return Sequence(
                RowStartingWith("Client name:"),
                AlphaNumericCellValue(),
                EOL()
        );
    }

    Rule RunReference() {
        return Sequence(
                RowStartingWith("Run ref:"),
                AlphaNumericCellValue(),
                EOL()
        );
    }


    Rule Segments() {
        return Sequence(
                SegmentSection(),
                SegmentData()
        );
    }

    Rule SegmentSection() {
        return Sequence(
                SegmentHeader(),
                SegmentPercentages(),
                SegmentThousands(),
                SegmentSample(),
                OneOrMore(EmptyLine()),
                RowStartingWith("Target definition"), EOL(),
                SegmentName(),
                SegmentPart(),
                Optional(Sequence(RowStartingWith("(Groups=Gross)"), EOL())),
                OneOrMore(EmptyLine())
        );
    }

    Rule SegmentData() {
        return Sequence(
                GRPSegmentDataHeaderLine1(),
                GRPSegmentDataHeaderLine2(),
                OneOrMore(GRPSegmentData()),
                ZeroOrMore(EmptyLine())
        );
    }

    Rule SegmentHeader() {
        return Sequence(
                RowStartingWith("Market size"),
                FixedValueCell("ALL"),
                AlphaNumericCellValue(),
                EOL()
        );
    }

    Rule SegmentPercentages() {
        return Sequence(
                RowStartingWith("%"),
                FloatCellValue(),
                FloatCellValue(),
                EOL()
        );
    }

    Rule SegmentThousands() {
        return Sequence(
                RowStartingWith("'000"),
                IntegerCellValue(),
                IntegerCellValue(),
                ZeroOrMore(EmptyCell()),
                EOL()
        );
    }

    Rule SegmentSample() {
        return Sequence(
                RowStartingWith("Sample"),
                IntegerCellValue(),
                IntegerCellValue(),
                ZeroOrMore(EmptyCell()),
                EOL()
        );
    }

    Rule SegmentName() {
        return Sequence(
                EmptyCell(),
                AlphaNumericCellValue(),
                EOL());
    }

    Rule SegmentPart() {
        return Sequence(
                EmptyCell(),
                AlphaNumericCellValue(),
                EOL());
    }


    Rule GRPSegmentDataHeaderLine1() {

        return Sequence(
                RowStartingWith("All reach net"),
                EOC(),
                FixedValueCell("Tgt reach net"),
                EOL()
        );
    }

    Rule GRPSegmentDataHeaderLine2() {

        return Sequence(
                RowStartingWith("%"),
                FixedValueCell("'000"),
                RowStartingWith("%"),
                FixedValueCell("'000"),
                EOL());
    }

    Rule GRPSegmentData() {
        return Sequence(
                IntegerCellValue(),
                AlphaNumericCellValue(),
                FloatCellValue(),
                FloatCellValue(),
                FloatCellValue(),
                FloatCellValue(),
                EOL()
        );
    }


    Rule RowStartingWith(String label) {
        return Sequence(
                ZeroOrMore(EmptyCell()),
                FixedValueCell(label)
        );
    }

    @SuppressSubnodes
    Rule AlphaNumericCellValue() {
        return Sequence(AlphaNumericValue(), EOC());
    }

    Rule AlphaNumericValue() {
        return OneOrMore(Sequence(TestNot(EOC()), ANY));
    }

    @SuppressSubnodes
    Rule DateCellValue() {
        return Sequence(DateValue(), EOC());
    }

    @SuppressSubnodes
    Rule DateValue() {
        return Sequence(
                Digit(),Digit(),
                Ch('-'),
                FirstOf("Jan","Feb","Mar", "Apr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                Ch('-'),
                Digit(),Digit()
        );
    }

    @SuppressSubnodes
    Rule TimeCellValue() {
        return Sequence(TimeValue(), EOC());
    }

    @SuppressSubnodes
    Rule TimeValue() {
        return Sequence(
                Digit(),Digit(),
                Ch(':'),
                Digit(),Digit()
        );
    }

    @SuppressSubnodes
    Rule IntegerCellValue() {
        return Sequence(IntegerValue(), EOC());
    }

    @SuppressSubnodes
    Rule IntegerValue() {
        return Sequence(
                Digit(),
                ZeroOrMore(Digit())
        );
    }

    @SuppressSubnodes
    Rule FloatCellValue() {
        return Sequence(FloatValue(), EOC());
    }

    @SuppressSubnodes
    Rule FloatValue() {
        return FirstOf(
                Sequence(OneOrMore(Digit()),Optional(Sequence(',', OneOrMore(Digit()))) ,'.', ZeroOrMore(Digit())),
                Sequence('.', OneOrMore(Digit())),
                OneOrMore(Sequence(Digit(),Optional(Sequence(',', OneOrMore(Digit()))) ))
        );
    }


    Rule EmptyLine() {
        return Sequence(
                ZeroOrMore(EOC()),
                OneOrMore(EOL())
        );
    }

    Rule EmptyCell() {
        return EOC();
    }

    Rule FixedValueCell(String string) {
        return Sequence(string, Spacing(), EOC()).label('\'' + string + '\'');
    }


    Rule Digit() {
        return CharRange('0','9');
    }

    Rule Spacing() {
        return ZeroOrMore(AnyOf(" \t\n").label("Whitespace"));
    }

    Rule EOC() {
        return Ch(COLUMN_SEPARATOR);
    }

    Rule EOL() {
        return Ch(ROW_SEPARATOR);
    }

}
